package cn.liaozh.payment.channel.xxpay;

import com.alibaba.fastjson2.JSONObject;
import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.PayOrder;
import cn.liaozh.core.exception.ResponseException;
import cn.liaozh.core.model.params.xxpay.XxpayNormalMchParams;
import cn.liaozh.payment.channel.AbstractChannelNoticeService;
import cn.liaozh.payment.model.MchAppConfigContext;
import cn.liaozh.payment.rqrs.msg.ChannelRetMsg;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/*
* 小新支付 支付回调接口实现类
*/
@Service
@Slf4j
public class XxpayChannelNoticeService extends AbstractChannelNoticeService {


    @Override
    public String getIfCode() {
        return CS.IF_CODE.XXPAY;
    }

    @Override
    public MutablePair<String, Object> parseParams(HttpServletRequest request, String urlOrderId, NoticeTypeEnum noticeTypeEnum) {

        try {

            JSONObject params = getReqParamJSON();
            String payOrderId = params.getString("mchOrderNo");
            return MutablePair.of(payOrderId, params);

        } catch (Exception e) {
            log.error("error", e);
            throw ResponseException.buildText("ERROR");
        }
    }



    @Override
    public ChannelRetMsg doNotice(HttpServletRequest request, Object params, PayOrder payOrder, MchAppConfigContext mchAppConfigContext, NoticeTypeEnum noticeTypeEnum) {
        try {
            XxpayNormalMchParams xxpayParams = (XxpayNormalMchParams)configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId(), getIfCode());

            // 获取请求参数
            JSONObject jsonParams = (JSONObject) params;
            String checkSign = jsonParams.getString("sign");
            jsonParams.remove("sign");
            // 验证签名
            if(!checkSign.equals(XxpayKit.getSign(jsonParams, xxpayParams.getKey()))) {
                throw ResponseException.buildText("ERROR");
            }

            //验签成功后判断上游订单状态
            ResponseEntity okResponse = textResp("success");

            // 支付状态,0-订单生成,1-支付中,2-支付成功,3-业务处理完成
            String status = jsonParams.getString("status");

            ChannelRetMsg result = new ChannelRetMsg();
            result.setChannelOrderId(jsonParams.getString("channelOrderNo")); //渠道订单号
            result.setResponseEntity(okResponse); //响应数据

            result.setChannelState(ChannelRetMsg.ChannelState.WAITING); // 默认支付中

            if("2".equals(status) || "3".equals(status)){
                result.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_SUCCESS);
            }

            return result;
        } catch (Exception e) {
            log.error("error", e);
            throw ResponseException.buildText("ERROR");
        }
    }

}
