package cn.liaozh.payment.ctrl.qr;

import com.alibaba.fastjson.JSON;
import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.PayOrder;
import cn.liaozh.core.exception.BizException;
import cn.liaozh.core.model.ApiRes;
import cn.liaozh.core.model.QRCodeParams;
import cn.liaozh.core.service.IMchQrcodeManager;
import cn.liaozh.core.utils.HenPayKit;
import cn.liaozh.core.utils.SeqKit;
import cn.liaozh.core.utils.SpringBeansUtil;
import cn.liaozh.payment.channel.IChannelUserService;
import cn.liaozh.payment.ctrl.payorder.AbstractPayOrderController;
import cn.liaozh.payment.model.MchAppConfigContext;
import cn.liaozh.payment.rqrs.payorder.UnifiedOrderRQ;
import cn.liaozh.payment.rqrs.payorder.payway.AliJsapiOrderRQ;
import cn.liaozh.payment.rqrs.payorder.payway.WxJsapiOrderRQ;
import cn.liaozh.payment.service.ConfigContextQueryService;
import cn.liaozh.payment.service.PayMchNotifyService;
import cn.liaozh.service.impl.PayOrderService;
import cn.liaozh.service.impl.SysConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * 聚合码支付二维码收银台controller
 */
@RestController
@RequestMapping("/api/cashier")
public class QrCashierController extends AbstractPayOrderController {

    @Autowired private PayOrderService payOrderService;
    @Autowired private ConfigContextQueryService configContextQueryService;
    @Autowired private SysConfigService sysConfigService;
    @Autowired private PayMchNotifyService payMchNotifyService;

    /**
     * 返回 oauth2【获取uerId跳转地址】
     * **/
    @PostMapping("/redirectUrl")
    public ApiRes redirectUrl(){

        //获取商户配置信息
        MchAppConfigContext mchAppConfigContext = this.commonQueryInfoMchAppConfigContext();

        //回调地址
        String redirectUrlEncode = sysConfigService.getDBApplicationConfig().genOauth2RedirectUrlEncode(this.getToken());

        //获取接口并返回数据
        IChannelUserService channelUserService = getServiceByWayCode(getWayCode(), "ChannelUserService", IChannelUserService.class);
        return ApiRes.ok(channelUserService.buildUserRedirectUrl(redirectUrlEncode, mchAppConfigContext));

    }

    /**
     * 获取userId
     * **/
    @PostMapping("/channelUserId")
    public ApiRes channelUserId() throws Exception {

        //获取商户配置信息
        MchAppConfigContext mchAppConfigContext = this.commonQueryInfoMchAppConfigContext();

        String wayCode = getWayCode();

        IChannelUserService channelUserService = getServiceByWayCode(wayCode, "ChannelUserService", IChannelUserService.class);
        return ApiRes.ok(channelUserService.getChannelUserId(getReqParamJSON(), mchAppConfigContext));

    }

    /**
     * 获取订单支付信息
     * **/
    @PostMapping("/payOrderInfo")
    public ApiRes payOrderInfo() throws Exception {

        //查询订单
        PayOrder payOrder = this.commonQueryPayOrder();

        PayOrder resOrder = new PayOrder();
        resOrder.setMchName(payOrder.getMchName());
        resOrder.setAmount(payOrder.getAmount());

        if(StringUtils.isNotEmpty(payOrder.getPayOrderId())){
            resOrder.setPayOrderId(payOrder.getPayOrderId());
            resOrder.setMchOrderNo(payOrder.getMchOrderNo());
            resOrder.setReturnUrl(payMchNotifyService.createReturnUrl(payOrder, configContextQueryService.queryMchInfoAndAppInfo(payOrder.getMchNo(), payOrder.getAppId()).getMchApp().getAppSecret()));
        }

        return ApiRes.ok(resOrder);
    }


