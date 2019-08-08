package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<Cart> findCartListByKey(String key) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(key);
        if(null == cartList){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据itemId查询商品对象
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //2.如果商品未查询到，抛商品不存在异常
        if(null == item){
            throw new RuntimeException("对不起，商品不存在，请购买其他商品！");
        }
        //3.如果查询到，判断商品是否有效status=0，抛异常，商品失效
        if(!"1".equals(item.getStatus())){
            throw new RuntimeException("对不起，商品已失效，请购买其他商品！");
        }
        //4.根据商家的id从cartList中查询该商家的cart对象
        Cart cart = searchCartBySellerId(cartList, item.getSellerId());
        if(null != cart){
            //5.查询到商家的cart对象
            //5.1从cart对象的orderItemList中查询要添加的orderItem对象
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), item.getId());
            if(null != orderItem){
                //5.1.1查询到orderItem对象，计算数量和总金额
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*orderItem.getNum()));
                //判断数量是否<=0，是，将orderItem从orderItemList中删除
                if(orderItem.getNum() <= 0){
                    cart.getOrderItemList().remove(orderItem);
                }
                if(cart.getOrderItemList().size() <= 0){
                    //判断orderItemList是否<=0, 是，将商家的购物车对象从cartList中删除
                    cartList.remove(cart);
                }
            } else {
                //5.1.2未查询到orderItem对象，根据item和num创建orderItem对象，将orderItem对象放入orderItemList
                orderItem = createOrderItem(item, num);
                cart.getOrderItemList().add(orderItem);
            }

        } else {
            //6.未查询到商家的cart对象
            //6.1创建cart对象，将数据封装到对象中，将cart新对象放到cartList集合中
            cart = new Cart();
            cart.setSellerId(item.getSellerId());
            cart.setSellerName(item.getSeller());
            List<TbOrderItem> orderItemList = new ArrayList<>();
            cart.setOrderItemList(orderItemList);
            //根据item创建一个TbOrderItem对象，将TbOrderItem对象放入orderItemList
            orderItemList.add(createOrderItem(item, num));
            cartList.add(cart);
        }
        //7.将cartList返回
        return cartList;
    }

    @Override
    public void saveCartListToRedis(List<Cart> cartList, String key) {
        redisTemplate.boundHashOps("cartList").put(key, cartList);
    }

    @Override
    public List<Cart> mergeList(List<Cart> userCartList, List<Cart> sessionCartList) {
        if(null == userCartList || userCartList.size() <= 0){
            return sessionCartList;
        }
        for(Cart cart : sessionCartList){
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for(TbOrderItem orderItem : orderItemList){
                addItemToCartList(userCartList, orderItem.getItemId(),orderItem.getNum());
            }
        }
        return userCartList;
    }

    @Override
    public void deleteCartListByKey(String key) {
        redisTemplate.boundHashOps("cartList").delete(key);
    }

    /**
     * 根据sellerId从cartList中查询商家的购物车对象，查询到返回cart对象，未查询到返回null
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId){
        for(Cart cart : cartList) {
            if(sellerId.equals(cart.getSellerId())){
                //该商家是我们要找的商家
                return cart;
            }
        }
        return null;
    }

    /**
     * 根据商品对象和商品数量创建订单详情对象
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num){
        //1.判断num是否合法，不合法抛商品数量异常
        if(null == num || num <= 0){
            throw new RuntimeException("商品数量不合法！");
        }
        //2.合法，根据item和num创建orderItem对象
        TbOrderItem orderItem=new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;
    }

    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId){
        for(TbOrderItem orderItem : orderItemList) {
            if(itemId.longValue() == orderItem.getItemId().longValue()){
                //该商家是我们要找的商家
                return orderItem;
            }
        }
        return null;
    }
}
