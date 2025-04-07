package cn.liaozh.manager.ctrl.sysuser;

import cn.liaozh.manager.ctrl.CommonCtrl;
import cn.liaozh.manager.service.AuthService;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.liaozh.core.aop.MethodLog;
import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.SysRole;
import cn.liaozh.core.entity.SysUserRoleRela;
import cn.liaozh.core.exception.BizException;
import cn.liaozh.core.model.ApiPageRes;
import cn.liaozh.core.model.ApiRes;
import cn.liaozh.core.utils.StringKit;
import cn.liaozh.service.impl.SysRoleEntRelaService;
import cn.liaozh.service.impl.SysRoleService;
import cn.liaozh.service.impl.SysUserRoleRelaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/*
* 角色管理
*/
@Tag(name = "系统管理（用户角色）")
@RestController
@RequestMapping("api/sysRoles")
public class SysRoleController extends CommonCtrl {

	@Autowired SysRoleService sysRoleService;
	@Autowired SysUserRoleRelaService sysUserRoleRelaService;
	@Autowired private AuthService authService;
	@Autowired private SysRoleEntRelaService sysRoleEntRelaService;


	/** list */
	@Operation(summary = "角色列表")
	@Parameters({
			@Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
			@Parameter(name = "pageNumber", description = "分页页码"),
			@Parameter(name = "pageSize", description = "分页条数（-1时查全部数据）"),
			@Parameter(name = "roleId", description = "角色ID, ROLE_开头"),
			@Parameter(name = "roleName", description = "角色名称")
	})
	@PreAuthorize("hasAnyAuthority( 'ENT_UR_ROLE_LIST', 'ENT_UR_USER_UPD_ROLE' )")
	@RequestMapping(value="", method = RequestMethod.GET)
	public ApiPageRes<SysRole> list() {

		SysRole queryObject = getObject(SysRole.class);

		QueryWrapper<SysRole> condition = new QueryWrapper<>();
		LambdaQueryWrapper<SysRole> lambdaCondition = condition.lambda();
		lambdaCondition.eq(SysRole::getSysType, CS.SYS_TYPE.MGR);
		lambdaCondition.eq(SysRole::getBelongInfoId, 0);

		if(StringUtils.isNotEmpty(queryObject.getRoleName())){
			lambdaCondition.like(SysRole::getRoleName, queryObject.getRoleName());
		}

		if(StringUtils.isNotEmpty(queryObject.getRoleId())){
			lambdaCondition.like(SysRole::getRoleId, queryObject.getRoleId());
		}

		//是否有排序字段
		MutablePair<Boolean, String> orderInfo = getSortInfo();
		if(orderInfo != null){
			condition.orderBy(true, orderInfo.getLeft(), orderInfo.getRight());
		}else{
			lambdaCondition.orderByDesc(SysRole::getUpdatedAt);
		}

		IPage<SysRole> pages = sysRoleService.page(getIPage(true), condition);
		return ApiPageRes.pages(pages);
	}


	/** detail */
	@Operation(summary = "角色详情")
	@Parameters({
			@Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
			@Parameter(name = "recordId", description = "角色ID, ROLE_开头", required = true)
	})
	@PreAuthorize("hasAuthority( 'ENT_UR_ROLE_EDIT' )")
	@RequestMapping(value="/{recordId}", method = RequestMethod.GET)
	public ApiRes<SysRole> detail(@PathVariable("recordId") String recordId) {
		return ApiRes.ok(sysRoleService.getById(recordId));
	}

	/** add */
	@Operation(summary = "添加角色信息")
	@Parameters({
			@Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
			@Parameter(name = "roleName", description = "角色名称", required = true),
			@Parameter(name = "entIdListStr", description = "权限信息集合，eg：[str1,str2]，字符串列表转成json字符串，若为空，则创建的角色无任何权限")
	})
	@PreAuthorize("hasAuthority( 'ENT_UR_ROLE_ADD' )")
	@MethodLog(remark = "添加角色信息")
	@RequestMapping(value="", method = RequestMethod.POST)
	public ApiRes add() {
		SysRole SysRole = getObject(SysRole.class);
		String roleId = "ROLE_" + StringKit.getUUID(6);
		SysRole.setRoleId(roleId);
		SysRole.setSysType(CS.SYS_TYPE.MGR); //后台系统
		sysRoleService.save(SysRole);

		//权限信息集合
		String entIdListStr =  getValString("entIdListStr");

		//如果包含： 可分配权限的权限 && entIdListStr 不为空
		if(getCurrentUser().getAuthorities().contains(new SimpleGrantedAuthority("ENT_UR_ROLE_DIST"))
				&& StringUtils.isNotEmpty(entIdListStr)){
			List<String> entIdList = JSONArray.parseArray(entIdListStr, String.class);

			sysRoleEntRelaService.resetRela(roleId, entIdList);
		}

		return ApiRes.ok();
	}

	/** update */
	@Operation(summary = "更新角色信息")
	@Parameters({
			@Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
			@Parameter(name = "recordId", description = "角色ID, ROLE_开头", required = true),
			@Parameter(name = "roleName", description = "角色名称", required = true),
			@Parameter(name = "entIdListStr", description = "权限信息集合，eg：[str1,str2]，字符串列表转成json字符串，若为空，则创建的角色无任何权限")
	})
	@PreAuthorize("hasAuthority( 'ENT_UR_ROLE_EDIT' )")
	@RequestMapping(value="/{recordId}", method = RequestMethod.PUT)
	@MethodLog(remark = "更新角色信息")
	public ApiRes update(@PathVariable("recordId") String recordId) {
		SysRole SysRole = getObject(SysRole.class);
		SysRole.setRoleId(recordId);
		sysRoleService.updateById(SysRole);

		//权限信息集合
		String entIdListStr =  getValString("entIdListStr");

		//如果包含： 可分配权限的权限 && entIdListStr 不为空
		if(getCurrentUser().getAuthorities().contains(new SimpleGrantedAuthority("ENT_UR_ROLE_DIST"))
				&& StringUtils.isNotEmpty(entIdListStr)){
			List<String> entIdList = JSONArray.parseArray(entIdListStr, String.class);

			sysRoleEntRelaService.resetRela(recordId, entIdList);

			List<Long> sysUserIdList = new ArrayList<>();
			sysUserRoleRelaService.list(SysUserRoleRela.gw().eq(SysUserRoleRela::getRoleId, recordId)).stream().forEach(item -> sysUserIdList.add(item.getUserId()));

			//查询到该角色的人员， 将redis更新
			authService.refAuthentication(sysUserIdList);
		}

		return ApiRes.ok();
	}

	/** delete */
	@Operation(summary = "删除角色")
	@Parameters({
			@Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
			@Parameter(name = "recordId", description = "角色ID, ROLE_开头", required = true)
	})
	@PreAuthorize("hasAuthority('ENT_UR_ROLE_DEL')")
	@MethodLog(remark = "删除角色")
	@RequestMapping(value="/{recordId}", method = RequestMethod.DELETE)
	public ApiRes del(@PathVariable("recordId") String recordId) {

		if(sysUserRoleRelaService.count(SysUserRoleRela.gw().eq(SysUserRoleRela::getRoleId, recordId)) > 0){
			throw new BizException("当前角色已分配到用户, 不可删除！");
		}
		sysRoleService.removeRole(recordId);
		return ApiRes.ok();
	}

}
