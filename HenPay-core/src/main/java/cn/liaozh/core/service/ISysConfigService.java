package cn.liaozh.core.service;

import cn.liaozh.core.model.DBApplicationConfig;

public interface ISysConfigService {

    /** 获取应用的配置参数 **/
    DBApplicationConfig getDBApplicationConfig();

}
