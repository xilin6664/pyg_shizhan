package com.pinyougou.pay.service;

import java.util.Map;

public interface WeixinPayService {
    Map createNative(String outTradeNo, long v);

    Map queryPayResultStatus(String outTradeNo);
}
