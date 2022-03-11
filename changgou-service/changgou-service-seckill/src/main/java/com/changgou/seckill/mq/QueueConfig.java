package com.changgou.seckill.mq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2020-08-03 17:44
 * @Description:
 */
@Configuration
public class QueueConfig {

    /**
     * 创建超时队列  Queue1
     * @return
     */
    @Bean
    public Queue delaySeckillQueue() {
        return QueueBuilder.durable("delaySeckillQueue")
                // 消息超市进入死信队列
                .withArgument("x-dead-letter-exchange", "seckillExchange")
                .withArgument("x-dead-letter-routing-key", "seckillQueue")
                .build();
    }

    /**
     * 真正坚挺的队列，接受超时后的消息   Queue2
     * @return
     */
    @Bean
    public Queue seckillQueue() {
        return new Queue("seckillQueue");
    }

    /**
     * 创建交换机
     */
    @Bean
    public Exchange seckillExchange() {
        return new DirectExchange("seckillExchange");
    }

    /**
     * 交换机绑定queue2
     * @param seckillQueue
     * @param seckillExchange
     * @return
     */
    @Bean
    public Binding secQueueBindingExchange(Queue seckillQueue, Exchange seckillExchange) {
        return BindingBuilder.bind(seckillQueue).to(seckillExchange).with("seckillQueue").noargs();
    }
}
