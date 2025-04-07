package cn.liaozh.payment.channel.plspay;

import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.PayOrder;
import cn.liaozh.core.model.params.plspay.PlspayConfig;
import cn.liaozh.core.model.params.plspay.PlspayNormalMchParams;
import cn.liaozh.payment.channel.IPayOrderQueryService;
import cn.liaozh.payment.model.MchAppConfigContext;
import cn.liaozh.payment.rqrs.msg.ChannelRetMsg;
import cn.liaozh.payment.service.ConfigContextQueryService;
import com.jeequan.jeepay.Jeepay;
import com.jeequan.jeepay.JeepayClient;
import com.jeequan.jeepay.model.PayOrderQueryReqModel;
import com.jeequan.jeepay.request.PayOrderQueryRequest;
import com.jeequan.jeepay.response.PayOrderQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 计全支付plus  查询订单
 */
@Service
@Slf4j
public class PlspayPayOrderQueryService implements IPayOrderQueryService {
    @Autowired
    private ConfigContextQueryService configContextQueryService;

    @Override
    public String getIfCode() {
        return CS.IF_CODE.PLSPAY;
    }

    @Override
    public ChannelRetMsg query(PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception {
        PayOrderQueryRequest request = new PayOrderQueryRequest();
        PayOrderQueryReqModel model = new PayOrderQueryReqModel();
        try {
            PlspayNormalMchParams normalMchParams = (PlspayNormalMchParams) configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId(), CS.IF_CODE.PLSPAY);
            model.setMchNo(normalMchParams.getMerchantNo());     // 商户号
            model.setAppId(normalMchParams.getAppId());          // 应用ID
            model.setMchOrderNo(payOrder.getPayOrderId());       // 支付订单号
            request.setBizModel(model);
            // 发起请求
            PayOrderQueryResponse response = new PayOrderQueryResponse();
            if (StringUtils.isEmpty(normalMchParams.getSignType()) || normalMchParams.getSignType().equals(PlspayConfig.DEFAULT_SIGN_TYPE)) {
                JeepayClient jeepayClient = JeepayClient.getInstance(normalMchParams.getAppId(), normalMchParams.getAppSecret(), Jeepay.getApiBase());
                response = jeepayClient.execute(request);

            } else if (normalMchParams.getSignType().equals(PlspayConfig.SIGN_TYPE_RSA2)) {
                JeepayClient jeepayClient = JeepayClient.getInstance(normalMchParams.getAppId(), normalMchParams.getRsa2AppPrivateKey(), Jeepay.getApiBase());
                response = jeepayClient.executeByRSA2(request);
            }

            // 下单返回状态
            Boolean isSuccess = PlspayKit.checkPayResp(response, mchAppConfigContext);

            // 请求响应状态
            if (isSuccess) {
                // 如果查询请求成功
                if (PlspayConfig.PAY_STATE_SUCCESS.equals(String.valueOf(response.get().getState()))) {
                    return ChannelRetMsg.confirmSuccess(response.get().getPayOrderId());
                } else if (PlspayConfig.PAY_STATE_FAIL.equals(String.valueOf(response.get().getState()))) {
                    // 失败
                    return ChannelRetMsg.confirmFail();
                }
            }
            // 支付中
            return ChannelRetMsg.waiting();
        } catch (Exception e) {
            // 支付中
            return ChannelRetMsg.waiting();
        }
    }
}
