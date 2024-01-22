package com.mobanker.engine.exec.business.invoke;

public enum EngineSynInvokeType {
	/**
	 * 正常运行
	 */
	MQ("mq"), 
	/**
	 * 模块测试，遇到调用数据源则会报错，并且路由器和桥接器不起作用
	 */
	DUBBO("dubbo"), 
	/**
	 * 流程测试，遇到调用数据源则会报错，但路由器和桥接器是起作用的
	 */
	HTTP("http");
	
	private String value;
	private EngineSynInvokeType(String value) {  
        this.value = value;  
    }
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}
