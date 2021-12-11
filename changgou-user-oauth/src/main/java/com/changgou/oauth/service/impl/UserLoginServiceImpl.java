package com.changgou.oauth.service.impl;

import com.changgou.oauth.service.UserLoginService;
import com.changgou.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Map;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-08 16:15
 * @Description:
 */
@Service
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    /**
     * 用户登录
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @param grant_type
     */
    @Override
    public AuthToken login(String username, String password, String clientId, String clientSecret, String grant_type) throws Exception {
        // 调用请求地址 http://localhost:9001/oauth/token
        //String url = "http://localhost:9001/oauth/token";
        ServiceInstance serviceInstance = loadBalancerClient.choose("user-auth");
        String url = serviceInstance.getUri() + "/oauth/token";
        // 请求提交数据封装
        MultiValueMap parameterMap = new LinkedMultiValueMap();
        parameterMap.add("username", username);
        parameterMap.add("password", password);
        parameterMap.add("grant_type", grant_type);

        // 请求头封装
        String Authorization = "Basic " + new String(Base64.getEncoder().encode((clientId + ":" + clientSecret).getBytes()), "UTF-8");
        MultiValueMap headerMap = new LinkedMultiValueMap();
        headerMap.add("Authorization", Authorization);
        HttpEntity httpEntity = new HttpEntity(parameterMap, headerMap);
        /**
         * 1)请求地址
         * 2）提交方式
         * 3）请求提交信息封装（请求体，请求头）
         * 4）响应数据类型
         */
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        Map<String, String> map = response.getBody();
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(map.get("access_token"));
        authToken.setRefreshToken(map.get("refresh_token"));
        authToken.setJti(map.get("jti"));
        return authToken;
    }
}
