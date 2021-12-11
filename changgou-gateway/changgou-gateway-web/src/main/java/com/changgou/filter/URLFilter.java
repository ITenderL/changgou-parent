package com.changgou.filter;

/**
 * @Description: 不需要认证就能访问的路径
 * @Author: Hewei.Yuan
 * @CreateTime: 2020-07-29 10:04
 */
public class URLFilter {

    private static final String allurl = "/user/login,api/user/add";

    public static boolean hasAuthorize(String url) {
        String[] urls = allurl.split(",");
        for (String uri : urls) {
            if (url.equals(uri)) {
                return false;
            }
        }
        return true;
    }
}
