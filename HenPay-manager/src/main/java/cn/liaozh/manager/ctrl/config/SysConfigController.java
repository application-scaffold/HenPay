package cn.liaozh.manager.ctrl.config;

import cn.liaozh.manager.ctrl.CommonCtrl;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.liaozh.components.mq.model.ResetAppConfigMQ;
import cn.liaozh.components.mq.vendor.IMQSender;
import cn.liaozh.core.aop.MethodLog;
import cn.liaozh.core.constants.ApiCodeEnum;
import cn.liaozh.core.entity.SysConfig;
import cn.liaozh.core.model.ApiRes;
import cn.liaozh.core.utils.SpringBeansUtil;
import cn.liaozh.service.impl.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 系统配置信息类
 */
@Tag(name = "系统管理（配置信息类）")
@Slf4j
@RestController
@RequestMapping("api/sysConfigs")
public class SysConfigController extends CommonCtrl {

    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private IMQSender mqSender;


    /**
     * 分组下的配置
     */
    @Operation(summary = "系统配置--查询分组下的配置", description = "")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "groupKey", description = "分组key")
    })
    @PreAuthorize("hasAuthority('ENT_SYS_CONFIG_INFO')")
    @RequestMapping(value = "/{groupKey}", method = RequestMethod.GET)
    public ApiRes<List<SysConfig>> getConfigs(@PathVariable("groupKey") String groupKey) {
        LambdaQueryWrapper<SysConfig> condition = SysConfig.gw();
        condition.orderByAsc(SysConfig::getSortNum);
        if (StringUtils.isNotEmpty(groupKey)) {
            condition.eq(SysConfig::getGroupKey, groupKey);
        }
        List<SysConfig> configList = sysConfigService.list(condition);
        //返回数据
        return ApiRes.ok(configList);
    }

    /**
     * 系统配置修改
     */
    @Operation(summary = "系统配置--修改分组下的配置", description = "")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "groupKey", description = "分组key", required = true),
            @Parameter(name = "mchSiteUrl", description = "商户平台网址(不包含结尾/)"),
            @Parameter(name = "mgrSiteUrl", description = "运营平台网址(不包含结尾/)"),
            @Parameter(name = "ossPublicSiteUrl", description = "公共oss访问地址(不包含结尾/)"),
            @Parameter(name = "paySiteUrl", description = "支付网关地址(不包含结尾/)")
    })
    @PreAuthorize("hasAuthority('ENT_SYS_CONFIG_EDIT')")
    @MethodLog(remark = "系统配置修改")
    @RequestMapping(value = "/{groupKey}", method = RequestMethod.PUT)
    public ApiRes update(@PathVariable("groupKey") String groupKey) {
        JSONObject paramJSON = getReqParamJSON();
        Map updateMap = paramJSON.to(Map.class);
        int update = sysConfigService.updateByConfigKey(updateMap);
        if (update <= 0) {
            return ApiRes.fail(ApiCodeEnum.SYSTEM_ERROR, "更新失败");
        }

        // 异步更新到MQ
        SpringBeansUtil.getBean(SysConfigController.class).updateSysConfigMQ(groupKey);

        return ApiRes.ok();
    }

    @Async
    public void updateSysConfigMQ(String groupKey) {
        mqSender.send(ResetAppConfigMQ.build(groupKey));
    }
}
