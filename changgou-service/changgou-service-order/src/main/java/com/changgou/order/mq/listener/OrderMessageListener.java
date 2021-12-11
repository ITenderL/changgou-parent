package com.changgou.order.mq.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-18 14:38
 * @Description:
 */
@Component
@RabbitListener(queues = "queue.order")
public class OrderMessageListener {

    @Autowired
    private OrderService orderService;

    /**
     * 支付结果监听
     * @param message
     */
    @RabbitHandler
    public void getMessage(String message) throws Exception {
        Map<String, String> resultMap = JSON.parseObject(message, Map.class);
        System.out.println("监听到的支付信息：" + resultMap);
        // 通信标识   return_code
        if ("SUCCESS".equals(resultMap.get("return_code"))) {
            // 业务结果  result_code、
            String result_code = resultMap.get("result_code");
            // 订单号   out_trade_no
            String out_trade_no = resultMap.get("out_trade_no");
            if ("SUCCESS".equals(result_code)) {
                // 微信支付交易流水号  transaction_id

                // 支付成功，修改订单
                orderService.updateStatus(out_trade_no, resultMap.get("time_end"), resultMap.get("transactionid"));
            }else {
                // 关闭支付

                // 支付失败，取消订单，库存回滚
                orderService.deleteOrder(out_trade_no);
            }
        }
    }
}
