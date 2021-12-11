package com.changgou.seckill.task;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import entity.IdWorker;
import entity.SeckillStatus;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-20 15:54
 * @Description:
 */
@Component
public class MultiThreadingCreateOrder {
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /***
     * 多线程下单操作
     */
    @Async
    public void createOrder() {
        try {
            System.out.println("先睡一会再下单！");
            Thread.sleep(10000);
            // Redis队列中获取用户排队信息
            SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps("SeckillOrderQueue").rightPop();
            if (seckillStatus == null) {
                return;
            }
            // 定义商品区间，id，username
            String time = seckillStatus.getTime();
            Long id = seckillStatus.getGoodsId();
            String username = seckillStatus.getUsername();
            // 先到SeckillGoodsQueue_ID队列中获取该商品信息，如果能获取则看可以下单，否则不能下单，清除排队信息
            Object sgoods = redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillStatus.getGoodsId()).rightPop();
            if (sgoods == null) {
                clearUserQueue(username);
                return;
            }
            String namespace = "SeckillGoods_" + time;
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(namespace).get(id);
            if (seckillGoods == null) {
                throw new RuntimeException("已售罄！");
            }
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setId(idWorker.nextId());
            seckillOrder.setSeckillId(id);
            seckillOrder.setMoney(seckillGoods.getCostPrice());
            seckillOrder.setUserId(username);
            seckillOrder.setCreateTime(new Date());
            // 支付状态
            seckillOrder.setStatus("0");
            // 秒杀订单存入redis
            /**
             * 存储订单redis中
             * 1.一个用户只允许一个未支付的秒杀订单
             * 2.订单存入reids
             *       hash  namespace -> SeckillOrder
             *                             username：SeckillOrder
             */
            redisTemplate.boundHashOps("SeckillOrder").put(username, seckillOrder);
            /**
             * 库存递减
             *    商品如果是最后一个，将Redis中商品删除，同步到MySql
             */
            seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
            Long size = redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillStatus.getGoodsId()).size();
            //if (seckillGoods.getStockCount() <= 0){
            if (size <= 0) {
                // 同步数量
                seckillGoods.setStockCount(size.intValue());
                // 同步到MySQL
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                // 删除Redis数据
                redisTemplate.boundHashOps(namespace).delete(id);
            } else {
                // 同步到Redis
                redisTemplate.boundHashOps(namespace).put(id, seckillGoods);
            }

            // 更新订单状态
            seckillStatus.setOrderId(seckillOrder.getId());
            seckillStatus.setMoney(Float.valueOf(seckillGoods.getCostPrice()));
            // 待付款
            seckillStatus.setStatus(2);
            redisTemplate.boundHashOps("UserQueueStatus").put(username, seckillStatus);

            // 输出发送时间
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            System.out.println("下单时间：" + simpleDateFormat.format(new Date()));
            // 给延时队列发消息
            rabbitTemplate.convertAndSend("delaySeckillQueue", (Object) JSON.toJSONString(seckillStatus), new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setExpiration("10000");
                    return message;
                }
            });
            System.out.println("下单完成了！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除Redis中用户排队状态和排队标识信息
     *
     * @param username
     */
    public void clearUserQueue(String username) {
        // 排队标识
        redisTemplate.boundHashOps("UserQueueCount").delete(username);
        // 排队信息清理掉
        redisTemplate.boundHashOps("UserQueueStatus").delete(username);
    }
}
