package com.mobanker.engine.rpc.dto;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年12月29日
 * @version 1.0
 */
public class EngineCallDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/********************************************************这4个属性用于必填**************************************************/
	/**
	 * 工程类型
	 */
	private String productType;
	/**
	 * 调用方应用名
	 */
	private String appName;
	
	/**
	 * 消息id，用于系统检查，可为null，如果为null，则引擎自动生成一个uuid
	 */
	private String appRequestId;		
	/**
	 * 调用方提供的原始数据
	 */
	private Map<String,Object> appParams = new HashMap<String, Object>();

	/**
	 * 可为空
	 */
	private int execMode = 0;

	
	public EngineCallDto(){}
	
	public EngineCallDto(String productType,String appName,String appRequestId){
		this.productType = productType;
		this.appName = appName;
		this.appRequestId = appRequestId;
	}
	
	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Map<String, Object> getAppParams() {
		return appParams;
	}

	public void setAppParams(Map<String, Object> appParams) {
		this.appParams = appParams;
	}

	public int getExecMode() {
		return execMode;
	}

	public void setExecMode(int execMode) {
		this.execMode = execMode;
	}
	
	
	
	public String getAppRequestId() {
		return appRequestId;
	}

	public void setAppRequestId(String appRequestId) {
		this.appRequestId = appRequestId;
	}

	public void putParam(String key,Object value){
		if(value == null)
			throw new RuntimeException("引擎不接受空值,key:"+key);
		appParams.put(key, value);
	}
	
}
