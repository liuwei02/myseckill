package com.cn.winter.controller;

import com.cn.winter.dto.Exposer;
import com.cn.winter.dto.SeckillExecution;
import com.cn.winter.dto.SeckillResult;
import com.cn.winter.entity.Seckill;
import com.cn.winter.enums.SeckillStatEnum;
import com.cn.winter.exception.RepeatKillException;
import com.cn.winter.exception.SeckillCloseException;
import com.cn.winter.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 秒杀控制层
 * Created by Administrator on 2017/6/8.
 */
@Controller
@RequestMapping("/seckill") //url: /模块/资源/{id}/细分
public class SeckillController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private SeckillService seckillService;

    /**
     * 获取所有的秒杀集合
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String getSeckillList(Model model) {

        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("data", list);
        return "list";
    }

    /**
     * 秒杀详细
     *
     * @param seckillId
     * @param model
     * @return
     */
    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {

        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getSeckillById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    /**
     * ajax获取当前的秒杀的状态
     *
     * @param seckillId
     */
    @ResponseBody
    @RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") long seckillId) {

        SeckillResult<Exposer> seckillResult;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            return new SeckillResult<Exposer>(true, exposer);

        } catch (Exception e) {

            logger.error(e.getMessage(), e);
            return new SeckillResult<Exposer>(false, e.getMessage());
        }
    }

    /**
     * ajax进行秒杀操作
     *
     * @param seckillId
     * @param md5
     * @param userPhone
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone", required = false) Long userPhone) {
        if (userPhone == null) {
            return new SeckillResult<SeckillExecution>(false, "未注册");
        }
        SeckillResult<SeckillExecution> seckillResult;
        try {
            //使用存储过程
            SeckillExecution seckillExecution = seckillService.executeSeckillByProcedure(seckillId, userPhone, md5);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        } catch (SeckillCloseException e) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(false, execution);
        } catch (RepeatKillException e2) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(false, execution);
        } catch (Exception e3) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(false, execution);
        }
    }

    /**
     * 拿到当前系统时间
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    public SeckillResult<Long> time(){
        Date date = new Date();
        return new SeckillResult<Long>(true,date.getTime());
    }
}
