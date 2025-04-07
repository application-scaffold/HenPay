package cn.liaozh.manager.ctrl.payconfig;

import cn.liaozh.manager.ctrl.CommonCtrl;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import cn.liaozh.core.aop.MethodLog;
import cn.liaozh.core.constants.ApiCodeEnum;
import cn.liaozh.core.entity.PayInterfaceConfig;
import cn.liaozh.core.entity.PayInterfaceDefine;
import cn.liaozh.core.entity.PayOrder;
import cn.liaozh.core.exception.BizException;
import cn.liaozh.core.model.ApiRes;
import cn.liaozh.service.impl.PayInterfaceConfigService;
import cn.liaozh.service.impl.PayInterfaceDefineService;
import cn.liaozh.service.impl.PayOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 支付接口定义管理类
 */
@Tag(name = "支付接口配置")
@RestController
@RequestMapping("api/payIfDefines")
public class PayInterfaceDefineController extends CommonCtrl {

    @Autowired private PayInterfaceDefineService payInterfaceDefineService;
    @Autowired private PayOrderService payOrderService;
    @Autowired private PayInterfaceConfigService payInterfaceConfigService;
    
    @Operation(summary = "支付接口--列表")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER)
    })
    @PreAuthorize("hasAuthority('ENT_PC_IF_DEFINE_LIST')")
    @GetMapping
    public ApiRes<List<PayInterfaceDefine>> list() {

        List<PayInterfaceDefine> list = payInterfaceDefineService.list(PayInterfaceDefine.gw()
                .orderByAsc(PayInterfaceDefine::getCreatedAt)
        );
        return ApiRes.ok(list);
    }

    
    @Operation(summary = "支付接口--详情")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "ifCode", description = "接口类型代码", required = true)
    })
    @PreAuthorize("hasAnyAuthority('ENT_PC_IF_DEFINE_VIEW', 'ENT_PC_IF_DEFINE_EDIT')")
    @GetMapping("/{ifCode}")
    public ApiRes<PayInterfaceDefine> detail(@PathVariable("ifCode") String ifCode) {
        return ApiRes.ok(payInterfaceDefineService.getById(ifCode));
    }
    
    @Operation(summary = "支付接口--新增")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "ifCode", description = "接口类型代码", required = true),
            @Parameter(name = "configPageType", description = "支付参数配置页面类型:1-JSON渲染,2-自定义", required = true),
            @Parameter(name = "icon", description = "页面展示：卡片-图标"),
            @Parameter(name = "bgColor", description = "页面展示：卡片-背景色"),
            @Parameter(name = "ifName", description = "接口名称", required = true),
            @Parameter(name = "isIsvMode", description = "是否支持服务商子商户模式: 0-不支持, 1-支持", required = true),
            @Parameter(name = "isMchMode", description = "是否支持普通商户模式: 0-不支持, 1-支持", required = true),
            @Parameter(name = "isvParams", description = "ISV接口配置定义描述,[{},{}]，当configPageType为1时必填"),
            @Parameter(name = "isvsubMchParams", description = "特约商户接口配置定义描述,[{},{}]，当configPageType为1时必填"),
            @Parameter(name = "normalMchParams", description = "普通商户接口配置定义描述,[{},{}]，当configPageType为1时必填"),
            @Parameter(name = "remark", description = "备注", required = true),
            @Parameter(name = "state", description = "状态: 0-停用, 1-启用", required = true),
            @Parameter(name = "wayCodeStrs", description = "接口类型代码（若干个接口类型代码用英文逗号拼接起来）", required = true)
    })
    @PreAuthorize("hasAuthority('ENT_PC_IF_DEFINE_ADD')")
    @PostMapping
    @MethodLog(remark = "新增支付接口")
    public ApiRes add() {
        PayInterfaceDefine payInterfaceDefine = getObject(PayInterfaceDefine.class);

        JSONArray jsonArray = new JSONArray();
        String[] wayCodes = getValStringRequired("wayCodeStrs").split(",");
        for (String wayCode : wayCodes) {
            JSONObject object = new JSONObject();
            object.put("wayCode", wayCode);
            jsonArray.add(object);
        }
        payInterfaceDefine.setWayCodes(jsonArray);

        boolean result = payInterfaceDefineService.save(payInterfaceDefine);
        if (!result) {
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_CREATE);
        }
        return ApiRes.ok();
    }
    
    @Operation(summary = "支付接口--更新")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "ifCode", description = "接口类型代码", required = true),
            @Parameter(name = "configPageType", description = "支付参数配置页面类型:1-JSON渲染,2-自定义", required = true),
            @Parameter(name = "icon", description = "页面展示：卡片-图标"),
            @Parameter(name = "bgColor", description = "页面展示：卡片-背景色"),
            @Parameter(name = "ifName", description = "接口名称", required = true),
            @Parameter(name = "isIsvMode", description = "是否支持服务商子商户模式: 0-不支持, 1-支持", required = true),
            @Parameter(name = "isMchMode", description = "是否支持普通商户模式: 0-不支持, 1-支持", required = true),
            @Parameter(name = "isvParams", description = "ISV接口配置定义描述,[{},{}]，当configPageType为1时必填"),
            @Parameter(name = "isvsubMchParams", description = "特约商户接口配置定义描述,[{},{}]，当configPageType为1时必填"),
            @Parameter(name = "normalMchParams", description = "普通商户接口配置定义描述,[{},{}]，当configPageType为1时必填"),
            @Parameter(name = "remark", description = "备注", required = true),
            @Parameter(name = "state", description = "状态: 0-停用, 1-启用", required = true),
            @Parameter(name = "wayCodeStrs", description = "接口类型代码（若干个接口类型代码用英文逗号拼接起来）", required = true),
            @Parameter(name = "wayCodes", description = "接口类型代码列表")
    })
    @PreAuthorize("hasAuthority('ENT_PC_IF_DEFINE_EDIT')")
    @PutMapping("/{ifCode}")
    @MethodLog(remark = "更新支付接口")
    public ApiRes update(@PathVariable("ifCode") String ifCode) {
        PayInterfaceDefine payInterfaceDefine = getObject(PayInterfaceDefine.class);
        payInterfaceDefine.setIfCode(ifCode);

        JSONArray jsonArray = new JSONArray();
        String[] wayCodes = getValStringRequired("wayCodeStrs").split(",");
        for (String wayCode : wayCodes) {
            JSONObject object = new JSONObject();
            object.put("wayCode", wayCode);
            jsonArray.add(object);
        }
        payInterfaceDefine.setWayCodes(jsonArray);

        boolean result = payInterfaceDefineService.updateById(payInterfaceDefine);
        if (!result) {
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_UPDATE);
        }
        return ApiRes.ok();
    }
    
    @Operation(summary = "支付接口--删除")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "ifCode", description = "接口类型代码", required = true)
    })
    @PreAuthorize("hasAuthority('ENT_PC_IF_DEFINE_DEL')")
    @DeleteMapping("/{ifCode}")
    @MethodLog(remark = "删除支付接口")
    public ApiRes delete(@PathVariable("ifCode") String ifCode) {

        // 校验该支付方式是否有服务商或商户配置参数或者已有订单
        if (payInterfaceConfigService.count(PayInterfaceConfig.gw().eq(PayInterfaceConfig::getIfCode, ifCode)) > 0
                || payOrderService.count(PayOrder.gw().eq(PayOrder::getIfCode, ifCode)) > 0) {
            throw new BizException("该支付接口已有服务商或商户配置参数或已发生交易，无法删除！");
        }

        boolean result = payInterfaceDefineService.removeById(ifCode);
        if (!result) {
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_DELETE);
        }
        return ApiRes.ok();
    }

}
