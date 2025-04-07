package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.payment.rqrs.payorder.CommonPayDataRQ;
import lombok.Data;

/**
 * 支付方式 订单码
 */
@Data
public class AliOcOrderRQ extends CommonPayDataRQ {

    public AliOcOrderRQ() {
        this.setWayCode(CS.PAY_WAY_CODE.ALI_OC);
    }
}
