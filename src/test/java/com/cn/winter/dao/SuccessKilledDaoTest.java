package com.cn.winter.dao;

import com.cn.winter.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2017/6/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() throws Exception {

        Integer count = successKilledDao.insertSuccessKilled(1001,15258800000L);
        System.out.println(count);

    }

    @Test
    public void queryByIdWithSeckill() throws Exception {

        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(1001,15258800000L);
        System.out.println(successKilled);
    }

}