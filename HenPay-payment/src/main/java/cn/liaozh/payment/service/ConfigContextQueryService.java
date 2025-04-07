package cn.liaozh.payment.service;

import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.MchApp;
import cn.liaozh.core.entity.MchInfo;
import cn.liaozh.core.entity.PayInterfaceConfig;
import cn.liaozh.core.model.params.IsvParams;
import cn.liaozh.core.model.params.IsvsubMchParams;
import cn.liaozh.core.model.params.NormalMchParams;
import cn.liaozh.core.model.params.alipay.AlipayIsvParams;
import cn.liaozh.core.model.params.alipay.AlipayNormalMchParams;
import cn.liaozh.core.model.params.pppay.PppayNormalMchParams;
import cn.liaozh.core.model.params.wxpay.WxpayIsvParams;
import cn.liaozh.core.model.params.wxpay.WxpayNormalMchParams;
import cn.liaozh.payment.model.*;
import cn.liaozh.service.impl.MchAppService;
import cn.liaozh.service.impl.MchInfoService;
import cn.liaozh.service.impl.PayInterfaceConfigService;
import cn.liaozh.service.impl.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
* 配置信息查询服务 （兼容 缓存 和 直接查询方式）
*/
@Slf4j
@Service
public class ConfigContextQueryService {

    @Autowired ConfigContextService configContextService;
    @Autowired private MchInfoService mchInfoService;
    @Autowired private MchAppService mchAppService;
    @Autowired private PayInterfaceConfigService payInterfaceConfigService;

    private boolean isCache(){
        return SysConfigService.IS_USE_CACHE;
    }

    public MchApp queryMchApp(String mchNo, String mchAppId){

        if(isCache()){
            return configContextService.getMchAppConfigContext(mchNo, mchAppId).getMchApp();
        }

        return mchAppService.getOneByMch(mchNo, mchAppId);
    }

    public MchAppConfigContext queryMchInfoAndAppInfo(String mchAppId) {
        return queryMchInfoAndAppInfo(mchAppService.getById(mchAppId).getMchNo(), mchAppId);
    }

    public MchAppConfigContext queryMchInfoAndAppInfo(String mchNo, String mchAppId){

        if(isCache()){
            return configContextService.getMchAppConfigContext(mchNo, mchAppId);
        }

        MchInfo mchInfo = mchInfoService.getById(mchNo);
        MchApp mchApp = queryMchApp(mchNo, mchAppId);

        if(mchInfo == null || mchApp == null){
            return null;
        }

        MchAppConfigContext result = new MchAppConfigContext();
        result.setMchInfo(mchInfo);
        result.setMchNo(mchNo);
        result.setMchType(mchInfo.getType());

        result.setMchApp(mchApp);
        result.setAppId(mchAppId);

        return result;
    }


    public NormalMchParams queryNormalMchParams(String mchNo, String mchAppId, String ifCode){

        if(isCache()){
            return configContextService.getMchAppConfigContext(mchNo, mchAppId).getNormalMchParamsByIfCode(ifCode);
        }

        // 查询商户的所有支持的参数配置
        PayInterfaceConfig payInterfaceConfig = payInterfaceConfigService.getOne(PayInterfaceConfig.gw()
                .select(PayInterfaceConfig::getIfCode, PayInterfaceConfig::getIfParams)
                .eq(PayInterfaceConfig::getState, CS.YES)
                .eq(PayInterfaceConfig::getInfoType, CS.INFO_TYPE_MCH_APP)
                .eq(PayInterfaceConfig::getInfoId, mchAppId)
                .eq(PayInterfaceConfig::getIfCode, ifCode)
        );

        if(payInterfaceConfig == null){
            return null;
        }

        return NormalMchParams.factory(payInterfaceConfig.getIfCode(), payInterfaceConfig.getIfParams());
    }


