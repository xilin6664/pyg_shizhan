package com.pinyougou.seckill.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import utils.SystemConst;

import java.util.Date;
import java.util.List;

@Component
public class SeckillGoodsDataImportTask {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0/20 * * * * ?")
    public void importSeckillGoodsFromDBToRedis(){
        //1.查询所有审核通过（status=1），剩余库存大于0（stockCount>0），开始时间<= 当前时间 <结束时间
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        criteria.andStockCountGreaterThan(0);
        Date date = new Date();
        criteria.andStartTimeLessThanOrEqualTo(date);
        criteria.andEndTimeGreaterThan(date);
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
        //2.循环将数据以goodsId为key保存到redis的hash中
        for(TbSeckillGoods goods : seckillGoodsList){
            redisTemplate.boundHashOps(TbSeckillGoods.class.getSimpleName()).put(goods.getId(),goods);
            //为每个商品创建该商品的队列，其中根据商品的剩余库存放商品id
            createQueueForGoods(goods);
        }
    }
    private void createQueueForGoods(TbSeckillGoods goods){
        //1.循环剩余库存量
        for(int i=0; i<goods.getStockCount(); i++){
            //2.将id根据库存量个数放到该商品对应的队列中
            redisTemplate.boundListOps(SystemConst.SECKILL_PREFIX_GOODSID+goods.getId()).leftPush(goods.getId());
        }

    }

}
