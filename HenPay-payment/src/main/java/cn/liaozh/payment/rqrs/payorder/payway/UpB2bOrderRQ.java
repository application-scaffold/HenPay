package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.payment.rqrs.payorder.CommonPayDataRQ;
import lombok.Data;

/*
 * 支付方式： UPACP_B2B
 */
@Data
public class UpB2bOrderRQ extends CommonPayDataRQ {

    /** 构造函数 **/
    public UpB2bOrderRQ(){
        this.setWayCode(CS.PAY_WAY_CODE.UP_B2B);
    }

}
