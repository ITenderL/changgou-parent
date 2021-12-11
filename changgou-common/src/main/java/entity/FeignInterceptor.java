package entity;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-10 16:39
 * @Description:
 */
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        /**
         * 获取用户令牌
         * 将令牌封装到头文件中
         */
        // 记录了当前用户请求得所有数据，包括请求头和请求参数
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 获取请求头中数据，所有头的名字
        Enumeration<String> headerNames = requestAttributes.getRequest().getHeaderNames();
        while (headerNames.hasMoreElements()) {
            // 请求头的key
            String headerKey = headerNames.nextElement();
            // 请求头的value
            String headerValue = requestAttributes.getRequest().getHeader(headerKey);
            System.out.println(headerKey + ":" + headerValue);

            // 将请求头信息封装到头中，feign调用的时候会传给下一个微服务
            requestTemplate.header(headerKey, headerValue);
        }

    }
}
