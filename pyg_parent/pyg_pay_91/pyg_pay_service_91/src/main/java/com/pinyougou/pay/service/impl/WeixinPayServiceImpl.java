package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import utils.HttpClient;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;

    @Override
    public Map createNative(String outTradeNo, long money) {
        //1.使用map组装微信请求参数
        Map<String, String> param = new HashMap<>();
        param.put("appid", appid);//公众号
        param.put("mch_id", partner);//商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("body", "品优购");//商品描述
        param.put("out_trade_no", outTradeNo);//商户订单号
        param.put("total_fee",String.valueOf(money));//总金额（分）
        param.put("spbill_create_ip", "127.0.0.1");//IP
        param.put("notify_url", "http://test.itcast.cn");//回调地址(随便写)
        param.put("trade_type", "NATIVE");//交易类型
        //2.使用httpclient发送请求
        try {
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);
            client.setXmlParam(WXPayUtil.generateSignedXml(param, partnerkey));
            client.post();
            String content = client.getContent();
            return WXPayUtil.xmlToMap(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //3.获取响应结果，转换成map返回
        return new HashMap();
    }

    @Override
    public Map queryPayResultStatus(String outTradeNo) {
        //1.使用map组装查询参数
        Map<String, String> param = new HashMap<>();
        param.put("appid", appid);//公众账号ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", outTradeNo);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        try {
            //2.使用httpclient调用微信服务
            int queryNum = 0;
            while(true){

                HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
                client.setHttps(true);
                client.setXmlParam(WXPayUtil.generateSignedXml(param, partnerkey));
                client.post();
                //3.获取查询结果
                String content = client.getContent();
                //进程睡眠3秒
                Thread.sleep(3000);
                if(queryNum >= 100){
                    //查询次数超限，默认是查询超时，返回查询超时
                    Map<String, String> resultMap = new HashMap<>();
                    resultMap.put("success", "0");
                    resultMap.put("message", "查询超时");
                    return resultMap;
                }
                queryNum ++;
                //4.转换成map，判断成功或失败
                if(StringUtils.isBlank(content)){
                    //返回为null，继续查询
                    continue;
                }
                Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
                String tradeState = resultMap.get("trade_state");

                //5.如果支付中，继续查询；如果是成功，返回成功；如果失败，返回失败；
                if(StringUtils.isBlank(tradeState)){
                    //状态码为空，表示失败，返回失败
                    resultMap.put("success", "0");
                    resultMap.put("message", "支付失败，请联系管理员！");
                    return resultMap;
                }
                if("SUCCESS".equals(tradeState)){
                    //支付成功，返回成功
                    resultMap.put("success", "1");
                    resultMap.put("message", "支付成功！");
                    return resultMap;
                }
                if("REFUND|CLOSED|REVOKED|PAYERROR".contains(tradeState)){
                    //支付失败，返回失败
                    resultMap.put("success", "0");
                    resultMap.put("message", "对不起由于您已退款或已关闭或其他原因导致支付失败！");
                    return resultMap;
                }
                if("NOTPAY|USERPAYING".contains(tradeState)){
                    //支付中，继续查询
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("success", "0");
        resultMap.put("message", "支付失败，请联系管理员！");
        return resultMap;
    }
}
