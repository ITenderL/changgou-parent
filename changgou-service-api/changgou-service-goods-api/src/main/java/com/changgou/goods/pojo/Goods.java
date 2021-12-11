package com.changgou.goods.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: yuanhewei
 * @CreateTime: 2020-07-16 17:17
 * @Description: spu和sku组信息
 */
public class Goods implements Serializable {
    // spu信息
    private Spu spu;

    // sku集合信息
    private List<Sku> skuList;

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}
