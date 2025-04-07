package cn.liaozh.manager.ctrl.merchant;

import cn.hutool.core.util.IdUtil;
import cn.liaozh.manager.ctrl.CommonCtrl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.liaozh.components.mq.model.ResetIsvMchAppInfoConfigMQ;
import cn.liaozh.components.mq.vendor.IMQSender;
import cn.liaozh.core.aop.MethodLog;
import cn.liaozh.core.constants.ApiCodeEnum;
import cn.liaozh.core.entity.MchApp;
import cn.liaozh.core.model.ApiPageRes;
import cn.liaozh.core.model.ApiRes;
import cn.liaozh.service.impl.MchAppService;
import cn.liaozh.service.impl.MchInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 商户应用管理类
 */
@Tag(name = "商户应用管理")
@RestController
@RequestMapping("/api/mchApps")
public class MchAppController extends CommonCtrl {

    @Autowired private MchInfoService mchInfoService;
    @Autowired private MchAppService mchAppService;
    @Autowired private IMQSender mqSender;

    /**
     * 应用列表
    */
    @Operation(summary = "查询应用列表", description = "")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "pageNumber", description = "分页页码"),
            @Parameter(name = "pageSize", description = "分页条数"),
            @Parameter(name = "mchNo", description = "商户号"),
            @Parameter(name = "appId", description = "应用ID"),
            @Parameter(name = "appName", description = "应用名称"),
            @Parameter(name = "state", description = "状态: 0-停用, 1-启用")
    })
    @PreAuthorize("hasAuthority('ENT_MCH_APP_LIST')")
    @GetMapping
    public ApiPageRes<MchApp> list() {
        MchApp mchApp = getObject(MchApp.class);

        IPage<MchApp> pages = mchAppService.selectPage(getIPage(), mchApp);
        return ApiPageRes.pages(pages);
    }

    /**
     * 新建应用
    */
    @Operation(summary = "新建应用", description = "")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "appName", description = "应用名称", required = true),
            @Parameter(name = "appSecret", description = "应用私钥", required = true),
            @Parameter(name = "mchNo", description = "商户号", required = true),
            @Parameter(name = "remark", description = "备注"),
            @Parameter(name = "state", description = "状态: 0-停用, 1-启用")
    })
    @PreAuthorize("hasAuthority('ENT_MCH_APP_ADD')")
    @MethodLog(remark = "新建应用")
    @PostMapping
    public ApiRes add() {
        MchApp mchApp = getObject(MchApp.class);
        mchApp.setAppId(IdUtil.objectId());

        if(mchInfoService.getById(mchApp.getMchNo()) == null) {
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_SELETE);
        }

        boolean result = mchAppService.save(mchApp);
        if (!result) {
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_CREATE);
        }
        return ApiRes.ok();
    }

    /**
     * 应用详情
     */
    @Operation(summary = "应用详情", description = "")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "appId", description = "应用ID", required = true)
    })
    @PreAuthorize("hasAnyAuthority('ENT_MCH_APP_VIEW', 'ENT_MCH_APP_EDIT')")
    @GetMapping("/{appId}")
    public ApiRes<MchApp> detail(@PathVariable("appId") String appId) {
        MchApp mchApp = mchAppService.selectById(appId);
        if (mchApp == null) {
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_SELETE);
        }

        return ApiRes.ok(mchApp);
    }

    /**
     * 更新应用信息
    */
    @Operation(summary = "更新应用信息", description = "")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "appId", description = "应用ID", required = true),
            @Parameter(name = "appName", description = "应用名称", required = true),
            @Parameter(name = "appSecret", description = "应用私钥", required = true),
            @Parameter(name = "mchNo", description = "商户号", required = true),
            @Parameter(name = "remark", description = "备注"),
            @Parameter(name = "state", description = "状态: 0-停用, 1-启用")
    })
    @PreAuthorize("hasAuthority('ENT_MCH_APP_EDIT')")
    @MethodLog(remark = "更新应用信息")
    @PutMapping("/{appId}")
    public ApiRes update(@PathVariable("appId") String appId) {
        MchApp mchApp = getObject(MchApp.class);
        mchApp.setAppId(appId);
        boolean result = mchAppService.updateById(mchApp);
        if (!result) {
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_UPDATE);
        }
        // 推送修改应用消息
        mqSender.send(ResetIsvMchAppInfoConfigMQ.build(ResetIsvMchAppInfoConfigMQ.RESET_TYPE_MCH_APP, null, mchApp.getMchNo(), appId));
        return ApiRes.ok();
    }

    /**
     * 删除应用
     */
    @Operation(summary = "删除应用", description = "")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "appId", description = "应用ID", required = true)
    })
    @PreAuthorize("hasAuthority('ENT_MCH_APP_DEL')")
    @MethodLog(remark = "删除应用")
    @DeleteMapping("/{appId}")
    public ApiRes delete(@PathVariable("appId") String appId) {

        MchApp mchApp = mchAppService.getById(appId);
        mchAppService.removeByAppId(appId);

        // 推送mq到目前节点进行更新数据
        mqSender.send(ResetIsvMchAppInfoConfigMQ.build(ResetIsvMchAppInfoConfigMQ.RESET_TYPE_MCH_APP, null, mchApp.getMchNo(), appId));
        return ApiRes.ok();
    }

}
