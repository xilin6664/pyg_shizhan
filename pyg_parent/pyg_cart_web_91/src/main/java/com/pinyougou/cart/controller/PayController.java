package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private OrderService orderService;
    @Reference
    private WeixinPayService weixinPayService;

    @RequestMapping("/createNative")
    public Result createNative(){
        //1.从redis中根据当前登录用户获取该用户的支付日志
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.findPayLogByUser(username);
        //2.判断是否获取到支付日志
        if(null != payLog){
            //3.获取到日志，将日志的交易号和总金额发送给微信二维码服务，生成url
            Map map = weixinPayService.createNative(payLog.getOutTradeNo(), 1);
            String code_url = (String) map.get("code_url");
            if(StringUtils.isNotBlank(code_url)){
                //3.1 判断是否有code_url，有，成功，返回成功
                return new Result(true,code_url);
            } else {
                //3.2没有code_url，失败，返回失败提示
                return new Result(false,"对不起，生成二维码失败！");
            }
        } else {
            //4.未获取到，返回失败提示信息
            return new Result(false, "对不起，您的订单已失效，请重新下单！");
        }
    }

    @RequestMapping("/queryPayResultStatus")
    public Result queryPayResultStatus(){
        //1.获取当前登录用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //2.从redis缓存获取该用户的支付日志
        TbPayLog payLog = orderService.findPayLogByUser(username);
        //3.根据支付日志中的交易码查询该交易的微信支付结果
        Map<String, String> map = weixinPayService.queryPayResultStatus(payLog.getOutTradeNo());
        //4.根据结果返回成功或失败
        String success = (String) map.get("success");
        //5.如果是支付成功，根据用户id更新用户支付日志和订单的支付状态
        orderService.updatePayStataus(payLog.getOutTradeNo(), map.get("transaction_id"));
       return new Result("1".equals(success)?true:false, String.valueOf(map.get("message")));
    }
}
