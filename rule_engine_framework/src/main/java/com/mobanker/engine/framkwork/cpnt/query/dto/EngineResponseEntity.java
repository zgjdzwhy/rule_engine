package com.mobanker.engine.framkwork.cpnt.query.dto;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 查询返回
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年6月22日
 * @version 1.0
 * @param <T>
 */


public class EngineResponseEntity {

	private Map<String,Object> data;
	private String status;
	private String error;
	private String msg;
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	

	public String toString(){
		return JSONObject.toJSONString(this);
	}
}
