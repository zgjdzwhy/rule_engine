package com.mobanker.engine.framkwork.data.entity;

import java.sql.Timestamp;

import com.alibaba.fastjson.JSONObject;

//@Table(name = "eng_step_info")
public class EngineStepInfo {
	
	//@Id
	private Long id;
	//@Column(nullable = false)
	private Long taskId;
	private String taskTime;
	private String appName;	
	//@Column(nullable = false)
	private String appRequestId;
	private Integer stepNum;
	private String stepBean;
	private String stepName;
	private String status;
	private Integer consume;
	private Timestamp addtime;
    
	
	
	
	public Long getId() {
		return id;
	}




	public void setId(Long id) {
		this.id = id;
	}




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




	public Integer getStepNum() {
		return stepNum;
	}




	public void setStepNum(Integer stepNum) {
		this.stepNum = stepNum;
	}




	public String getStepBean() {
		return stepBean;
	}




	public void setStepBean(String stepBean) {
		this.stepBean = stepBean;
	}




	public String getStepName() {
		return stepName;
	}




	public void setStepName(String stepName) {
		this.stepName = stepName;
	}




	public String getStatus() {
		return status;
	}




	public void setStatus(String status) {
		this.status = status;
	}




	public Integer getConsume() {
		return consume;
	}




	public void setConsume(Integer consume) {
		this.consume = consume;
	}




	public Timestamp getAddtime() {
		return addtime;
	}




	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}




	public String toString(){
		return JSONObject.toJSONString(this);
	}
}