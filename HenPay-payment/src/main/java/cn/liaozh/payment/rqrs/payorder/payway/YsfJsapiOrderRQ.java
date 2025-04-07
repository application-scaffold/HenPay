package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRQ;
import cn.liaozh.core.constants.CS;
import lombok.Data;

/*
 * 支付方式： YSF_JSAPI
 */
@Data
public class YsfJsapiOrderRQ extends UnifiedOrderRQ {

    /** 构造函数 **/
    public YsfJsapiOrderRQ(){
        this.setWayCode(CS.PAY_WAY_CODE.YSF_JSAPI);
    }

}
