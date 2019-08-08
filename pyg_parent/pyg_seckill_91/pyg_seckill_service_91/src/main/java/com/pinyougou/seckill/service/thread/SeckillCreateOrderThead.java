package com.pinyougou.seckill.service.thread;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import entity.OrderRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import utils.IdWorker;

import java.util.Date;

@Component
public class SeckillCreateOrderThead implements Runnable {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Override
    public void run() {
        System.out.println("下单线程启动开始处理");
        //1.从orderRecord队列中获取要下的订单信息
        OrderRecord orderRecord = (OrderRecord) redisTemplate
                .boundListOps(OrderRecord.class.getSimpleName()).rightPop();
        //2.是否获取到数据
        if(null != orderRecord){
            Long id = orderRecord.getGoodId();
            String username = orderRecord.getUsername();
            //3.获取到数据，下单
            TbSeckillGoods goods = (TbSeckillGoods) redisTemplate
                    .boundHashOps(TbSeckillGoods.class.getSimpleName()).get(id);
            //4.存在且库存大于>0，
            // 4.1根据商品信息创建秒杀订单，
            TbSeckillOrder order = new TbSeckillOrder();
            order.setCreateTime(new Date());
            order.setId(idWorker.nextId());
            order.setMoney(goods.getCostPrice());
            order.setSeckillId(goods.getId());//秒杀商品id
            order.setSellerId(goods.getSellerId());
            order.setStatus("0");//未支付
            order.setUserId(username);
            // 4.2将秒杀订单保存到redis缓存
            redisTemplate.boundHashOps(TbSeckillOrder.class.getSimpleName()).put(username, order);
            // 4.3库存量-1，判断库存是否<0，
            goods.setStockCount(goods.getStockCount()-1);
            if(goods.getStockCount() <= 0){
                //4.3.1库存<=0，将商品数据更新到数据库，从redis中删除该商品缓存
                seckillGoodsMapper.updateByPrimaryKey(goods);
                redisTemplate.boundHashOps(TbSeckillGoods.class.getSimpleName()).delete(goods.getId());
            } else {
                //4.3.2库存>0，将库存量更新到缓存
                redisTemplate.boundHashOps(TbSeckillGoods.class.getSimpleName()).put(goods.getId(), goods);
            }
        }
        System.out.println("下单线程处理完毕！");
    }
}
