package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.core.utils.JsonKit;
import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRS;
import lombok.Data;

/*
 * 支付方式： ALI_LITE
 */
@Data
public class AliLiteOrderRS extends UnifiedOrderRS {

    /** 调起支付插件的支付宝订单号 **/
    private String alipayTradeNo;

    @Override
    public String buildPayDataType(){
        return CS.PAY_DATA_TYPE.ALI_APP;
    }

    @Override
    public String buildPayData(){
        return JsonKit.newJson("alipayTradeNo", alipayTradeNo).toString();
    }

}
