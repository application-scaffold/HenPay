package cn.liaozh.payment.rqrs.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.payment.rqrs.payorder.CommonPayDataRQ;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PPPcOrderRQ extends CommonPayDataRQ {

    /**
     * 商品描述信息
     **/
    @NotBlank(message = "取消支付返回站点")
    private String cancelUrl;

    public PPPcOrderRQ() {
        this.setWayCode(CS.PAY_WAY_CODE.PP_PC);
    }
}
