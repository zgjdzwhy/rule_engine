package com.mobanker.engine.exec.business.tasktrace;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.mobanker.engine.exec.pojo.EngineTaskQco;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.data.EngineRuntimePersistence;
import com.mobanker.engine.framkwork.data.dao.EngineStepInfoDao;
import com.mobanker.engine.framkwork.data.dao.EngineTaskInfoDao;
import com.mobanker.engine.framkwork.data.entity.EngineStepInfo;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.exception.EngineAssert;

/**
 * 任务追踪服务
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年11月17日
 * @version 1.0
 */
@Service
public class EngineTaskTraceServiceImpl implements EngineTaskTraceService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	@Autowired 
	private EngineTaskInfoDao engineTaskInfoDao;
	
	@Autowired
	private EngineStepInfoDao engineStepInfoDao;
	
	@Resource(name="engineHbasePersistence")
	private EngineRuntimePersistence engineRuntimePersistence;
	
	@Override
	public List<EngineTaskInfo> queryTaskInfo(EngineTaskQco qco) {
		EngineAssert.checkNotNull(qco);
		EngineAssert.checkNotBlank(qco.getProductType(),"上送工程模型为空");
		EngineAssert.checkNotBlank(qco.getBeginTime(),"上送起始时间为空");
		EngineAssert.checkNotBlank(qco.getEndTime(),"上送终止时间为空");		
		
		PageHelper.startPage(qco.getPageNum(), qco.getPageSize());		
		EngineTaskInfo query = new EngineTaskInfo();
		query.setProductType(qco.getProductType());
		query.setBeginTime(qco.getBeginTime());
		query.setEndTime(qco.getEndTime());
		query.setAppRequestId(qco.getAppRequestId());
		query.setAppParams(qco.getAppParams());
		query.setStatus(qco.getStatus());
		query.setAssignStep(qco.getAssignStep());
		query.setRuleVersion(qco.getRuleVersion());
		
		List<EngineTaskInfo> result = engineTaskInfoDao.queryTaskCondition(query);
		return result;
	}

	@Override
	public List<EngineStepInfo> queryStepInfo(Long taskId) {		
		return engineStepInfoDao.queryByTaskId(taskId);
	}	
	
	@Override
	public Map<String,Object> queryStepInVar(String productType, String appRequestId, Long stepId) {
		EngineAssert.checkNotBlank(productType,"上送productType为空");
		EngineAssert.checkNotBlank(appRequestId,"上送appRequestId为空");
		EngineAssert.checkNotBlank(stepId,"上送stepId为空");		
		
		EngineTransferData etd = engineRuntimePersistence.queryLogInEtdByStep(productType, appRequestId, stepId);
		if(etd == null) return Collections.emptyMap();
		JSONObject result = new JSONObject();
		result.put("input", etd.getInputParam());
		result.put("output", etd.getOutputParam());
		return result;
	}

	@Override
	public Map<String,Object> queryStepOutVar(String productType, String appRequestId, Long stepId) {
		EngineTransferData etd = engineRuntimePersistence.queryLogOutEtdByStep(productType, appRequestId, stepId);
		if(etd == null) return Collections.emptyMap();
		return etd.getOutputParam();
	}

}
