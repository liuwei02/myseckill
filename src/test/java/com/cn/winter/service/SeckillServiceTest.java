package com.cn.winter.service;

import com.cn.winter.dto.Exposer;
import com.cn.winter.dto.SeckillExecution;
import com.cn.winter.entity.Seckill;
import com.cn.winter.exception.RepeatKillException;
import com.cn.winter.exception.SeckillCloseException;
import com.cn.winter.exception.SeckillException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/6/8.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/spring/spring-dao.xml","classpath:/spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}",list);
    }

    @Test
    public void getSeckillById() throws Exception {

        Seckill seckill = seckillService.getSeckillById(1000L);
        logger.info("seckill={}",seckill);

    }
    //测试代码完整逻辑，注意可以重复执行
    @Test
    public void testSeckillLogic() throws Exception {
        long seckillId = 1002;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        logger.info("exposer={}",exposer);
        if (exposer.isExposed()){
            long phone = 13838383838L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, phone, md5);
                logger.info("seckillExecution={}", seckillExecution);

            } catch (RepeatKillException e){
                logger.error(e.getMessage());
            }catch (SeckillCloseException e1){
                logger.error(e1.getMessage());
            }catch (SeckillException e2){
                logger.error(e2.getMessage());
            }

        }else{
            logger.warn("exposer={}",exposer);
        }
    }

    @Test
    public void executeSeckillProcedure(){
        long seckillId = 1001;
        long phone = 13543432623L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){

            String md5 = exposer.getMd5();
            seckillService.executeSeckillByProcedure(seckillId,phone,md5);
        }

    }



}