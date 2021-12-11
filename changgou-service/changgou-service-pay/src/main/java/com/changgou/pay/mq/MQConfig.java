package com.changgou.pay.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-18 14:15
 * @Description:
 */
@Configuration
public class MQConfig {
    @Autowired
    private Environment env;

    /**
     * 创建队列
     * @return
     */
    public Queue orderQueue() {
        return new Queue(env.getProperty("mq.pay.queue.order"));
    }

    /**
     * 创建交换机
     * @return
     */
    public Exchange orderExchange() {
        return new DirectExchange(env.getProperty("mq.pay.exchange.order"), true, false);
    }

    /**
     * 绑定交换机和队列
     * @param orderQueue
     * @param orderExchange
     * @return
     */
    public Binding orderQueueExchange(Queue orderQueue, Exchange orderExchange) {
        return BindingBuilder.bind(orderExchange).to(orderExchange).with(env.getProperty("mq.pay.routing.key")).noargs();
    }

    /***************************秒杀队列创建******************************/

    /**
     * 创建队列
     * @return
     */
    @Bean
    public Queue orderSeckillQueue() {
        return new Queue(env.getProperty("mq.pay.queue.seckillorder"));
    }

    /**
     * 创建交换机
     * @return
     */
    @Bean
    public Exchange orderSeckillExchange() {
        return new DirectExchange(env.getProperty("mq.pay.exchange.seckillorder"),true,false);
    }

    /**
     * 队列绑定交换机
     * @param orderSeckillQueue
     * @param orderSeckillExchange
     * @return
     */
    public Binding orderSeckillQueueExchange(Queue orderSeckillQueue, Exchange orderSeckillExchange) {
        return BindingBuilder.bind(orderSeckillQueue).to(orderSeckillExchange).with(env.getProperty("my.pay.routing.seckillkey")).noargs();
    }

}
