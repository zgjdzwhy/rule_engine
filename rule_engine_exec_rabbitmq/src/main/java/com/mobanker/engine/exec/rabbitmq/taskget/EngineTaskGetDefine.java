package com.mobanker.engine.exec.rabbitmq.taskget;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月14日
 * @version 1.0
 */
public abstract class EngineTaskGetDefine {
	public static final String EXCHANGETYPE = "direct";
	
	public static final String EXCHANGE = "engine_task_get";
	public static final String ROUTING = "engine_task_get";
	public static final String QUEUE = "engine_task_get";
	
	public static final int RECEIVE_THREAD_COUNT = 10;
	
}
