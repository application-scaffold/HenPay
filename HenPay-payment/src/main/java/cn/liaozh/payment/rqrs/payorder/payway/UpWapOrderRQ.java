package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.payment.rqrs.payorder.CommonPayDataRQ;
import lombok.Data;

/*
 * 支付方式： UPACP_WAP
 */
@Data
public class UpWapOrderRQ extends CommonPayDataRQ {

    /** 构造函数 **/
    public UpWapOrderRQ(){
        this.setWayCode(CS.PAY_WAY_CODE.UP_WAP);
    }

}
