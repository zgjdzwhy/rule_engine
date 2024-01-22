package com.mobanker.engine.framkwork.data.dto;

import java.io.Serializable;
import java.util.Map;


public class RuleEngineResultDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long taskId;
	private String taskTime;
	
	private String productType;
	private String appName;
	private String appRequestId;
	
	private Map<String,Object> inputParam;
	private Map<String,Object> outputParam;
	
	private String ruleVersion;
	


	public Long getTaskId() {
		return taskId;
	}


	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}


	public String getTaskTime() {
		return taskTime;
	}


	public void setTaskTime(String taskTime) {
		this.taskTime = taskTime;
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


	public String getAppRequestId() {
		return appRequestId;
	}


	public void setAppRequestId(String appRequestId) {
		this.appRequestId = appRequestId;
	}


	public Map<String, Object> getInputParam() {
		return inputParam;
	}


	public void setInputParam(Map<String, Object> inputParam) {
		this.inputParam = inputParam;
	}


	public Map<String, Object> getOutputParam() {
		return outputParam;
	}


	public void setOutputParam(Map<String, Object> outputParam) {
		this.outputParam = outputParam;
	}


	public String getRuleVersion() {
		return ruleVersion;
	}


	public void setRuleVersion(String ruleVersion) {
		this.ruleVersion = ruleVersion;
	}
	
	
	
	
	
}
