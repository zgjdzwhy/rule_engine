package com.mobanker.engine.framkwork.exception.monitor;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年2月20日
 * @version 1.0
 */
public class EngineDefaultTransaction implements EngineMonitorTransaction{

	@Override
	public void setStatus(String status) {
		// DONOTHING		
	}

	@Override
	public void complete() {
		// DONOTHING
		
	}

	@Override
	public void setStatus(Throwable e, String logError) {
		// DONOTHING
		
	}

	@Override
	public void logEvent(String type, String name) {
		// DONOTHING		
	}

}
