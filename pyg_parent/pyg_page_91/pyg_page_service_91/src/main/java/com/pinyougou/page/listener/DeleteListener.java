package com.pinyougou.page.listener;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.File;
import java.util.List;

public class DeleteListener implements MessageListener{

    @Autowired
    private TbItemMapper itemMapper;
    @Value("${PAGE_STATIC_DIR}")
    private String PAGE_STATIC_DIR;

    @Override
    public void onMessage(Message message) {
        //1.从message中获取goodsId
        TextMessage textMessage = (TextMessage)message;
        try {
            String goodsId = textMessage.getText();
            //2.根据goodsId查询库存列表
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(Long.valueOf(goodsId));
            criteria.andStatusEqualTo("1");
            List<TbItem> itemList = itemMapper.selectByExample(example);
            for(TbItem item : itemList){
                //3.根据库存对象的id动态拼接文件路径：D:\Work\appdata\htmls\91\[item.id].html
                //4.使用文件的删除方法删除该文件
                new File(PAGE_STATIC_DIR+item.getId()+".html").delete();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
