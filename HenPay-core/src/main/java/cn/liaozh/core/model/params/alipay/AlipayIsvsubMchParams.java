package cn.liaozh.core.model.params.alipay;

import cn.liaozh.model.params.IsvsubMchParams;
import lombok.Data;

/*
 * 支付宝 特约商户参数定义
 */
@Data
public class AlipayIsvsubMchParams  extends IsvsubMchParams {

    private String appAuthToken;

}
