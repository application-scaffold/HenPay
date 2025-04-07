package cn.liaozh.payment.channel;

import cn.liaozh.payment.service.ConfigContextQueryService;
import cn.liaozh.payment.util.ChannelCertConfigKitBean;
import cn.liaozh.service.impl.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;

/*
* 退款接口抽象类
*/
public abstract class AbstractRefundService implements IRefundService{

    @Autowired protected SysConfigService sysConfigService;
    @Autowired protected ChannelCertConfigKitBean channelCertConfigKitBean;
    @Autowired protected ConfigContextQueryService configContextQueryService;

    protected String getNotifyUrl(){
        return sysConfigService.getDBApplicationConfig().getPaySiteUrl() + "/api/refund/notify/" + getIfCode();
    }

    protected String getNotifyUrl(String refundOrderId){
        return sysConfigService.getDBApplicationConfig().getPaySiteUrl() + "/api/refund/notify/" + getIfCode() + "/" + refundOrderId;
    }

}
