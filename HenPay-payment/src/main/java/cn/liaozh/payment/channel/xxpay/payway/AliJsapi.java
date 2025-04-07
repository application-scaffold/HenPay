package cn.liaozh.payment.channel.xxpay.payway;

import com.alibaba.fastjson.JSONObject;
import cn.liaozh.core.entity.PayOrder;
import cn.liaozh.core.exception.BizException;
import cn.liaozh.core.model.params.xxpay.XxpayNormalMchParams;
import cn.liaozh.payment.channel.xxpay.XxpayPaymentService;
import cn.liaozh.payment.model.MchAppConfigContext;
import cn.liaozh.payment.rqrs.AbstractRS;
import cn.liaozh.payment.rqrs.msg.ChannelRetMsg;
import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRQ;
import cn.liaozh.payment.rqrs.payorder.payway.AliJsapiOrderRQ;
import cn.liaozh.payment.rqrs.payorder.payway.AliJsapiOrderRS;
import cn.liaozh.payment.util.ApiResBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

/*
 * 小新支付 支付宝jsapi支付
 */
@Service("xxpayPaymentByAliJsapiService") //Service Name需保持全局唯一性
public class AliJsapi extends XxpayPaymentService {

    @Override
    public String preCheck(UnifiedOrderRQ rq, PayOrder payOrder) {

        AliJsapiOrderRQ bizRQ = (AliJsapiOrderRQ) rq;
        if(StringUtils.isEmpty(bizRQ.getBuyerUserId())){
            throw new BizException("[buyerUserId]不可为空");
        }

        return null;
    }

    @Override
    public AbstractRS pay(UnifiedOrderRQ rq, PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception{
        AliJsapiOrderRQ bizRQ = (AliJsapiOrderRQ) rq;
        XxpayNormalMchParams params = (XxpayNormalMchParams)configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId(), getIfCode());
        // 构造支付请求参数
        Map<String,Object> paramMap = new TreeMap();
        paramMap.put("mchId", params.getMchId());
        paramMap.put("productId", "8008"); // 支付宝服务端支付
        paramMap.put("mchOrderNo", payOrder.getPayOrderId());
        paramMap.put("amount", payOrder.getAmount() + "");
        paramMap.put("currency", "cny");
        paramMap.put("clientIp", payOrder.getClientIp());
        paramMap.put("device", "web");
        paramMap.put("returnUrl", getReturnUrl());
        paramMap.put("notifyUrl", getNotifyUrl(payOrder.getPayOrderId()));
        paramMap.put("subject", payOrder.getSubject());
        paramMap.put("body", payOrder.getBody());
        paramMap.put("channelUserId", bizRQ.getBuyerUserId());
        // 构造函数响应数据
        AliJsapiOrderRS res = ApiResBuilder.buildSuccess(AliJsapiOrderRS.class);
        ChannelRetMsg channelRetMsg = new ChannelRetMsg();
        res.setChannelRetMsg(channelRetMsg);
        // 发起支付
        JSONObject resObj = doPay(payOrder, params, paramMap, channelRetMsg);
        if(resObj == null) {
            return res;
        }
        String alipayTradeNo = resObj.getJSONObject("payParams").getString("alipayTradeNo");
        res.setAlipayTradeNo(alipayTradeNo);
        return res;
    }

}
