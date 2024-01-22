package com.mobanker.engine.framkwork.script.executer.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.framkwork.api.params.EngineParam;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineScriptAssist;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.script.executer.EngineScriptExecuter;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年2月14日
 * @version 1.0
 */
public class EngineScriptExecuterImpl implements EngineScriptExecuter {

	private final static Logger logger = LoggerFactory.getLogger(EngineScriptExecuterImpl.class);			
	
	public ScriptEngine nashorn;
	
	public EngineScriptExecuterImpl(){
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();  			
		ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");	
		if(nashorn==null)
			throw new EngineException("环境缺少nashorn引擎!请升级jdk");
		logger.info("创建一个新的js执行器:"+nashorn);
		this.nashorn = nashorn;
	}
	
	@Override
	public void eval(String script) throws Exception{
		nashorn.eval(script);
	}

	@Override
	public void bindVar(EngineScriptAssist scriptCpnt, EngineTransferData etd) throws Exception{
		//绑定变量
		StringBuilder sb = new StringBuilder();
	
		nashorn.eval("var input = {};var output = {};_result = null;");
		EngineParam params = null;
		params = etd.getInputParam();		
		for(String key : scriptCpnt.inputRela()){
			Object value = params.get(key);
			EngineAssert.checkNotNull(value, EngineUtil.logPrefix(etd, "绑定变量")+"必要参数缺失,组件名称:%s,字段名:input.%s",scriptCpnt.name(),key);			
			key = key.trim();
			nashorn.put(key, value);
			//nashorn.eval("input."+key+" = "+key+";");
			sb.append("input."+key+" = "+key+";");			
			logger.info(EngineUtil.logPrefix(etd, "绑定变量")+"将变量放入环境中,input类型:{},{},valueType:{}",key,value,value==null?"null":value.getClass().getName());			
		}
		
		params = etd.getOutputParam();
		for(String key : scriptCpnt.outputRela()){
			Object value = params.get(key);
			if(value == null){
				logger.info(EngineUtil.logPrefix(etd, "绑定变量")+"发现有关联output字段内容为空,字段名:{}",key);	
				continue;
			}
			//EngineAssert.checkNotNull(value, EngineUtil.logPrefix(etd, "绑定变量")+"参数字段的值不存在,组件名称:%s,字段名:output.%s",scriptCpnt.name(),key);
			key = key.trim();
			nashorn.put(key, value);
			sb.append("output."+key+" = "+key+";");
			//nashorn.eval("output."+key+" = "+key+";");			
			logger.info(EngineUtil.logPrefix(etd, "绑定变量")+"将变量放入环境中,output类型:{},{},valueType:{}",key,value,value==null?"null":value.getClass().getName());
		}		
		nashorn.eval(sb.toString());
	}

	@Override
	public Object getResult(String key) {
		return nashorn.get(key);
	}

	@Override
	public Object getOutput(String key) {
		ScriptObjectMirror outputObj = (ScriptObjectMirror)nashorn.get("output");
		Object value = outputObj.get(key);
		value = transfer(value);		
		return value;
	}

	
	private Object transfer(Object srcObj){		
		if(srcObj instanceof Object[]){//如果是外面传进去的数组
			Object[] temp = (Object[]) srcObj;
			return Arrays.asList(temp);						
		}else if(srcObj instanceof ScriptObjectMirror){//如果是里面生成的array
			ScriptObjectMirror temp = (ScriptObjectMirror) srcObj;
			if(temp.isArray()){
				List<Object> result = new LinkedList<Object>();
				for(int p = 0;p<temp.size();p++){
					result.add(temp.get(String.valueOf(p)));
				}
				return result;
			}else
				throw new EngineException("js脚本返回的内容数据格式异常，请检查脚本");			
		}else
			return srcObj;		
	}
}
