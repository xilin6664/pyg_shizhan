package com.pinyougou.sms.controller;

import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.SMSUtils;

@RestController
@RequestMapping("/sms")
public class SmsController {

    @RequestMapping("/sendSms")
    public Result sendSms(String phoneNum,
                          String templateParam,String signName, String templateCode){
        //1.调用短信工具类发送短信
        boolean success = SMSUtils.sendSms(phoneNum, templateParam, signName, templateCode);
        //2.根据结果返回成功或失败
        return new Result(success, success?"短信发送成功！":"短信发送失败！");
    }
}
