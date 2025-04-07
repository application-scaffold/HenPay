package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.core.utils.JsonKit;
import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRS;
import lombok.Data;

/*
 * 支付方式： YSF_JSAPI
 */
@Data
public class YsfJsapiOrderRS extends UnifiedOrderRS {

    /** 调起支付插件的云闪付订单号 **/
    private String redirectUrl;

    @Override
    public String buildPayDataType(){
        return CS.PAY_DATA_TYPE.YSF_APP;
    }

    @Override
    public String buildPayData(){
        return JsonKit.newJson("redirectUrl", redirectUrl).toString();
    }

}
