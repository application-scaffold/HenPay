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

import java.io.Serializable;
import java.util.Date;

@Schema(description = "系统配置表")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_config")
public class SysConfig extends BaseModel implements Serializable {

    //gw
    public static final LambdaQueryWrapper<SysConfig> gw() {
        return new LambdaQueryWrapper<>();
    }

    private static final long serialVersionUID = 1L;

    /**
     * 配置KEY
     */
    @Schema(title = "configKey", description = "配置KEY")
    @TableId(value = "config_key", type = IdType.INPUT)
    private String configKey;

    /**
     * 配置名称
     */
    @Schema(title = "configName", description = "配置名称")
    private String configName;

    /**
     * 描述信息
     */
    @Schema(title = "configDesc", description = "描述信息")
    private String configDesc;

    /**
     * 分组key
     */
    @Schema(title = "groupKey", description = "分组key")
    private String groupKey;

    /**
     * 分组名称
     */
    @Schema(title = "groupName", description = "分组名称")
    private String groupName;

    /**
     * 配置内容项
     */
    @Schema(title = "configVal", description = "配置内容项")
    private String configVal;

    /**
     * 类型: text-输入框, textarea-多行文本, uploadImg-上传图片, switch-开关
     */
    @Schema(title = "type", description = "类型: text-输入框, textarea-多行文本, uploadImg-上传图片, switch-开关")
    private String type;

    /**
     * 显示顺序
     */
    @Schema(title = "sortNum", description = "显示顺序")
    private Long sortNum;

    /**
     * 更新时间
     */
    @Schema(title = "updatedAt", description = "更新时间")
    private Date updatedAt;

}
