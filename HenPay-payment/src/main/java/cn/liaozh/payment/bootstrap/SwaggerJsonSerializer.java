package cn.liaozh.payment.bootstrap;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import io.swagger.v3.oas.models.OpenAPI;

import java.lang.reflect.Type;

// 实现FastJson序列号接口
public class SwaggerJsonSerializer implements ObjectSerializer, ObjectDeserializer {

    public final static SwaggerJsonSerializer instance = new SwaggerJsonSerializer();

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
        SerializeWriter out = serializer.getWriter();
        byte[] byteArr = (byte[]) object;

        try {
            String result = new String(byteArr);
            // OpenAPI openAPI = JSON.parseObject(result, OpenAPI.class);
            // if (openAPI != null && new OpenAPI().getOpenapi().equals(openAPI.getOpenapi())) {
            //     out.write(result);
            // }

            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject != null && new OpenAPI().getOpenapi().equals(jsonObject.getString("openapi"))) {
                out.write(result);
            }

        } catch (Exception e) {
//            out.writeByteArray(byteArr);
            String base64Str = Base64.encode(byteArr);
            out.writeString(base64Str);
        }
    }

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        return null;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
