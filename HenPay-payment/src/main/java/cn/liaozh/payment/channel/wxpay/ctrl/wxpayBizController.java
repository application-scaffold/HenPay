package cn.liaozh.payment.channel.wxpay.ctrl;

import cn.liaozh.core.constants.CS;
import cn.liaozh.core.controllers.AbstractController;
import cn.liaozh.core.entity.TransferOrder;
import cn.liaozh.core.exception.BizException;
import cn.liaozh.core.utils.HenPayKit;
import cn.liaozh.service.impl.TransferOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
* 渠道侧自定义业务ctrl - 微信用户确认API
*/
@Slf4j
@Controller
@RequestMapping("/api/channelbiz/wxpay")
public class wxpayBizController extends AbstractController {

    @Autowired private TransferOrderService transferOrderService;

    @RequestMapping("/transferUserConfirm/{transerNoAES}")
    public String transferUserConfirm(@PathVariable("transerNoAES") String transerNoAES) throws IOException {

        try {
            TransferOrder transferOrder = transferOrderService.getById(HenPayKit.aesDecode(transerNoAES));

            if(transferOrder == null || transferOrder.getState() != TransferOrder.STATE_ING){
                throw new BizException("转账订单不存在或状态不正确");
            }

            if(!CS.IF_CODE.WXPAY.equals(transferOrder.getIfCode())){
                throw new BizException("渠道有误，仅支持微信转账");
            }

            if(StringUtils.isEmpty(transferOrder.getChannelResData())){
                throw new BizException("转账订单数据格式不存在，请重新下单");
            }

            request.setAttribute("channelResData", transferOrder.getChannelResData());
        } catch (Exception e) {
            log.error("微信用户确认异常", e);
            request.setAttribute("errMsg", e.getMessage());
        }

        return "channel/wxpay/wxTransferUserConfirm";
    }

}
