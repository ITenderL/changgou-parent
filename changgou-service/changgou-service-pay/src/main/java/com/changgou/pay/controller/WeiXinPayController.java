package com.changgou.pay.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeiXinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-18 11:18
 * @Description:
 */
@RestController
@RequestMapping(value = "/weixin/pay")
public class WeiXinPayController {
    @Autowired
    private WeiXinPayService weiXinPayService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping(value = "/notify/url")
    public String notifyUrl(HttpServletRequest request) throws Exception{
        // 获取网络字节流
        ServletInputStream is = request.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1){
            baos.write(buffer, 0, len);
        }
        byte[] bytes = baos.toByteArray();
        String xmlResult = new String(bytes, "UTF-8");
        Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);
        System.out.println(resultMap);
        String attach = resultMap.get("attach");
        Map<String, String> attachMap = JSON.parseObject(attach, Map.class);
        // 发送消息给mq
        rabbitTemplate.convertAndSend(attachMap.get("exchange"),attachMap.get("routingKey"),JSON.toJSONString(resultMap));
        //rabbitTemplate.convertAndSend("exchange.order","queue.order",JSON.toJSONString(resultMap));

        String result = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        return result;
    }

    /**
     * 查询订单支付状态
     * @param outtradeno
     * @return
     */
    @GetMapping("/status/query")
    public Result queryStatus(String outtradeno) {
        Map map = weiXinPayService.queryStatus(outtradeno);
        return new Result(true, StatusCode.OK, "支付状态查询成功！", map);
    }

    /**
     * 普通订单：
     *     exchange：exchange.order
     *     routingKey：queue.order
     * 秒杀订单：
     *      exchange：exchange.seckillorder
     *      routingKey：queue.seckillorder
     *      routingKey + exchange -> json -> attach发给微信服务器
     * @param parameterMap
     * @return
     */
    @GetMapping(value = "/create/native")
    public Result createNative(@RequestParam Map<String, String> parameterMap) {
        Map<String, String> resultMap = weiXinPayService.createNative(parameterMap);
        return new Result(true, StatusCode.OK,"创建二维码预付订单成功！", resultMap);
    }
}
