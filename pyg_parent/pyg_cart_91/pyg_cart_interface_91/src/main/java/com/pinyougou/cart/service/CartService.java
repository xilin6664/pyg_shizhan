package com.pinyougou.cart.service;

import com.pinyougou.vo.Cart;

import java.util.List;

public interface CartService {
    List<Cart> findCartListByKey(String key);

    List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num);

    void saveCartListToRedis(List<Cart> cartList, String key);

    List<Cart> mergeList(List<Cart> userCartList, List<Cart> sessionCartList);

    void deleteCartListByKey(String key);
}
