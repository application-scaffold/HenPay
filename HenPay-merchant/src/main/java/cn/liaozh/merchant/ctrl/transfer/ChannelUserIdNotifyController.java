package cn.liaozh.merchant.ctrl.transfer;

import cn.liaozh.merchant.ctrl.CommonCtrl;
import cn.liaozh.merchant.websocket.server.WsChannelUserIdServer;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
* 获取用户ID - 回调函数
*/
@Tag(name = "商户转账回调函数")
@Controller
@RequestMapping("/api/anon/channelUserIdCallback")
public class ChannelUserIdNotifyController extends CommonCtrl {

    @Operation(summary = "（转账）获取用户ID - 回调函数")
    @Parameters({
            @Parameter(name = "extParam", description = "扩展参数"),
            @Parameter(name = "channelUserId", description = "用户userId"),
            @Parameter(name = "appId", description = "应用ID")
    })
    @RequestMapping("")
    public String channelUserIdCallback() {

        try {
            //请求参数
            JSONObject params = getReqParamJSON();

            String extParam = params.getString("extParam");
            String channelUserId = params.getString("channelUserId");
            String appId = params.getString("appId");

            //推送到前端
            WsChannelUserIdServer.sendMsgByAppAndCid(appId, extParam, channelUserId);

        } catch (Exception e) {
            request.setAttribute("errMsg", e.getMessage());
        }

        return "channelUser/getChannelUserIdPage";
    }
}
