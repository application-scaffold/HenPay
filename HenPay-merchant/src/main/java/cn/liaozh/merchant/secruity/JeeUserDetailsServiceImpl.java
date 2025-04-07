package cn.liaozh.merchant.secruity;

import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.MchInfo;
import cn.liaozh.core.entity.SysUser;
import cn.liaozh.core.entity.SysUserAuth;
import cn.liaozh.core.exception.JeepayAuthenticationException;
import cn.liaozh.core.model.security.JeeUserDetails;
import cn.liaozh.core.utils.RegKit;
import cn.liaozh.service.impl.MchInfoService;
import cn.liaozh.service.impl.SysUserAuthService;
import cn.liaozh.service.impl.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * UserDetailsService实现类
 */
@Service
public class JeeUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserAuthService sysUserAuthService;

    @Autowired
    private MchInfoService mchInfoService;

    /**
     *
     * 此函数为： authenticationManager.authenticate(upToken) 内部调用 ;
     * 需返回 用户信息载体 / 用户密码  。
     * 用户角色+权限的封装集合 (暂时不查询， 在验证通过后再次查询，避免用户名密码输入有误导致查询资源浪费)
     *
     * **/
    @Override
    public UserDetails loadUserByUsername(String loginUsernameStr) throws UsernameNotFoundException {

        //登录方式， 默认为账号密码登录
        Byte identityType = CS.AUTH_TYPE.LOGIN_USER_NAME;
        if(RegKit.isMobile(loginUsernameStr)){
            identityType = CS.AUTH_TYPE.TELPHONE; //手机号登录
        }

        //首先根据登录类型 + 用户名得到 信息
        SysUserAuth auth = sysUserAuthService.selectByLogin(loginUsernameStr, identityType, CS.SYS_TYPE.MCH);

        if(auth == null){ //没有该用户信息
            throw JeepayAuthenticationException.build("用户名/密码错误！");
        }

        //用户ID
        Long userId = auth.getUserId();

        SysUser sysUser = sysUserService.getById(userId);

        if (sysUser == null) {
            throw JeepayAuthenticationException.build("用户名/密码错误！");
        }

        MchInfo mchInfo = mchInfoService.getById(sysUser.getBelongInfoId());
        if (mchInfo == null) {
            throw JeepayAuthenticationException.build("所属商户为空，请联系管理员！");
        }

        if (CS.PUB_USABLE != sysUser.getState()) {//用户角色状态停用
            throw JeepayAuthenticationException.build("用户状态不可登录，请联系管理员！");
        }

        if(CS.PUB_USABLE != mchInfo.getState()){ //商户状态停用
            throw JeepayAuthenticationException.build("商户状态停用，请联系管理员！");
        }

        return new JeeUserDetails(sysUser, auth.getCredential());

    }
}
