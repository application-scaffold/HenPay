package cn.liaozh.merchant.ctrl;

import cn.liaozh.core.constants.ApiCodeEnum;
import cn.liaozh.core.constants.CS;
import cn.liaozh.core.controllers.AbstractController;
import cn.liaozh.core.entity.SysUser;
import cn.liaozh.core.model.ApiRes;
import cn.liaozh.core.model.security.JeeUserDetails;
import cn.liaozh.merchant.config.SystemYmlConfig;
import cn.liaozh.service.impl.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 通用ctrl类
 */
public abstract class CommonCtrl extends AbstractController {

    @Autowired
    protected SystemYmlConfig mainConfig;

    @Autowired
    protected SysConfigService sysConfigService;

    /** 获取当前用户ID */
    protected JeeUserDetails getCurrentUser(){

        return (JeeUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /** 获取当前商户ID **/
    protected String getCurrentMchNo() {
        return getCurrentUser().getSysUser().getBelongInfoId();
    }

    /**
     * 获取当前用户登录IP
     * @return
     */
    protected String getIp() {
        return getClientIp();
    }

    /**
     * 校验当前用户是否为超管
     * @return
     */
    protected ApiRes checkIsAdmin() {
        SysUser sysUser = getCurrentUser().getSysUser();
        if (sysUser.getIsAdmin() != CS.YES) {
            return ApiRes.fail(ApiCodeEnum.SYS_PERMISSION_ERROR);
        }else {
            return null;
        }

    }

}
