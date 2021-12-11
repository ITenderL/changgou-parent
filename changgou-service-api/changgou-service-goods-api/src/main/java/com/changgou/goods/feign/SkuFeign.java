package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-04-30 16:37
 * @Description:
 */
@FeignClient(name = "goods")
@RequestMapping(value = "/sku")
public interface SkuFeign {

    /**
     * 商品库存递减
     * @param decrmap
     * @return
     */
    @GetMapping(value = "/decr/count")
    Result decrCount(@RequestParam Map<String, Integer> decrmap);

    /**
     * 查询所有sku
     * @return
     */
    @GetMapping
    Result<List<Sku>> findAll();

    /***
     * 根据ID查询Sku数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    Result<Sku> findById(@PathVariable Long id);
}
