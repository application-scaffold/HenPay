package cn.liaozh.payment.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cn.liaozh.components.mq.model.PayOrderDivisionMQ;
import cn.liaozh.components.mq.vendor.IMQSender;
import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.PayOrder;
import cn.liaozh.core.exception.BizException;
import cn.liaozh.payment.rqrs.msg.ChannelRetMsg;
import cn.liaozh.service.impl.PayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/***
* 订单处理通用逻辑
*/
@Service
@Slf4j
public class PayOrderProcessService {


    @Autowired private PayOrderService payOrderService;
    @Autowired private PayMchNotifyService payMchNotifyService;
    @Autowired private IMQSender mqSender;

    /** 明确成功的处理逻辑（除更新订单其他业务） **/
    public void confirmSuccess(PayOrder payOrder){

        // 查询查询订单详情
        payOrder = payOrderService.getById(payOrder.getPayOrderId());

        //设置订单状态
        payOrder.setState(PayOrder.STATE_SUCCESS);

        //自动分账 处理逻辑， 不影响主订单任务
        this.updatePayOrderAutoDivision(payOrder);

        //发送商户通知
        payMchNotifyService.payOrderNotify(payOrder);

    }

    /** 更新订单自动分账业务 **/
    private void updatePayOrderAutoDivision(PayOrder payOrder){

        try {

            //默认不分账  || 其他非【自动分账】逻辑时， 不处理
            if(payOrder == null || payOrder.getDivisionMode() == null || payOrder.getDivisionMode() != PayOrder.DIVISION_MODE_AUTO){
                return ;
            }

            //更新订单表分账状态为： 等待分账任务处理
            boolean updDivisionState = payOrderService.update(new LambdaUpdateWrapper<PayOrder>()
                    .set(PayOrder::getDivisionState, PayOrder.DIVISION_STATE_WAIT_TASK)
                    .eq(PayOrder::getPayOrderId, payOrder.getPayOrderId())
                    .eq(PayOrder::getDivisionState, PayOrder.DIVISION_STATE_UNHAPPEN)
            );

            if(updDivisionState){
                //推送到分账MQ
                mqSender.send(PayOrderDivisionMQ.build(payOrder.getPayOrderId(), CS.YES,null), 80); //80s 后执行
            }

        } catch (Exception e) {
            log.error("订单[{}]自动分账逻辑异常：", payOrder.getPayOrderId(), e);
        }
    }


    /***
     *
     * 支付中 --》 支付成功或者失败
     * **/
    @Transactional
    public void updateIngAndSuccessOrFailByCreatebyOrder(PayOrder payOrder, ChannelRetMsg channelRetMsg){

        boolean isSuccess = payOrderService.updateInit2Ing(payOrder.getPayOrderId(), payOrder);
        if(!isSuccess){
            log.error("updateInit2Ing更新异常 payOrderId={}", payOrder.getPayOrderId());
            throw new BizException("更新订单异常!");
        }

        isSuccess = payOrderService.updateIng2SuccessOrFail(payOrder.getPayOrderId(), payOrder.getState(),
                channelRetMsg.getChannelOrderId(), channelRetMsg.getChannelUserId(), channelRetMsg.getChannelErrCode(), channelRetMsg.getChannelErrMsg());
        if(!isSuccess){
            log.error("updateIng2SuccessOrFail更新异常 payOrderId={}", payOrder.getPayOrderId());
            throw new BizException("更新订单异常!");
        }
    }

}
