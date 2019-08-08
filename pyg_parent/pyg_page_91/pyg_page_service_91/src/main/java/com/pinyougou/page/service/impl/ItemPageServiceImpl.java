package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ItemPageServiceImpl implements ItemPageService {
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("${PAGE_TEMPLATE_NAME}")
    private String PAGE_TEMPLATE_NAME;
    @Value("${PAGE_STATIC_DIR}")
    private String PAGE_STATIC_DIR;

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Override
    public void generateHtml(Long goodsId) throws Exception {
        generateHtmlByGoodsId(goodsId);
    }

    @Override
    public void generateHtmlAll() throws Exception {
        //1.查询所有，有效的，通过审核的商品
        TbGoodsExample example = new TbGoodsExample();
        TbGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeleteEqualTo("0");//未删除
        criteria.andAuditStatusEqualTo("2");//审核通过
        List<TbGoods> goodsList = goodsMapper.selectByExample(example);
        //2.根据goodsId生成静态页
        for(TbGoods goods : goodsList){
            generateHtmlByGoodsId(goods.getId());
        }
    }

    private void generateHtmlByGoodsId(Long goodsId) throws Exception {
        //0.创建模板文件
        //1.创建Configuration变量
        //2.设置参数：版本/模板目录/默认编码格式
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        //3.根据模板名称获取模板变量
        Template template = configuration.getTemplate(PAGE_TEMPLATE_NAME);
        //4.准备数据：查询Goods/Goodsdesc/item
        TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
        //根据商品的三级分类id查询三级分类名称，放到freemarker数据模型中
        TbItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id());
        TbItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id());
        TbItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());

        TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goodsId);
        criteria.andStatusEqualTo("1");//查询有效的status=1的库存对象
        List<TbItem> itemList = itemMapper.selectByExample(example);
        Map map = new HashMap<>();
        map.put("goods", goods);
        map.put("goodsDesc", goodsDesc);
        map.put("cat1Name", itemCat1.getName());
        map.put("cat2Name", itemCat2.getName());
        map.put("cat3Name", itemCat3.getName());
        map.put("itemList", itemList);
        for(TbItem item : itemList){
            map.put("item", item);
            //5.合并数据和模板生成到指定位置：D:\Work\appdata\htmls\91\[itemid].html
            Writer out = new FileWriter(new File(PAGE_STATIC_DIR+item.getId()+".html"));
            template.process(map, out);
            //6.关闭流
            out.close();
        }
    }
}
