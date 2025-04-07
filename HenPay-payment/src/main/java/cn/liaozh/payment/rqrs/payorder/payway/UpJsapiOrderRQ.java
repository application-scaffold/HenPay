package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRQ;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/*
 * 支付方式： UP_JSAPI
 */
@Data
public class UpJsapiOrderRQ extends UnifiedOrderRQ {

    /** 支付宝用户ID **/
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /** 构造函数 **/
    public UpJsapiOrderRQ(){
        this.setWayCode(CS.PAY_WAY_CODE.UP_JSAPI);
    }

    @Override
    public String getChannelUserId(){
        return this.userId;
    }

}
