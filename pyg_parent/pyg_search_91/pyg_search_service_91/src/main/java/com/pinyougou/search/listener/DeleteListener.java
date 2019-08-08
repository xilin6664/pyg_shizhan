package com.pinyougou.search.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;

import javax.jms.*;

public class DeleteListener implements MessageListener{

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        //1.从消息中获取goodsId
        TextMessage textMessage = (TextMessage)message;
        try {
            String goodsId = textMessage.getText();
            //2.根据goodId将索引库中的数据删除
            SimpleQuery query = new SimpleQuery("item_goodsid:"+goodsId);
            solrTemplate.delete(query);
            solrTemplate.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
