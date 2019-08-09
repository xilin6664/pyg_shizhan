package com.pinyougou.seckill.task;

import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import utils.SystemConst;

import java.util.Date;
import java.util.List;

/**
 * 定时清除无效的订单,普通订单30分钟,秒杀订单5分钟
 */
@Component
public class OrderManagerTask {
    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    //@Scheduled(cron = "0 0/1 * * * ?")//每分钟执行一次
    public void clearOrder(){
        //1,查询所有未付款的普通订单
        TbOrderExample example = new TbOrderExample();
        example.createCriteria().andStatusEqualTo("0");
        List<TbOrder> orderList = orderMapper.selectByExample(example);
        Date now = new Date();//现在的时间
        if (orderList != null && orderList.size() > 0) {
           //查询到的订单集合有数据进行如下操作
            for (TbOrder tbOrder : orderList) {
                //判断该订单的创建时间+30分钟是否小于现在时间
                Date createTime = tbOrder.getCreateTime();
                if ((createTime.getTime() + 30L * 60L * 1000L) < now.getTime()) {
                    //小于现在的时间是无效订单将其状态跟新成逻辑删除状态
                    TbOrder order = new TbOrder();
                    order.setOrderId(tbOrder.getOrderId());
                    order.setStatus("6");
                    // orderMapper.updateByPrimaryKeySelective(order);//跟新数据库数据
                    //根据订单id查询到对应的订单商品
                    TbOrderItemExample itemExample = new TbOrderItemExample();
                    itemExample.createCriteria().andOrderIdEqualTo(tbOrder.getOrderId());
                    List<TbOrderItem> tbOrderItems = orderItemMapper.selectByExample(itemExample);
                    if (tbOrderItems != null && tbOrderItems.size() > 0) {
                        //订单商品集合中有数据
                        for (TbOrderItem orderItem : tbOrderItems) {
                            //获取到订单商品的商品id和数量
                            Long itemId = orderItem.getItemId();
                            Integer num = orderItem.getNum();
                            //根据itemId查询item
                            TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
                            //设置剩余商品数量
                            if (tbItem != null) {
                                tbItem.setStockCount(tbItem.getStockCount() + num);
                                //将修改后的数据跟新到数据库
                                itemMapper.updateByPrimaryKey(tbItem);
                            }

                        }
                    }
                }
            }
        }
        //2,查询所有未支付的秒杀的订单
        //从缓存中查询所有的订单
        List<TbSeckillOrder> seckillOrderList = redisTemplate.boundHashOps(TbSeckillOrder.class.getSimpleName()).values();
        if (seckillOrderList != null && seckillOrderList.size() > 0) {
            //如果查询到的订单有数据
            for (TbSeckillOrder order : seckillOrderList) {
                //判断订单是否是未支付,并且时间+5分钟是否小于现在时间
                if ("0".equals(order.getStatus()) && (order.getCreateTime().getTime() + 5 * 60 * 1000L) < now.getTime()) {
                    //未支付并且过期的订单
                    order.setStatus("6");//设置为关闭状态
                    seckillOrderMapper.updateByPrimaryKey(order);//更新到数据库
                    //根据秒杀订单查询秒杀商品
                    TbSeckillGoods seckillGoods = seckillGoodsMapper.selectByPrimaryKey(order.getSeckillId());
                    if (seckillGoods != null) {
                        //2.将商品放到该商品对应的队列中接着参与秒杀
                        redisTemplate.boundListOps(SystemConst.SECKILL_PREFIX_GOODSID + seckillGoods.getGoodsId()).leftPush(seckillGoods.getGoodsId());
                    }
                }
            }
        }

    }
}
