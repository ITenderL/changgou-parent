package com.changgou.order.mq.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2020-07-30 17:29
 * @Description:
 */
@Component
@RabbitListener(queues = "orderListenerQueue")
public class DelayMessageListener {

    /**
     * 延时队列监听
     * @param message
     */
    @RabbitHandler
    public void getListenerMessage(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("监听消息的时间：" + sdf.format(new Date()));
        System.out.println("监听到的消息：" + message);
    }
}
