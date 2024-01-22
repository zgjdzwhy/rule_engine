package com.mobanker.engine.framkwork.data.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.data.EngineRuntimePersistence;
import com.mobanker.engine.framkwork.data.dao.EngineStepInfoDao;
import com.mobanker.engine.framkwork.data.dao.EngineTaskInfoDao;
import com.mobanker.engine.framkwork.data.dto.RuleEngineResultDto;
import com.mobanker.engine.framkwork.data.entity.EngineStepInfo;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.framework.tracking.EE;
import com.mobanker.framework.tracking.constant.BizDataEvent;
import com.mobanker.framework.tracking.model.BigData;
import com.mobanker.framework.utils.sequence.GenerateSequenceUtil;


/**
 * 请查看 com.mobanker.engine.exec.business.snapshot.EngineQueueEsPersistenceImpl
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月12日
 * @version 1.0
 */
@Deprecated
public class EngineEsPersistenceImpl implements EngineRuntimePersistence {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());						
	
	private static final String STEP_DETAIL_LOGIN = "rule_step_login";
	private static final String STEP_DETAIL_LOGOUT = "rule_step_logout";
	
	private static final String TASK_PARAM = "rule_task_param";
	
	@Autowired
	private EngineTaskInfoDao engTaskInfoDao;
	
	@Autowired
	private EngineStepInfoDao engStepInfoDao;
	
	@Autowired
	private Client client;	
	
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
	        EE.sendData("biz_rule_engine_result",data);				
	        logger.info("结果数据发送kafka!");
		}catch(Exception e){
			logger.error("将结果埋入大数据失败，但不影响正常流程",e);
		}
		
		logStepParam(TASK_PARAM,etd);
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
		EngineAssert.checkNotBlank(appRequestId);
		EngineAssert.checkNotNull(stepId);
		
		BoolQueryBuilder qb = QueryBuilders.boolQuery();		
		qb.must(QueryBuilders.termQuery("appRequestId", appRequestId.toLowerCase()));		
		qb.must(QueryBuilders.termQuery("curStepId", stepId));
		
		SearchResponse searchresponse = client.prepareSearch(tableName)
				.setTypes(productType)
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setQuery(qb)                 
                .setSize(100)
                .setExplain(true)  
                .execute().actionGet();  
		
		long hitNum = searchresponse.getHits().getTotalHits();
		if(hitNum > 1){
			logger.warn("发现多条快照,table:{},productType:{},requestId:{},stepId:{}",tableName,productType,appRequestId,stepId);		
		}
		if(hitNum == 0){
			logger.warn("没有查询到参数快照,table:{},productType:{},requestId:{},stepId:{}",tableName,productType,appRequestId,stepId);
			return null;
		}
		SearchHit hit = searchresponse.getHits().getHits()[0];		
		String jsonText = hit.getSourceAsString();
		
		EngineTransferData result = JSONObject.parseObject(jsonText, EngineTransferData.class);		
		return result;		
	}	
	
	@Override
	public EngineTransferData querySnapshotByTask(String productType,
			String appRequestId, Long taskId) {
		EngineAssert.checkNotBlank(appRequestId);
		EngineAssert.checkNotNull(taskId);
		
		BoolQueryBuilder qb = QueryBuilders.boolQuery();		
		qb.must(QueryBuilders.termQuery("appRequestId", appRequestId.toLowerCase()));		
		qb.must(QueryBuilders.termQuery("taskId", taskId));
		
		SearchResponse searchresponse = client.prepareSearch(TASK_PARAM)
				.setTypes(productType)
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setQuery(qb)                 
                .setSize(100)
                .setExplain(true)  
                .execute().actionGet();  
		
		long hitNum = searchresponse.getHits().getTotalHits();
		if(hitNum > 1){
			logger.warn("发现多条快照,table:{},productType:{},requestId:{},taskId:{}",TASK_PARAM,productType,appRequestId,taskId);		
		}
		if(hitNum == 0){
			logger.warn("没有查询到参数快照,table:{},productType:{},requestId:{},taskId:{}",TASK_PARAM,productType,appRequestId,taskId);
			return null;
		}
		SearchHit hit = searchresponse.getHits().getHits()[0];		
		String jsonText = hit.getSourceAsString();
		
		EngineTransferData result = JSONObject.parseObject(jsonText, EngineTransferData.class);		
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
		logStepParam(STEP_DETAIL_LOGIN,etd);
	}
	
	@Override
	public void logOutParam(EngineTransferData etd) {
		logStepParam(STEP_DETAIL_LOGOUT,etd);
	}
	
	protected void logStepParam(String table,EngineTransferData etd){
		String productType = etd.getProductType();
		EngineAssert.checkNotBlank(productType);
		client.prepareIndex(table, productType).setSource(JSONObject.toJSONString(etd)).get();				
	}
	
	

	@Override
	public List<EngineStepInfo> queryLastFailTaskSteps(Long taskId) {		
		return engStepInfoDao.queryLastFailTaskSteps(taskId);
	}


}
