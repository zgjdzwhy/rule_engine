package com.mobanker.engine.framkwork.manager;

import java.util.List;

import com.mobanker.engine.framkwork.cpnt.step.EngineStep;

/**
 * step执行器接口
 * <p>Title: EngineStepProcessor.java<／p>
 * <p>Description: <／p>
 * <p>Company: mobanker.com<／p>
 * @author taojinn
 * @date 2016年2月16日
 * @version 1.0
 */
public interface EngineStepProcessor {
	/**
	 * 添加步骤
	 * @param step
	 */
	public void addStep(EngineStep step);
	
	/**
	 * 添加步骤
	 * @param step
	 */
	public void addStepList(List<EngineStep> stepList);
	
	/**
	 * 添加异步步骤，异步步骤无法再次增加步骤
	 * @param step
	 */
	public void addAsyn(EngineStep step);
	
	/**
	 * 启动
	 */
	public void launch();
}
