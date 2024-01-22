package com.mobanker.engine.framkwork.parse;

import java.util.Collection;
import java.util.Map;

import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;

public interface EngineCpntParser {
	
	/**
	 * 添加外部已经有的组件
	 */
	public void addExternalCpnt(Collection<EngineCpnt> cpntList);
	
	/**
	 * 解析单个组件
	 * @param cpntId
	 * @param clazz
	 * @return
	 */
	public <T extends EngineCpnt> T parse(String cpntId,Class<T> clazz);
	
	/**
	 * 获得组件集合
	 * @return
	 */
	public Map<String,EngineCpnt> getParsedCpnt();
}
