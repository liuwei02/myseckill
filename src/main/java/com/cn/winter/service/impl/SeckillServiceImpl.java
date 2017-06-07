package com.cn.winter.service.impl;

import com.cn.winter.dao.SeckillDao;
import com.cn.winter.dao.SuccessKilledDao;
import com.cn.winter.dto.Exposer;
import com.cn.winter.dto.SeckillExecution;
import com.cn.winter.entity.Seckill;
import com.cn.winter.entity.SuccessKilled;
import com.cn.winter.enums.SeckillStatEnum;
import com.cn.winter.exception.RepeatKillException;
import com.cn.winter.exception.SeckillCloseException;
import com.cn.winter.exception.SeckillException;
import com.cn.winter.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/6/7.
 */
@Service("seckillService")
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    //MD5盐值混淆，
    private final String salt = "regnaiwjHEFGAEfn243423423@#$@#2352348uwer";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAllSeckill(0, 4);
    }

    public Seckill getSeckillById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);

        if (seckill == null) {
            return new Exposer(false, seckillId);
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if (startTime.getTime() > nowTime.getTime() || endTime.getTime() < nowTime.getTime()) {
            return new Exposer(false, nowTime.getTime(), startTime.getTime(), endTime.getTime(), seckillId);
        }
        //转化特定字符串的过程,不可逆
        String md5 = getMd5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    /**
     * 获取md5
     *
     * @param seckillId
     * @return
     */
    private String getMd5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;

    }

    /**
     * 使用注解控制事务方法的优点：
     * 1：开发团队达成一致约定，明确标注事务方法的编程风格
     * 2：保证事务方法的执行时间尽可能短，不要穿插其他网络操作。
     * 3：不是所有的方法都需要事务，如只有一条操作，只读操作不需要事务管理。
     */
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {


        if (md5 == null || !getMd5(seckillId).equals(md5)) {
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑: 减库存+添加购买记录
        Integer count = seckillDao.updateReduceNumber(seckillId, new Date());
        try {

            if (count <= 0) {
                //没有更新到记录，秒杀结束
                throw new SeckillCloseException("seckill is close");
            } else {
                //减库存成功了 记录购买行为
                Integer insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);

                if (insertCount <= 0) {
                    //重复秒杀
                    throw new RepeatKillException("seckill repeated");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译期异常转化为运行时异常
            throw new SeckillException("seckill inner error: " + e.getMessage());
        }
    }
}
