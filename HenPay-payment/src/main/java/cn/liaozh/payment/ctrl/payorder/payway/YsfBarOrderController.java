package cn.liaozh.payment.ctrl.payorder.payway;

import cn.liaozh.core.constants.CS;
import cn.liaozh.core.model.ApiRes;
import cn.liaozh.payment.ctrl.payorder.AbstractPayOrderController;
import cn.liaozh.payment.rqrs.payorder.payway.YsfBarOrderRQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * 云闪付 条码支付 controller
 */
@Slf4j
@RestController
public class YsfBarOrderController extends AbstractPayOrderController {

    /**
     * 统一下单接口
     * **/
    @PostMapping("/api/pay/ysfBarOrder")
    public ApiRes aliBarOrder(){

        //获取参数 & 验证
        YsfBarOrderRQ bizRQ = getRQByWithMchSign(YsfBarOrderRQ.class);

        // 统一下单接口
        return unifiedOrder(CS.PAY_WAY_CODE.YSF_BAR, bizRQ);

    }
}
