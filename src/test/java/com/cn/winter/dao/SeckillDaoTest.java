package com.cn.winter.dao;

import com.cn.winter.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/6/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    @Resource
    private SeckillDao seckillDao;

    @Test
    public void queryById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill);
    }

    @Test
    public void queryAllSeckill() throws Exception {

        List<Seckill> list = seckillDao.queryAllSeckill(0,100);
        for(Seckill se : list){
            System.out.println(se);
        }

    }

    @Test
    public void updateReduceNumber() throws Exception {
        Integer count = seckillDao.updateReduceNumber(1000,new Date());
        System.out.println(count);

    }

}