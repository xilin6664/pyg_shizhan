package com.pinyougou.seckill.service.impl;
import java.util.Date;
import java.util.List;

import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.thread.SeckillCreateOrderThead;
import entity.OrderRecord;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.pojo.TbSeckillGoodsExample.Criteria;
import com.pinyougou.seckill.service.SeckillGoodsService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Transactional;

import entity.PageResult;
import utils.IdWorker;
import utils.SystemConst;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillGoods> findAll() {
		return redisTemplate.boundHashOps(TbSeckillGoods.class.getSimpleName()).values();
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillGoods> page=   (Page<TbSeckillGoods>) seckillGoodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillGoods seckillGoods) {
		seckillGoodsMapper.insert(seckillGoods);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillGoods seckillGoods){
		seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillGoods findOne(Long id){
		return (TbSeckillGoods) redisTemplate.boundHashOps(TbSeckillGoods.class.getSimpleName()).get(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillGoodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillGoodsExample example=new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillGoods!=null){			
						if(seckillGoods.getTitle()!=null && seckillGoods.getTitle().length()>0){
				criteria.andTitleLike("%"+seckillGoods.getTitle()+"%");
			}
			if(seckillGoods.getSmallPic()!=null && seckillGoods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+seckillGoods.getSmallPic()+"%");
			}
			if(seckillGoods.getSellerId()!=null && seckillGoods.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillGoods.getSellerId()+"%");
			}
			if(seckillGoods.getStatus()!=null && seckillGoods.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillGoods.getStatus()+"%");
			}
			if(seckillGoods.getIntroduction()!=null && seckillGoods.getIntroduction().length()>0){
				criteria.andIntroductionLike("%"+seckillGoods.getIntroduction()+"%");
			}
	
		}
		
		Page<TbSeckillGoods> page= (Page<TbSeckillGoods>)seckillGoodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private IdWorker idWorker;
	@Autowired
	private ThreadPoolTaskExecutor executor;
	@Autowired
	private SeckillCreateOrderThead seckillCreateOrderThead;

    @Override
    public void saveOrder(Long id, String username) {
    	//从用户该商品的set中判断是否有该用户
		Boolean isMember = redisTemplate.boundSetOps(SystemConst.SECKILL_PREFIX_USERID + id).isMember(username);
		if(isMember){
			//在队列中，判断用户是否已下单成功，
			TbSeckillOrder order = (TbSeckillOrder) redisTemplate
					.boundHashOps(TbSeckillOrder.class.getSimpleName()).get(username);
			if(null != order){
				//成功，抛您以秒杀成功，请尽快支付
				throw new RuntimeException("您以秒杀成功，请尽快支付！");
			} else {
				//未成功，抛您正在排队异常
				throw new RuntimeException("您正在排队，请耐心等待！");
			}
		}

        //1.根据商品id从该商品的队列中获取数据
		id = (Long) redisTemplate.boundListOps(SystemConst.SECKILL_PREFIX_GOODSID + id).rightPop();
		//2.判断商品队列中的id是获取到，未获取到，说明商品售罄
		if(null == id){
			//3.不存在或<=0，抛异常，对不起商品已售罄
			throw new RuntimeException("对不起，商品已售罄！");
		}
		//商品未售罄，可以下单
		//将用户id保存到用户该商品的set中
		redisTemplate.boundSetOps(SystemConst.SECKILL_PREFIX_USERID + id).add(username);
		//记录用户id和商品id，放入下单队列：OrderRecord
		redisTemplate.boundListOps(OrderRecord.class.getSimpleName()).leftPush(new OrderRecord(id, username));
		//调用线程池方法，开启线程在后台异步处理订单
		executor.execute(seckillCreateOrderThead);
    }

}
