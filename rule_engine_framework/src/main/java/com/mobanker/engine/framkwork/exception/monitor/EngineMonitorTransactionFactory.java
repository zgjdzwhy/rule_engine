package com.mobanker.engine.framkwork.exception.monitor;

public interface EngineMonitorTransactionFactory {
	public EngineMonitorTransaction create(String type, String name);
}
