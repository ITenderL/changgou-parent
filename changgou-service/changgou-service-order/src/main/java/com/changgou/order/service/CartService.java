package com.changgou.order.service;

import com.changgou.order.pojo.OrderItem;

import java.util.List;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-10 15:05
 * @Description:
 */
public interface CartService {
    /**
     * 查询购物车商品集合
     * @param username
     * @return
     */
    List<OrderItem> list(String username);
    /**
     * 添加购物车
     * @param num
     * @param id
     * @param username
     */
    void add(Integer num, Long id, String username);
}
