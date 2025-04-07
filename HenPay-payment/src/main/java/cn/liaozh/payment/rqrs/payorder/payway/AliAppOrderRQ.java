package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRQ;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/*
* 支付方式： ALI_APP
*/
@Data
public class AliAppOrderRQ extends UnifiedOrderRQ {

    /** 支付宝用户ID **/
    @NotBlank(message = "用户ID不能为空")
    private String buyerUserId;

    /** 构造函数 **/
    public AliAppOrderRQ(){
        this.setWayCode(CS.PAY_WAY_CODE.ALI_APP); //默认 wayCode, 避免validate出现问题
    }

    @Override
    public String getChannelUserId(){
        return this.buyerUserId;
    }
}
