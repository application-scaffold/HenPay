package cn.liaozh.payment.channel.alipay;

import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.PayOrder;
import cn.liaozh.payment.channel.AbstractPaymentService;
import cn.liaozh.payment.model.MchAppConfigContext;
import cn.liaozh.payment.rqrs.AbstractRS;
import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRQ;
import cn.liaozh.payment.util.PaywayUtil;
import org.springframework.stereotype.Service;

/*
* 支付接口： 支付宝官方
* 支付方式： 自适应
*/
@Service
public class AlipayPaymentService extends AbstractPaymentService {

    @Override
    public String getIfCode() {
        return CS.IF_CODE.ALIPAY;
    }

    @Override
    public boolean isSupport(String wayCode) {
        return true;
    }

    @Override
    public String preCheck(UnifiedOrderRQ rq, PayOrder payOrder) {
        return PaywayUtil.getRealPaywayService(this, payOrder.getWayCode()).preCheck(rq, payOrder);
    }

    @Override
    public AbstractRS pay(UnifiedOrderRQ rq, PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception {
        return PaywayUtil.getRealPaywayService(this, payOrder.getWayCode()).pay(rq, payOrder, mchAppConfigContext);
    }

}
