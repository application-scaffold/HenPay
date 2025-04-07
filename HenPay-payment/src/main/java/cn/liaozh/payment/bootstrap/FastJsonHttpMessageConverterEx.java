package cn.liaozh.payment.bootstrap;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

public class FastJsonHttpMessageConverterEx extends FastJsonHttpMessageConverter {

    public FastJsonHttpMessageConverterEx() {
        super();
        this.getFastJsonConfig().getSerializeConfig().put(byte[].class, SwaggerJsonSerializer.instance);
    }

}
