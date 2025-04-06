package cn.liaozh.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Schema(description = "操作员<->角色 关联表")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_user_role_rela")
public class SysUserRoleRela implements Serializable {

    //gw
    public static final LambdaQueryWrapper<SysUserRoleRela> gw() {
        return new LambdaQueryWrapper<>();
    }

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(title = "userId", description = "用户ID")
    private Long userId;

    /**
     * 角色ID
     */
    @Schema(title = "roleId", description = "角色ID")
    private String roleId;

}
