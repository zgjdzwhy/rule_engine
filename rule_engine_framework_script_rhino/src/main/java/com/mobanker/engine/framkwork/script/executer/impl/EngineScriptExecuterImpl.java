package com.mobanker.engine.framkwork.script.executer.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

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
	
	public ScriptEngine rhino;
	
	public EngineScriptExecuterImpl(){
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();  			
		ScriptEngine rhino = scriptEngineManager.getEngineByName("js");	
		if(rhino==null)
			throw new EngineException("环境缺少rhino引擎!");
		logger.info("创建一个新的js执行器:"+rhino);
		this.rhino = rhino;
	}
	
	@Override
	public void eval(String script) throws Exception{
		rhino.eval(script);
	}

	@Override
	public void bindVar(EngineScriptAssist scriptCpnt, EngineTransferData etd) throws Exception{
		//绑定变量
		StringBuilder sb = new StringBuilder();
	
		rhino.eval("var input = {};var output = {};_result = null;");
		EngineParam params = null;
		params = etd.getInputParam();		
		for(String key : scriptCpnt.inputRela()){
			Object value = params.get(key);
			EngineAssert.checkNotNull(value, EngineUtil.logPrefix(etd, "绑定变量")+"必要参数缺失,组件名称:%s,字段名:input.%s",scriptCpnt.name(),key);			
			key = key.trim();
			rhino.put(key, value);
			//nashorn.eval("input."+key+" = "+key+";");
			sb.append("input."+key+" = "+key+";");			
			logger.info(EngineUtil.logPrefix(etd, "绑定变量")+"将变量放环境中,input类型:{},{},valueType:{}",key,value,value==null?"null":value.getClass().getName());			
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
			rhino.put(key, value);
			sb.append("output."+key+" = "+key+";");
			//nashorn.eval("output."+key+" = "+key+";");			
			logger.info(EngineUtil.logPrefix(etd, "绑定变量")+"将变量放入环境中,output类型:{},{},valueType:{}",key,value,value==null?"null":value.getClass().getName());
		}		
		rhino.eval(sb.toString());
	}

	@Override
	public Object getResult(String key) {
		return rhino.get(key);
	}

	@Override
	public Object getOutput(String key) {
		Object obj = rhino.get("output");
		@SuppressWarnings("unchecked")
		Map<String,Object> map = (Map<String, Object>) obj;
		return map.get(key);
	}

	public static void main(String[] args) throws Exception {
		
		
		EngineScriptExecuterImpl engineScriptExecuter = new EngineScriptExecuterImpl();
		engineScriptExecuter.eval("var input = {};var output = {};_result = null;");
		
		List<String> list = new LinkedList<String>();
		list.add("list1");
		list.add("list2");
		engineScriptExecuter.rhino.put("list", list);
		engineScriptExecuter.eval("output.a = 1");
		engineScriptExecuter.eval("output.b = 'abc'");
		engineScriptExecuter.eval("output.c = ['efg','bcd']");
		
		engineScriptExecuter.eval("var bflag = false;");
		engineScriptExecuter.eval("for(x in list){if(list[x]=='list5'){bflag = true;}}");
		System.out.println(engineScriptExecuter.getResult("bflag"));
		
		
		System.out.println(engineScriptExecuter.getResult("list").getClass().getName());
		
		Object obj = engineScriptExecuter.getResult("output");
		System.out.println(obj.getClass().getName());
		
		@SuppressWarnings("unchecked")
		Map<String,Object> nativeObject = (Map<String, Object>) obj;
		
		for(Object key : nativeObject.keySet()){
			Object value = nativeObject.get(key);						
			System.out.println("value:"+value);				
		}
		
		
		
	}
	
}
