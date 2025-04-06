package cn.liaozh.core.entity;

import cn.liaozh.core.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Schema(description = "系统用户表")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_user")
public class SysUser extends BaseModel {

    //gw
    public static final LambdaQueryWrapper<SysUser> gw() {
        return new LambdaQueryWrapper<>();
    }

    private static final long serialVersionUID = 1L;

    /**
     * 系统用户ID
     */
    @Schema(title = "sysUserId", description = "系统用户ID")
    @TableId(value = "sys_user_id", type = IdType.AUTO)
    private Long sysUserId;

    /**
     * 登录用户名
     */
    @Schema(title = "loginUsername", description = "登录用户名")
    private String loginUsername;

    /**
     * 真实姓名
     */
    @Schema(title = "realname", description = "真实姓名")
    private String realname;

    /**
     * 手机号
     */
    @Schema(title = "telphone", description = "手机号")
    private String telphone;

    /**
     * 性别 0-未知, 1-男, 2-女
     */
    @Schema(title = "sex", description = "性别 0-未知, 1-男, 2-女")
    private Byte sex;

    /**
     * 头像地址
     */
    @Schema(title = "avatarUrl", description = "头像地址")
    private String avatarUrl;

    /**
     * 员工编号
     */
    @Schema(title = "userNo", description = "员工编号")
    private String userNo;

    /**
     * 是否超管（超管拥有全部权限） 0-否 1-是
     */
    @Schema(title = "isAdmin", description = "是否超管（超管拥有全部权限） 0-否 1-是")
    private Byte isAdmin;

    /**
     * 状态 0-停用 1-启用
     */
    @Schema(title = "state", description = "状态 0-停用 1-启用")
    private Byte state;

    /**
     * 所属系统： MGR-运营平台, MCH-商户中心
     */
    @Schema(title = "sysType", description = "所属系统： MGR-运营平台, MCH-商户中心")
    private String sysType;

    /**
     * 所属商户ID / 0(平台)
     */
    @Schema(title = "belongInfoId", description = "所属商户ID / 0(平台)")
    private String belongInfoId;

    /**
     * 创建时间
     */
    @Schema(title = "createdAt", description = "创建时间")
    private Date createdAt;

    /**
     * 更新时间
     */
    @Schema(title = "updatedAt", description = "更新时间")
    private Date updatedAt;

}
