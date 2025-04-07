package cn.liaozh.payment.service;

import cn.liaozh.core.exception.BizException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/*
* 通用 Validator
*/
@Service
public class ValidateService {

    @Autowired private Validator validator;

    public void validate(Object obj){

        Set<ConstraintViolation<Object>> resultSet = validator.validate(obj);
        if(resultSet == null || resultSet.isEmpty()){
            return ;
        }
        resultSet.stream().forEach(item -> {throw new BizException(item.getMessage());});
    }

}
