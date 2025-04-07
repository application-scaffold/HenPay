package cn.liaozh.merchant.ctrl.transfer;

import cn.liaozh.merchant.ctrl.CommonCtrl;
import cn.liaozh.merchant.websocket.server.WsTransferOrderServer;
import com.alibaba.fastjson2.JSONObject;
import cn.liaozh.core.entity.MchApp;
import cn.liaozh.service.impl.MchAppService;
import cn.liaozh.core.utils.HenPayKit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/*
* 转账 - 回调函数
*/
@Tag(name = "商户转账")
@RestController
@RequestMapping("/api/anon/transferNotify")
public class TransferNotifyController extends CommonCtrl {

    @Autowired private MchAppService mchAppService;

    @Operation(summary = "转账回调信息")
    @Parameters({
            @Parameter(name = "appId", description = "应用ID", required = true),
            @Parameter(name = "mchNo", description = "商户号", required = true),
            @Parameter(name = "sign", description = "签名值", required = true)
    })
    @RequestMapping("/transferOrder")
    public void transferOrderNotify() throws IOException {

        //请求参数
        JSONObject params = getReqParamJSON();

        String mchNo = params.getString("mchNo");
        String appId = params.getString("appId");
        String sign = params.getString("sign");
        MchApp mchApp = mchAppService.getById(appId);
        if(mchApp == null || !mchApp.getMchNo().equals(mchNo)){
            response.getWriter().print("app is not exists");
            return;
        }

        params.remove("sign");
        if(!HenPayKit.getSign(params, mchApp.getAppSecret()).equalsIgnoreCase(sign)){
            response.getWriter().print("sign fail");
            return;
        }

        JSONObject msg = new JSONObject();
        msg.put("state", params.getIntValue("state"));
        msg.put("errCode", params.getString("errCode"));
        msg.put("errMsg", params.getString("errMsg"));

        //推送到前端
        WsTransferOrderServer.sendMsgBytransferId(params.getString("transferId"), msg.toJSONString());

        response.getWriter().print("SUCCESS");
    }

}
