package com.mobanker.engine.framkwork.script.executer;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineScriptAssist;

/**
 * 脚本执行器的接口
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年2月14日
 * @version 1.0
 */
public interface EngineScriptExecuter {
	
	/**
	 * 执行
	 * @param script
	 */
	public void eval(String script) throws Exception;
	
	/**
	 * 绑定变量
	 * @param scriptCpnt
	 * @param etd
	 */
	public void bindVar(EngineScriptAssist scriptCpnt,EngineTransferData etd) throws Exception;
	
	/**
	 * 获取output字段结果
	 * @param key
	 * @return
	 */
	public Object getOutput(String key);
	
	/**
	 * 获取结果
	 * @param key
	 * @return
	 */
	public Object getResult(String key);
}
