package com.mobanker.engine.exec.business.zk.taskassign;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月14日
 * @version 1.0
 */
public interface EngineTaskExecSwitchWatcher {
	
	public void open(String productType);
	
	public void close(String productType);
	
	public boolean isClose(String productType);		
}
