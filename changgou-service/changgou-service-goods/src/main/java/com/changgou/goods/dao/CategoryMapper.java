package com.changgou.goods.dao;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:Category的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface CategoryMapper extends Mapper<Category> {

}
