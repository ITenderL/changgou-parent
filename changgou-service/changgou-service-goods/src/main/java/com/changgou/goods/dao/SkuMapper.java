package com.changgou.goods.dao;
import com.changgou.goods.pojo.Sku;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.repository.query.Param;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:shenkunlin
 * @Description:Sku的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface SkuMapper extends Mapper<Sku> {
    @Update("update tb_sku set num = num - #{num} where id = #{id} and num >= #{num}")
    int decrCount(@Param("id") String id, @Param("num") Integer num);
}
