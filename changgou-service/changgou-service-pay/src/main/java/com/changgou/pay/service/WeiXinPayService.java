package com.changgou.pay.service;

import java.util.Map;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-18 11:17
 * @Description:
 */
public interface WeiXinPayService {
    /**
     * 查询订单支付状态
     * @param outtradeno
     * @return
     */
    Map queryStatus(String outtradeno);

    /**
     * 获取微信支付二维码
     * @param parameterMap
     * @return
     */
    Map<String, String> createNative(Map<String, String> parameterMap);
}
