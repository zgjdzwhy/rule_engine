package com.mobanker.engine.framkwork.data.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import com.alibaba.fastjson.JSONObject;

//@Table(name = "eng_task_info")
public class EngineTaskInfo implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//@Id
	private Long id;
	/**
	 * 产品类型
	 */
	private String productType;
	
	private String appName;		
	private String appRequestId;
	private String appCallBack;
	private String appParams;
	
	private String beginTime;
	private String endTime;
	
	/**
	 * 分配状态 0:未被分配 1:被分配中 2:已被分配,-1:无需分配
	 */	
	//@Column(nullable = false)
	private Integer assignStep;
	/**
	 * 任务执行状态 0:初始,1:进行中,2:完成,3:失败
	 */
	private String status;
	
	/**
	 * 失败次数
	 */
	private Integer failTimes;
	/**
	 * 优先级
	 */
	private Integer priority;
	/**
	 * mq or http
	 */
	private String channel;
	/**
	 * 可执行时间 为了让任务能延时执行
	 */
	private String execTime;
	
	/**
	 * 来源额外信息 可为空
	 */
	private String sourceDetail;
	
	/**
	 * 结束步骤
	 */
	private String endStep;

	/**
	 * 规则版本号
	 */
	private String ruleVersion;
	/**
	 * 备注
	 */
	private String memo;
	
	
	private Timestamp addtime;
	
	
	
	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
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



	public String getAppCallBack() {
		return appCallBack;
	}



	public void setAppCallBack(String appCallBack) {
		this.appCallBack = appCallBack;
	}



	public String getAppParams() {
		return appParams;
	}



	public void setAppParams(String appParams) {
		this.appParams = appParams;
	}



	public String getBeginTime() {
		return beginTime;
	}



	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}



	public String getEndTime() {
		return endTime;
	}



	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}



	public Integer getAssignStep() {
		return assignStep;
	}



	public void setAssignStep(Integer assignStep) {
		this.assignStep = assignStep;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public Integer getFailTimes() {
		return failTimes;
	}



	public void setFailTimes(Integer failTimes) {
		this.failTimes = failTimes;
	}



	public Integer getPriority() {
		return priority;
	}



	public void setPriority(Integer priority) {
		this.priority = priority;
	}



	public String getChannel() {
		return channel;
	}



	public void setChannel(String channel) {
		this.channel = channel;
	}



	public String getExecTime() {
		return execTime;
	}



	public void setExecTime(String execTime) {
		this.execTime = execTime;
	}



	public String getSourceDetail() {
		return sourceDetail;
	}



	public void setSourceDetail(String sourceDetail) {
		this.sourceDetail = sourceDetail;
	}



	public String getEndStep() {
		return endStep;
	}



	public void setEndStep(String endStep) {
		this.endStep = endStep;
	}



	public String getRuleVersion() {
		return ruleVersion;
	}



	public void setRuleVersion(String ruleVersion) {
		this.ruleVersion = ruleVersion;
	}



	public String getMemo() {
		return memo;
	}



	public void setMemo(String memo) {
		this.memo = memo;
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
