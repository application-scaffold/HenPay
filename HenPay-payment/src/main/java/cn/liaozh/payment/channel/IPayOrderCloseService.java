package cn.liaozh.payment.channel;

import cn.liaozh.core.entity.PayOrder;
import cn.liaozh.payment.model.MchAppConfigContext;
import cn.liaozh.payment.rqrs.msg.ChannelRetMsg;

/**
 * 关闭订单（渠道侧）接口定义
 */
public interface IPayOrderCloseService {

    /** 获取到接口code **/
    String getIfCode();

    /** 查询订单 **/
    ChannelRetMsg close(PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception;

}
