package com.changgou.search.dao;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-04-30 16:42
 * @Description:
 */
@Repository
public interface SkuEsMapper extends ElasticsearchRepository<SkuInfo, Long> {
}
