package com.changgou.seckill.timer;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import entity.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-05-20 11:36
 * @Description:
 */
@Component
public class SeckillGoodsPushTask {
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0 0 0 * * ?")
    //@Scheduled(cron = "0/10 * * * * ?")
    public void loadGoodsPushRedis() {
        /**
         * 1.查询符合当前秒杀时间的菜单
         * 2.秒杀商品库存 > 0 stock_count
         * 3.审核状态 审核通过  status=1
         * 4.开始时间 >= start_time 结束时间 < end_time
         */
        // 获取时间菜单
        List<Date> dateMenus = DateUtil.getDateMenus();
        // 循环查询每个时间区间的秒杀商品
        for (Date dateMenu : dateMenus) {
            // 将时间转换成字符串格式yyyyMMddHH
            String timeSpace = "SeckillGoods_" + DateUtil.date2Str(dateMenu);
            /**
             * 1.查询符合当前秒杀时间的菜单
             * 2.秒杀商品库存 > 0 stock_count
             * 3.审核状态 审核通过  status=1
             * 4.开始时间 >= start_time 结束时间 < end_time
             */
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            // 审核状态 审核通过  status=1
            criteria.andEqualTo("status", "1");
            // 库存大于0
            criteria.andGreaterThan("stockCount",0);
            // 大于等于秒杀开始时间
            criteria.andGreaterThanOrEqualTo("startTime", dateMenu);
            // 时间小于秒杀结束时间
            criteria.andLessThan("endTime", DateUtil.addDateHour(dateMenu, 2));
            // 排除redis中已经存在seckillGoods
            Set keys = redisTemplate.boundHashOps(timeSpace).keys();
            if (keys != null && keys.size() > 0) {
                criteria.andNotIn("id", keys);
            }
            // 执行查询
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);
            // 将秒杀商品存放到redis
            for (SeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps(timeSpace).put(seckillGood.getId(), seckillGood);
                // 给每个商品做一个队列
                redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillGood.getId()).leftPushAll(pushAllIds(seckillGood.getStockCount(), seckillGood.getId()));
            }
        }
    }

    /**
     * 获取每个商品的id集合
     * @param num
     * @param id
     * @return
     */
    public Long[] pushAllIds(Integer num, Long id) {
        Long[] ids = new Long[num];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = id;
        }
        return ids;
    }
}
