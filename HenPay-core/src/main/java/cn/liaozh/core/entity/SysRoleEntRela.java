package cn.liaozh.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Schema(description = "系统角色权限关联表")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_role_ent_rela")
public class SysRoleEntRela implements Serializable {

    //gw
    public static final LambdaQueryWrapper<SysRoleEntRela> gw() {
        return new LambdaQueryWrapper<>();
    }

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @Schema(title = "roleId", description = "角色ID")
    private String roleId;

    /**
     * 权限ID
     */
    @Schema(title = "entId", description = "权限ID")
    private String entId;

}
