package com.mobanker.engine.framkwork.test;

public enum EngineExecMode {
	/**
	 * 正常运行
	 */
	NO_TEST(0), 
	/**
	 * 模块测试，遇到调用数据源则会报错，并且路由器和桥接器不起作用
	 */
	MODULE_TEST(1), 
	/**
	 * 流程测试，遇到调用数据源则会报错，但路由器和桥接器是起作用的
	 */
	WHOLE_TEST(2);
	
	private int value;
	private EngineExecMode(int value) {  
        this.value = value;  
    }
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}

}
