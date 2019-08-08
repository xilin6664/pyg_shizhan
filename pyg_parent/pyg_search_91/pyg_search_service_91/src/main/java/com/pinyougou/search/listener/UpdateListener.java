package com.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

public class UpdateListener implements MessageListener {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        //1.从message中获取goodsId
        TextMessage textMessage = (TextMessage)message;
        try {
            String goodsId = textMessage.getText();
            //2.根据goodId和状态=1查询该商品的所有库存列表
            //1.查询商品库存表有效（1==status）的所有库存数据
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andGoodsIdEqualTo(Long.valueOf(goodsId));
            List<TbItem> itemList = itemMapper.selectByExample(example);
            //1.1循环商品列表，将spec转换成map，放到动态映射属性specMap
            for(TbItem item : itemList){
                Map<String, String> map = JSON.parseObject(item.getSpec(), Map.class);
                item.setSpecMap(map);
            }
            //2.将数据保存到索引库
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
