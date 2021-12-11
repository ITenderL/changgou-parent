package com.changgou.search.controller;

import com.changgou.search.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-04-30 16:51
 * @Description:
 */
@RestController
@CrossOrigin
@RequestMapping(value = "search")
public class SkuController {

    @Autowired
    private SkuService skuService;

    /**
     * 搜索
     * @param searchMap
     * @return
     */
    @GetMapping
    public Map search(@RequestParam(required = false) Map<String, String> searchMap){
        return  skuService.search(searchMap);
    }

    /**
     * 导入索引库
     * @return
     */
    @GetMapping(value = "/import")
    public Result importData() {
        skuService.importData();
        return new Result(true, StatusCode.OK, "导入成功！");
    }
}
