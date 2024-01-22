package com.mobanker.engine.framkwork.cpnt.stage;

import java.util.List;

import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.step.EngineStep;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;
import com.mobanker.engine.framkwork.relation.EngineRelaField;

/**
 * 流程组件，每个stage包含多个step
 * <p>Title: EngineExecuteStepContainer.java<／p>
 * <p>Description: <／p>
 * <p>Company: mobanker.com<／p>
 * @author taojinn
 * @date 2016年2月16日
 * @version 1.0
 */
public interface EngineStage extends EngineCpnt,EngineRelaCpnt,EngineRelaField{
	
	/**
	 * 获取步骤组件
	 * @return
	 */
	public List<EngineStep> getList();	
}
