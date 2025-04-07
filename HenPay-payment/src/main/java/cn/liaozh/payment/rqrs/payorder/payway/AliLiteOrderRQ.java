package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRQ;
import cn.liaozh.core.constants.CS;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/*
 * 支付方式： ALI_LITE
 */
@Data
public class AliLiteOrderRQ extends UnifiedOrderRQ {

    /** 支付宝用户ID **/
    @NotBlank(message = "用户ID不能为空")
    private String buyerUserId;

    /** 构造函数 **/
    public AliLiteOrderRQ(){
        this.setWayCode(CS.PAY_WAY_CODE.ALI_LITE);
    }

    @Override
    public String getChannelUserId(){
        return this.buyerUserId;
    }

}
