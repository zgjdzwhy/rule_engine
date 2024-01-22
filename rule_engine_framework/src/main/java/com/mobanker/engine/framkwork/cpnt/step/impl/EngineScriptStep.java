package com.mobanker.engine.framkwork.cpnt.step.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.framkwork.api.params.EngineParam;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineScriptAssist;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineSelfCheck;
import com.mobanker.engine.framkwork.cpnt.step.EngineAbstractStep;
import com.mobanker.engine.framkwork.cpnt.step.EngineStep;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineCpntSelfCheckException;
import com.mobanker.engine.framkwork.exception.EngineScriptException;
import com.mobanker.engine.framkwork.relation.EngineDependField;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;
import com.mobanker.engine.framkwork.script.executer.EngineScriptExecuter;
import com.mobanker.engine.framkwork.script.executer.EngineScriptThreadLocal;
import com.mobanker.engine.framkwork.script.function.EngineScriptFunction;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月18日
 * @version 1.0
 */
public class EngineScriptStep extends EngineAbstractStep implements 
	EngineStep,EngineScriptAssist,EngineRelaCpnt,EngineSelfCheck,EngineDependField{
	
	private static final long serialVersionUID = 1L;
	private final static Logger logger = LoggerFactory.getLogger(EngineScriptStep.class);						
	private final String BUSI_DESC = "脚本执行";	
		
	
	/**
	 * 相关入参字段
	 */
	private Set<String> inputField = new HashSet<String>();
	/**
	 *  相关出参字段
	 */
	private Set<String> outputField = new HashSet<String>();
	
	
	private String script;
	private List<EngineScriptFunction> functions;

//	@Override
//	protected void run(EngineTransferData etd) {
//		logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"开始创建js脚本引擎");
//		
//		Context cx = Context.enter();
//		Scriptable scope = cx.initStandardObjects();
//		//初始化环境
//		EngineUtil.jsInitContext(cx, scope);
//		
//        logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"开始绑定js函数");
//		//绑定函数
//		String functionStr = EngineUtil.getFunctionStr(etd, functions);
//		cx.evaluateString(scope, functionStr, null, 1, null);
//		
//		logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"开始绑定js变量");
//		
//		//绑定变量
//		EngineUtil.jsBingVar(this, cx, scope, etd);
//
//		//执行脚本
//		try{
//			cx.evaluateString(scope, script, null, 1, null);
//			NativeObject _result = (NativeObject)scope.get("output", scope);
//			for(Object key : _result.keySet()){
//				Object value = _result.get(key);
//				EngineParam output = etd.getOutputParam();
//				logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"将输出结果放入内存中:{}-{},valueType:{}",key,value,value.getClass().getName());
//				output.put(key.toString(), value);				
//			}
//		}catch(Throwable e){			
//			throw new EngineScriptException(EngineUtil.logPrefix(etd, BUSI_DESC)+"脚本发生错误,cpntName:"+name()+",script:"+script,e);
//		}finally{
//			Context.exit();
//		}
//	}

	@Override
	protected void run(EngineTransferData etd) {
		if(CollectionUtils.isEmpty(inputField)&&CollectionUtils.isEmpty(outputField)){
			logger.warn(EngineUtil.logPrefix(etd, BUSI_DESC)+"该脚本不包含任何入参和出参,{},{}",name(),id());
		}
		
		try{						
			EngineScriptExecuter scriptExecuter = EngineScriptThreadLocal.getCurThreadEngine();
			
			long t = System.currentTimeMillis();
			
			logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"开始绑定js函数");
			//绑定函数
			String functionStr = EngineUtil.getFunctionStr(etd, functions);
			scriptExecuter.eval(functionStr);
			
			logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"开始绑定js变量");		
			//绑定变量
			scriptExecuter.bindVar(this, etd);			
					
			logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"绑定函数和变量，耗时:{}",System.currentTimeMillis() - t);
			
			t = System.currentTimeMillis();
			
			scriptExecuter.eval(script);
			//获取返回值			
			for(String key : outputField){				
				Object value = scriptExecuter.getOutput(key);				
				EngineParam output = etd.getOutputParam();
				logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"将输出结果放入内存中:{}-{},valueType:{}",key,value,value.getClass().getName());
				output.put(key.toString(), value);				
			}
			
			logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"运行完成，耗时:{}",System.currentTimeMillis() - t);
		}catch(Throwable e){			
			throw new EngineScriptException(EngineUtil.logPrefix(etd, BUSI_DESC)+"脚本发生错误,cpntName:"+name()+",script:"+script,e);
		}					
				
	}	
	
