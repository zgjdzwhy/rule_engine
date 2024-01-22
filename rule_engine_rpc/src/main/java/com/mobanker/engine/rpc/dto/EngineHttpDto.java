package com.mobanker.engine.rpc.dto;

import java.io.Serializable;


public class EngineHttpDto implements Serializable{
	
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
	private String appParams;

	/**
	 * 运行模式 @see EngineExecMode
	 */
	private int execMode = 0;

	
	public EngineHttpDto(){}
	
	public EngineHttpDto(String productType,String appName){
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

	public String getAppParams() {
		return appParams;
	}

	public void setAppParams(String appParams) {
		this.appParams = appParams;
	}

	public int getExecMode() {
		return execMode;
	}

	public void setExecMode(int execMode) {
		this.execMode = execMode;
	}
	
	
}
