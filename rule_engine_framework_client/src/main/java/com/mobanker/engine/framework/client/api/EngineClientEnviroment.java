package com.mobanker.engine.framework.client.api;

import com.mobanker.engine.framework.client.filemonitor.EngineRuleFileMonitor;
import com.mobanker.engine.framkwork.data.EngineDoNothingPersistence;
import com.mobanker.engine.framkwork.entry.EngineTaskLauncher;
import com.mobanker.engine.framkwork.promanager.EngineRuntimeProductManager;
import com.mobanker.engine.framkwork.promanager.EngineRuntimeProductManagerImpl;

/**
 * 
 * @author taojinn
 *
 */
public abstract class EngineClientEnviroment {
	private static EngineRuntimeProductManager engineRuntimeProductManager = new EngineRuntimeProductManagerImpl();
	
	private static EngineTaskLauncher engineTaskLauncher = new EngineTaskLauncher();
	static{
		EngineRuleFileMonitor engineRuleFileMonitor = new EngineRuleFileMonitor();
		engineRuleFileMonitor.init();
		engineRuleFileMonitor.start();
		
		engineTaskLauncher.setEngineRuntimePersistence(new EngineDoNothingPersistence());
	}
	public static EngineRuntimeProductManager getProductManager(){
		return engineRuntimeProductManager;
	} 
	
	public static EngineTaskLauncher getTaskLauncher(){
		return engineTaskLauncher;
	} 
}
