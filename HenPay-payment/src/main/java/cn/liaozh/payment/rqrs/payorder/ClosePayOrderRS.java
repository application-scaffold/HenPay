package cn.liaozh.payment.rqrs.payorder;

import cn.liaozh.payment.rqrs.AbstractRS;
import cn.liaozh.payment.rqrs.msg.ChannelRetMsg;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/*
 * 关闭订单 响应参数
 */
@Data
public class ClosePayOrderRS extends AbstractRS {

    /** 上游渠道返回数据包 (无需JSON序列化) **/
    @JSONField(serialize = false)
    private ChannelRetMsg channelRetMsg;

}
