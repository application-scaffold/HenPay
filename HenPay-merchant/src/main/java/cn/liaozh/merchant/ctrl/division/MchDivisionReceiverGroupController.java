package cn.liaozh.merchant.ctrl.division;

import cn.liaozh.merchant.ctrl.CommonCtrl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.liaozh.core.aop.MethodLog;
import cn.liaozh.core.constants.ApiCodeEnum;
import cn.liaozh.core.constants.CS;
import cn.liaozh.core.entity.MchDivisionReceiver;
import cn.liaozh.core.entity.MchDivisionReceiverGroup;
import cn.liaozh.core.exception.BizException;
import cn.liaozh.core.model.ApiPageRes;
import cn.liaozh.core.model.ApiRes;
import cn.liaozh.service.impl.MchDivisionReceiverGroupService;
import cn.liaozh.service.impl.MchDivisionReceiverService;
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
 * 商户分账接收者账号组
 */
@Tag(name = "分账管理（账号组）")
@RestController
@RequestMapping("api/divisionReceiverGroups")
public class MchDivisionReceiverGroupController extends CommonCtrl {

	@Autowired private MchDivisionReceiverGroupService mchDivisionReceiverGroupService;
	@Autowired private MchDivisionReceiverService mchDivisionReceiverService;

	/** list */
	@Operation(summary = "账号组列表")
	@Parameters({
			@Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
			@Parameter(name = "pageNumber", description = "分页页码"),
			@Parameter(name = "pageSize", description = "分页条数（-1时查全部数据）"),
			@Parameter(name = "receiverGroupId", description = "账号组ID"),
			@Parameter(name = "receiverGroupName", description = "组名称")
	})
	@PreAuthorize("hasAnyAuthority( 'ENT_DIVISION_RECEIVER_GROUP_LIST' )")
	@RequestMapping(value="", method = RequestMethod.GET)
	public ApiPageRes<MchDivisionReceiverGroup> list() {

		MchDivisionReceiverGroup queryObject = getObject(MchDivisionReceiverGroup.class);

		LambdaQueryWrapper<MchDivisionReceiverGroup> condition = MchDivisionReceiverGroup.gw();
		condition.eq(MchDivisionReceiverGroup::getMchNo, getCurrentMchNo());

		if(StringUtils.isNotEmpty(queryObject.getReceiverGroupName())){
			condition.like(MchDivisionReceiverGroup::getReceiverGroupName, queryObject.getReceiverGroupName());
		}

		if(queryObject.getReceiverGroupId() != null){
			condition.eq(MchDivisionReceiverGroup::getReceiverGroupId, queryObject.getReceiverGroupId());
		}

		condition.orderByDesc(MchDivisionReceiverGroup::getCreatedAt); //时间倒序

		IPage<MchDivisionReceiverGroup> pages = mchDivisionReceiverGroupService.page(getIPage(true), condition);
		return ApiPageRes.pages(pages);
	}


	/** detail */
	@Operation(summary = "账号组详情")
	@Parameters({
			@Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
			@Parameter(name = "recordId", description = "账号组ID", required = true)
	})
	@PreAuthorize("hasAuthority( 'ENT_DIVISION_RECEIVER_GROUP_VIEW' )")
	@RequestMapping(value="/{recordId}", method = RequestMethod.GET)
	public ApiRes<MchDivisionReceiverGroup> detail(@PathVariable("recordId") Long recordId) {
		MchDivisionReceiverGroup record = mchDivisionReceiverGroupService
				.getOne(MchDivisionReceiverGroup.gw()
						.eq(MchDivisionReceiverGroup::getMchNo, getCurrentMchNo())
						.eq(MchDivisionReceiverGroup::getReceiverGroupId, recordId));
		if (record == null) {
            throw new BizException(ApiCodeEnum.SYS_OPERATION_FAIL_SELETE);
        }
		return ApiRes.ok(record);
	}

