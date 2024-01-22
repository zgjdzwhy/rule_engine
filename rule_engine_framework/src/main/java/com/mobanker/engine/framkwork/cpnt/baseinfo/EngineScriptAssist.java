package com.mobanker.engine.framkwork.cpnt.baseinfo;

import java.util.List;
import java.util.Map;

import com.mobanker.engine.framkwork.relation.EngineRelaField;
import com.mobanker.engine.framkwork.script.function.EngineScriptFunction;

/**
 * 包含脚本的组件
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月1日
 * @version 1.0
 */

public interface EngineScriptAssist extends EngineCpnt,EngineRelaField{		
	/**
	 * 绑定函数
	 * @param functions
	 */
	public void bindFunctions(List<EngineScriptFunction> functions);	
	/**
	 * 获取脚本内容
	 * @return
	 */
	public String getScript();		
	
	/**
	 * 将中文替换掉
	 */
	public void replaceVar(Map<String,String> map);
	
	/**
	 * 关联字段
	 * @param type
	 * @param fieldKey
	 */
	public void relaField(String type,String fieldKey);	
}
