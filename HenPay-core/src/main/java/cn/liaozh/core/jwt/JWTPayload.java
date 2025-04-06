package cn.liaozh.core.jwt;

import cn.liaozh.model.security.JeeUserDetails;
import com.alibaba.fastjson.JSONObject;
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
        JSONObject json = (JSONObject)JSONObject.toJSON(this);
        return json.toJavaObject(Map.class);
    }

}
