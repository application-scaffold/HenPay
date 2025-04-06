package cn.liaozh.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.liaozh.core.entity.MchNotifyRecord;
import org.apache.ibatis.annotations.Param;

/**
 * 商户通知表 Mapper 接口
 */
public interface MchNotifyRecordMapper extends BaseMapper<MchNotifyRecord> {

    Integer updateNotifyResult(@Param("notifyId") Long notifyId, @Param("state") Byte state, @Param("resResult") String resResult);

    /*
     * 功能描述: 更改为通知中 & 增加允许重发通知次数
     * @param notifyId
     */
    Integer updateIngAndAddNotifyCountLimit(@Param("notifyId") Long notifyId);

}
