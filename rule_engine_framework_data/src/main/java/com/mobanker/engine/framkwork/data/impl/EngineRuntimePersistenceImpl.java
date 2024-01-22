package com.mobanker.engine.framkwork.data.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.data.EngineRuntimePersistence;
import com.mobanker.engine.framkwork.data.dao.EngineStepInfoDao;
import com.mobanker.engine.framkwork.data.dao.EngineTaskInfoDao;
import com.mobanker.engine.framkwork.data.entity.EngineStepInfo;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.framework.tracking.EE;
import com.mobanker.framework.tracking.constant.BizDataEvent;
import com.mobanker.framework.tracking.model.BigData;
import com.mobanker.framework.utils.sequence.GenerateSequenceUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;


/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月12日
 * @version 1.0
 */
@Deprecated
public class EngineRuntimePersistenceImpl implements EngineRuntimePersistence {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());						
	
	private static final String STEP_DETAIL_LOGIN = "rule_step_login";
	private static final String STEP_DETAIL_LOGOUT = "rule_step_logout";
	
	@Autowired
	private EngineTaskInfoDao engTaskInfoDao;
	
	@Autowired
	private EngineStepInfoDao engStepInfoDao;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	//@Autowired
	//private IdGenContract idGenContract;
	
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
				
//		String messageId = GenerateSequenceUtil.generateSequenceNo();
//		//将结果埋入大数据
//		try{
//			BigData data = new BigData();
//	        data.setDomain(etd.getProductType());
//	        data.setMessageId(messageId);
//	        data.setName(etd.getProductType());
//	        data.setEvent(BizDataEvent.INSERT);
//	        data.setTypeId(etd.getProductType());
//	        data.setSellerId("ruleEngine");
//	        data.setTimestamp(System.currentTimeMillis());
//	        data.setData(JSONObject.toJSONString(etd));
//	        EE.sendData("rule_engine_result",data);				
//	        logger.info("结果数据发送kafka!");
//		}catch(Exception e){
//			logger.error("将结果埋入大数据失败，但不影响正常流程",e);
//		}
	}


	
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
	
	
	@Override
	public EngineTransferData queryLogInEtdByStep(String productType,String appRequestId,Long stepId) {
		return queryEtdByStep(STEP_DETAIL_LOGIN,productType,appRequestId,stepId);
	}
	
	@Override
	public EngineTransferData queryLogOutEtdByStep(String productType,String appRequestId,Long stepId) {
		return queryEtdByStep(STEP_DETAIL_LOGOUT,productType,appRequestId,stepId);
	}	
	
	private EngineTransferData queryEtdByStep(String tableName,String productType,String appRequestId,Long stepId) {
		String BUSI_CONTENT = "获取当时步骤的参数信息,type:"+tableName;		
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(tableName);
		BasicDBObject query = new BasicDBObject("productType",productType);
		query.put("appRequestId",appRequestId);
		if(stepId != null) query.put("curStepId",stepId);
		DBCursor cursor = table.find(query).limit(1);
		EngineTransferData result = null;
		if(cursor != null){
			logger.info(EngineUtil.logPrefix(BUSI_CONTENT)+"step:{}获取游标，游标是否有数据:{}",stepId,cursor.hasNext());
			while (cursor.hasNext()) {   
				String jsonText = cursor.next().toString();   
				logger.info(EngineUtil.logPrefix(BUSI_CONTENT)+"参数数据：{}",jsonText);
				result = JSONObject.parseObject(jsonText, EngineTransferData.class);
			} 			
		}else{
			logger.warn(EngineUtil.logPrefix(BUSI_CONTENT)+"step:{}游标不存在",stepId);
		}
		return result;
	}	
	

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

	@Override
	public int updateTaskStep(EngineStepInfo stepInfo) throws EngineException {
		EngineAssert.checkNotNull(stepInfo);
		EngineAssert.checkNotNull(stepInfo.getId());
		EngineAssert.checkNotNull(stepInfo.getConsume());
		EngineAssert.checkNotBlank(stepInfo.getStatus());
		
		int count = engStepInfoDao.updateField(stepInfo);		
		return count;
	}

	@Override
	public void logInParam(EngineTransferData etd) throws EngineException {
		String paramLog = JSONObject.toJSONString(etd);
		try {
			DB db = mongoTemplate.getDb();
			DBCollection dBCollection = db.getCollection(STEP_DETAIL_LOGIN);
			DBObject dbObject = (DBObject) JSON.parse(paramLog);
			dBCollection.insert(dbObject);
		} catch (Exception e) {
			throw new EngineException("logInParam Exception!!!", e);
		}
	}
	
	@Override
	public void logOutParam(EngineTransferData etd) {
		String paramLog = JSONObject.toJSONString(etd);
		try {
			DB db = mongoTemplate.getDb();
			DBCollection dBCollection = db.getCollection(STEP_DETAIL_LOGOUT);
			DBObject dbObject = (DBObject) JSON.parse(paramLog);
			dBCollection.insert(dbObject);
		} catch (Exception e) {
			throw new EngineException("logInParam Exception!!!", e);
		}
	}
	

	@Override
	public List<EngineStepInfo> queryLastFailTaskSteps(Long taskId) {		
		return engStepInfoDao.queryLastFailTaskSteps(taskId);
	}

	@Override
	public EngineTransferData querySnapshotByTask(String productType,
			String appRequestId, Long taskId) {
		throw new EngineException("不支持");		
	}

}