    /** 调起下单接口, 返回支付数据包  **/
    @PostMapping("/pay")
    public ApiRes pay() throws Exception {

        //查询订单
        PayOrder payOrder = this.commonQueryPayOrder();

        String wayCode = getWayCode();

        ApiRes apiRes = null;

        if(wayCode.equals(CS.PAY_WAY_CODE.ALI_JSAPI)){
            apiRes = packageAlipayPayPackage(payOrder);
        }else if(wayCode.equals(CS.PAY_WAY_CODE.WX_JSAPI)){
            apiRes = packageWxpayPayPackage(payOrder);
        }

        return ApiRes.ok(apiRes);
    }


    /** 获取支付宝的 支付参数 **/
    private ApiRes packageAlipayPayPackage(PayOrder payOrder){

        String channelUserId = getValStringRequired("channelUserId");
        AliJsapiOrderRQ rq = new AliJsapiOrderRQ();
        this.commonSetRQ(rq, payOrder);
        rq.setBuyerUserId(channelUserId);
        return this.unifiedOrder(getWayCode(), rq, StringUtils.isNotEmpty(payOrder.getPayOrderId()) ? payOrder : null );
    }


    /** 获取微信的 支付参数 **/
    private ApiRes packageWxpayPayPackage(PayOrder payOrder){

        String openId = getValStringRequired("channelUserId");
        WxJsapiOrderRQ rq = new WxJsapiOrderRQ();
        this.commonSetRQ(rq, payOrder);
        rq.setOpenid(openId);
        return this.unifiedOrder(getWayCode(), rq, StringUtils.isNotEmpty(payOrder.getPayOrderId()) ? payOrder : null );
    }

    /** 赋值通用字段 **/
    private void commonSetRQ(UnifiedOrderRQ rq, PayOrder payOrder){

        // 存在订单数据， 不需要处理
        if(payOrder != null && StringUtils.isNotEmpty(payOrder.getPayOrderId())){
            return ;
        }

        rq.setMchNo(payOrder.getMchNo());
        rq.setAppId(payOrder.getAppId());
        rq.setMchOrderNo(SeqKit.genMhoOrderId());
        rq.setAmount(getRequiredAmountL("amount"));
        rq.setCurrency("cny");
        rq.setSubject("静态码支付");
        rq.setBody("静态码支付");
        rq.setSignType("MD5"); // 设置默认签名方式为MD5
    }


    private String getToken(){
        return getValStringRequired("token");
    }

    private String getWayCode(){
        return getValStringRequired("wayCode");
    }




    private <T> T getServiceByWayCode(String wayCode, String serviceSuffix, Class<T> cls){

        if(CS.PAY_WAY_CODE.ALI_JSAPI.equals(wayCode)){
            return SpringBeansUtil.getBean(CS.IF_CODE.ALIPAY + serviceSuffix, cls);
        }else if(CS.PAY_WAY_CODE.WX_JSAPI.equals(wayCode)){
            return SpringBeansUtil.getBean(CS.IF_CODE.WXPAY + serviceSuffix, cls);
        }

        return null;
    }


    private QRCodeParams tokenConvert(){
        QRCodeParams qrCodeParams = JSON.parseObject(HenPayKit.aesDecode(getToken()), QRCodeParams.class); //解析token
        return qrCodeParams;
    }


    /** 通用查询订单信息 **/
    private PayOrder commonQueryPayOrder(){

        QRCodeParams qrCodeParams = tokenConvert();
        if(qrCodeParams.getType() == QRCodeParams.TYPE_PAY_ORDER){ // 订单

            String payOrderId = this.tokenConvert().getId(); //解析token

            PayOrder payOrder = payOrderService.getById(payOrderId);
            if(payOrder == null || payOrder.getState() != PayOrder.STATE_INIT){
                throw new BizException("订单不存在或状态不正确");
            }

            return payOrderService.getById(payOrderId);

        }else if(qrCodeParams.getType() == QRCodeParams.TYPE_QRC){

            return SpringBeansUtil.getBean(IMchQrcodeManager.class).queryMchInfoByQrc(qrCodeParams.getId());

        }

        return null;
    }

    /** 查询配置信息 **/
    private MchAppConfigContext commonQueryInfoMchAppConfigContext(){

        PayOrder payOrder = this.commonQueryPayOrder();
        return configContextQueryService.queryMchInfoAndAppInfo(payOrder.getMchNo(), payOrder.getAppId());

    }
}
