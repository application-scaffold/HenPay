package cn.liaozh.payment.ctrl.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.core.model.ApiRes;
import cn.liaozh.payment.ctrl.payorder.AbstractPayOrderController;
import cn.liaozh.payment.rqrs.payorder.payway.YsfJsapiOrderRQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * 云闪付 jsapi支付 controller
 */
@Slf4j
@RestController
public class YsfJsapiOrderController extends AbstractPayOrderController {

    /**
     * 统一下单接口
     * **/
    @PostMapping("/api/pay/ysfJsapiOrder")
    public ApiRes aliJsapiOrder(){

        //获取参数 & 验证
        YsfJsapiOrderRQ bizRQ = getRQByWithMchSign(YsfJsapiOrderRQ.class);

        // 统一下单接口
        return unifiedOrder(CS.PAY_WAY_CODE.YSF_JSAPI, bizRQ);

    }
}
