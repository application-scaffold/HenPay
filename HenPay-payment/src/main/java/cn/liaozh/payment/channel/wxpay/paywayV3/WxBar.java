package cn.liaozh.payment.channel.wxpay.paywayV3;

import cn.liaozh.core.entity.PayOrder;
import cn.liaozh.payment.channel.wxpay.WxpayPaymentService;
import cn.liaozh.payment.model.MchAppConfigContext;
import cn.liaozh.payment.rqrs.AbstractRS;
import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * 微信 条码支付
 */
@Service("wxpayPaymentByBarV3Service") //Service Name需保持全局唯一性
public class WxBar extends WxpayPaymentService {

    @Autowired
    private cn.liaozh.payment.channel.wxpay.payway.WxBar wxBar;

    @Override
    public String preCheck(UnifiedOrderRQ rq, PayOrder payOrder) {
        return wxBar.preCheck(rq, payOrder);
    }

    @Override
    public AbstractRS pay(UnifiedOrderRQ rq, PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception {
        return wxBar.pay(rq, payOrder, mchAppConfigContext);
    }
}
