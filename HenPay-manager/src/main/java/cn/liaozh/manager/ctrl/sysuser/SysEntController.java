package cn.liaozh.manager.ctrl.sysuser;

import cn.liaozh.manager.ctrl.CommonCtrl;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import cn.liaozh.core.aop.MethodLog;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
* 权限 菜单 管理
*/
@Tag(name = "系统管理（用户权限）")
@RestController
@RequestMapping("api/sysEnts")
public class SysEntController extends CommonCtrl {

	@Autowired SysEntitlementService sysEntitlementService;


	/** getOne */
	@Operation(summary = "查询菜单权限详情")
	@Parameters({
			@Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
			@Parameter(name = "entId", description = "权限ID[ENT_功能模块_子模块_操作], eg: ENT_ROLE_LIST_ADD", required = true),
			@Parameter(name = "sysType", description = "所属系统： MGR-运营平台, MCH-商户中心", required = true)
	})
	@PreAuthorize("hasAnyAuthority( 'ENT_UR_ROLE_ENT_LIST' )")
	@RequestMapping(value="/bySysType", method = RequestMethod.GET)
	public ApiRes<SysEntitlement> bySystem() {

		return ApiRes.ok(sysEntitlementService.getOne(SysEntitlement.gw()
				.eq(SysEntitlement::getEntId, getValStringRequired("entId"))
				.eq(SysEntitlement::getSysType, getValStringRequired("sysType")))
		);
	}

	/** updateById */
	@Operation(summary = "更新权限资源")
	@Parameters({
			@Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
			@Parameter(name = "entId", description = "权限ID[ENT_功能模块_子模块_操作], eg: ENT_ROLE_LIST_ADD", required = true),
			@Parameter(name = "entName", description = "权限名称", required = true),
			@Parameter(name = "menuUri", description = "菜单uri/路由地址"),
			@Parameter(name = "entSort", description = "排序字段, 规则：正序"),
			@Parameter(name = "quickJump", description = "快速开始菜单 0-否, 1-是"),
			@Parameter(name = "state", description = "状态 0-停用, 1-启用")
	})
	@PreAuthorize("hasAuthority( 'ENT_UR_ROLE_ENT_EDIT')")
	@MethodLog(remark = "更新资源权限")
	@RequestMapping(value="/{entId}", method = RequestMethod.PUT)
	public ApiRes updateById(@PathVariable("entId") String entId) {

		SysEntitlement queryObject = getObject(SysEntitlement.class);
		sysEntitlementService.update(queryObject, SysEntitlement.gw().eq(SysEntitlement::getEntId, entId).eq(SysEntitlement::getSysType, queryObject.getSysType()));
		return ApiRes.ok();
	}


	/** 查询权限集合 */
	@Operation(summary = "查询权限集合")
	@Parameters({
			@Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
			@Parameter(name = "sysType", description = "所属系统： MGR-运营平台, MCH-商户中心", required = true)
	})
	@PreAuthorize("hasAnyAuthority( 'ENT_UR_ROLE_ENT_LIST', 'ENT_UR_ROLE_DIST' )")
	@RequestMapping(value="/showTree", method = RequestMethod.GET)
	public ApiRes<List<JSONObject>> showTree() {

		//查询全部数据
		List<SysEntitlement> list = sysEntitlementService.list(SysEntitlement.gw().eq(SysEntitlement::getSysType, getValStringRequired("sysType")));

		//转换为json树状结构
		JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(list));
		List<JSONObject> leftMenuTree = new TreeDataBuilder(jsonArray,
				"entId", "pid", "children", "entSort", true)
				.buildTreeObject();

		return ApiRes.ok(leftMenuTree);
	}
}
