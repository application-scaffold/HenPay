package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRS;
import lombok.Data;

/*
 * 支付方式： WX_APP
 */
@Data
public class WxAppOrderRS extends UnifiedOrderRS {

    /** 预支付数据包 **/
    private String payInfo;

    @Override
    public String buildPayDataType(){
        return CS.PAY_DATA_TYPE.WX_APP;
    }

    @Override
    public String buildPayData(){
        return payInfo;
    }

}
