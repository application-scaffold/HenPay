package cn.liaozh.payment.channel.plspay;

import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.PayOrder;
import cn.liaozh.payment.channel.AbstractPaymentService;
import cn.liaozh.payment.model.MchAppConfigContext;
import cn.liaozh.payment.rqrs.AbstractRS;
import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRQ;
import cn.liaozh.payment.util.PaywayUtil;
import org.springframework.stereotype.Service;

/**
 * 计全支付plus
 */
@Service
public class PlspayPaymentService extends AbstractPaymentService {

    @Override
    public String getIfCode() {
        return CS.IF_CODE.PLSPAY;
    }

    @Override
    public boolean isSupport(String wayCode) {
        return true;
    }

    @Override
    public String preCheck(UnifiedOrderRQ bizRQ, PayOrder payOrder) {
        return PaywayUtil.getRealPaywayService(this, payOrder.getWayCode()).preCheck(bizRQ, payOrder);
    }

    @Override
    public AbstractRS pay(UnifiedOrderRQ bizRQ, PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception {
        return PaywayUtil.getRealPaywayService(this, payOrder.getWayCode()).pay(bizRQ, payOrder, mchAppConfigContext);
    }
}
