package cn.liaozh.manager.ctrl.order;

import cn.liaozh.manager.ctrl.CommonCtrl;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.liaozh.core.constants.ApiCodeEnum;
import cn.liaozh.core.entity.RefundOrder;
import cn.liaozh.core.model.ApiPageRes;
import cn.liaozh.core.model.ApiRes;
import cn.liaozh.service.impl.RefundOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 退款订单类
 */
@Tag(name = "订单管理（退款类）")
@RestController
@RequestMapping("/api/refundOrder")
public class RefundOrderController extends CommonCtrl {

    @Autowired private RefundOrderService refundOrderService;

    /**
     * 退款订单信息列表
     */
    @Operation(summary = "退款订单信息列表")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "pageNumber", description = "分页页码"),
            @Parameter(name = "pageSize", description = "分页条数"),
            @Parameter(name = "createdStart", description = "日期格式字符串（yyyy-MM-dd HH:mm:ss），时间范围查询--开始时间，查询范围：大于等于此时间"),
            @Parameter(name = "createdEnd", description = "日期格式字符串（yyyy-MM-dd HH:mm:ss），时间范围查询--结束时间，查询范围：小于等于此时间"),
            @Parameter(name = "mchNo", description = "商户号"),
            @Parameter(name = "unionOrderId", description = "支付/退款订单号"),
            @Parameter(name = "isvNo", description = "服务商号"),
            @Parameter(name = "appId", description = "应用ID"),
            @Parameter(name = "state", description = "退款状态:0-订单生成,1-退款中,2-退款成功,3-退款失败,4-退款任务关闭"),
            @Parameter(name = "mchType", description = "类型: 1-普通商户, 2-特约商户(服务商模式)")
    })
    @PreAuthorize("hasAuthority('ENT_REFUND_LIST')")
    @RequestMapping(value="", method = RequestMethod.GET)
    public ApiPageRes<RefundOrder> list() {

        RefundOrder refundOrder = getObject(RefundOrder.class);
        JSONObject paramJSON = getReqParamJSON();
        LambdaQueryWrapper<RefundOrder> wrapper = RefundOrder.gw();
        IPage<RefundOrder> pages = refundOrderService.pageList(getIPage(), wrapper, refundOrder, paramJSON);

        return ApiPageRes.pages(pages);
    }

    /**
     * 退款订单信息
     */
    @Operation(summary = "退款订单信息详情")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "refundOrderId", description = "退款订单号", required = true)
    })
    @PreAuthorize("hasAuthority('ENT_REFUND_ORDER_VIEW')")
    @RequestMapping(value="/{refundOrderId}", method = RequestMethod.GET)
    public ApiRes<RefundOrder> detail(@PathVariable("refundOrderId") String refundOrderId) {
        RefundOrder refundOrder = refundOrderService.getById(refundOrderId);
        if (refundOrder == null) {
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_SELETE);
        }
        return ApiRes.ok(refundOrder);
    }
}
