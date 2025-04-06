package cn.liaozh.core.service;

import cn.liaozh.core.entity.PayOrder;

/***
* 码牌相关逻辑
*/
public interface IMchQrcodeManager {

    /**
     * 功能描述: 查询商户配置信息
     */
    PayOrder queryMchInfoByQrc(String id);

}
