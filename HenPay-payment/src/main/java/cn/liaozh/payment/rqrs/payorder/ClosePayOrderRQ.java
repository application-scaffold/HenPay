package cn.liaozh.payment.rqrs.payorder;

import cn.liaozh.payment.rqrs.AbstractMchAppRQ;
import lombok.Data;

/*
 * 关闭订单 请求参数对象
 */
@Data
public class ClosePayOrderRQ extends AbstractMchAppRQ {

    /** 商户订单号 **/
    private String mchOrderNo;

    /** 支付系统订单号 **/
    private String payOrderId;

}
