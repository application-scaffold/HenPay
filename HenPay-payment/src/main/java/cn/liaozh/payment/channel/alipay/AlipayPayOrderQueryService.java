package cn.liaozh.payment.channel.alipay;

import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.PayOrder;
import cn.liaozh.payment.channel.IPayOrderQueryService;
import cn.liaozh.payment.model.MchAppConfigContext;
import cn.liaozh.payment.rqrs.msg.ChannelRetMsg;
import cn.liaozh.payment.service.ConfigContextQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
* 支付宝 查单接口实现类
*/
@Service
public class AlipayPayOrderQueryService implements IPayOrderQueryService {

    @Autowired private ConfigContextQueryService configContextQueryService;

    @Override
    public String getIfCode() {
        return CS.IF_CODE.ALIPAY;
    }

    @Override
    public ChannelRetMsg query(PayOrder payOrder, MchAppConfigContext mchAppConfigContext){

        AlipayTradeQueryRequest req = new AlipayTradeQueryRequest();

        // 商户订单号，商户网站订单系统中唯一订单号，必填
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(payOrder.getPayOrderId());
        req.setBizModel(model);

        //通用字段
        AlipayKit.putApiIsvInfo(mchAppConfigContext, req, model);

        AlipayTradeQueryResponse resp = configContextQueryService.getAlipayClientWrapper(mchAppConfigContext).execute(req);
        String result = resp.getTradeStatus();

        if("TRADE_SUCCESS".equals(result)) {
            return ChannelRetMsg.confirmSuccess(resp.getTradeNo());  //支付成功
        }else if("WAIT_BUYER_PAY".equals(result)) {
            return ChannelRetMsg.waiting(); //支付中
        }
        return ChannelRetMsg.waiting(); //支付中
    }


}
