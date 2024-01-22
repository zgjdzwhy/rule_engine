package com.mobanker.engine.exec.business.tasktrace;

import java.util.List;
import java.util.Map;

import com.mobanker.engine.exec.pojo.EngineTaskQco;
import com.mobanker.engine.framkwork.data.entity.EngineStepInfo;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;

/**
 * 任务追踪服务
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年11月9日
 * @version 1.0
 */
public interface EngineTaskTraceService {

	public List<EngineTaskInfo> queryTaskInfo(EngineTaskQco qco);
	
	public List<EngineStepInfo> queryStepInfo(Long taskId);
	
	
	public Map<String,Object> queryStepInVar(String productType,String appRequestId,Long stepId);
	
	public Map<String,Object> queryStepOutVar(String productType,String appRequestId,Long stepId);
}