    public IsvsubMchParams queryIsvsubMchParams(String mchNo, String mchAppId, String ifCode){

        if(isCache()){
            return configContextService.getMchAppConfigContext(mchNo, mchAppId).getIsvsubMchParamsByIfCode(ifCode);
        }

        // 查询商户的所有支持的参数配置
        PayInterfaceConfig payInterfaceConfig = payInterfaceConfigService.getOne(PayInterfaceConfig.gw()
                .select(PayInterfaceConfig::getIfCode, PayInterfaceConfig::getIfParams)
                .eq(PayInterfaceConfig::getState, CS.YES)
                .eq(PayInterfaceConfig::getInfoType, CS.INFO_TYPE_MCH_APP)
                .eq(PayInterfaceConfig::getInfoId, mchAppId)
                .eq(PayInterfaceConfig::getIfCode, ifCode)
        );

        if(payInterfaceConfig == null){
            return null;
        }

        return IsvsubMchParams.factory(payInterfaceConfig.getIfCode(), payInterfaceConfig.getIfParams());
    }



    public IsvParams queryIsvParams(String isvNo, String ifCode){

        if(isCache()){
            IsvConfigContext isvConfigContext = configContextService.getIsvConfigContext(isvNo);
            return isvConfigContext == null ? null : isvConfigContext.getIsvParamsByIfCode(ifCode);
        }

        // 查询商户的所有支持的参数配置
        PayInterfaceConfig payInterfaceConfig = payInterfaceConfigService.getOne(PayInterfaceConfig.gw()
                .select(PayInterfaceConfig::getIfCode, PayInterfaceConfig::getIfParams)
                .eq(PayInterfaceConfig::getState, CS.YES)
                .eq(PayInterfaceConfig::getInfoType, CS.INFO_TYPE_ISV)
                .eq(PayInterfaceConfig::getInfoId, isvNo)
                .eq(PayInterfaceConfig::getIfCode, ifCode)
        );

        if(payInterfaceConfig == null){
            return null;
        }

        return IsvParams.factory(payInterfaceConfig.getIfCode(), payInterfaceConfig.getIfParams());

    }

    public AlipayClientWrapper getAlipayClientWrapper(MchAppConfigContext mchAppConfigContext){

        if(isCache()){
            return
                    configContextService.getMchAppConfigContext(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId()).getAlipayClientWrapper();
        }

        if(mchAppConfigContext.isIsvsubMch()){

            AlipayIsvParams alipayParams = (AlipayIsvParams)queryIsvParams(mchAppConfigContext.getMchInfo().getIsvNo(), CS.IF_CODE.ALIPAY);
            return AlipayClientWrapper.buildAlipayClientWrapper(alipayParams);
        }else{

            AlipayNormalMchParams alipayParams = (AlipayNormalMchParams)queryNormalMchParams(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId(), CS.IF_CODE.ALIPAY);
            return AlipayClientWrapper.buildAlipayClientWrapper(alipayParams);
        }

    }

    public WxServiceWrapper getWxServiceWrapper(MchAppConfigContext mchAppConfigContext){

        if(isCache()){
            return
                    configContextService.getMchAppConfigContext(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId()).getWxServiceWrapper();
        }

        if(mchAppConfigContext.isIsvsubMch()){

            WxpayIsvParams wxParams = (WxpayIsvParams)queryIsvParams(mchAppConfigContext.getMchInfo().getIsvNo(), CS.IF_CODE.WXPAY);
            return WxServiceWrapper.buildWxServiceWrapper(wxParams);
        }else{

            WxpayNormalMchParams wxParams = (WxpayNormalMchParams)queryNormalMchParams(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId(), CS.IF_CODE.WXPAY);
            return WxServiceWrapper.buildWxServiceWrapper(wxParams);
        }

    }

    public PaypalWrapper getPaypalWrapper(MchAppConfigContext mchAppConfigContext){
        if(isCache()){
            return
                    configContextService.getMchAppConfigContext(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId()).getPaypalWrapper();
        }
        PppayNormalMchParams ppPayNormalMchParams = (PppayNormalMchParams) queryNormalMchParams(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId(), CS.IF_CODE.PPPAY);;
        return PaypalWrapper.buildPaypalWrapper(ppPayNormalMchParams);

    }

}
