package cn.liaozh.core.utils;

import com.alibaba.fastjson2.JSONObject;

/*
* json工具类
*/
public class JsonKit {

	public static JSONObject newJson(String key, Object val){

		JSONObject result = new JSONObject();
		result.put(key, val);
		return result;
	}

}
