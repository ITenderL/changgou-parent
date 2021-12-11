package com.itheima.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-07 10:16
 * @Description:
 */
@Controller
@RequestMapping(value = "test")
public class TestController {
    /***
     * 访问/test/hello 跳转到demo1页面
     * @param model
     * @return
     */
    @RequestMapping("/hello")
    public String hello(Model model){
        model.addAttribute("message","Hello Thymeleaf!");
        return "demo1";
    }
}
