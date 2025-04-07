package cn.liaozh.payment.mq;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import cn.liaozh.components.mq.model.PayOrderMchNotifyMQ;
import cn.liaozh.components.mq.vendor.IMQSender;
import cn.liaozh.core.entity.MchNotifyRecord;
import cn.liaozh.service.impl.MchNotifyRecordService;
import cn.liaozh.service.impl.PayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 接收MQ消息
 * 业务： 支付订单商户通知
 */
@Slf4j
@Component
public class PayOrderMchNotifyMQReceiver implements PayOrderMchNotifyMQ.IMQReceiver {

    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private MchNotifyRecordService mchNotifyRecordService;
    @Autowired
    private IMQSender mqSender;

    @Override
    public void receive(PayOrderMchNotifyMQ.MsgPayload payload) {

        try {
            log.info("接收商户通知MQ, msg={}", payload.toString());

            Long notifyId = payload.getNotifyId();
            MchNotifyRecord record = mchNotifyRecordService.getById(notifyId);
            if(record == null || record.getState() != MchNotifyRecord.STATE_ING){
                log.info("查询通知记录不存在或状态不是通知中");
                return;
            }
            if( record.getNotifyCount() >= record.getNotifyCountLimit() ){
                log.info("已达到最大发送次数");
                return;
            }

            //1. (发送结果最多6次)
            Integer currentCount = record.getNotifyCount() + 1;

            String notifyUrl = record.getNotifyUrl();
            String res = "";
            try {
                // res = HttpUtil.createPost(notifyUrl).timeout(20000).execute().body();

                String host = notifyUrl.split("\\?")[0];
                Map bodyMap = HttpUtil.decodeParamMap(notifyUrl, CharsetUtil.CHARSET_UTF_8);
                res = HttpUtil.post(host, bodyMap, 20000);

            } catch (Exception e) {
                log.error("http error", e);
                res = "连接["+ UrlBuilder.of(notifyUrl).getHost() +"]异常:【" + e.getMessage() + "】";
            }

            //支付订单 & 第一次通知: 更新为已通知
            if(currentCount == 1 && MchNotifyRecord.TYPE_PAY_ORDER == record.getOrderType()){
                payOrderService.updateNotifySent(record.getOrderId());
            }

            //通知成功
            if("SUCCESS".equalsIgnoreCase(res)){
                mchNotifyRecordService.updateNotifyResult(notifyId, MchNotifyRecord.STATE_SUCCESS, res);
                return;
            }

            //通知次数 >= 最大通知次数时， 更新响应结果为异常， 不在继续延迟发送消息
            if( currentCount >= record.getNotifyCountLimit() ){
                mchNotifyRecordService.updateNotifyResult(notifyId, MchNotifyRecord.STATE_FAIL, res);
                return;
            }

            // 继续发送MQ 延迟发送
            mchNotifyRecordService.updateNotifyResult(notifyId, MchNotifyRecord.STATE_ING, res);
            // 通知延时次数
            //        1   2  3  4   5   6
            //        0  30 60 90 120 150
            mqSender.send(PayOrderMchNotifyMQ.build(notifyId), currentCount * 30);

            return;
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return;
        }
    }
}
