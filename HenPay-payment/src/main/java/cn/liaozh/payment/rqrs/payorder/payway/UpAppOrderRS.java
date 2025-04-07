package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.payment.rqrs.payorder.CommonPayDataRS;
import lombok.Data;

/*
 * 支付方式： UPACP_APP
 */
@Data
public class UpAppOrderRS extends CommonPayDataRS {

    private String payData;

    @Override
    public String buildPayDataType(){
        return CS.PAY_DATA_TYPE.YSF_APP;
    }

    @Override
    public String buildPayData(){
        return payData;
    }

}
