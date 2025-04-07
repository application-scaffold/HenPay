package cn.liaozh.manager.ctrl.order;

import cn.liaozh.manager.ctrl.CommonCtrl;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.liaozh.components.mq.model.PayOrderMchNotifyMQ;
import cn.liaozh.components.mq.vendor.IMQSender;
import cn.liaozh.core.constants.ApiCodeEnum;
import cn.liaozh.core.entity.MchNotifyRecord;
import cn.liaozh.core.exception.BizException;
import cn.liaozh.core.model.ApiPageRes;
import cn.liaozh.core.model.ApiRes;

import cn.liaozh.service.impl.MchNotifyRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商户通知类
 */
@Tag(name = "订单管理（通知类）")
@RestController
@RequestMapping("/api/mchNotify")
public class MchNotifyController extends CommonCtrl {

    @Autowired private MchNotifyRecordService mchNotifyService;
    @Autowired private IMQSender mqSender;

    /**
     * 商户通知列表
     */
    @Operation(summary = "查询商户通知列表")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "pageNumber", description = "分页页码"),
            @Parameter(name = "pageSize", description = "分页条数"),
            @Parameter(name = "createdStart", description = "日期格式字符串（yyyy-MM-dd HH:mm:ss），时间范围查询--开始时间，查询范围：大于等于此时间"),
            @Parameter(name = "createdEnd", description = "日期格式字符串（yyyy-MM-dd HH:mm:ss），时间范围查询--结束时间，查询范围：小于等于此时间"),
            @Parameter(name = "mchNo", description = "商户号"),
            @Parameter(name = "orderId", description = "订单ID"),
            @Parameter(name = "mchOrderNo", description = "商户订单号"),
            @Parameter(name = "isvNo", description = "服务商号"),
            @Parameter(name = "appId", description = "应用ID"),
            @Parameter(name = "state", description = "通知状态,1-通知中,2-通知成功,3-通知失败"),
            @Parameter(name = "orderType", description = "订单类型:1-支付,2-退款")
    })
    @PreAuthorize("hasAuthority('ENT_NOTIFY_LIST')")
    @RequestMapping(value="", method = RequestMethod.GET)
    public ApiPageRes<MchNotifyRecord> list() {

        MchNotifyRecord mchNotify = getObject(MchNotifyRecord.class);
        JSONObject paramJSON = getReqParamJSON();
        LambdaQueryWrapper<MchNotifyRecord> wrapper = MchNotifyRecord.gw();
        if (StringUtils.isNotEmpty(mchNotify.getOrderId())) {
            wrapper.eq(MchNotifyRecord::getOrderId, mchNotify.getOrderId());
        }
        if (StringUtils.isNotEmpty(mchNotify.getMchNo())) {
            wrapper.eq(MchNotifyRecord::getMchNo, mchNotify.getMchNo());
        }
        if (StringUtils.isNotEmpty(mchNotify.getIsvNo())) {
            wrapper.eq(MchNotifyRecord::getIsvNo, mchNotify.getIsvNo());
        }
        if (StringUtils.isNotEmpty(mchNotify.getMchOrderNo())) {
            wrapper.eq(MchNotifyRecord::getMchOrderNo, mchNotify.getMchOrderNo());
        }
        if (mchNotify.getOrderType() != null) {
            wrapper.eq(MchNotifyRecord::getOrderType, mchNotify.getOrderType());
        }
        if (mchNotify.getState() != null) {
            wrapper.eq(MchNotifyRecord::getState, mchNotify.getState());
        }
        if (StringUtils.isNotEmpty(mchNotify.getAppId())) {
            wrapper.eq(MchNotifyRecord::getAppId, mchNotify.getAppId());
        }

        if (paramJSON != null) {
            if (StringUtils.isNotEmpty(paramJSON.getString("createdStart"))) {
                wrapper.ge(MchNotifyRecord::getCreatedAt, paramJSON.getString("createdStart"));
            }
            if (StringUtils.isNotEmpty(paramJSON.getString("createdEnd"))) {
                wrapper.le(MchNotifyRecord::getCreatedAt, paramJSON.getString("createdEnd"));
            }
        }
        wrapper.orderByDesc(MchNotifyRecord::getCreatedAt);
        IPage<MchNotifyRecord> pages = mchNotifyService.page(getIPage(), wrapper);

        return ApiPageRes.pages(pages);
    }

    /**
     * 商户通知信息
     */
    @Operation(summary = "通知信息详情")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "notifyId", description = "商户通知记录ID", required = true)
    })
    @PreAuthorize("hasAuthority('ENT_MCH_NOTIFY_VIEW')")
    @RequestMapping(value="/{notifyId}", method = RequestMethod.GET)
    public ApiRes<MchNotifyRecord> detail(@PathVariable("notifyId") String notifyId) {
        MchNotifyRecord mchNotify = mchNotifyService.getById(notifyId);
        if (mchNotify == null) {
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_SELETE);
        }
        return ApiRes.ok(mchNotify);
    }

   /*
    * 商户通知重发操作
    */
    @Operation(summary = "商户通知重发")
    @Parameters({
           @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
           @Parameter(name = "notifyId", description = "商户通知记录ID", required = true)
    })
    @PreAuthorize("hasAuthority('ENT_MCH_NOTIFY_RESEND')")
    @RequestMapping(value="resend/{notifyId}", method = RequestMethod.POST)
    public ApiRes<MchNotifyRecord> resend(@PathVariable("notifyId") Long notifyId) {
        MchNotifyRecord mchNotify = mchNotifyService.getById(notifyId);
        if (mchNotify == null) {
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_SELETE);
        }
        if (mchNotify.getState() != MchNotifyRecord.STATE_FAIL) {
            throw new BizException("请选择失败的通知记录");
        }

        //更新通知中
        mchNotifyService.getBaseMapper().updateIngAndAddNotifyCountLimit(notifyId);

        //调起MQ重发
        mqSender.send(PayOrderMchNotifyMQ.build(notifyId));

        return ApiRes.ok(mchNotify);
    }

}
