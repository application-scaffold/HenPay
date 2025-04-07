package cn.liaozh.core.jwt;

import cn.liaozh.core.model.security.JeeUserDetails;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.Data;

import java.util.Map;

/*
* JWT payload 载体
* 格式：
    {
        "sysUserId": "10001",
        "created": "1568250147846",
        "cacheKey": "KEYKEYKEYKEY",
    }
*/
@Data
public class JWTPayload {

    private Long sysUserId;       //登录用户ID
    private Long created;         //创建时间, 格式：13位时间戳
    private String cacheKey;      //redis保存的key

    protected JWTPayload(){}

    public JWTPayload(JeeUserDetails jeeUserDetails){

        this.setSysUserId(jeeUserDetails.getSysUser().getSysUserId());
        this.setCreated(System.currentTimeMillis());
        this.setCacheKey(jeeUserDetails.getCacheKey());
    }

    /** toMap **/
    public Map<String, Object> toMap(){
        // 直接解析对象为 Map
        return JSON.parseObject(JSON.toJSONString(this), new TypeReference<Map<String, Object>>() {});
    }

}
