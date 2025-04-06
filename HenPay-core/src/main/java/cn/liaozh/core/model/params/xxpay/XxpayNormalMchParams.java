package cn.liaozh.core.model.params.xxpay;

import cn.liaozh.model.params.NormalMchParams;
import cn.liaozh.utils.StringKit;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/*
 * 小新支付 普通商户参数定义
 */
@Data
public class XxpayNormalMchParams extends NormalMchParams {

    /** 商户号 */
    private String mchId;

    /** 私钥 */
    private String key;

    /** 支付网关地址 */
    private String payUrl;

    @Override
    public String deSenData() {
        XxpayNormalMchParams mchParams = this;
        if (StringUtils.isNotBlank(this.key)) {
            mchParams.setKey(StringKit.str2Star(this.key, 4, 4, 6));
        }
        return ((JSONObject) JSON.toJSON(mchParams)).toJSONString();
    }

}