	/** add */
	@Operation(summary = "新增分账账号组")
	@Parameters({
			@Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
			@Parameter(name = "autoDivisionFlag", description = "自动分账组（当订单分账模式为自动分账，改组将完成分账逻辑） 0-否 1-是", required = true),
			@Parameter(name = "receiverGroupName", description = "组名称", required = true)
	})
	@PreAuthorize("hasAuthority( 'ENT_DIVISION_RECEIVER_GROUP_ADD' )")
	@RequestMapping(value="", method = RequestMethod.POST)
	@MethodLog(remark = "新增分账账号组")
	public ApiRes add() {
		MchDivisionReceiverGroup record = getObject(MchDivisionReceiverGroup.class);
		record.setMchNo(getCurrentMchNo());
		record.setCreatedUid(getCurrentUser().getSysUser().getSysUserId());
		record.setCreatedBy(getCurrentUser().getSysUser().getRealname());
		if(mchDivisionReceiverGroupService.save(record)){

			//更新其他组为非默认分账组
			if(record.getAutoDivisionFlag() == CS.YES){
				mchDivisionReceiverGroupService.update(new LambdaUpdateWrapper<MchDivisionReceiverGroup>()
						.set(MchDivisionReceiverGroup::getAutoDivisionFlag, CS.NO)
						.eq(MchDivisionReceiverGroup::getMchNo, getCurrentMchNo())
						.ne(MchDivisionReceiverGroup::getReceiverGroupId, record.getReceiverGroupId())
				);
			}
		}
		return ApiRes.ok();
	}

	/** update */
	@Operation(summary = "更新分账账号组")
	@Parameters({
			@Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
			@Parameter(name = "recordId", description = "账号组ID", required = true),
			@Parameter(name = "autoDivisionFlag", description = "自动分账组（当订单分账模式为自动分账，改组将完成分账逻辑） 0-否 1-是", required = true),
			@Parameter(name = "receiverGroupName", description = "组名称", required = true)
	})
	@PreAuthorize("hasAuthority( 'ENT_DIVISION_RECEIVER_GROUP_EDIT' )")
	@RequestMapping(value="/{recordId}", method = RequestMethod.PUT)
	@MethodLog(remark = "更新分账账号组")
	public ApiRes update(@PathVariable("recordId") Long recordId) {

		MchDivisionReceiverGroup reqRecord = getObject(MchDivisionReceiverGroup.class);

		MchDivisionReceiverGroup record = new MchDivisionReceiverGroup();
		record.setReceiverGroupName(reqRecord.getReceiverGroupName());
		record.setAutoDivisionFlag(reqRecord.getAutoDivisionFlag());

		LambdaUpdateWrapper<MchDivisionReceiverGroup> updateWrapper = new LambdaUpdateWrapper<>();
		updateWrapper.eq(MchDivisionReceiverGroup::getReceiverGroupId, recordId);
		updateWrapper.eq(MchDivisionReceiverGroup::getMchNo, getCurrentMchNo());

		if(mchDivisionReceiverGroupService.update(record, updateWrapper)){

			//更新其他组为非默认分账组
			if(record.getAutoDivisionFlag() == CS.YES){
				mchDivisionReceiverGroupService.update(new LambdaUpdateWrapper<MchDivisionReceiverGroup>()
				.set(MchDivisionReceiverGroup::getAutoDivisionFlag, CS.NO)
						.eq(MchDivisionReceiverGroup::getMchNo, getCurrentMchNo())
						.ne(MchDivisionReceiverGroup::getReceiverGroupId, recordId)
				);
			}
		}

		return ApiRes.ok();
	}

	/** delete */
	@Operation(summary = "删除分账账号组")
	@Parameters({
			@Parameter(name = "iToken", description = "用户身份凭证", required = true, in = ParameterIn.HEADER),
			@Parameter(name = "recordId", description = "账号组ID", required = true)
	})
	@PreAuthorize("hasAuthority('ENT_DIVISION_RECEIVER_GROUP_DELETE')")
	@RequestMapping(value="/{recordId}", method = RequestMethod.DELETE)
	@MethodLog(remark = "删除分账账号组")
	public ApiRes del(@PathVariable("recordId") Long recordId) {
		MchDivisionReceiverGroup record = mchDivisionReceiverGroupService.getOne(MchDivisionReceiverGroup.gw()
				.eq(MchDivisionReceiverGroup::getReceiverGroupId, recordId).eq(MchDivisionReceiverGroup::getMchNo, getCurrentMchNo()));
		if (record == null) {
            throw new BizException(ApiCodeEnum.SYS_OPERATION_FAIL_SELETE);
        }

		if( mchDivisionReceiverService.count(MchDivisionReceiver.gw().eq(MchDivisionReceiver::getReceiverGroupId, recordId)) > 0){
			throw new BizException("该组存在账号，无法删除");
		}

		mchDivisionReceiverGroupService.removeById(recordId);
		return ApiRes.ok();
	}


}
