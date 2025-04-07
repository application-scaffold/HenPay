package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRQ;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/*
 * 支付方式： ALI_BAR
 */
@Data
public class AliBarOrderRQ extends UnifiedOrderRQ {

    /** 用户 支付条码 **/
    @NotBlank(message = "支付条码不能为空")
    private String authCode;

    /** 构造函数 **/
    public AliBarOrderRQ(){
        this.setWayCode(CS.PAY_WAY_CODE.ALI_BAR); //默认 ali_bar, 避免validate出现问题
    }

}
