package com.mobanker.engine.framkwork.data;

import java.util.List;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.data.entity.EngineStepInfo;
import com.mobanker.engine.framkwork.exception.EngineException;

/**
 * 决策引擎执行持久化组件
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月2日
 * @version 1.0
 */
public interface EngineRuntimePersistence {
	/**
	 * 
	 * @param productType
	 * @param appRequestId
	 * @param stepId 可以为空
	 * @return
	 */
	public EngineTransferData queryLogInEtdByStep(String productType,String appRequestId,Long stepId);
	/**
	 * 
	 * @param productType
	 * @param appRequestId
	 * @param stepId 可以为空
	 * @return
	 */
	public EngineTransferData queryLogOutEtdByStep(String productType,String appRequestId,Long stepId);
	
	public EngineTransferData querySnapshotByTask(String productType,String appRequestId,Long taskId);
	
	public Long recordTaskStep(EngineStepInfo stepInfo) throws EngineException;
	public int updateTaskStep(EngineStepInfo stepInfo) throws EngineException;
	
	/**
	 * 记录指标信息
	 * @param paramJson
	 * @return
	 * @throws EngineException
	 */
	public void logInParam(EngineTransferData etd);
	public void logOutParam(EngineTransferData etd);
		
	public List<EngineStepInfo> queryLastFailTaskSteps(Long taskId);
	/**
	 * 将任务状态更新成开始
	 * @param taskId
	 */
	public void taskStart(EngineTransferData etd);
	/**
	 * 将任务状态更新为结束
	 * @param taskId
	 */
	public void taskFinish(EngineTransferData etd);
	/**
	 * 将任务状态更新为异常
	 * @param taskId
	 */
	public void taskFail(EngineTransferData etd);
}
