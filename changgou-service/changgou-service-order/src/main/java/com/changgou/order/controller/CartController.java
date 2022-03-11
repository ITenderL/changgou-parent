package com.changgou.order.controller;

import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import com.changgou.order.service.OrderService;
import com.google.common.collect.Ordering;
import entity.Result;
import entity.StatusCode;
import entity.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-10 14:08
 * @Description:
 */
@RestController
@RequestMapping(value = "cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping(value = "list")
    public Result<List<OrderItem>> list() {
        // 获取当前用户信息
        // OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // String token = details.getTokenValue();
        Map<String, String> userInfo = TokenDecode.getUserInfo();
        System.out.println(userInfo);
        String username = userInfo.get("username");
        // 获取用户登录名
        // String username = "szitheima";
        // 查询购物车列表
        List<OrderItem> orderItems = cartService.list(username);
        return new Result<>(true, StatusCode.OK, "购物车列表查询成功！", orderItems);
    }

    /**
     * 添加购物车
     * @param num
     * @param id
     * @return
     */
    @GetMapping(value = "/add")
    public Result add(Integer num, Long id) {
        Map<String, String> userInfo = TokenDecode.getUserInfo();
        System.out.println(userInfo);
        String username = userInfo.get("username");
        cartService.add(num, id, username);
        return new Result(true, StatusCode.OK, "添加购物车成功！");
    }
}
