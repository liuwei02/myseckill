package com.cn.winter.dao;

import com.cn.winter.entity.SuccessKilled;

import java.util.List;

/**
 * Created by Administrator on 2017/6/6.
 */
public interface SuccessKilledDao {

    /**
     * 插入购买明细,可过滤重复
     * @param seckillId
     * @param userPhone
     * @return
     */
    Integer insertSuccessKilled(long seckillId, long userPhone);

    /**
     * 根据秒杀id查找秒杀成功记录的列表，并且携带秒杀的实体
     * @param seckillId
     * @return
     */
    List<SuccessKilled> queryByIdWithSeckill(long seckillId);
}
