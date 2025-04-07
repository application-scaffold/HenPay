package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.payment.rqrs.payorder.CommonPayDataRQ;
import lombok.Data;

/*
 * 支付方式： ALI_QR
 */
@Data
public class AliQrOrderRQ extends CommonPayDataRQ {

    /** 构造函数 **/
    public AliQrOrderRQ(){
        this.setWayCode(CS.PAY_WAY_CODE.ALI_QR);
    }

}
