package com.mobanker.engine.design.rule.encrypt;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.zkc.util.EncryptUtils;


/**
 * 本地加密方法，用于测试环境
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年1月18日
 * @version 1.0
 */
@Service("engineLocalRuleEngcryptImpl")
public class EngineLocalRuleEngcryptImpl implements EngineRuleEngcrypt {

	private static final String ENCRYPT_KEY = "taojin12";
	
	@Override
	public JSONObject encrypt(JSONObject json) {
		JSONObject temp = new JSONObject(json);
		
		JSONObject result = new JSONObject();
		result.put("id", temp.remove("id"));
		result.put("name", temp.remove("name"));
		result.put("type", temp.remove("type"));
		result.put("right", temp.remove("right"));
		result.put("version", temp.remove("version"));
		result.put("updatetime", temp.remove("updatetime"));		
		
		String content = temp.toJSONString(); 		
		content = EncryptUtils.encryptMode(content, ENCRYPT_KEY);
		result.put("content", content);
		return result;
	}

	@Override
	public JSONObject descrypt(JSONObject json) {
		String content = json.getString("content");
		content = EncryptUtils.decryptMode(content, ENCRYPT_KEY);
		JSONObject jsonContent = JSONObject.parseObject(content);
		
		json.remove("content");
		json.putAll(jsonContent);		
		
		return json;
	}

}
