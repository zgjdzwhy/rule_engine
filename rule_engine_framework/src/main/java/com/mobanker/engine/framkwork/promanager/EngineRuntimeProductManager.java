package com.mobanker.engine.framkwork.promanager;

import com.mobanker.engine.framkwork.cpnt.container.EngineCpntContainer;

public interface EngineRuntimeProductManager {
	public EngineCpntContainer getFlow(String product);
	
	public void putFlow(String productType,EngineCpntContainer flow);
	
	public void putFlow(String productType,byte[] bytes);
}
