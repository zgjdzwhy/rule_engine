package com.mobanker.engine.framkwork.exception.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年2月20日
 * @version 1.0
 */
public abstract class EngineStaticMonitorEnvironment {
	private static final Logger logger = LoggerFactory.getLogger(EngineStaticMonitorEnvironment.class);	
	
	public static EngineMonitorTransactionFactory engineTransactionFactory;
	private final static EngineDefaultTransaction defaultTransaction = new EngineDefaultTransaction();
	
	public static synchronized void init(EngineMonitorTransactionFactory one){		
		if(engineTransactionFactory == null){
			engineTransactionFactory = one;
			logger.info("初始化异常监控处理器:{}",one);
		}else
			logger.warn("异常监控处理器已经存在，无法再次初始化",one);
	}
	
	public static EngineMonitorTransaction create(String type,String name){
		if(engineTransactionFactory == null){
			//logger.info("由于没有设置异常监控处理器，使用默认的异常处理器");
			return defaultTransaction;
		} 		
		return engineTransactionFactory.create(type, name);
	}
}
