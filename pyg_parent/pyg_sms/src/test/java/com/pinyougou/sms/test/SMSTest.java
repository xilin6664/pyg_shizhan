package com.pinyougou.sms.test;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import utils.HttpClient;

import java.io.IOException;
import java.text.ParseException;

public class SMSTest {
    
    @Test
    public void testSms() throws IOException, ParseException {
        //1.创建httpclient对象，设置请求路径
        HttpClient httpClient = new HttpClient("http://localhost:7788/sms/sendSms");
        //2.添加请求参数
        httpClient.addParameter("phoneNum", "234234234");
        String validecode = RandomStringUtils.randomNumeric(4);
        httpClient.addParameter("templateParam","{'validcode':'"+validecode+"'}");
        httpClient.addParameter("signName", "九纹龙");
        httpClient.addParameter("templateCode", "SMS_94290024");
        //3.发送post请求
        httpClient.post();
        //4.获取结果，打印
        String content = httpClient.getContent();
        System.out.println(content);
    }
}
