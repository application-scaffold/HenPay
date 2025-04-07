package cn.liaozh.merchant.ctrl.merchant;

import cn.liaozh.merchant.ctrl.CommonCtrl;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import cn.liaozh.core.entity.MchInfo;
import cn.liaozh.core.entity.SysUser;
import cn.liaozh.core.model.ApiRes;
import cn.liaozh.service.impl.MchInfoService;
import cn.liaozh.service.impl.PayOrderService;
import cn.liaozh.service.impl.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 主页数据类
 */
@Tag(name = "主页统计")
@Slf4j
@RestController
@RequestMapping("api/mainChart")
public class MainChartController extends CommonCtrl {

    @Autowired private PayOrderService payOrderService;

    @Autowired private SysUserService sysUserService;

    @Autowired private MchInfoService mchInfoService;


    /** 周交易总金额 */
    @Operation(summary = "周交易总金额",description = "")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER)
    })
    @PreAuthorize("hasAuthority('ENT_MCH_MAIN_PAY_AMOUNT_WEEK')")
    @RequestMapping(value="/payAmountWeek", method = RequestMethod.GET)
    public ApiRes payAmountWeek() {
        return ApiRes.ok(payOrderService.mainPageWeekCount(getCurrentMchNo()));
    }

    /**
     * 商户总数量、服务商总数量、总交易金额、总交易笔数
     * @return
     */
    @Operation(summary = "商户总数量、服务商总数量、总交易金额、总交易笔数", description = "")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER)
    })
    @PreAuthorize("hasAuthority('ENT_MCH_MAIN_NUMBER_COUNT')")
    @RequestMapping(value="/numCount", method = RequestMethod.GET)
    public ApiRes numCount() {
        return ApiRes.ok(payOrderService.mainPageNumCount(getCurrentMchNo()));
    }

    /** 交易统计 */
    @Operation(summary = "交易统计", description = "")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "createdStart", description = "日期格式字符串（yyyy-MM-dd），时间范围查询--开始时间，须和结束时间一起使用，否则默认查最近七天（含今天）"),
            @Parameter(name = "createdEnd", description = "日期格式字符串（yyyy-MM-dd），时间范围查询--结束时间，须和开始时间一起使用，否则默认查最近七天（含今天）")
    })
    @PreAuthorize("hasAuthority('ENT_MCH_MAIN_PAY_COUNT')")
    @RequestMapping(value="/payCount", method = RequestMethod.GET)
    public ApiRes<List<Map>> payCount() {
        // 获取传入参数
        JSONObject paramJSON = getReqParamJSON();
        String createdStart = paramJSON.getString("createdStart");
        String createdEnd = paramJSON.getString("createdEnd");

        List<Map> mapList = payOrderService.mainPagePayCount(getCurrentMchNo(), createdStart, createdEnd);
        //返回数据
        return ApiRes.ok(mapList);
    }

    /** 支付方式统计 */
    @Operation(summary = "支付方式统计", description = "")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "createdStart", description = "日期格式字符串（yyyy-MM-dd），时间范围查询--开始时间，须和结束时间一起使用，否则默认查最近七天（含今天）"),
            @Parameter(name = "createdEnd", description = "日期格式字符串（yyyy-MM-dd），时间范围查询--结束时间，须和开始时间一起使用，否则默认查最近七天（含今天）")
    })
    @PreAuthorize("hasAuthority('ENT_MCH_MAIN_PAY_TYPE_COUNT')")
    @RequestMapping(value="/payTypeCount", method = RequestMethod.GET)
    public ApiRes<ArrayList> payWayCount() {
        JSONObject paramJSON = getReqParamJSON();
        // 开始、结束时间
        String createdStart = paramJSON.getString("createdStart");
        String createdEnd = paramJSON.getString("createdEnd");
        ArrayList arrayResult = payOrderService.mainPagePayTypeCount(getCurrentMchNo(), createdStart, createdEnd);
        return ApiRes.ok(arrayResult);
    }

    /** 商户基本信息、用户基本信息 **/
    @Operation(summary = "商户基本信息", description = "")
    @Parameters({
            @Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
    })
    @PreAuthorize("hasAuthority('ENT_MCH_MAIN_USER_INFO')")
    @RequestMapping(value="", method = RequestMethod.GET)
    public ApiRes userDetail() {
        SysUser sysUser = sysUserService.getById(getCurrentUser().getSysUser().getSysUserId());
        MchInfo mchInfo = mchInfoService.getById(getCurrentMchNo());
        JSONObject json = (JSONObject) JSON.toJSON(mchInfo);
        json.put("loginUsername", sysUser.getLoginUsername());
        json.put("realname", sysUser.getRealname());
        return ApiRes.ok(json);
    }
}
