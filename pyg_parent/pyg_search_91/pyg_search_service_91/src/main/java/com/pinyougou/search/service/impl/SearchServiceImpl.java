package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map search(Map searchEntity) {
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchEntity.get("keywords"));
        query.addCriteria(criteria);

        //使用分组查询获取商品的分类并展示
        SimpleQuery groupQuery = new SimpleQuery();
        groupQuery.addCriteria(criteria);//设置查询 条件和高亮条件一致，保证查询结果是一致的
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");//设置分组的域名
        groupQuery.setGroupOptions(groupOptions);
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(groupQuery, TbItem.class);
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");//获取域名分组结果
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        List<String> categoryList = new ArrayList<>();
        for(GroupEntry<TbItem> groupEntry : groupEntries){
            categoryList.add(groupEntry.getGroupValue());
        }

        //根据商品第一个分类查询该分类下的品牌和规格数据，并返回
        List<Map> brandList = new ArrayList<>();//品牌list
        List<Map> specList = new ArrayList<>();//规格list
        if(categoryList.size() > 0){
            String categoryName = categoryList.get(0);
            //根据名获取品牌，放入结果中
            brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(categoryName);
            //根据名获取规格，放入返回结果中
            specList = (List<Map>) redisTemplate.boundHashOps("specList").get(categoryName);
        }

        //给返回的关键字添加高亮显示
        HighlightOptions hightLightOptions = new HighlightOptions();
        hightLightOptions.addField("item_title");//设置需要高亮显示的域名
        hightLightOptions.setSimplePrefix("<font style='color:red'>");//设置高亮显示标签前缀
        hightLightOptions.setSimplePostfix("</font>");//设置高亮显示标签后缀
        query.setHighlightOptions(hightLightOptions);

        //添加过滤查询条件
        SimpleFilterQuery filterQuery = new SimpleFilterQuery();

        //获取过滤条件，判断是否存在
        String category = (String) searchEntity.get("category");
        if(StringUtils.isNotBlank(category)){
            //存在时，添加到过滤查询条件中
            filterQuery.addCriteria(new Criteria("item_category").is(category));
        }
        //获取过滤条件，判断是否存在
        String brand = (String) searchEntity.get("brand");
        if(StringUtils.isNotBlank(brand)){
            //存在时，添加到过滤查询条件中
            filterQuery.addCriteria(new Criteria("item_brand").is(brand));
        }
        //获取过滤条件，判断是否存在
        Map<String, String> specMap = (Map) searchEntity.get("spec");
        if(null != specMap && specMap.keySet().size() > 0){
            for(String key : specMap.keySet()){
                String value = specMap.get(key);
                if(StringUtils.isNotBlank(value)){
                    //存在时，添加到过滤查询条件中
                    filterQuery.addCriteria(new Criteria("item_spec_"+key).is(value));
                }
            }
        }
        //获取过滤条件，判断是否存在
        String price = (String) searchEntity.get("price");
        if(StringUtils.isNotBlank(price)){
            String[] priceArr = price.split("-");
            if("*".equals(priceArr[1])){
                //3000元以上
                //存在时，添加到过滤查询条件中
                filterQuery.addCriteria(new Criteria("item_price").greaterThanEqual(priceArr[0]));
            } else {
                //3000元以下的区间
                filterQuery.addCriteria(new Criteria("item_price").between(priceArr[0], priceArr[1]));
            }
        }

        //添加排序条件
        String sort = (String) searchEntity.get("sort");
        String sortField = (String) searchEntity.get("sortField");
        //判断是否有排序条件
        if(StringUtils.isNotBlank(sort)
                && StringUtils.isNotBlank(sortField)){
            //有，判断是升序或降序
            if("ASC".equals(sort)){
                //升序，按升序查询
                query.addSort(new Sort(Sort.Direction.ASC, sortField));
            } else {
                //降序，按降序查询
                query.addSort(new Sort(Sort.Direction.DESC, sortField));
            }
        }

        query.addFilterQuery(filterQuery);

        //设置分页参数
        Integer page = (Integer) searchEntity.get("page");//当前页
        Integer size = (Integer) searchEntity.get("size");//每页条数
        //将分页参数取出，判断是否存在
        if(null == page || page <= 0){
            page = 1;//数据不合法，改为默认第一页
        }
        if(null == size || size <= 0){
            size = 20;//数据不合法，改为默认第一页
        }
        //存在，将参数设置到查询条件中
        query.setOffset((page - 1) * size);//当前页第一条数据的索引值：（当前页-1）*每页条数
        query.setRows(size);//每页条数

        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);
        List<HighlightEntry<TbItem>> highlighted = tbItems.getHighlighted();
        for(HighlightEntry<TbItem> highlightEntry : highlighted){
            TbItem entity = highlightEntry.getEntity();//高亮对应的原始的item对象
            //判断高亮数据是否存在
            List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
            if(null != highlights && highlights.size() > 0
                    && null != highlights.get(0).getSnipplets() && highlights.get(0).getSnipplets().size() > 0){
                entity.setTitle(highlights.get(0).getSnipplets().get(0));
            }
        }


        Map map = new HashMap();
        map.put("total", tbItems.getTotalElements());//总条数
        map.put("totalPages", tbItems.getTotalPages());//总页数
        map.put("itemList", tbItems.getContent());//每页数据集合
        map.put("categoryList", categoryList);//分类名集合
        map.put("brandList", brandList);//品牌集合
        map.put("specList", specList);//规格集合
        return map;
    }

}
