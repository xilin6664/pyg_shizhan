package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.vo.Cart;
import entity.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @RequestMapping("/addItemToCartList/{itemId}/{num}")
    @CrossOrigin(origins="http://item.pinyougou.com",allowCredentials="true")
    public Result addItemToCartList(HttpServletRequest request, HttpServletResponse response,
                                    @PathVariable("itemId") Long itemId, @PathVariable("num") Integer num){
        try{
            //1.获取sessionId
            String key = getSessionId(request, response);
            //获取用户登录名称，用户登录：username != anonymouseUser，key=username
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if(!"anonymousUser".equals(username)){
                key = username;
            }
            //2.根据sessionId从redis中获取cartList
            List<Cart> cartList = cartService.findCartListByKey(key);
            //3.将itemId和数量加入到的cartList
            cartList = cartService.addItemToCartList(cartList, itemId, num);
            //4.将新生成的cartList存入redis中
            cartService.saveCartListToRedis(cartList, key);
            return new Result(true, "恭喜您，商品加入购物车成功，请去购物车下单！");
        } catch (Exception e){
            return new Result(false, "对不起，您的商品已售罄或失效，请购买其他商品！");
        }

    }

    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request, HttpServletResponse response){
        //1.获取sessionId
        String key = getSessionId(request, response);
        //获取用户登录名称，判断是否登录
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //未登录，直接返回sessionId的购物车列表
        List<Cart> sessionCartList = cartService.findCartListByKey(key);
        if("anonymousUser".equals(username)){
            return sessionCartList;
        }
        //已登录，根据用户名查询用户购物车数据
        List<Cart> userCartList = cartService.findCartListByKey(username);
        //判断sessionid的购物车列表是否存在数据
        if(sessionCartList.size() > 0){
            //如果存在，将sessionid的购物车合并到用户的购物车列表中，将结果更新到缓存，返回合并结果
            userCartList = cartService.mergeList(userCartList, sessionCartList);
            cartService.saveCartListToRedis(userCartList, username);//保存用户的购物车
            cartService.deleteCartListByKey(key);//删除session的缓存
        }
        //如果不存在，直接返回用户购物车
        return userCartList;
    }

    /**
     * 根据sessionIdkey 从cookies获取sessionId
     * @param request
     * @param response
     * @return
     */
    private String getSessionId(HttpServletRequest request, HttpServletResponse response){
        //1.从cookie中获取sessionid：获取的key=sessionId
        String sessionId = CookieUtil.getCookieValue(request, "sessionId", "utf-8");
        //2.判断sessionId是否存在，不存在，根据request获取到session对象，获取sessionId，存入cookies，key=sessionId
        if(StringUtils.isBlank(sessionId)){
            sessionId = request.getSession().getId();
            CookieUtil.setCookie(request, response, "sessionId",
                    sessionId, 24*60*60, "utf-8");
        }
        //3.将sessionId返回
        return sessionId;
    }
}
