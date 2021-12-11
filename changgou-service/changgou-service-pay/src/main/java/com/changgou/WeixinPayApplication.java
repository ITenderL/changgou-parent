package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-18 09:20
 * @Description:
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableEurekaClient
public class WeixinPayApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeixinPayApplication.class, args);
    }
}
