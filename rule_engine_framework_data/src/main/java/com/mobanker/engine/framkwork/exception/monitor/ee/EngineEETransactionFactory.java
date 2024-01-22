package com.mobanker.engine.framkwork.exception.monitor.ee;

import com.mobanker.engine.framkwork.exception.monitor.EngineMonitorTransaction;
import com.mobanker.engine.framkwork.exception.monitor.EngineMonitorTransactionFactory;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年2月20日
 * @version 1.0
 */
public class EngineEETransactionFactory implements
		EngineMonitorTransactionFactory {

	@Override
	public EngineMonitorTransaction create(String type, String name) {
		return new EngineEETransaction(type,name);
	}

}
