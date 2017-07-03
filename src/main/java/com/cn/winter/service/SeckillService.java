package com.cn.winter.service;

import com.cn.winter.dto.Exposer;
import com.cn.winter.dto.SeckillExecution;
import com.cn.winter.entity.Seckill;
import com.cn.winter.exception.RepeatKillException;
import com.cn.winter.exception.SeckillCloseException;
import com.cn.winter.exception.SeckillException;

import java.util.List;

/**
 * 业务接口：站在“使用者”的角度设计接口
 * 三个方面：方法定义力度，参数，返回类型（return 类型、异常）
 * Created by Administrator on 2017/6/7.
 */
public interface SeckillService {

    /**
     * 获取所有秒杀
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询一个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getSeckillById(long seckillId);

    /**
     * 秒杀开启时输出秒杀接口地址,否则输出系统时间和秒杀时间
     * @param seckillId
     * @return
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException,RepeatKillException,SeckillCloseException;
    /**
     * 执行秒杀操作 by 存储过程
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5)
            throws SeckillException,RepeatKillException,SeckillCloseException;

}
