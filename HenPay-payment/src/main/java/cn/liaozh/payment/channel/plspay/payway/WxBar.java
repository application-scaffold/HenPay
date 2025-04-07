package cn.liaozh.payment.channel.plspay.payway;

import com.alibaba.fastjson.JSONObject;
import cn.liaozh.core.entity.PayOrder;
import cn.liaozh.core.exception.BizException;
import cn.liaozh.core.model.params.plspay.PlspayConfig;
import cn.liaozh.payment.channel.plspay.PlspayKit;
import cn.liaozh.payment.channel.plspay.PlspayPaymentService;
import cn.liaozh.payment.model.MchAppConfigContext;
import cn.liaozh.payment.rqrs.AbstractRS;
import cn.liaozh.payment.rqrs.msg.ChannelRetMsg;
import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRQ;
import cn.liaozh.payment.rqrs.payorder.payway.AliBarOrderRS;
import cn.liaozh.payment.rqrs.payorder.payway.WxBarOrderRQ;
import cn.liaozh.payment.util.ApiResBuilder;
import com.jeequan.jeepay.exception.JeepayException;
import com.jeequan.jeepay.model.PayOrderCreateReqModel;
import com.jeequan.jeepay.response.PayOrderCreateResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/*
 * 计全付 微信 bar
 */
@Service("plspayPaymentByWxBarService") //Service Name需保持全局唯一性
public class WxBar extends PlspayPaymentService {

    @Override
    public String preCheck(UnifiedOrderRQ rq, PayOrder payOrder) {
        WxBarOrderRQ bizRQ = (WxBarOrderRQ) rq;
        if (StringUtils.isEmpty(bizRQ.getAuthCode())) {
            throw new BizException("用户支付条码[authCode]不可为空");
        }
        return null;
    }

    @Override
    public AbstractRS pay(UnifiedOrderRQ rq, PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception {
        WxBarOrderRQ bizRQ = (WxBarOrderRQ) rq;

        // 构造函数响应数据
        AliBarOrderRS res = ApiResBuilder.buildSuccess(AliBarOrderRS.class);
        ChannelRetMsg channelRetMsg = new ChannelRetMsg();
        res.setChannelRetMsg(channelRetMsg);
        try {
            // 构建请求数据
            PayOrderCreateReqModel model = new PayOrderCreateReqModel();
            // 支付方式
            model.setWayCode(PlspayConfig.WX_BAR);
            // 异步通知地址
            model.setNotifyUrl(getNotifyUrl());
            JSONObject channelExtra = new JSONObject();
            channelExtra.put("authCode", bizRQ.getAuthCode());
            // 用户付款码值
            model.setChannelExtra(channelExtra.toString());

            // 发起统一下单
            PayOrderCreateResponse response = PlspayKit.payRequest(payOrder, mchAppConfigContext, model);
            // 下单返回状态
            Boolean isSuccess = PlspayKit.checkPayResp(response, mchAppConfigContext);

            if (isSuccess) {
                // 下单成功
                if (PlspayConfig.PAY_STATE_SUCCESS.equals(response.getData().getString("orderState"))) {
                    // 支付成功
                    channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_SUCCESS);
                }else {
                    // 支付中
                    channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.WAITING);
                }
                channelRetMsg.setChannelOrderId(response.get().getPayOrderId());
            } else {
                channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
                channelRetMsg.setChannelErrCode(response.getCode()+"");
                channelRetMsg.setChannelErrMsg(response.getMsg());
            }
        } catch (JeepayException e) {
            channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
        }
        return res;
    }
}
