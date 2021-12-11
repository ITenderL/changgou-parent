package com.changgou.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeiXinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-18 11:17
 * @Description:
 */
@Service
public class WeiXinPayServiceImpl implements WeiXinPayService {
    @Value("${weixin.appid}")
    private String appid;

    @Value("${weixin.partner}")
    private String partner;

    @Value("${weixin.partnerkey}")
    private String partnerkey;

    @Value("${weixin.notifyurl}")
    private String notifyurl;

    /**
     * 查询订单支付状态
     * @param outtradeno
     * @return
     */
    @Override
    public Map queryStatus(String outtradeno) {
        try {
            // 参数
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("appid", appid);
            paramMap.put("mch_id", partner);
            // 随机字符串
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            // 订单号
            paramMap.put("out_trade_no", outtradeno);

            // 将map转换成xml类型,可以携带签名
            String xmlparameters = WXPayUtil.generateSignedXml(paramMap, partnerkey);

            // URL地址
            String url = "https://api.mch.weixin.qq.com/pay/orderquery";
            // 提交方式
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            // 提交参数
            httpClient.setXmlParam(xmlparameters);
            // 执行请求
            httpClient.post();
            // 获取返回数据
            String result = httpClient.getContent();
            // 返回数据转换成map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取微信支付二维码
     * @param parameterMap
     * @return
     */
    @Override
    public Map<String, String> createNative(Map<String, String> parameterMap) {
        try {
            // 参数
            Map<String, String> paramMap = new HashMap<String, String>();

            paramMap.put("appid", appid);
            paramMap.put("mch_id", partner);
            // 随机字符串
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            // 内容
            paramMap.put("body", "胖将军给我还钱！臭胖胖！！！！！");
            // 订单号
            paramMap.put("out_trade_no", parameterMap.get("outtradeno"));
            // 交易金额 单位：分
            paramMap.put("total_fee", parameterMap.get("totalfee"));
            paramMap.put("spbill_create_ip", "127.0.0.1");
            // 交易结果返回通知地址
            paramMap.put("notify_url", notifyurl);
            paramMap.put("trade_type", "NATIVE");

            // 封装自定义数据
            String exchange = parameterMap.get("exchange");
            String routingKey = parameterMap.get("routingKey");
            Map<String, String> attachMap = new HashMap<>();
            attachMap.put("exchange", exchange);
            attachMap.put("routingKey", routingKey);
            // 如果是秒杀订单，需要把username传过去
            String username = parameterMap.get("username");
            if (!StringUtils.isEmpty(username)){
                attachMap.put("username", username);
            }
            String attach = JSON.toJSONString(attachMap);
            paramMap.put("attach", attach);

            // 将map转换成xml类型,可以携带签名
            String xmlparameters = WXPayUtil.generateSignedXml(paramMap, partnerkey);

            // URL地址
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            // 提交方式
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            // 提交参数
            httpClient.setXmlParam(xmlparameters);
            // 执行请求
            httpClient.post();
            // 获取返回数据
            String result = httpClient.getContent();
            // 返回数据转换成map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
