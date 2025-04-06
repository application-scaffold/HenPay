package cn.liaozh.core.model.params.ysf;

import cn.liaozh.model.params.IsvsubMchParams;
import lombok.Data;

/*
 * 云闪付 配置信息
 */
@Data
public class YsfpayIsvsubMchParams extends IsvsubMchParams {

    private String merId;   // 商户编号

}
