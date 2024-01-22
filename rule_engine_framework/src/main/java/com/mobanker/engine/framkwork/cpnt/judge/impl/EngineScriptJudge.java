package com.mobanker.engine.framkwork.cpnt.judge.impl;

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

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpntBaseInfo;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineScriptAssist;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineSelfCheck;
import com.mobanker.engine.framkwork.cpnt.judge.EngineJudge;
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
 * 脚本规则
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年10月2日
 * @version 1.0
 */

public class EngineScriptJudge extends EngineCpntBaseInfo implements
		EngineJudge,EngineScriptAssist,EngineRelaCpnt,EngineSelfCheck,EngineDependField {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = LoggerFactory.getLogger(EngineScriptJudge.class);						
	
	private final String BUSI_DESC = "脚本规则";	
	private final static String JUDGE_RESULT_VAR = "_result";
	
	/**
	 * 相关入参字段
	 */
	protected Set<String> inputField = new HashSet<String>();
	/**
	 *  相关出参字段
	 */
	protected Set<String> outputField = new HashSet<String>();
	
	/**
	 * 脚本内容
	 */
	protected String script;
	/**
	 * 相关函数
	 */
	protected List<EngineScriptFunction> functions;

	@Override
	public Map<String,Boolean> relaCpntIds() {		
		return Collections.emptyMap();
	}	
	
	
	
//	@Override
//	public boolean judge(EngineTransferData etd) {
//		if(CollectionUtils.isEmpty(inputField)&&CollectionUtils.isEmpty(outputField)){
//			logger.warn(EngineUtil.logPrefix(etd, BUSI_DESC)+"该脚本不包含任何入参和出参,{},{}",name(),id());
//		}
//		
//		
//		logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"开始创建js脚本引擎:{},{}",name(),id());
//		
//		long t = System.currentTimeMillis();
//		
//		Context cx = Context.enter();
//		Scriptable scope = cx.initStandardObjects();
//		//初始化环境
//		EngineUtil.jsInitContext(cx, scope);
//		
//		logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"初始化环境，耗时:{}",System.currentTimeMillis() - t);
//		
//		t = System.currentTimeMillis();
//		
//        logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"开始绑定js函数");
//		//绑定函数
//		String functionStr = EngineUtil.getFunctionStr(etd, functions);
//		cx.evaluateString(scope, functionStr, null, 1, null);
//		
//		logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"开始绑定js变量");		
//		//绑定变量
//		EngineUtil.jsBingVar(this, cx, scope, etd);
//				
//		logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"绑定函数和变量，耗时:{}",System.currentTimeMillis() - t);
//		
//		t = System.currentTimeMillis();
//		Boolean result = null;
//		try{
//			cx.evaluateString(scope, script, null, 1, null);
//			result = (Boolean) scope.get(JUDGE_RESULT_VAR, scope);
//		}catch(Throwable e){			
//			throw new EngineScriptException(EngineUtil.logPrefix(etd, BUSI_DESC)+"脚本发生错误,cpntName:"+name()+",script:"+script,e);
//		}finally{
//			Context.exit();
//		}				
//		logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"运行完成，切退出JS环境，耗时:{}",System.currentTimeMillis() - t);
//		
//		logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"{}判断结果:{}",name(),result);
//		return result;
//	}

	@Override
	public boolean judge(EngineTransferData etd) {
		if(CollectionUtils.isEmpty(inputField)&&CollectionUtils.isEmpty(outputField)){
			logger.warn(EngineUtil.logPrefix(etd, BUSI_DESC)+"该脚本不包含任何入参和出参,{},{}",name(),id());
		}
		
        Boolean result = null;
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
			Object obj = scriptExecuter.getResult(JUDGE_RESULT_VAR);
			EngineAssert.checkNotNull(obj, "没有找到规则的返回变量，请确认该规则设置了_result的值");
			result = (Boolean) obj;
			
			logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"运行完成，耗时:{}",System.currentTimeMillis() - t);
		}catch(Throwable e){			
			throw new EngineScriptException(EngineUtil.logPrefix(etd, BUSI_DESC)+"脚本发生错误,cpntName:"+name()+",script:"+script,e);
		}					
		
		logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"{}判断结果:{}",name(),result);
		return result;
	}	
	
	@Override
	public void replaceVar(Map<String, String> map) {
		for(String key : map.keySet()){
			String name = map.get(key);
			EngineAssert.checkNotBlank(key);
			EngineAssert.checkNotBlank(name);			
			script = StringUtils.replace(script, "#"+name+"#", key);
		}
	}
	
	
	
	public String getScript() {
		return script;
	}
	
	public void setScript(String script) {
		this.script = script;
	}




	public List<EngineScriptFunction> getFunctions() {
		return functions;
	}


	public void setFunctions(List<EngineScriptFunction> functions) {
		this.functions = functions;
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
		if(temp.indexOf(JUDGE_RESULT_VAR+"=") == -1){
			throw new EngineCpntSelfCheckException("类型["+BUSI_DESC+"]名称["+name()+"]创建者["+right()+"]脚本内容不包含结果返回信息==>"+JUDGE_RESULT_VAR+"=");
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
		if(!(obj instanceof EngineScriptJudge)) return false;
		EngineScriptJudge another = (EngineScriptJudge) obj;	
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


//	public static void main(String[] args) {
//	
//	List<String> list = new ArrayList<String>();
//	list.add("ccc");
//	list.add("bbb");
//	list.add("ddd");	
//	
//	for(int i=0;i<1000;i++){
//		list.add("i="+i);
//	}
//	
//	
//	long time1 = System.currentTimeMillis();
//	
//	ScriptEngineManager manager = new ScriptEngineManager();
//	ScriptEngine engine = manager.getEngineByName("javascript");
//	
//	
//	
//	
//	engine.put("a", 4);
//	engine.put("b", 3);
//	engine.put("list", list);
//	
//	
//	
//	for(int i=0;i<100;i++){
//		engine.put("param"+i, i);
//	}
//	
//	
//	
//	
//	//Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
//	try {
//		// 只能为Double，使用Float和Integer会抛出异常
//		Double result = (Double) engine.eval("parseInt(a+b)");
//
//		engine.eval("var c=a+b;");
//		
//		
//		engine.eval("list.add('5');");
//		
//		engine.eval("println(list);");
//		//engine.eval("println(list.class())");
//		
//		engine.eval("var result = false;");
//		
//		
//		engine.eval("for(xx in list){println(xx);}");
//		
//		engine.eval("{println(list.iterator());}");
//		
//		engine.eval("for(x in list.iterator()){println(x);}");
//		
//		engine.eval("for(var i=0;i<list.size();i++){if(list.get(i)=='ccc'){result = true;}}");
//		
//		
//		engine.eval("var person=new Object();");
//		engine.eval("person.firstname='Bill'");
//		engine.eval("person.lastname='Gates'");
//		engine.eval("person.age=56");
//		
//		Object rs = engine.get("person");
//		
//		String jsonStr = JSONObject.toJSONString(rs);
//		JSONObject obj = JSON.parseObject(jsonStr);
//		
//		System.out.println(obj.get("age").getClass().getSimpleName());
//		
//		Double c = (Double) engine.get("c");
//		
//		Boolean tf =  (Boolean) engine.get("result");
//
//		//System.out.println("list = " + list);
//		System.out.println("tf = " + tf);
//
//		System.out.println("consume = "+(System.currentTimeMillis()-time1));
//		
//	} catch (ScriptException e) {
//		e.printStackTrace();
//
//	}
//
//}
	
	

}
