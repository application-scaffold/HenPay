package cn.liaozh.payment.channel;

import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.PayOrder;
import cn.liaozh.payment.model.MchAppConfigContext;
import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRQ;
import cn.liaozh.payment.service.ConfigContextQueryService;
import cn.liaozh.payment.util.ChannelCertConfigKitBean;
import cn.liaozh.service.impl.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;

/*
* 支付接口抽象类
*/
public abstract class AbstractPaymentService implements IPaymentService{

    @Autowired protected SysConfigService sysConfigService;
    @Autowired protected ChannelCertConfigKitBean channelCertConfigKitBean;
    @Autowired protected ConfigContextQueryService configContextQueryService;

    @Override
    public String customPayOrderId(UnifiedOrderRQ bizRQ, PayOrder payOrder, MchAppConfigContext mchAppConfigContext){
        return null; //使用系统默认支付订单号
    }

    /** 订单分账（一般用作 如微信订单将在下单处做标记） */
    protected boolean isDivisionOrder(PayOrder payOrder){
        //订单分账， 将冻结商户资金。
        if(payOrder.getDivisionMode() != null && (PayOrder.DIVISION_MODE_AUTO == payOrder.getDivisionMode() || PayOrder.DIVISION_MODE_MANUAL == payOrder.getDivisionMode() )){
            return true;
        }
        return false;
    }

    protected String getNotifyUrl(){
        return sysConfigService.getDBApplicationConfig().getPaySiteUrl() + "/api/pay/notify/" + getIfCode();
    }

    protected String getNotifyUrl(String payOrderId){
        return sysConfigService.getDBApplicationConfig().getPaySiteUrl() + "/api/pay/notify/" + getIfCode() + "/" + payOrderId;
    }

    protected String getReturnUrl(){
        return sysConfigService.getDBApplicationConfig().getPaySiteUrl() + "/api/pay/return/" + getIfCode();
    }

    protected String getReturnUrl(String payOrderId){
        return sysConfigService.getDBApplicationConfig().getPaySiteUrl() + "/api/pay/return/" + getIfCode() + "/" + payOrderId;
    }

    protected String getReturnUrlOnlyJump(String payOrderId){
        return sysConfigService.getDBApplicationConfig().getPaySiteUrl() + "/api/pay/return/" + getIfCode() + "/" + CS.PAY_RETURNURL_FIX_ONLY_JUMP_PREFIX + payOrderId;
    }

}
