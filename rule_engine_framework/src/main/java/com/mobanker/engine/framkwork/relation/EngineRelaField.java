package com.mobanker.engine.framkwork.relation;

import java.util.Set;

/**
 * 找出与之有管理的组件
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年10月21日
 * @version 1.0
 */
public interface EngineRelaField{
	/**
	 * 与之有关的组件
	 * @return key:组件ID,value:该依赖组件是否是直系依赖
	 */
	public Set<String> inputRela();
	public Set<String> outputRela();
}
