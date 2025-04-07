package cn.liaozh.merchant.ctrl.transfer;

import cn.liaozh.merchant.ctrl.CommonCtrl;
import com.alibaba.fastjson2.JSONObject;
import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.MchApp;
import cn.liaozh.core.entity.PayInterfaceConfig;
import cn.liaozh.core.entity.PayInterfaceDefine;
import cn.liaozh.core.exception.BizException;
import cn.liaozh.core.model.ApiRes;
import cn.liaozh.core.model.DBApplicationConfig;
import cn.liaozh.core.utils.HenPayKit;
import cn.liaozh.core.utils.StringKit;
import cn.liaozh.service.impl.MchAppService;
import cn.liaozh.service.impl.PayInterfaceConfigService;
import cn.liaozh.service.impl.PayInterfaceDefineService;
import cn.liaozh.service.impl.SysConfigService;
import com.jeequan.jeepay.JeepayClient;
import com.jeequan.jeepay.exception.JeepayException;
import com.jeequan.jeepay.model.TransferOrderCreateReqModel;
import com.jeequan.jeepay.request.TransferOrderCreateRequest;
import com.jeequan.jeepay.response.TransferOrderCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
* 转账api
*/
@Tag(name = "商户转账")
@RestController
@RequestMapping("/api/mchTransfers")
public class MchTransferController extends CommonCtrl {

    @Autowired private MchAppService mchAppService;
    @Autowired private PayInterfaceConfigService payInterfaceConfigService;
    @Autowired private PayInterfaceDefineService payInterfaceDefineService;
    @Autowired private SysConfigService sysConfigService;

    /** 查询商户对应应用下支持的支付通道 **/
    @Operation(summary = "查询商户对应应用下支持的支付通道")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "appId", description = "应用ID", required = true)
    })
    @PreAuthorize("hasAuthority('ENT_MCH_TRANSFER_IF_CODE_LIST')")
    @GetMapping("/ifCodes/{appId}")
    public ApiRes<List<PayInterfaceDefine>> ifCodeList(@PathVariable("appId") String appId) {

        List<String> ifCodeList = new ArrayList<>();
        List<PayInterfaceConfig> list = payInterfaceConfigService.list(
                PayInterfaceConfig.gw().select(PayInterfaceConfig::getIfCode)
                        .eq(PayInterfaceConfig::getInfoType, CS.INFO_TYPE_MCH_APP)
                        .eq(PayInterfaceConfig::getInfoId, appId)
                        .eq(PayInterfaceConfig::getState, CS.PUB_USABLE)
        );
        if(list.isEmpty()){
            return ApiRes.ok(new ArrayList<PayInterfaceDefine>());
        }

        list.forEach(r -> ifCodeList.add(r.getIfCode()));

        List<PayInterfaceDefine> result = payInterfaceDefineService.list(PayInterfaceDefine.gw().in(PayInterfaceDefine::getIfCode, ifCodeList));
        return ApiRes.ok(result);
    }


    /** 获取渠道侧用户ID **/
    @Operation(summary = "获取渠道侧用户ID")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "appId", description = "应用ID", required = true),
            @Parameter(name = "ifCode", description = "接口类型代码", required = true),
            @Parameter(name = "extParam", description = "扩展参数", required = true)
    })
    @PreAuthorize("hasAuthority('ENT_MCH_TRANSFER_CHANNEL_USER')")
    @GetMapping("/channelUserId")
    public ApiRes channelUserId() {

        String appId = getValStringRequired("appId");
        MchApp mchApp = mchAppService.getById(appId);
        if(mchApp == null || mchApp.getState() != CS.PUB_USABLE || !mchApp.getMchNo().equals(getCurrentMchNo())){
            throw new BizException("商户应用不存在或不可用");
        }

        JSONObject param = getReqParamJSON();
        param.put("mchNo", getCurrentMchNo());
        param.put("appId", appId);
        param.put("ifCode", getValStringRequired("ifCode"));
        param.put("extParam", getValStringRequired("extParam"));
        param.put("reqTime", System.currentTimeMillis() + "");
        param.put("version", "1.0");
        param.put("signType", "MD5");

        DBApplicationConfig dbApplicationConfig = sysConfigService.getDBApplicationConfig();

        param.put("redirectUrl", dbApplicationConfig.getMchSiteUrl() + "/api/anon/channelUserIdCallback");

        param.put("sign", HenPayKit.getSign(param, mchApp.getAppSecret()));
        String url = StringKit.appendUrlQuery(dbApplicationConfig.getPaySiteUrl() + "/api/channelUserId/jump", param);

        return ApiRes.ok(url);
    }

    /** 调起下单接口 **/
    @Operation(summary = "调起转账接口")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "mchOrderNo", description = "商户订单号", required = true),
            @Parameter(name = "entryType", description = "入账方式： WX_CASH-微信零钱; ALIPAY_CASH-支付宝转账; BANK_CARD-银行卡", required = true),
            @Parameter(name = "ifCode", description = "接口类型代码", required = true),
            @Parameter(name = "amount", description = "转账金额,单位元", required = true),
            @Parameter(name = "accountNo", description = "收款账号", required = true),
            @Parameter(name = "accountName", description = "收款人姓名"),
            @Parameter(name = "bankName", description = "收款人开户行名称"),
            @Parameter(name = "clientIp", description = "客户端IP"),
            @Parameter(name = "transferDesc", description = "转账备注信息"),
            @Parameter(name = "notifyUrl", description = "通知地址"),
            @Parameter(name = "channelExtra", description = "特定渠道发起时额外参数"),
            @Parameter(name = "extParam", description = "扩展参数")
    })
    @PreAuthorize("hasAuthority('ENT_MCH_PAY_TEST_DO')")
    @PostMapping("/doTransfer")
    public ApiRes doTransfer() {

        handleParamAmount("amount");
        TransferOrderCreateReqModel model = getObject(TransferOrderCreateReqModel.class);

        MchApp mchApp = mchAppService.getById(model.getAppId());
        if(mchApp == null || mchApp.getState() != CS.PUB_USABLE || !mchApp.getMchNo().equals(getCurrentMchNo()) ){
            throw new BizException("商户应用不存在或不可用");
        }

        TransferOrderCreateRequest request = new TransferOrderCreateRequest();
        model.setMchNo(this.getCurrentMchNo());
        model.setAppId(mchApp.getAppId());
        model.setCurrency("CNY");
        DBApplicationConfig dbApplicationConfig = sysConfigService.getDBApplicationConfig();
        model.setNotifyUrl(dbApplicationConfig.getMchSiteUrl() + "/api/anon/transferNotify/transferOrder"); //回调地址
        request.setBizModel(model);

        JeepayClient jeepayClient = new JeepayClient(sysConfigService.getDBApplicationConfig().getPaySiteUrl(), mchApp.getAppSecret());

        try {
            TransferOrderCreateResponse response = jeepayClient.execute(request);
            if(response.getCode() != 0){
                throw new BizException(response.getMsg());
            }
            return ApiRes.ok(response.get());
        } catch (JeepayException e) {
            throw new BizException(e.getMessage());
        }
    }

}
