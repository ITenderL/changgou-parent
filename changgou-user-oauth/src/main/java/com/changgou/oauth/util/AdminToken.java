package com.changgou.oauth.util;

import com.alibaba.fastjson.JSON;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-10 16:34
 * @Description:
 */
public class AdminToken {

    /**
     * 管理员令牌发放
     * @return
     */
    public static String adminToken() {
        // 加载证书
        ClassPathResource resource = new ClassPathResource("changgou.jks");
        // 读取证书数据
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, "changgou".toCharArray());
        // 获取私钥
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair("changgou", "changgou".toCharArray());
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 创建令牌
        Map<String,Object> payload = new HashMap<>();
        payload.put("nikename", "changgou");
        payload.put("address", "china");
        payload.put("authorities", new String[] {"admin", "oauth"});
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(payload), new RsaSigner(privateKey));
        String token = jwt.getEncoded();
        return token;
    }
}
