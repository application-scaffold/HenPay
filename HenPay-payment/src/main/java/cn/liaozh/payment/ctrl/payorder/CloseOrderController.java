package cn.liaozh.payment.ctrl.payorder;

import cn.liaozh.core.entity.PayOrder;
import cn.liaozh.core.exception.BizException;
import cn.liaozh.core.model.ApiRes;
import cn.liaozh.core.utils.SpringBeansUtil;
import cn.liaozh.payment.channel.IPayOrderCloseService;
import cn.liaozh.payment.ctrl.ApiController;
import cn.liaozh.payment.model.MchAppConfigContext;
import cn.liaozh.payment.rqrs.msg.ChannelRetMsg;
import cn.liaozh.payment.rqrs.payorder.ClosePayOrderRQ;
import cn.liaozh.payment.rqrs.payorder.ClosePayOrderRS;
import cn.liaozh.payment.service.ConfigContextQueryService;
import cn.liaozh.service.impl.PayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * 关闭订单 controller
 */
@Slf4j
@RestController
public class CloseOrderController extends ApiController {

    @Autowired private PayOrderService payOrderService;
    @Autowired private ConfigContextQueryService configContextQueryService;

    /**
     * 关闭订单
     */
    @RequestMapping("/api/pay/close")
    public ApiRes closeOrder(){

        //获取参数 & 验签
        ClosePayOrderRQ rq = getRQByWithMchSign(ClosePayOrderRQ.class);

        if(StringUtils.isAllEmpty(rq.getMchOrderNo(), rq.getPayOrderId())){
            throw new BizException("mchOrderNo 和 payOrderId 不能同时为空");
        }

        PayOrder payOrder = payOrderService.queryMchOrder(rq.getMchNo(), rq.getPayOrderId(), rq.getMchOrderNo());
        if(payOrder == null){
            throw new BizException("订单不存在");
        }

        if (payOrder.getState() != PayOrder.STATE_INIT && payOrder.getState() != PayOrder.STATE_ING) {
            throw new BizException("当前订单不可关闭");
        }

        ClosePayOrderRS bizRes = new ClosePayOrderRS();

        // 订单生成状态  直接修改订单状态
        if (payOrder.getState() == PayOrder.STATE_INIT) {
            payOrderService.updateInit2Close(payOrder.getPayOrderId());
            bizRes.setChannelRetMsg(ChannelRetMsg.confirmSuccess(null));
            return ApiRes.okWithSign(bizRes, configContextQueryService.queryMchApp(rq.getMchNo(), rq.getAppId()).getAppSecret());
        }

        try {

            String payOrderId = payOrder.getPayOrderId();

            //查询支付接口是否存在
            IPayOrderCloseService closeService = SpringBeansUtil.getBean(payOrder.getIfCode() + "PayOrderCloseService", IPayOrderCloseService.class);

            // 支付通道接口实现不存在
            if(closeService == null){
                log.error("{} interface not exists!", payOrder.getIfCode());
                return null;
            }

            //查询出商户应用的配置信息
            MchAppConfigContext mchAppConfigContext = configContextQueryService.queryMchInfoAndAppInfo(payOrder.getMchNo(), payOrder.getAppId());

            ChannelRetMsg channelRetMsg = closeService.close(payOrder, mchAppConfigContext);
            if(channelRetMsg == null){
                log.error("channelRetMsg is null");
                return null;
            }

            log.info("关闭订单[{}]结果为：{}", payOrderId, channelRetMsg);

            // 关闭订单 成功
            if(channelRetMsg.getChannelState() == ChannelRetMsg.ChannelState.CONFIRM_SUCCESS) {
                payOrderService.updateIng2Close(payOrderId);
            }else {
                return ApiRes.customFail(channelRetMsg.getChannelErrMsg());
            }

            bizRes.setChannelRetMsg(channelRetMsg);
        } catch (Exception e) {  // 关闭订单异常
            log.error("error payOrderId = {}", payOrder.getPayOrderId(), e);
            return null;
        }

        return ApiRes.okWithSign(bizRes, configContextQueryService.queryMchApp(rq.getMchNo(), rq.getAppId()).getAppSecret());
    }
}
