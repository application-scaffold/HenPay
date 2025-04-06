package cn.liaozh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.liaozh.core.entity.MchDivisionReceiverGroup;
import cn.liaozh.service.mapper.MchDivisionReceiverGroupMapper;
import org.springframework.stereotype.Service;

/**
 * 分账账号组 服务实现类
 */
@Service
public class MchDivisionReceiverGroupService extends ServiceImpl<MchDivisionReceiverGroupMapper, MchDivisionReceiverGroup> {

    /** 根据ID和商户号查询 **/
    public MchDivisionReceiverGroup findByIdAndMchNo(Long groupId, String mchNo){
        return getOne(MchDivisionReceiverGroup.gw().eq(MchDivisionReceiverGroup::getReceiverGroupId, groupId).eq(MchDivisionReceiverGroup::getMchNo, mchNo));
    }

}
