package com.changgou.search.service;

import java.util.Map;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-04-30 16:39
 * @Description:
 */
public interface SkuService {

    /**
     * 多条件搜索
     * @param searchMap
     * @return
     */
    Map<String, Object> search(Map<String, String> searchMap);

    /**
     * 导入数据
     */
    void importData();
}
