package com.mobanker.engine.framkwork.data;

import java.util.List;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.data.entity.EngineStepInfo;
import com.mobanker.engine.framkwork.exception.EngineException;

/**
 * 用于测试，啥也不记
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年10月15日
 * @version 1.0
 */
public class EngineDoNothingPersistence implements EngineRuntimePersistence {



	@Override
	public Long recordTaskStep(EngineStepInfo stepInfo) throws EngineException {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public int updateTaskStep(EngineStepInfo stepInfo) throws EngineException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void logInParam(EngineTransferData etd) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<EngineStepInfo> queryLastFailTaskSteps(Long taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void taskStart(EngineTransferData etd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void taskFinish(EngineTransferData etd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void taskFail(EngineTransferData etd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logOutParam(EngineTransferData etd) {
		// TODO Auto-generated method stub
	}

	@Override
	public EngineTransferData queryLogInEtdByStep(String productType,
			String appRequestId, Long stepId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EngineTransferData queryLogOutEtdByStep(String productType,
			String appRequestId, Long stepId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EngineTransferData querySnapshotByTask(String productType,
			String appRequestId, Long taskId) {
		// TODO Auto-generated method stub
		return null;
	}



}
