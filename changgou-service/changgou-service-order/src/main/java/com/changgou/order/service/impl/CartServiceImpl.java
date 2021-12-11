package com.changgou.order.service.impl;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-10 15:06
 * @Description:
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SpuFeign spuFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询购物车商品集合
     * @param username
     * @return
     */
    @Override
    public List<OrderItem> list(String username) {
        // 获取命名空间下的所有数据
        return redisTemplate.boundHashOps("Cart_" + username).values();
    }

    /**
     * 添加购物车操作
     * @param num
     * @param id
     * @param username
     */
    @Override
    public void add(Integer num, Long id, String username) {
        if (num <= 0) {
            // 如果购物车中商品数量，<= 0则移除购物车中该商品的信息
            redisTemplate.boundHashOps("Cart_" + username).delete(id);
            Long size = redisTemplate.boundHashOps("Cart_" + username).size();
            if (size == null || size <= 0) {
                // 如果购物车中没有商品，则购物车也要移除
                redisTemplate.delete("Cart_" + username);
            }
            return;
        }
        // 查询商品详情
        // 1）sku
        Result<Sku> skuResult = skuFeign.findById(id);
        Sku sku = skuResult.getData();
        // 2)spu
        Result<Spu> spuResult = spuFeign.findById(sku.getSpuId());
        Spu spu = spuResult.getData();
        // 将加入购物车的信息封装成orderItem对象
        OrderItem orderItem = createOrderItem(num, id, sku, spu);
        // 将购物车数据添加到redis
        redisTemplate.boundHashOps("Cart_" + username).put(id, orderItem);
    }

    /**
     * 创建一个orderItem对象
     * @param num
     * @param id
     * @param sku
     * @param spu
     * @return
     */
    private OrderItem createOrderItem(Integer num, Long id, Sku sku, Spu spu) {
        // 把加入购物车的信息封装成OrderItem对象
        OrderItem orderItem = new OrderItem();
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());
        orderItem.setSpuId(Long.valueOf(spu.getId()));
        orderItem.setSkuId(id);
        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());
        orderItem.setNum(num);
        orderItem.setMoney(num*orderItem.getPrice());
        orderItem.setImage(sku.getImage());
        return orderItem;
    }
}