//	private Object transfer(Object srcObj){		
//		if(srcObj instanceof Object[]){//如果是外面传进去的数组
//			Object[] temp = (Object[]) srcObj;
//			return Arrays.asList(temp);						
//		}else if(srcObj instanceof ScriptObjectMirror){//如果是里面生成的array
//			ScriptObjectMirror temp = (ScriptObjectMirror) srcObj;
//			if(temp.isArray()){
//				List<Object> result = new LinkedList<Object>();
//				for(int p = 0;p<temp.size();p++){
//					result.add(temp.get(String.valueOf(p)));
//				}
//				return result;
//			}else
//				throw new EngineException("js脚本返回的内容数据格式异常，请检查脚本");			
//		}else
//			return srcObj;		
//	}
	
	
	@Override
	public void replaceVar(Map<String, String> map) {
		for(String key : map.keySet()){
			String name = map.get(key);
			EngineAssert.checkNotBlank(key);
			EngineAssert.checkNotBlank(name);			
			script = StringUtils.replace(script, "#"+name+"#", key);
		}
	}
	
	@Override
	public Map<String,Boolean> relaCpntIds() {
		return Collections.emptyMap();
	}		
	
	public String getScript() {
		return script;
	}


	public void setScript(String script) {
		this.script = script;
	}
	
	@Override
	public void bindFunctions(List<EngineScriptFunction> functions) {
		this.functions = functions;
	}
	

	@Override
	public void checkSelf() throws EngineCpntSelfCheckException {
		//脚本中需要需要包含_result字符			
		String temp = new String(script);
		if(StringUtils.isBlank(temp))
			throw new EngineCpntSelfCheckException("类型["+BUSI_DESC+"]名称["+name()+"]创建者["+right()+"]脚本内容为空");
		temp = temp.replaceAll("\\s+", "");
		if(temp.indexOf("output.") == -1){
			throw new EngineCpntSelfCheckException("类型["+BUSI_DESC+"]名称["+name()+"]创建者["+right()+"]脚本内容不包含输出字段，这将导致该脚本不能起到任何作用");
		}
	}

	@Override
	public Set<String> inputRela() {		
		return inputField;
	}

	@Override
	public Set<String> outputRela() {
		return outputField;
	}	
	
	@Override
	public void relaField(String type,String fieldKey) {
		if("input".equals(type))
			inputField.add(fieldKey);
		else if("output".equals(type))
			outputField.add(fieldKey);
		else
			throw new EngineCpntSelfCheckException("关联字段异常，没有该类型:"+type);			
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof EngineScriptStep)) return false;
		EngineScriptStep another = (EngineScriptStep) obj;	
		if(!StringUtils.equals(this.script, another.script)){
			logger.info("equals,this:{},another:{}",this.script, another.script);
			return false;
		}					
					
		return true;
	}		
	
	@Override
	public Map<String,String> dependFields() {
		Map<String,String> result = new HashMap<String,String>();
		for(String key : inputField){
			result.put(key,"input");
		}
		for(String key : outputField){
			result.put(key,"output");
		}
		return result;
	}	
	
	@Override
	public String type() {
		return BUSI_DESC;
	}


	
}
