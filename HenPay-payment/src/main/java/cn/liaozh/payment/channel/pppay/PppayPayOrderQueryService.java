package cn.liaozh.payment.channel.pppay;

import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.PayOrder;
import cn.liaozh.payment.channel.IPayOrderQueryService;
import cn.liaozh.payment.model.MchAppConfigContext;
import cn.liaozh.payment.rqrs.msg.ChannelRetMsg;
import cn.liaozh.payment.service.ConfigContextQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PppayPayOrderQueryService implements IPayOrderQueryService {

    @Override
    public String getIfCode() {
        return CS.IF_CODE.PPPAY;
    }

    @Autowired
    private ConfigContextQueryService configContextQueryService;

    @Override
    public ChannelRetMsg query(PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception {
        return configContextQueryService.getPaypalWrapper(mchAppConfigContext).processOrder(null, payOrder);
    }
}
