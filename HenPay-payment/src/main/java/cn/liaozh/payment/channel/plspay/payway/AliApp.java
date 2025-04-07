package cn.liaozh.payment.channel.plspay.payway;

import com.alibaba.fastjson.JSONObject;
import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.PayOrder;
import cn.liaozh.core.model.params.plspay.PlspayConfig;
import cn.liaozh.payment.channel.plspay.PlspayKit;
import cn.liaozh.payment.channel.plspay.PlspayPaymentService;
import cn.liaozh.payment.model.MchAppConfigContext;
import cn.liaozh.payment.rqrs.AbstractRS;
import cn.liaozh.payment.rqrs.msg.ChannelRetMsg;
import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRQ;
import cn.liaozh.payment.rqrs.payorder.payway.AliAppOrderRS;
import cn.liaozh.payment.util.ApiResBuilder;
import com.jeequan.jeepay.exception.JeepayException;
import com.jeequan.jeepay.model.PayOrderCreateReqModel;
import com.jeequan.jeepay.response.PayOrderCreateResponse;
import org.springframework.stereotype.Service;

/*
 * 计全付 支付宝 APP支付
 */
@Service("plspayPaymentByAliAppService") //Service Name需保持全局唯一性
public class AliApp extends PlspayPaymentService {

    @Override
    public String preCheck(UnifiedOrderRQ rq, PayOrder payOrder) {
        return null;
    }

    @Override
    public AbstractRS pay(UnifiedOrderRQ rq, PayOrder payOrder, MchAppConfigContext mchAppConfigContext) {
        // 构造函数响应数据
        AliAppOrderRS res = ApiResBuilder.buildSuccess(AliAppOrderRS.class);
        ChannelRetMsg channelRetMsg = new ChannelRetMsg();
        res.setChannelRetMsg(channelRetMsg);
        try {
            // 构建请求数据
            PayOrderCreateReqModel model = new PayOrderCreateReqModel();
            // 支付方式
            model.setWayCode(PlspayConfig.ALI_APP);
            // 异步通知地址
            model.setNotifyUrl(getNotifyUrl());
            // 支付宝app支付参数
            JSONObject channelExtra = new JSONObject();
            channelExtra.put("payDataType", CS.PAY_DATA_TYPE.ALI_APP);
            model.setChannelExtra(channelExtra.toString());

            // 发起统一下单
            PayOrderCreateResponse response = PlspayKit.payRequest(payOrder, mchAppConfigContext, model);
            // 下单返回状态
            Boolean isSuccess = PlspayKit.checkPayResp(response, mchAppConfigContext);

            if (isSuccess) {
                // 下单成功
                String payData = response.getData().getString("payData");
                res.setPayData(payData);
                channelRetMsg.setChannelAttach(payData);
                channelRetMsg.setChannelOrderId(response.get().getPayOrderId());
                channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.WAITING);
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
