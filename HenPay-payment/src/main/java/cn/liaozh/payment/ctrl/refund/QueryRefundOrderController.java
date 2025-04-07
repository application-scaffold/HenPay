package cn.liaozh.payment.ctrl.refund;

import cn.liaozh.core.entity.RefundOrder;
import cn.liaozh.core.exception.BizException;
import cn.liaozh.core.model.ApiRes;
import cn.liaozh.payment.ctrl.ApiController;
import cn.liaozh.payment.rqrs.refund.QueryRefundOrderRQ;
import cn.liaozh.payment.rqrs.refund.QueryRefundOrderRS;
import cn.liaozh.payment.service.ConfigContextQueryService;
import cn.liaozh.service.impl.RefundOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* 商户退款单查询controller
*/
@Slf4j
@RestController
public class QueryRefundOrderController extends ApiController {

    @Autowired private RefundOrderService refundOrderService;
    @Autowired private ConfigContextQueryService configContextQueryService;

    /**
     * 查单接口
     * **/
    @RequestMapping("/api/refund/query")
    public ApiRes queryRefundOrder(){

        //获取参数 & 验签
        QueryRefundOrderRQ rq = getRQByWithMchSign(QueryRefundOrderRQ.class);

        if(StringUtils.isAllEmpty(rq.getMchRefundNo(), rq.getRefundOrderId())){
            throw new BizException("mchRefundNo 和 refundOrderId不能同时为空");
        }

        RefundOrder refundOrder = refundOrderService.queryMchOrder(rq.getMchNo(), rq.getMchRefundNo(), rq.getRefundOrderId());
        if(refundOrder == null){
            throw new BizException("订单不存在");
        }

        QueryRefundOrderRS bizRes = QueryRefundOrderRS.buildByRefundOrder(refundOrder);
        return ApiRes.okWithSign(bizRes, configContextQueryService.queryMchApp(rq.getMchNo(), rq.getAppId()).getAppSecret());
    }
}
