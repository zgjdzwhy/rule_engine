package com.mobanker.engine.framkwork.relation;

import java.util.Map;


/**
 * 找出直接依赖的字段
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年10月21日
 * @version 1.0
 */
public interface EngineDependField{

	//字段名,input/output
	public Map<String,String> dependFields();	
}
