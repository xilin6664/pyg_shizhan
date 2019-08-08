package com.pinyougou.dataimport.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DataImportService {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    public void importDataFromDB2Solr(){
        //1.查询商品库存表有效（1==status）的所有库存数据
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> itemList = itemMapper.selectByExample(example);
        //1.1循环商品列表，将spec转换成map，放到动态映射属性specMap
        for(TbItem item : itemList){
            Map map = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(map);
        }
        //2.将数据保存到索引库
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    /**
     * 查询所有的商品分类，根据分类对应的模板获取品牌、规格和扩展属性值，以分类名为key存入redis
     */
    public void importDataFromDB2Redis(){
        //1.查询所有商品分类数据
        List<TbItemCat> itemCatList = itemCatMapper.selectByExample(null);
        for(TbItemCat itemCat : itemCatList){
            //2.循环分类数据，根据分类模板id查询模板对象
            TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(itemCat.getTypeId());
            //3.将模板的品牌以分类名为key放入redis
            List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(itemCat.getName(), brandList);
            //4.将模板的规格以分类名为key放入redis
            List<Map> specList = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
            //根据规格id查询规格选项集合，放到规格map中，key=options
            for(Map map : specList){
                TbSpecificationOptionExample example = new TbSpecificationOptionExample();
                TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
                criteria.andSpecIdEqualTo(Long.valueOf(String.valueOf(map.get("id"))));
                List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);
                map.put("options", options);
            }
            redisTemplate.boundHashOps("specList").put(itemCat.getName(), specList);
            //5.将模板的扩展属性以分类名为key 放入redis
            List<Map> custAttrList = JSON.parseArray(typeTemplate.getCustomAttributeItems(), Map.class);
            redisTemplate.boundHashOps("custAttrList").put(itemCat.getName(), custAttrList);
        }
    }

    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath*:spring/applicationContext*.xml");
        DataImportService dataImportService = (DataImportService) context.getBean("dataImportService");
        dataImportService.importDataFromDB2Solr();//将数据导入solr
//        dataImportService.importDataFromDB2Redis();//将数据导入redis
    }
}
