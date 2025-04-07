package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRQ;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/*
 *  支付方式： WX_APP
 */
@Data
public class WxAppOrderRQ extends UnifiedOrderRQ {

    /** 微信openid **/
    @NotBlank(message = "openid不能为空")
    private String openid;

    /** 构造函数 **/
    public WxAppOrderRQ(){
        this.setWayCode(CS.PAY_DATA_TYPE.WX_APP); //默认 wayCode, 避免validate出现问题
    }


    @Override
    public String getChannelUserId(){
        return this.openid;
    }
}
