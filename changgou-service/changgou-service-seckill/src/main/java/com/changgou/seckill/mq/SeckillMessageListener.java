package com.changgou.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-21 14:25
 * @Description:
 */
@RabbitListener(queues = "${mq.pay.queue.seckillorder}")
public class SeckillMessageListener {
    @Autowired
    private SeckillOrderService seckillOrderService;

    @RabbitHandler
    public void getMessage(String message) {
        try {
            // 将支付信息转成map
            Map<String, String> resultMap = JSON.parseObject(message, Map.class);
            // return_code->通信标识->SUCCESS
            String return_code = resultMap.get("return_code");
            // 自定义数据
            String attach = resultMap.get("attach");
            Map<String, String> attachMap = JSON.parseObject(attach, Map.class);
            // 订单号
            String out_trade_no = resultMap.get("out_trade_no");
            if (return_code.equals("SUCCESS")) {
                // result_code->业务结果->SUCCESS->该订单的状态
                String result_code = resultMap.get("result_code");
                if (result_code.equals("SUCCESS")) {
                    // 修改订单状态
                    seckillOrderService.updatePayStatus(attachMap.get("username"), resultMap.get("transaction_id"), resultMap.get("time_end"));

                }else {
                    // FAIL订单回滚, 写入数据库，清除redis中的信息
                    seckillOrderService.deleteOrder(attachMap.get("username"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
