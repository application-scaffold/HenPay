package cn.liaozh.core.model.params.alipay;

import cn.liaozh.core.model.params.IsvsubMchParams;
import lombok.Data;
import lombok.EqualsAndHashCode;

/*
 * 支付宝 特约商户参数定义
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AlipayIsvsubMchParams  extends IsvsubMchParams {

    private String appAuthToken;

}
