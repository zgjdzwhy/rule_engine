package com.mobanker.engine.exec.business.invoke;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dianping.cat.message.Transaction;
import com.mobanker.engine.exec.business.zk.taskassign.EngineTaskExecSwitchWatcher;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.container.EngineCpntContainer;
import com.mobanker.engine.framkwork.cpnt.flow.EnginePolicyFlow;
import com.mobanker.engine.framkwork.data.dao.EngineTaskInfoDao;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.entry.EngineTaskLauncher;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.exception.EngineProductCloseException;
import com.mobanker.engine.framkwork.exception.EngineRunningException;
import com.mobanker.engine.framkwork.promanager.EngineRuntimeProductManager;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.engine.rpc.dto.EngineCallDto;
import com.mobanker.framework.tracking.EE;

/**
 * 独立运行模块或者整理,用于同步调用
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年10月18日
 * @version 1.0
 */
@Service
public class EngineSynInvokeEntryImpl implements EngineSynInvokeEntry {

	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	private final static String BUSI_DESC = "同步调用";
	
	
	@Autowired
	private EngineTaskInfoDao engineTaskInfoDao;
	
	@Autowired
	private EngineTaskLauncher engineTaskLauncher;
	
	@Autowired
	private EngineRuntimeProductManager engineRuntimeProductManager;
	
	@Autowired
	private EngineTaskExecSwitchWatcher engineTaskExecSwitchWatcher;
	
	@Override
	public Map<String, Object> invoke(EngineSynInvokeType type,EngineCallDto dto) {
		String productType = dto.getProductType();		
		Transaction trans = EE.newTransaction("SynInvoke-"+type.getValue(), BUSI_DESC+"-->"+productType);
		try {
			EngineAssert.checkNotBlank(productType,"productType为空");
			
			if(engineTaskExecSwitchWatcher.isClose(productType))
				throw new EngineProductCloseException("该产品线被临时关闭!productType:"+productType);
			
			EE.logEvent("SynInvoke-"+type.getValue(), BUSI_DESC+"-->"+productType);		
			trans.setStatus(Transaction.SUCCESS);
		
			EngineTaskInfo taskInfo = insertTaskInfo(type,dto);
			
			EngineCpntContainer ruleModel = engineRuntimeProductManager.getFlow(productType);
			if(ruleModel == null)
				throw new EngineProductCloseException("该工程模块不存在!");
			taskInfo.setRuleVersion(ruleModel.getRuleVersion());
			
			EnginePolicyFlow execFlow = ruleModel.getRoot();
			EngineAssert.checkNotNull(execFlow, EngineUtil.logPrefix(BUSI_DESC)+"该产品线没有root节点,productType:%s",dto.getProductType());			
						
			EngineTransferData etd = engineTaskLauncher.runTask(taskInfo, ruleModel.getRoot());		
			return etd.result();
		} catch (EngineRunningException e) {//由于engineTaskLauncher里面已经做了cat错误日志埋点，所以这里不用再次埋了
			String msg = "同步调用"+productType+"产品线失败";
			throw new EngineException(msg+"=>"+e.getMessage(), e);		
		}catch (Exception e) {
			String msg = "同步调用"+productType+"产品线失败，发生了未知异常";
			trans.setStatus(e);
			EE.logError(msg+"=>"+e.getMessage(),e);
			throw new EngineException(msg+"=>"+e.getMessage(), e);		
		} finally{
			trans.complete();
		}
		
		
	}

	
	private EngineTaskInfo insertTaskInfo(EngineSynInvokeType type,EngineCallDto dto){
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"获取任务信息:{}",dto);
		
		//将数据保存到数据库中
		EngineAssert.checkNotNull(dto);
		EngineAssert.checkNotBlank(dto.getAppName(),BUSI_DESC+"task.appName为空");
		EngineAssert.checkNotBlank(dto.getProductType(),BUSI_DESC+"task.product为空");
		EngineAssert.checkNotBlank(dto.getAppParams(),BUSI_DESC+"task.appParams为空");
		//dto.getExecTime()可为空
		//dto.getPriorty()可为空
		
		EngineTaskInfo entity = new EngineTaskInfo();
		entity.setProductType(dto.getProductType());
		entity.setAppName(dto.getAppName());		
		entity.setAppParams(JSONObject.toJSONString(dto.getAppParams()));
		
		String requestId = dto.getAppRequestId();
		if(StringUtils.isBlank(requestId)){
			requestId = EngineUtil.getUUID();
		}
		entity.setAppRequestId(requestId);			
		
		entity.setExecTime("00000000000000"); 
		entity.setPriority(99);
		
		entity.setAssignStep(-1);
		entity.setStatus("0");
		entity.setChannel(type.getValue());
		entity.setFailTimes(0);
		entity.setSourceDetail("");		
		entity.setBeginTime(EngineUtil.get14CurrentDateTime());
		entity.setEndTime("");
		entity.setRuleVersion("");
		engineTaskInfoDao.insert(entity);	
		
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"任务落地成功:{}",dto);
		return entity;
	}
	
	
}
