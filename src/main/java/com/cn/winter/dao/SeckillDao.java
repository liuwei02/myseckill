package com.cn.winter.dao;

import com.cn.winter.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/6/6.
 */
public interface SeckillDao {

    /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return
     */
    Integer updateReduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据id查找秒杀
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 查找所有的秒杀
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAllSeckill(@Param("offset") Integer offset, @Param("limit") Integer limit);
}
