package com.mobanker.engine.framkwork.exception.monitor.ee;

import org.apache.commons.lang3.StringUtils;

import com.dianping.cat.message.Transaction;
import com.mobanker.engine.framkwork.exception.monitor.EngineMonitorTransaction;
import com.mobanker.framework.tracking.EE;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年2月20日
 * @version 1.0
 */
public class EngineEETransaction implements EngineMonitorTransaction{

	private Transaction trans;
	
	private String type;
	private String name;
	
	public EngineEETransaction(String type,String name){		
		this.type = type;
		this.name = name;
		trans = EE.newTransaction(type, name);
	}
	
	@Override
	public void setStatus(String status) {
		trans.setStatus(status);
		EE.logEvent(type, name);
	}

	@Override
	public void complete() {
		trans.complete();
	}

	@Override
	public void setStatus(Throwable e, String logError) {
		trans.setStatus(e);
		if(StringUtils.isNoneBlank(logError)) EE.logError(logError, e);		
	}

	@Override
	public void logEvent(String type, String name) {
		EE.logEvent(type, name);
	}

}
