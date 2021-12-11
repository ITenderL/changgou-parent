package com.changgou.user.feign;

import com.changgou.user.pojo.User;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-10 10:42
 * @Description:
 */
@FeignClient(value = "user")
@RequestMapping(value = "/user")
public interface UserFeign {

    /**
     * 查询用户信息
     * @param id
     * @return
     */
    @GetMapping("load/{id}")
    Result<User> findById(@PathVariable String id);
}
