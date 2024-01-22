package com.mobanker.engine.design.rule.encrypt;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.common.exception.BusinessException;
import com.mobanker.framework.dto.ResponseEntity;
import com.mobanker.safe.business.contract.DecryptManager;
import com.mobanker.safe.business.contract.EncryptManager;
import com.mobanker.safe.business.dto.DecryptRequest;
import com.mobanker.safe.business.dto.EncryptRequest;
import com.mobanker.safe.business.dto.EncryptResponse;

/**
 * 前隆加密方法，用于前隆生产环境
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年1月18日
 * @version 1.0
 */
@Service("engineQlRuleEngcryptImpl")
public class EngineQlRuleEngcryptImpl implements EngineRuleEngcrypt {

	private Logger logger = LoggerFactory.getLogger(this.getClass());		
	
	@Autowired
	private EncryptManager encryptManager;
	
	@Autowired
	private DecryptManager decryptManager;
	
	@SuppressWarnings("deprecation")
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
		
		EncryptRequest request = new EncryptRequest();
		request.setBusinessCode("qianlong");
		request.setType("_3DES");
		request.setData(content);
		request.setBusinessType("dataEncrypt");

		ResponseEntity<EncryptResponse> res = encryptManager.encrypt(request);
		
		String errorCode = res.getError();
		if(!StringUtils.equals(errorCode, "00000000")){
			throw new BusinessException("加密系统返回状态失败,errorCode="+errorCode);
		}
		
		
		String ciphertext = res.getData().getCiphertext();
		Long businessId = res.getData().getBusinessId();
		
		logger.debug("加密完成,businessId:{}",businessId);
		result.put("content", ciphertext);
		result.put("businessId", businessId);
		return result;		
	}

	@SuppressWarnings("deprecation")
	@Override
	public JSONObject descrypt(JSONObject json) {
		String content = json.getString("content");
		Long businessId = json.getLong("businessId");
		if(businessId == null){
			throw new BusinessException("无解密businessId");
		}
		
		logger.debug("准备解密,businessId:{}",businessId);
		
		DecryptRequest decryptRequest = new DecryptRequest();
		decryptRequest.setBusinessId(businessId);
		decryptRequest.setData(content);
		ResponseEntity<String> res = decryptManager.decrypt(decryptRequest);
		String errorCode = res.getError();
		if(!StringUtils.equals(errorCode, "00000000")){
			throw new BusinessException("加密系统返回状态失败,errorCode="+errorCode);
		}
		
		String desContent = res.getData();
		JSONObject jsonContent = JSONObject.parseObject(desContent);
		
		json.remove("content");
		json.putAll(jsonContent);		
		return json;
	}

}
