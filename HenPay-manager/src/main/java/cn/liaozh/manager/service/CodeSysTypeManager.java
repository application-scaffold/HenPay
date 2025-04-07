package cn.liaozh.manager.service;

import cn.liaozh.core.constants.CS;
import cn.liaozh.core.service.ICodeSysTypeManager;
import org.springframework.stereotype.Component;

@Component
public class CodeSysTypeManager implements ICodeSysTypeManager {

    @Override
    public String getCodeSysName() {
        return CS.CODE_SYS_NAME_SET.JEEPAY_MANAGER;
    }
}
