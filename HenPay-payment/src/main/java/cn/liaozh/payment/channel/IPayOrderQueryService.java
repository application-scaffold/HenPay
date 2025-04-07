package cn.liaozh.payment.channel;

import cn.liaozh.core.entity.PayOrder;
import cn.liaozh.payment.model.MchAppConfigContext;
import cn.liaozh.payment.rqrs.msg.ChannelRetMsg;

/**
* 查单（渠道侧）接口定义
*/
public interface IPayOrderQueryService {

    /** 获取到接口code **/
    String getIfCode();

    /** 查询订单 **/
    ChannelRetMsg query(PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception;

}
