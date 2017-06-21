//存放主要交互逻辑js代码
// javascript 模块化

var seckill = {
    //封装秒杀相关的ajax地址
    URL: {

        now: function () {
            return '/seckill/time/now';
        },
        exposer: function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        execution: function (seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/execution'
        }
    },
    handleSeckill: function (seckillId, node) {
        node.hide().html('<button class="btn bg-primary btn-lg" id="killBtn">start seckill</button>');
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            // console.log(result.success);
            // console.log(result.data.md5);
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    //秒杀按钮点击
                    $('#killBtn').one('click', function () {

                        $(this).addClass('disabled');
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']) {
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            } else {
                                node.html('<span class="label label-danger">不可重复秒杀哦</span>');
                            }
                        });
                    });
                    node.show();
                } else {
                    //未开启秒杀
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    //重新计算计时逻辑
                    seckill.countdown(seckillId, now, start, end);
                    //console.log('还没开始呢');
                }
            }
        });
    },
    //验证手机号
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },

    countdown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        if (nowTime > endTime) {
            //秒杀已经结束了
            seckillBox.html("秒杀已经结束了");
        } else if (nowTime < startTime) {
            //秒杀未开始,计时事件绑定
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (event) {
                //时间的格式
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown', function () { //倒计时结束事件
                //获取秒杀地址，控制显示逻辑，执行秒杀
                $('#timeIcon').hide();
                seckillBox.hide();
                seckill.handleSeckill(seckillId, seckillBox);
            });
        } else {
            //秒杀开始了
            $('#timeIcon').hide();
            seckill.handleSeckill(seckillId, seckillBox);
        }


    },

    //详情页秒杀逻辑
    detail: {
        init: function (params) {
            //手机验证和登录，计时交互
            //规划我们的交互流程
            //在cookie中查找手机号
            var killPhone = $.cookie('killPhone');
            //验证手机
            if (!seckill.validatePhone(killPhone)) {
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    //显示弹出层
                    show: true,
                    //禁止位置关闭
                    backdrop: 'static',
                    //关闭键盘上事件
                    keyboard: false
                });
                //按钮点击事件
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killphoneKey').val();
                    if (seckill.validatePhone(inputPhone)) {
                        //电话写入cookie,有效期7天,只在seckill下有用
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        //刷新页面
                        window.location.reload();
                    } else {
                        $('#killphoneMessage').hide().html('<label class="label label-danger">请输入正确的手机号</label>')
                            .show(300);
                    }
                });
            }
            //已经登录
            //计时交互
            var startTime = params['startTime'];
            var seckillId = params['seckillId'];
            var endTime = params['endTime'];
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var timeNow = result['data'];
                    //时间判断
                    seckill.countdown(seckillId, timeNow, startTime, endTime);
                } else {
                    console.log("result= " + result);//TODO
                }
            });

        },
    },
}

