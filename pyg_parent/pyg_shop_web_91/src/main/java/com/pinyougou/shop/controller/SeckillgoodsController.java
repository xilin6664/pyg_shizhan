package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.sellergoods.service.GoodsDescService;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemService;
import com.pinyougou.sellergoods.service.SeckillGoodsService;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/seckillGoods")
public class SeckillgoodsController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private GoodsDescService goodsDescService;

    @Reference
    private ItemService itemService;

    @Reference
    private SeckillGoodsService seckillGoodsService;

    /**
     * 返回全部列表
     */
    @RequestMapping("/findAll")
    public List<TbItem> findAll() {
        return itemService.findAll();
    }


    /**
     * 返回全部列表
     */
    @RequestMapping("/findPage/{page}/{rows}")
    public PageResult findPage(@PathVariable("page") int page, @PathVariable("rows") int rows) {
        TbSeckillGoods seckillGoods = new TbSeckillGoods();
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        seckillGoods.setSellerId(sellerId);
        return seckillGoodsService.findPage(seckillGoods, page, rows);
    }

    /**
     * 增加
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbSeckillGoods seckillGoods) {

        //设置商家 ID
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        seckillGoods.setSellerId(sellerId);
        seckillGoods.setCreateTime(new Date());
        seckillGoods.setCheckTime(new Date());
        seckillGoods.setStatus("2");
        try {
            seckillGoodsService.add(seckillGoods);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbSeckillGoods seckillGoods) {
        try {
            seckillGoodsService.update(seckillGoods);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     */
    @RequestMapping("/findOne/{id}")
    public TbSeckillGoods findOne(@PathVariable("id") Long id) {
        return seckillGoodsService.findOne(id);
    }


    /**
     * 批量删除
     */
    @RequestMapping("/delete/{ids}")
    public Result delete(@PathVariable("ids") Long[] ids) {
        try {
            goodsService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }


    /**
     * 提交审核
     */
    @RequestMapping("/updateStatus/{status}/{selectIds}")
    public Result updateStatus(@PathVariable("status") String status, @PathVariable("selectIds") Long[] selectIds) {
        try {
            seckillGoodsService.updateStatus(status, selectIds);
            return new Result(true, "提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "提交失败");
        }
    }


    /**
     * 查询+分页
     */
    @RequestMapping("/search/{page}/{rows}")
    public PageResult search(@RequestBody TbGoods goods, @PathVariable("page") int page, @PathVariable("rows") int rows) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.findPage(goods, page, rows);
    }


}
