package com.changgou.token;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-08 15:18
 * @Description:
 */
public class CreateJwtTestDemo {

    @Test
    public void testCreateToken() {
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
        System.out.println(token);
    }

    @Test
    public void testParseToken() {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZGRyZXNzIjoiY2hpbmEiLCJyb2xlIjoiYWRtaW4sdXNlciIsIm5pa2VuYW1lIjoiY2hhbmdnb3UifQ.qu0YWqVYoRkFVtZyTMGsUAwG-FTxl3dwLvWQHj0Gens9tBk2rz32GX5433gf1tRcHK2XSWfl_KwEEMuf4BadjBn8q8saO8I7xNq-iF4bgvO0WgUtrlzRmeT3wQWvct1MsV3a1_BQDtKxNfEjOuwG_y7RB0UunOkderW2m9vnkIgRUBIwAA8TVsZAbJNYa6t5Okrclsg0EoChbuFKkRohxOAtbvTyHX-IZYM6OLzClI2SojWZrgmWUns0i91Z4o9zAKj2KuU2Ps5bFQlese_-hP03C6os9nFcIMTsRrk8hW3JaoI_IxLZkxxl2z17igzX1uqP6ljHcBDFFM48XLzYNQ";
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA553Q6WjS/KdrkLVdep+lx2mxziRq4fswc0s4lY3B6ce6+3too697fCVqpeKuq0xxI86kxU6yTd+zPukFocVBNFyP6oOFtkOuyjoEFWBmsRGcKblEPeo5GvzNFZjnfD8+q0M95CwCjOlmHVM6vK+Nmmg1be/14B9O0oYER6POBLLpTiol69Fw5AEXwxVALXzJQo8/t8k60nswbM4j+PJiPvK0+B/iw/ZhDTALvjBzzx5RANmR3eVM7IW6p0UcHeWZp5qrEY3ceeij57oe0+SLStAlmUQhehxQ57QaURmf6p+L7L1u/8XYgb/TjBPfyQcICEzTfAaUl3+XXfKgH3FjSwIDAQAB-----END PUBLIC KEY-----";
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));
        String claims = jwt.getClaims();
        System.out.println(claims);
    }
}
