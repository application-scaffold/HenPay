package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.payment.rqrs.payorder.CommonPayDataRQ;
import lombok.Data;

/*
 * 支付方式： WX_NATIVE
 */
@Data
public class WxNativeOrderRQ extends CommonPayDataRQ {

    /** 构造函数 **/
    public WxNativeOrderRQ() {
        this.setWayCode(CS.PAY_WAY_CODE.WX_NATIVE);
    }

}
