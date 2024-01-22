package com.mobanker.engine.rpc.dto;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class EngineInvokeDto implements Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/********************************************************这4个属性用于必填**************************************************/
	/**
	 * 产品类型
	 */
	private String productType;
	/**
	 * 调用方应用名
	 */
	private String appName;		
	/**
	 * 调用方提供的原始数据
	 */
	private Map<String,Object> appParams = new HashMap<String, Object>();

	/**
	 * 运行模式 @see EngineExecMode
	 */
	private int execMode = 0;

	
	public EngineInvokeDto(){}
	
	public EngineInvokeDto(String productType,String appName){
		this.productType = productType;
		this.appName = appName;
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
	
	public void putParam(String key,Object value){
		if(value == null)
			throw new RuntimeException("引擎不接受空值,key:"+key);
		appParams.put(key, value);
	}
	
}
