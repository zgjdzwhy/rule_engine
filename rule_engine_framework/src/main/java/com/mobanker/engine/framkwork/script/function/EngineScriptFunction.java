package com.mobanker.engine.framkwork.script.function;

import java.io.Serializable;

/**
 * 脚本函数
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年10月2日
 * @version 1.0
 */
public class EngineScriptFunction implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String function;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	
	
}
