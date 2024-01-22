package com.mobanker.engine.framkwork.data.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.data.EngineRuntimePersistence;
import com.mobanker.engine.framkwork.data.dao.EngineStepInfoDao;
import com.mobanker.engine.framkwork.data.dao.EngineTaskInfoDao;
import com.mobanker.engine.framkwork.data.dto.RuleEngineResultDto;
import com.mobanker.engine.framkwork.data.entity.*;
import com.mobanker.engine.framkwork.data.hbase.StepLoginInHbaseDao;
import com.mobanker.engine.framkwork.data.hbase.StepLoginOutHbaseDao;
import com.mobanker.engine.framkwork.data.hbase.TaskParamHbaseDao;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.engine.framkwork.util.IdUtil;
import com.mobanker.framework.exception.HBaseORMException;
import com.mobanker.framework.tracking.EE;
import com.mobanker.framework.tracking.EETransaction;
import com.mobanker.framework.tracking.constant.BizDataEvent;
import com.mobanker.framework.tracking.model.BigData;
import com.mobanker.framework.utils.sequence.GenerateSequenceUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 * <p>Company: mobanker.com</p>
 * @author zhujj
 * @date 2017年6月20日
 * @version 1.0
 */

public class EngineHbasePersistenceImpl implements EngineRuntimePersistence {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());						
	
	@Autowired
	private EngineTaskInfoDao engTaskInfoDao;
	
	@Autowired
	private EngineStepInfoDao engStepInfoDao;
	
	@Autowired
	private StepLoginInHbaseDao stepLoginInHbaseDao;
	
	@Autowired
	private StepLoginOutHbaseDao stepLoginOutHbaseDao;
	
	@Autowired
	private TaskParamHbaseDao taskParamHbaseDao;
	
	
	@EETransaction(type = "Service", name = "EngineHbasePersistenceImpl.taskStart")
	@Override
	public void taskStart(EngineTransferData etd) {
		EngineTaskInfo taskInfo = new EngineTaskInfo();
		taskInfo.setId(etd.getTaskId());
		taskInfo.setStatus("1");		
		taskInfo.setAssignStep(2);		
		taskInfo.setBeginTime(etd.getTaskTime());
		if(StringUtils.isNoneBlank(etd.getRuleVersion()))
			taskInfo.setRuleVersion(etd.getRuleVersion());
		engTaskInfoDao.updateField(taskInfo);
	}

	@EETransaction(type = "Service", name = "EngineHbasePersistenceImpl.taskFinish")
	@Override
	public void taskFinish(EngineTransferData etd) {
		EngineTaskInfo taskInfo = new EngineTaskInfo();
		taskInfo.setId(etd.getTaskId());
		taskInfo.setStatus("2");		
		taskInfo.setEndTime(EngineUtil.get14CurrentDateTime());			
		String endStepName = etd.getCurStepName();
		if(StringUtils.isNotBlank(endStepName)){
			if(endStepName.length()>50) endStepName = endStepName.substring(0, 50);				
		}
		taskInfo.setEndStep(endStepName);
		engTaskInfoDao.updateField(taskInfo);
				
		String messageId = GenerateSequenceUtil.generateSequenceNo();
		RuleEngineResultDto resultDto = createByEtd(etd);
		//将结果埋入大数据
		try{
			BigData data = new BigData();
	        data.setDomain(etd.getProductType());
	        data.setMessageId(messageId);
	        data.setName(etd.getProductType());
	        data.setEvent(BizDataEvent.INSERT);
	        data.setTypeId(etd.getProductType());
	        data.setSellerId("ruleEngine");
	        data.setTimestamp(System.currentTimeMillis());
	        data.setData(JSONObject.toJSONString(resultDto));
	        EE.sendData("rule_engine_result",data);				
	        logger.info("结果数据发送kafka!");
		}catch(Exception e){
			logger.error("将结果埋入大数据失败，但不影响正常流程",e);
		}
		
		logTaskParam(etd);
	}


	public RuleEngineResultDto createByEtd(EngineTransferData etd){
		RuleEngineResultDto me = new RuleEngineResultDto();
		me.setAppName(etd.getAppName());
		me.setAppRequestId(etd.getAppRequestId());
		me.setInputParam(new HashMap<String,Object>(etd.getInputParam()));
		me.setOutputParam(new HashMap<String,Object>(etd.getOutputParam()));
		me.setProductType(etd.getProductType());
		me.setRuleVersion(etd.getRuleVersion());
		me.setTaskId(etd.getTaskId());
		me.setTaskTime(etd.getTaskTime());
				
		return me;		
	}
	
	@EETransaction(type = "Service", name = "EngineHbasePersistenceImpl.taskFail")
	@Override
	public void taskFail(EngineTransferData etd) {
		EngineTaskInfo taskInfo = new EngineTaskInfo();
		taskInfo.setId(etd.getTaskId());
		taskInfo.setStatus("3");		
		taskInfo.setEndTime(EngineUtil.get14CurrentDateTime());		
		
		Integer failTimes = taskInfo.getFailTimes();
		if(failTimes==null||failTimes==0){
			taskInfo.setFailTimes(1);
		}else{
			taskInfo.setFailTimes(failTimes+1);
		}
		taskInfo.setEndStep(etd.getCurStepBean());
		engTaskInfoDao.updateField(taskInfo);	
	}	
	
	private Map<String,Object> parseInOutParam(String param){
		Map<String,Object> appParams=null;
		if(StringUtils.isNotBlank(param)){
			appParams = JSONObject.parseObject(param);
		}
		return appParams;
	}
	
	@EETransaction(type = "Service", name = "EngineHbasePersistenceImpl.queryLogInEtdByStep")
	@Override
	public EngineTransferData queryLogInEtdByStep(String productType,String appRequestId,Long stepId) {
		EngineAssert.checkNotNull(stepId);
		EngineAssert.checkNotNull(productType);
		
		String id=IdUtil.getRowkeyId(stepId.toString(), productType);

		StepLoginInEntity entity= null;
		try {
			entity = stepLoginInHbaseDao.get(id);
		} catch (HBaseORMException e) {
			e.printStackTrace();
		}
		if(entity==null){
			logger.warn("没有查询到参数快照,table:StepLoginIn,productType:{},requestId:{},stepId:{}",productType,appRequestId,stepId);
			return null;
		}
		EngineTransferData etd=EngineTransferData.getInstance();
		
		BeanUtils.copyProperties(entity,etd);
		etd.getInputParam().putAll(parseInOutParam(entity.getInputParam()));
		etd.getOutputParam().putAll(parseInOutParam(entity.getOutputParam()));
		
		return etd;
	}
	

	
	
	@EETransaction(type = "Service", name = "EngineHbasePersistenceImpl.queryLogOutEtdByStep")
	@Override
	public EngineTransferData queryLogOutEtdByStep(String productType,String appRequestId,Long stepId) {
		EngineAssert.checkNotNull(stepId);
		EngineAssert.checkNotNull(productType);
		
		String id=IdUtil.getRowkeyId(stepId.toString(), productType);

		StepLoginOutEntity entity= null;
		try {
			entity = stepLoginOutHbaseDao.get(id);
		} catch (HBaseORMException e) {
			e.printStackTrace();
		}
		if(entity==null){
			logger.warn("没有查询到参数快照,table:StepLoginOut,productType:{},requestId:{},stepId:{}",productType,appRequestId,stepId);
			return null;
		}
		EngineTransferData etd=EngineTransferData.getInstance();
		
		BeanUtils.copyProperties(entity,etd);
		etd.getInputParam().putAll(parseInOutParam(entity.getInputParam()));
		etd.getOutputParam().putAll(parseInOutParam(entity.getOutputParam()));
		
		return etd;
	}	
	
	@EETransaction(type = "Service", name = "EngineHbasePersistenceImpl.querySnapshotByTask")
	@Override
	public EngineTransferData querySnapshotByTask(String productType,
			String appRequestId, Long taskId) {
		EngineAssert.checkNotNull(taskId);
		EngineAssert.checkNotNull(productType);
		
		String id=IdUtil.getRowkeyId(taskId.toString(), productType);

		TaskParamEntity entity= null;
		try {
			entity = taskParamHbaseDao.get(id);
		} catch (HBaseORMException e) {
			e.printStackTrace();
		}
		if(entity==null){
			logger.warn("没有查询到参数快照,table:TaskParam,productType:{},requestId:{},taskId:{}",productType,appRequestId,taskId);
			return null;
		}
		
		EngineTransferData etd=EngineTransferData.getInstance();
		
		BeanUtils.copyProperties(entity,etd);
		etd.getInputParam().putAll(parseInOutParam(entity.getInputParam()));
		etd.getOutputParam().putAll(parseInOutParam(entity.getOutputParam()));
		
		return etd;	
	}	
	
	@EETransaction(type = "Service", name = "EngineHbasePersistenceImpl.recordTaskStep")
	@Override
	public Long recordTaskStep(EngineStepInfo stepInfo) throws EngineException { 
		EngineAssert.checkNotNull(stepInfo);
		EngineAssert.checkNotNull(stepInfo.getTaskId());
		EngineAssert.checkNotNull(stepInfo.getTaskTime());
		EngineAssert.checkNotNull(stepInfo.getAppName());
		EngineAssert.checkNotNull(stepInfo.getAppRequestId());		
		
		EngineAssert.checkNotNull(stepInfo.getStepNum());
		EngineAssert.checkNotBlank(stepInfo.getStepBean());
		EngineAssert.checkNotBlank(stepInfo.getStepName());
		EngineAssert.checkNotBlank(stepInfo.getStatus());
		EngineAssert.checkNotNull(stepInfo.getConsume());
		
		engStepInfoDao.insert(stepInfo);
		
		Long stepId = stepInfo.getId();			
		return stepId;
	}

	@EETransaction(type = "Service", name = "EngineHbasePersistenceImpl.updateTaskStep")
	@Override
	public int updateTaskStep(EngineStepInfo stepInfo) throws EngineException {
		EngineAssert.checkNotNull(stepInfo);
		EngineAssert.checkNotNull(stepInfo.getId());
		EngineAssert.checkNotNull(stepInfo.getConsume());
		EngineAssert.checkNotBlank(stepInfo.getStatus());
		
		int count = engStepInfoDao.updateField(stepInfo);		
		return count;
	}

	@EETransaction(type = "Service", name = "EngineHbasePersistenceImpl.logInParam")
	@Override
	public void logInParam(EngineTransferData etd) throws EngineException {
		StepLoginInEntity entity=new StepLoginInEntity();
		
		BeanUtils.copyProperties(etd,entity);
		
		//在hbase中先转换成json串保存
		entity.setInputParam(JSON.toJSONString(etd.getInputParam()));
		entity.setOutputParam(JSON.toJSONString(etd.getOutputParam()));
		entity.setRowKey();
		try {
			stepLoginInHbaseDao.save(entity);
		} catch (HBaseORMException e) {
			e.printStackTrace();
		}
	}
	
	@EETransaction(type = "Service", name = "EngineHbasePersistenceImpl.logOutParam")
	@Override
	public void logOutParam(EngineTransferData etd) {
		StepLoginOutEntity entity=new StepLoginOutEntity();
		
		BeanUtils.copyProperties(etd,entity);
		
		//在hbase中先转换成json串保存
		entity.setInputParam(JSON.toJSONString(etd.getInputParam()));
		entity.setOutputParam(JSON.toJSONString(etd.getOutputParam()));
		entity.setRowKey();
	//	stepLoginOutHbaseDao.save(entity);
	}
	
	@EETransaction(type = "Service", name = "EngineHbasePersistenceImpl.logTaskParam")
	private void logTaskParam(EngineTransferData etd) {
		TaskParamEntity entity=new TaskParamEntity();
		
		BeanUtils.copyProperties(etd,entity);
		
		//在hbase中先转换成json串保存
		entity.setInputParam(JSON.toJSONString(etd.getInputParam()));
		entity.setOutputParam(JSON.toJSONString(etd.getOutputParam()));
		entity.setRowKey();
		//taskParamHbaseDao.save(entity);
	}

	@EETransaction(type = "Service", name = "EngineHbasePersistenceImpl.queryLastFailTaskSteps")
	@Override
	public List<EngineStepInfo> queryLastFailTaskSteps(Long taskId) {		
		return engStepInfoDao.queryLastFailTaskSteps(taskId);
	}


}
