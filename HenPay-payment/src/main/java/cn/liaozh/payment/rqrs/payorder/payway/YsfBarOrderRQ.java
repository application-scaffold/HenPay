package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRQ;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/*
 * 支付方式： YSF_BAR
 */
@Data
public class YsfBarOrderRQ extends UnifiedOrderRQ {

    /** 用户 支付条码 **/
    @NotBlank(message = "支付条码不能为空")
    private String authCode;

    /** 构造函数 **/
    public YsfBarOrderRQ(){
        this.setWayCode(CS.PAY_WAY_CODE.YSF_BAR);
    }

}
