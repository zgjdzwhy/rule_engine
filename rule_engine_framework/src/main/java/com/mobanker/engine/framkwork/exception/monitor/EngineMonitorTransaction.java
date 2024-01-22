package com.mobanker.engine.framkwork.exception.monitor;

public interface EngineMonitorTransaction {
	public final static String SUCCESS = "0";
	
	public void setStatus(String status);
	
	public void setStatus(Throwable e,String logError);
	
	public void logEvent(String type,String name);
	
	public void complete();
}
