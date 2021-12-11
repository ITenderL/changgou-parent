package com.changgou.oauth.interceptor;

import com.changgou.oauth.util.AdminToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-10 16:39
 * @Description:
 */
@Configuration
public class TokenRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        /**
         * 没有令牌，feign调用之前，生成令牌
         * feign调用之前，把令牌携带过去
         * feign调用之前，把令牌放到头中
         * feign调用之前，进行拦截
         */
        String token = AdminToken.adminToken();
        requestTemplate.header("Authorization", "bearer " + token);
    }
}
