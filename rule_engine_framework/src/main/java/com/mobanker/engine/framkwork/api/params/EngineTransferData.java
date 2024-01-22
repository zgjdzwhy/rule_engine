package com.mobanker.engine.framkwork.api.params;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 整个流程用于传递参数的主体，类型为EngineParam的成员变量都是线程安全的
 * <p>Title: EngineTransferData.java<／p>
 * <p>Description: <／p>
 * <p>Company: mobanker.com<／p>
 * @author taojinn
 * @date 2016年1月27日
 * @version 1.0
 */
public class EngineTransferData implements Cloneable,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//系统要素
	private Long taskId;
	private String taskTime;
	private String curStepName;
	private Long curStepId;
	private String curStepBean;
	private long curTime = 0;
	private String ipAddress;
	
	//产品线
	private String productType;
	private String appName;
	private String appRequestId;
	
	//引擎三要素
	private EngineParam inputParam;
	private EngineParam outputParam;
	
	//是否测试模式 0-非测试 1-模块测试模式 2-整体流程测试模式
//	private EngineExecMode execMode = EngineExecMode.NO_TEST;
	
	private String ruleVersion;
	
	private EngineTransferData(){		
		inputParam = new EngineParam(100);
		outputParam = new EngineParam(100);
	}
	
	public void logCurStep(String curStepBean,String curStepName){
		curTime = System.currentTimeMillis();
		this.curStepBean = curStepBean;
		this.curStepName = curStepName;
	}
	
	public static EngineTransferData createByDefault(EngineTaskInfo taskInfo){
		EngineTransferData me = new EngineTransferData();
		me.setRuleVersion(taskInfo.getRuleVersion());
		me.setTaskId(taskInfo.getId());
		me.setProductType(taskInfo.getProductType());
		me.setAppName(taskInfo.getAppName());
		me.setAppRequestId(taskInfo.getAppRequestId());
		me.setTaskTime(EngineUtil.get14CurrentDateTime());
		String appParamStr = taskInfo.getAppParams();
		if(StringUtils.isNotBlank(appParamStr)){
			Map<String,Object> appParams = JSONObject.parseObject(appParamStr);
			me.inputParam.putAll(appParams);
		}		
				
		return me;		
	}
	
	
	public static EngineTransferData getInstance(){
		EngineTransferData me = new EngineTransferData();
		return me;		
	}


	@Override
	public String toString(){
		return "\nEngineTransferData ===================> "+
				"\ninputParam:"+inputParam+
				"\noutputParam:"+outputParam+
				"\nEngineTransferData <=================== \n";
	}
	
	@Override
	public EngineTransferData clone(){
		EngineTransferData newOne = null;
		try {
			newOne = (EngineTransferData) super.clone();
			
			//以下数据深克隆
			EngineParam engineParam = null;
			engineParam = new EngineParam();
			engineParam.putAll(inputParam);
			newOne.setInputParam(engineParam);
			
			engineParam = new EngineParam();
			engineParam.putAll(outputParam);
			newOne.setOutputParam(engineParam);
		
			//newOne.execMode = this.execMode;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return newOne;
	}
	
	public Map<String,Object> result(){
		Map<String,Object> result = new HashMap<String, Object>();		
		for(String key : outputParam.keySet()){
			Object value = outputParam.get(key);
			if(value instanceof List){
				List<?> objList = (List<?>) value;
				List<Object> transf=new ArrayList<Object>();
				for(Object obj:objList){
					if(obj instanceof Number){
						obj=new BigDecimal(obj.toString());
					}
					transf.add(obj);
				}
				value = new ArrayList<Object>(transf);
			}else{
				if(value instanceof Number){
					value=new BigDecimal(value.toString());
				}
			}
			result.put(key, value);			
		}
		return result;
	}
	

	public void setOutput(String key,Object value){
		this.outputParam.put(key, value);		
	}
	
	public void setInput(String key,Object value){
		this.inputParam.put(key, value);		
	}
	
	public Object getOutValue(String key){
		return this.outputParam.get(key);
	}
	
	public String getOutString(String key){
		return this.outputParam.getString(key);
	}
	
	public Integer getOutInteger(String key){
		return this.outputParam.getInteger(key);
	}
	
	public BigDecimal getOutBigDecimal(String key){
		return this.outputParam.getBigDecimal(key);
	}
	
	public Long getOutLong(String key){
		return this.outputParam.getLong(key);
	}

	
//	/**
//	 * 是否是测试模式
//	 * @return
//	 */
//	public boolean modeIsTest(){
//		return execMode!=EngineExecMode.NO_TEST;
//	}
//	/**
//	 * 是否是模块测试模式
//	 * @return
//	 */
//	public boolean modeIsModuleTest(){
//		return execMode==EngineExecMode.MODULE_TEST;
//	}
//	/**
//	 * 是否是整体流程测试模式
//	 * @return
//	 */
//	public boolean modeIsWholeTest(){
//		return execMode==EngineExecMode.WHOLE_TEST;
//	}

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

	public String getCurStepName() {
		return curStepName;
	}

	public void setCurStepName(String curStepName) {
		this.curStepName = curStepName;
	}

	public Long getCurStepId() {
		return curStepId;
	}

	public void setCurStepId(Long curStepId) {
		this.curStepId = curStepId;
	}

	public String getCurStepBean() {
		return curStepBean;
	}

	public void setCurStepBean(String curStepBean) {
		this.curStepBean = curStepBean;
	}

	public long getCurTime() {
		return curTime;
	}

	public void setCurTime(long curTime) {
		this.curTime = curTime;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
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

	public EngineParam getInputParam() {
		return inputParam;
	}

	public void setInputParam(EngineParam inputParam) {
		this.inputParam = inputParam;
	}

	public EngineParam getOutputParam() {
		return outputParam;
	}

	public void setOutputParam(EngineParam outputParam) {
		this.outputParam = outputParam;
	}

//	public EngineExecMode getExecMode() {
//		return execMode;
//	}
//
//	public void setExecMode(EngineExecMode execMode) {
//		this.execMode = execMode;
//	}

	public String getRuleVersion() {
		return ruleVersion;
	}

	public void setRuleVersion(String ruleVersion) {
		this.ruleVersion = ruleVersion;
	}
	
}
