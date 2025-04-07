package cn.liaozh.merchant.ctrl.sysuser;

import cn.liaozh.merchant.ctrl.CommonCtrl;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.SysEntitlement;
import cn.liaozh.core.model.ApiRes;
import cn.liaozh.core.utils.TreeDataBuilder;
import cn.liaozh.service.impl.SysEntitlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 权限菜单管理类
 */
@Tag(name = "系统管理（用户权限）")
@RestController
@RequestMapping("api/sysEnts")
public class SysEntController extends CommonCtrl {

	@Autowired SysEntitlementService sysEntitlementService;

	/** 查询权限集合 */
	@Operation(summary = "查询权限集合")
	@Parameters({
			@Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
	})
	@PreAuthorize("hasAnyAuthority( 'ENT_UR_ROLE_ENT_LIST', 'ENT_UR_ROLE_DIST' )")
	@RequestMapping(value="/showTree", method = RequestMethod.GET)
	public ApiRes<List<JSONObject>> showTree() {

		//查询全部数据
		List<SysEntitlement> list = sysEntitlementService.list(SysEntitlement.gw().eq(SysEntitlement::getSysType, CS.SYS_TYPE.MCH));

		//4. 转换为json树状结构
		JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(list));
		List<JSONObject> leftMenuTree = new TreeDataBuilder(jsonArray,
				"entId", "pid", "children", "entSort", true)
				.buildTreeObject();

		return ApiRes.ok(leftMenuTree);
	}

}
