package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.payment.rqrs.payorder.CommonPayDataRQ;
import lombok.Data;

/*
 * 支付方式： UPACP_APP
 */
@Data
public class UpAppOrderRQ extends CommonPayDataRQ {

    /** 构造函数 **/
    public UpAppOrderRQ(){
        this.setWayCode(CS.PAY_WAY_CODE.UP_APP);
    }

}
