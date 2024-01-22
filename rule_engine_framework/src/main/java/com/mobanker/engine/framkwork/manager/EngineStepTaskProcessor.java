package com.mobanker.engine.framkwork.manager;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.step.EngineStep;
import com.mobanker.engine.framkwork.data.EngineRuntimePersistence;
import com.mobanker.engine.framkwork.data.entity.EngineStepInfo;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.exception.EngineParamMissException;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月12日
 * @version 1.0
 */
public class EngineStepTaskProcessor implements EngineStepProcessor{
	
	private static final String BUSI_DESC = "步骤执行";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());			
	
	private LinkedList<EngineExecuteStepInner> stepQueue;
	
	private List<EngineStep> asynStepList;
	
	private EngineTransferData etd;
	
	private EngineRuntimePersistence engineRuntimePersistence;
	
	private int stepNum = 1;
	
	private EngineStepTaskProcessor(){
		stepQueue = new LinkedList<EngineExecuteStepInner>();
		asynStepList = new LinkedList<EngineStep>();		
	}
	
	public static EngineStepTaskProcessor create(EngineRuntimePersistence engineRuntimePersistence,EngineTransferData etd){
		//创建管理器，并且将管理器挂到环境上		 
		EngineStepTaskProcessor esp = new EngineStepTaskProcessor();
		
		//设置持久化组件		 
		esp.setengineRuntimePersistence(engineRuntimePersistence);
		
		//设置执行环境
		esp.etd = etd;
		
		return esp;
	}
	
	
	@Override
	public void addStepList(List<EngineStep> stepList) {
		if(CollectionUtils.isEmpty(stepList)){
			logger.warn(EngineUtil.logPrefix(etd,BUSI_DESC)+"需要添加的步骤数量为空!");
			return;
		}
		for(EngineStep step : stepList){
			this.addStep(step);
		}
	}
	
	@Override
	public void addStep(EngineStep step){		
		logger.info(EngineUtil.logPrefix(etd,BUSI_DESC)+"步骤处理器增加了步骤:{}",step);
		
		//持久化步骤
		Long stepId = this.persistentStepInfo(step);
		EngineExecuteStepInner sinner = new EngineExecuteStepInner(stepId,step);		
		stepQueue.add(sinner);
	}
	

	@Override
	public void addAsyn(EngineStep step){		
		logger.info(EngineUtil.logPrefix(etd,BUSI_DESC)+"步骤处理器增加了异步步骤:{}",step);
		asynStepList.add(step);
	}
	
	@Override
	public void launch(){		
		//清除该线程查询缓存
		//EngineUtil.getCurThreadQueryCache().clear();
		
		if(etd == null)
			throw new EngineParamMissException(EngineUtil.logPrefix(etd,BUSI_DESC)+"初始化步骤管理器的时候，必要参数为空!");
			
		//先执行异步任务 留了口子，现在暂时没有异步任务需求
//		try{
//			for(EngineStep asynStep : asynStepList){
//				//这里有etc线程不安全问题，所以在EngineStepAsynWrap内部已经进行了克隆
//				EngineStepAsynWrap stepAsynWrap = new EngineStepAsynWrap(asynStep,etc);
//				//将来会使用线程池
//				new Thread(stepAsynWrap).start();
//			}
//		}catch(Exception e){//不影响正常流程			
//			logger.warn(EngineUtil.logPrefix(etd,"执行异步步骤")
//					+"发生未知错误",e);
//		}
		
		EngineExecuteStepInner sinner = stepQueue.poll();
		while(sinner!=null){	
			long flagTime = System.currentTimeMillis();
			Long stepId = sinner.stepId;
			
			//环境记录步骤id和一些基本信息，为了将stepId保存到参数快照，以及将步骤信息记录到一些额外信息中
			etd.setCurStepId(stepId);		
			etd.logCurStep(sinner.step.id(),sinner.step.name());
			
			String temp = EngineUtil.logPrefix(etd, BUSI_DESC)+sinner.step.name()+" start";
			logger.info(StringUtils.center(temp, 120, '*'));
			
			//将参数快照保留一份到mongo
			snapShotInParam();			
			try{
				sinner.step.execute(etd);			
			}catch(Exception e){
				//将步骤的状态改成异常
				updateStepExceptionFlag(stepId);
				throw new EngineException(e);
			}			
			snapShotOutParam();
			//将步骤状态改为完成
			updateStepFinishFlag(stepId);
			
			temp = EngineUtil.logPrefix(etd, BUSI_DESC)+sinner.step.name()+" consume:"+(System.currentTimeMillis() - flagTime);
			logger.info(StringUtils.center(temp, 120, '*')+"\n");
			
			sinner = stepQueue.poll();
		}		
		
		logger.info(EngineUtil.logPrefix(etd,"任务结束")+"引擎输出结果:{}",etd.getOutputParam());
		
		logger.info(EngineUtil.logPrefix(etd,"任务结束")
				+"<======================================================= end task ==================================================>");
	}

	
	public EngineRuntimePersistence getengineRuntimePersistence() {
		return engineRuntimePersistence;
	}

	public void setengineRuntimePersistence(EngineRuntimePersistence engineRuntimePersistence) {
		this.engineRuntimePersistence = engineRuntimePersistence;
	}
	
	private void snapShotInParam(){
		try {
			engineRuntimePersistence.logInParam(etd);
		} catch (EngineException e) {
			throw new EngineException(EngineUtil.logPrefix(etd,BUSI_DESC)+"插入stepParam失败", e);
		}		
	}
	private void snapShotOutParam(){
		try {
			engineRuntimePersistence.logOutParam(etd);
		} catch (EngineException e) {
			throw new EngineException(EngineUtil.logPrefix(etd,BUSI_DESC)+"插入stepParam失败", e);
		}		
	}
	/**
	 * 持久化步骤到数据库
	 * @param step
	 * @return
	 */
	private Long persistentStepInfo(EngineStep step){		
		EngineStepInfo stepInfo = new EngineStepInfo();
		stepInfo.setTaskId(etd.getTaskId());
		stepInfo.setTaskTime(etd.getTaskTime());
		stepInfo.setAppName(etd.getAppName());
		stepInfo.setAppRequestId(etd.getAppRequestId());
		stepInfo.setStepNum(stepNum++);
		stepInfo.setStepBean(step.id());
		stepInfo.setStepName(step.name());
		stepInfo.setStatus("0");
		stepInfo.setConsume(0);
		
		Long stepId = -1L;
		try {
			stepId = engineRuntimePersistence.recordTaskStep(stepInfo);
		} catch (EngineException e) {
			throw new EngineException(EngineUtil.logPrefix(etd,BUSI_DESC)+"插入stepInfo失败", e);
		}		
		
		stepInfo.setId(stepId.longValue());
		return stepId;
	}
	
	/**
	 * 更新步骤状态完成
	 * @param step
	 * @return
	 */
	private void updateStepFinishFlag(Long stepId){	
		if(stepId == null)
			throw new EngineParamMissException(EngineUtil.logPrefix(etd,BUSI_DESC)+"逻辑异常，更新taskStep时候，taskId缺失!");
		

		EngineStepInfo stepInfo = new EngineStepInfo();
		stepInfo.setId(stepId.longValue());
		stepInfo.setStatus("1");
		Long consume = System.currentTimeMillis() - etd.getCurTime();
		stepInfo.setConsume(consume.intValue());
		
		try {
			engineRuntimePersistence.updateTaskStep(stepInfo);
		} catch (EngineException e) {
			throw new EngineException(EngineUtil.logPrefix(etd,BUSI_DESC)+"更新stepInfo失败", e);
		}		
	}
	
	/**
	 * 更新步骤状态异常
	 * @param step
	 * @return
	 */
	private void updateStepExceptionFlag(Long stepId){	
		if(stepId == null)
			throw new EngineParamMissException(EngineUtil.logPrefix(etd,BUSI_DESC)+"逻辑异常，更新taskStep时候，taskId缺失!");
		
		EngineStepInfo stepInfo = new EngineStepInfo();
		stepInfo.setId(stepId);
		stepInfo.setStatus("2");
		Long consume = System.currentTimeMillis() - etd.getCurTime();
		stepInfo.setConsume(consume.intValue());
		
		try {
			engineRuntimePersistence.updateTaskStep(stepInfo);
		} catch (EngineException e) {
			throw new EngineException(EngineUtil.logPrefix(etd,BUSI_DESC)+"更新stepInfo失败", e);
		}		
	}
	
	/**
	 * 只是为了把eng_step_info.id绑定到step上
	 * <p>Title: EngineStepTaskProcessor.java<／p>
	 * <p>Description: <／p>
	 * <p>Company: mobanker.com<／p>
	 * @author taojinn
	 * @date 2016年2月16日
	 * @version 1.0
	 */
	private class EngineExecuteStepInner{
		private EngineStep step;
		private Long stepId;  
		
		private EngineExecuteStepInner(Long stepId,EngineStep step){
			this.step = step;
			this.stepId = stepId;
		}
	}


	
}
