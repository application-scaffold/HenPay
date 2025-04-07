package cn.liaozh.payment.ctrl.division;

import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.MchDivisionReceiver;
import cn.liaozh.core.entity.MchDivisionReceiverGroup;
import cn.liaozh.core.entity.MchInfo;
import cn.liaozh.core.exception.BizException;
import cn.liaozh.core.model.ApiRes;
import cn.liaozh.core.utils.SpringBeansUtil;
import cn.liaozh.payment.channel.IDivisionService;
import cn.liaozh.payment.ctrl.ApiController;
import cn.liaozh.payment.model.MchAppConfigContext;
import cn.liaozh.payment.rqrs.division.DivisionReceiverBindRQ;
import cn.liaozh.payment.rqrs.division.DivisionReceiverBindRS;
import cn.liaozh.payment.rqrs.msg.ChannelRetMsg;
import cn.liaozh.payment.service.ConfigContextQueryService;
import cn.liaozh.service.impl.MchDivisionReceiverGroupService;
import cn.liaozh.service.impl.MchDivisionReceiverService;
import cn.liaozh.service.impl.PayInterfaceConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

/**
* 分账账号绑定
*/
@Slf4j
@RestController
public class MchDivisionReceiverBindController extends ApiController {

    @Autowired private ConfigContextQueryService configContextQueryService;
    @Autowired private PayInterfaceConfigService payInterfaceConfigService;
    @Autowired private MchDivisionReceiverService mchDivisionReceiverService;
    @Autowired private MchDivisionReceiverGroupService mchDivisionReceiverGroupService;

    /** 分账账号绑定 **/
    @PostMapping("/api/division/receiver/bind")
    public ApiRes bind(){

        //获取参数 & 验签
        DivisionReceiverBindRQ bizRQ = getRQByWithMchSign(DivisionReceiverBindRQ.class);

        try {

            //检查商户应用是否存在该接口
            String ifCode = bizRQ.getIfCode();


            // 商户配置信息
            MchAppConfigContext mchAppConfigContext = configContextQueryService.queryMchInfoAndAppInfo(bizRQ.getMchNo(), bizRQ.getAppId());
            if(mchAppConfigContext == null){
                throw new BizException("获取商户应用信息失败");
            }

            MchInfo mchInfo = mchAppConfigContext.getMchInfo();

            if(!payInterfaceConfigService.mchAppHasAvailableIfCode(bizRQ.getAppId(), ifCode)){
                throw new BizException("商户应用的支付配置不存在或已关闭");
            }

            MchDivisionReceiverGroup group = mchDivisionReceiverGroupService.findByIdAndMchNo(bizRQ.getReceiverGroupId(), bizRQ.getMchNo());
            if(group == null){
                throw new BizException("商户分账账号组不存在，请检查或进入商户平台进行创建操作");
            }

            BigDecimal divisionProfit = new BigDecimal(bizRQ.getDivisionProfit());
            if(divisionProfit.compareTo(BigDecimal.ZERO) <= 0  || divisionProfit.compareTo(BigDecimal.ONE) > 1){
                throw new BizException("账号分账比例有误, 配置值为[0.0001~1.0000]");
            }


            //生成数据库对象信息 （数据不完成， 暂时不可入库操作）
            MchDivisionReceiver receiver = genRecord(bizRQ, group, mchInfo, divisionProfit);

            //调起上游接口

            IDivisionService divisionService = SpringBeansUtil.getBean(ifCode + "DivisionService", IDivisionService.class);
            if(divisionService == null){
                throw new BizException("系统不支持该分账接口");
            }


            ChannelRetMsg retMsg = divisionService.bind(receiver, mchAppConfigContext);
            if(retMsg.getChannelState() == ChannelRetMsg.ChannelState.CONFIRM_SUCCESS){

                receiver.setState(CS.YES);
                receiver.setBindSuccessTime(new Date());
                mchDivisionReceiverService.save(receiver);

            }else{

                receiver.setState(CS.NO);
                receiver.setChannelBindResult(retMsg.getChannelErrMsg());
            }

            DivisionReceiverBindRS bizRes = DivisionReceiverBindRS.buildByRecord(receiver);

            if(retMsg.getChannelState() == ChannelRetMsg.ChannelState.CONFIRM_SUCCESS){

                bizRes.setBindState(CS.YES);

            }else{
                bizRes.setBindState(CS.NO);
                bizRes.setErrCode(retMsg.getChannelErrCode());
                bizRes.setErrMsg(retMsg.getChannelErrMsg());
            }

            return ApiRes.okWithSign(bizRes, mchAppConfigContext.getMchApp().getAppSecret());

        }  catch (BizException e) {
            return ApiRes.customFail(e.getMessage());

        } catch (Exception e) {
            log.error("系统异常：{}", e);
            return ApiRes.customFail("系统异常");
        }
    }

    private MchDivisionReceiver genRecord(DivisionReceiverBindRQ bizRQ, MchDivisionReceiverGroup group, MchInfo mchInfo, BigDecimal divisionProfit){

        MchDivisionReceiver receiver = new MchDivisionReceiver();
        receiver.setReceiverAlias(StringUtils.defaultIfEmpty(bizRQ.getReceiverAlias(), bizRQ.getAccNo())); //别名
        receiver.setReceiverGroupId(bizRQ.getReceiverGroupId()); //分组ID
        receiver.setReceiverGroupName(group.getReceiverGroupName()); //组名称
        receiver.setMchNo(bizRQ.getMchNo()); //商户号
        receiver.setIsvNo(mchInfo.getIsvNo()); //isvNo
        receiver.setAppId(bizRQ.getAppId()); //appId
        receiver.setIfCode(bizRQ.getIfCode()); //接口代码
        receiver.setAccType(bizRQ.getAccType()); //账号类型
        receiver.setAccNo(bizRQ.getAccNo()); //账号
        receiver.setAccName(bizRQ.getAccName()); //账号名称
        receiver.setRelationType(bizRQ.getRelationType()); //关系

        receiver.setRelationTypeName(getRelationTypeName(bizRQ.getRelationType())); //关系名称

        if(receiver.getRelationTypeName() == null){
            receiver.setRelationTypeName(bizRQ.getRelationTypeName());
        }

        receiver.setDivisionProfit(divisionProfit); //分账比例
        receiver.setChannelExtInfo(bizRQ.getChannelExtInfo()); //渠道信息

        return receiver;
    }

    public String getRelationTypeName(String relationType){

        if("PARTNER".equals(relationType)){
            return "合作伙伴";
        }else if("SERVICE_PROVIDER".equals(relationType)){
            return "服务商";
        }else if("STORE".equals(relationType)){
            return "门店";
        }else if("STAFF".equals(relationType)){
            return "员工";
        }else if("STORE_OWNER".equals(relationType)){
            return "店主";
        }else if("HEADQUARTER".equals(relationType)){
            return "总部";
        }else if("BRAND".equals(relationType)){
            return "品牌方";
        }else if("DISTRIBUTOR".equals(relationType)){
            return "分销商";
        }else if("USER".equals(relationType)){
            return "用户";
        }else if("SUPPLIER".equals(relationType)){
            return "供应商";
        }
        return null;
    }
}
