package cn.liaozh.payment.ctrl.qr;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import cn.liaozh.core.constants.CS;
import cn.liaozh.core.exception.BizException;
import cn.liaozh.core.utils.HenPayKit;
import cn.liaozh.core.utils.SpringBeansUtil;
import cn.liaozh.core.utils.StringKit;
import cn.liaozh.payment.channel.IChannelUserService;
import cn.liaozh.payment.ctrl.payorder.AbstractPayOrderController;
import cn.liaozh.payment.model.MchAppConfigContext;
import cn.liaozh.payment.rqrs.ChannelUserIdRQ;
import cn.liaozh.payment.service.ConfigContextQueryService;
import cn.liaozh.service.impl.SysConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
* 商户获取渠道用户ID接口
*/
@RestController
@RequestMapping("/api/channelUserId")
public class ChannelUserIdController extends AbstractPayOrderController {

    @Autowired private ConfigContextQueryService configContextQueryService;
    @Autowired private SysConfigService sysConfigService;

    /**  重定向到微信地址  **/
    @RequestMapping("/jump")
    public void jump() throws Exception {

        //获取请求数据
        ChannelUserIdRQ rq = getRQByWithMchSign(ChannelUserIdRQ.class);

        String ifCode = "AUTO".equalsIgnoreCase(rq.getIfCode()) ? getIfCodeByUA() : rq.getIfCode();

        // 获取接口
        IChannelUserService channelUserService = SpringBeansUtil.getBean(ifCode + "ChannelUserService", IChannelUserService.class);

        if(channelUserService == null){
            throw new BizException("不支持的客户端");
        }

        if(!StringKit.isAvailableUrl(rq.getRedirectUrl())){
            throw new BizException("跳转地址有误！");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mchNo", rq.getMchNo());
        jsonObject.put("appId", rq.getAppId());
        jsonObject.put("extParam", rq.getExtParam());
        jsonObject.put("ifCode", ifCode);
        jsonObject.put("redirectUrl", rq.getRedirectUrl());

        //回调地址
        String callbackUrl = sysConfigService.getDBApplicationConfig().genMchChannelUserIdApiOauth2RedirectUrlEncode(jsonObject);

        //获取商户配置信息
        MchAppConfigContext mchAppConfigContext = configContextQueryService.queryMchInfoAndAppInfo(rq.getMchNo(), rq.getAppId());
        String redirectUrl = channelUserService.buildUserRedirectUrl(callbackUrl, mchAppConfigContext);
        response.sendRedirect(redirectUrl);

    }


    /**  回调地址  **/
    @RequestMapping("/oauth2Callback/{aesData}")
    public void oauth2Callback(@PathVariable("aesData") String aesData) throws Exception {

        JSONObject callbackData = JSON.parseObject(HenPayKit.aesDecode(aesData));

        String mchNo = callbackData.getString("mchNo");
        String appId = callbackData.getString("appId");
        String ifCode = callbackData.getString("ifCode");
        String extParam = callbackData.getString("extParam");
        String redirectUrl = callbackData.getString("redirectUrl");

        // 获取接口
        IChannelUserService channelUserService = SpringBeansUtil.getBean(ifCode + "ChannelUserService", IChannelUserService.class);

        if(channelUserService == null){
            throw new BizException("不支持的客户端");
        }

        //获取商户配置信息
        MchAppConfigContext mchAppConfigContext = configContextQueryService.queryMchInfoAndAppInfo(mchNo, appId);

        //获取渠道用户ID
        String channelUserId = channelUserService.getChannelUserId(getReqParamJSON(), mchAppConfigContext);

        //同步跳转
        JSONObject appendParams = new JSONObject();
        appendParams.put("appId", appId);
        appendParams.put("channelUserId", channelUserId);
        appendParams.put("extParam", extParam);
        response.sendRedirect(StringKit.appendUrlQuery(redirectUrl, appendParams));
    }


    /** 根据UA获取支付接口 */
    private String getIfCodeByUA() {

        String ua = request.getHeader("User-Agent");

        // 无法识别扫码客户端
        if (StringUtils.isBlank(ua)) {
            return null;
        }

        if(ua.contains("Alipay")) {
            return CS.IF_CODE.ALIPAY;  //支付宝服务窗支付
        }else if(ua.contains("MicroMessenger")) {
            return CS.IF_CODE.WXPAY;
        }
        return null;
    }
}
