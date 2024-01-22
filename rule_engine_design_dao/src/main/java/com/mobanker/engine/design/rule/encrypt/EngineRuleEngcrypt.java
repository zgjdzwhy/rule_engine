package com.mobanker.engine.design.rule.encrypt;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年1月18日
 * @version 1.0
 */
public interface EngineRuleEngcrypt {
	/**
	 * 加密
	 * @param json
	 * @return
	 */
	public JSONObject encrypt(JSONObject json);
	/**
	 * 解密
	 * @param json
	 * @return
	 */
	public JSONObject descrypt(JSONObject json);
}
