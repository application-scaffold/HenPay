package cn.liaozh.manager.ctrl.merchant;

import cn.hutool.core.util.StrUtil;
import cn.liaozh.manager.ctrl.CommonCtrl;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.liaozh.core.aop.MethodLog;
import cn.liaozh.core.constants.ApiCodeEnum;
import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.MchApp;
import cn.liaozh.core.entity.MchInfo;
import cn.liaozh.core.entity.MchPayPassage;
import cn.liaozh.core.entity.PayWay;
import cn.liaozh.core.exception.BizException;
import cn.liaozh.core.model.ApiPageRes;
import cn.liaozh.core.model.ApiRes;
import cn.liaozh.service.impl.MchAppService;
import cn.liaozh.service.impl.MchInfoService;
import cn.liaozh.service.impl.MchPayPassageService;
import cn.liaozh.service.impl.PayWayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

/**
 * 商户支付通道管理类
 */
@Tag(name = "商户支付通道管理")
@RestController
@RequestMapping("/api/mch/payPassages")
public class MchPayPassageConfigController extends CommonCtrl {

    @Autowired private MchPayPassageService mchPayPassageService;
    @Autowired private PayWayService payWayService;
    @Autowired private MchInfoService mchInfoService;
    @Autowired private MchAppService mchAppService;


    /**
     * 查询支付方式列表，并添加是否配置支付通道状态
    */
    @Operation(summary = "查询支付方式列表")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "pageNumber", description = "分页页码"),
            @Parameter(name = "pageSize", description = "分页条数"),
            @Parameter(name = "appId", description = "应用ID", required = true),
            @Parameter(name = "wayCode", description = "支付方式代码"),
            @Parameter(name = "wayName", description = "支付方式名称")
    })
    @PreAuthorize("hasAuthority('ENT_MCH_PAY_PASSAGE_LIST')")
    @GetMapping
    public ApiPageRes<PayWay> list() {

        String appId = getValStringRequired("appId");
        String wayCode = getValString("wayCode");
        String wayName = getValString("wayName");

        //支付方式集合
        LambdaQueryWrapper<PayWay> wrapper = PayWay.gw();
        if (StrUtil.isNotBlank(wayCode)) {
            wrapper.eq(PayWay::getWayCode, wayCode);
        }
        if (StrUtil.isNotBlank(wayName)) {
            wrapper.like(PayWay::getWayName, wayName);
        }
        IPage<PayWay> payWayPage = payWayService.page(getIPage(), wrapper);

        if (!CollectionUtils.isEmpty(payWayPage.getRecords())) {

            // 支付方式代码集合
            List<String> wayCodeList = new LinkedList<>();
            payWayPage.getRecords().stream().forEach(payWay -> wayCodeList.add(payWay.getWayCode()));

            // 应用支付通道集合
            List<MchPayPassage> mchPayPassageList = mchPayPassageService.list(MchPayPassage.gw()
                    .select(MchPayPassage::getWayCode, MchPayPassage::getState)
                    .eq(MchPayPassage::getAppId, appId)
                    .in(MchPayPassage::getWayCode, wayCodeList));

            for (PayWay payWay : payWayPage.getRecords()) {
                payWay.addExt("passageState", CS.NO);
                for (MchPayPassage mchPayPassage : mchPayPassageList) {
                    // 某种支付方式多个通道的情况下，只要有一个通道状态为开启，则该支付方式对应为开启状态
                    if (payWay.getWayCode().equals(mchPayPassage.getWayCode()) && mchPayPassage.getState() == CS.YES) {
                        payWay.addExt("passageState", CS.YES);
                        break;
                    }
                }
            }
        }

        return ApiPageRes.pages(payWayPage);
    }

    /**
     * 根据appId、支付方式查询可用的支付接口列表
    */
    @Operation(summary = "根据[应用ID]、[支付方式代码]查询可用的支付接口列表")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "appId", description = "应用ID", required = true),
            @Parameter(name = "wayCode", description = "支付方式代码", required = true)
    })
    @PreAuthorize("hasAuthority('ENT_MCH_PAY_PASSAGE_CONFIG')")
    @GetMapping("/availablePayInterface/{appId}/{wayCode}")
    public ApiRes availablePayInterface(@PathVariable("appId") String appId, @PathVariable("wayCode") String wayCode) {

        MchApp mchApp = mchAppService.getById(appId);
        if (mchApp == null || mchApp.getState() != CS.YES) {
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_SELETE);
        }

        MchInfo mchInfo = mchInfoService.getById(mchApp.getMchNo());
        if (mchInfo == null || mchInfo.getState() != CS.YES) {
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_SELETE);
        }

        // 根据支付方式查询可用支付接口列表
        List<JSONObject> list = mchPayPassageService.selectAvailablePayInterfaceList(wayCode, appId, CS.INFO_TYPE_MCH_APP, mchInfo.getType());

        return ApiRes.ok(list);
    }

    /**
     * 应用支付通道配置
    */
    @Operation(summary = "更新商户支付通道")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "reqParams", description = "商户支付通道配置信息", required = true)
    })
    @PreAuthorize("hasAuthority('ENT_MCH_PAY_PASSAGE_ADD')")
    @PostMapping
    @MethodLog(remark = "更新商户支付通道")
    public ApiRes saveOrUpdate() {

        String reqParams = getValStringRequired("reqParams");

        try {
            List<MchPayPassage> mchPayPassageList = JSONArray.parseArray(reqParams, MchPayPassage.class);
            if (CollectionUtils.isEmpty(mchPayPassageList)) {
                throw new BizException("操作失败");
            }
            MchApp mchApp = mchAppService.getById(mchPayPassageList.get(0).getAppId());
            if (mchApp == null || mchApp.getState() != CS.YES) {
                return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_SELETE);
            }

            mchPayPassageService.saveOrUpdateBatchSelf(mchPayPassageList, mchApp.getMchNo());
            return ApiRes.ok();
        }catch (Exception e) {
            return ApiRes.fail(ApiCodeEnum.SYSTEM_ERROR);
        }
    }

}
