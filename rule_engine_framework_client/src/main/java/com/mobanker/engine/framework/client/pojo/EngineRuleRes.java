package com.mobanker.engine.framework.client.pojo;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author taojinn
 *
 * @param <T>
 */
public class EngineRuleRes implements Serializable {
	private static final long serialVersionUID = -720807478055084231L;
	
	public static final String SUCCESS = "00000000";
	
	private String error;
	private String msg;
	private Map<String,Object> data;

	public EngineRuleRes(){
		this.error = SUCCESS;
	}
	
	public EngineRuleRes(String error, Map<String,Object> data){
		this.error = error;
		this.data = data;
	}
	
	public EngineRuleRes(Map<String,Object> data){
		this.error = SUCCESS;
		this.data = data;
	}
	
	
	public boolean isSuccess(){
		return StringUtils.equals(error, SUCCESS);
	}
	
	public Map<String, Object> getData() {
		return data;
	}


	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	
	
	public void setError(String error) {
		this.error = error;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	
}