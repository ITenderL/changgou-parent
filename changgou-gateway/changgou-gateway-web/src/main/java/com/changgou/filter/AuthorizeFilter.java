package com.changgou.filter;

import com.changgou.utils.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-07 15:06
 * @Description: 全局过滤器，实现用户权限鉴别
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    //令牌头名字
    private static final String AUTHORIZE_TOKEN = "Authorization";
    /**
     * 全局拦截
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 如果用户是登陆或者一些不需要权限认证功能，直接放行
        String url = request.getURI().toString();
        if (!URLFilter.hasAuthorize(url)) {
            return chain.filter(exchange);
        }

        // 获取用户令牌信息
        // 1）头文件中
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        // boolean true令牌在头中，false令牌不在头中，将令牌封装到头文件中
        boolean hasToken = true;
        // 2）参数中
        if (StringUtils.isEmpty(token)) {
            token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
            hasToken = false;
        }
        // 3）cookie中
        if (StringUtils.isEmpty(token)) {
            HttpCookie httpCookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if (httpCookie != null) {
                token = httpCookie.getValue();
            }
        }
        // 有令牌则校验令牌是否有效
        //try {
        //    JwtUtil.parseJWT(token);
        //} catch (Exception e) {
        //    // 无效拦截
        //    // 设置响应状态码401
        //    response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //    // 相应空数据
        //    return response.setComplete();
        //}
        //无令牌则拦截
        if (StringUtils.isEmpty(token)) {
            // 设置响应状态码401
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }else {
            if (!hasToken) {
                if (!token.startsWith("bearer ") && !token.startsWith("Bearer ")){
                    token = "bearer " + token;
                }
                // 将令牌封装到头文件中
                request.mutate().header(AUTHORIZE_TOKEN, token);
            }
        }

        // 有效放行
        return chain.filter(exchange);
    }

    /**
     * 排序，越小越先执行
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
