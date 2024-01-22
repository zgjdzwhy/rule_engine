package com.mobanker.engine.framkwork.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;
import com.mobanker.engine.framkwork.script.function.EngineScriptFunction;



/**
 * 辅助工具集合
 * <p>Title: EngineUtil.java<／p>
 * <p>Description: <／p>
 * <p>Company: mobanker.com<／p>
 * @author taojinn
 * @date 2016年2月19日
 * @version 1.0
 */
public abstract class EngineUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(EngineUtil.class);			

//	private static ThreadLocal<ScriptEngine> curJsEngine = new ThreadLocal<ScriptEngine>(){
//		@Override
//		protected ScriptEngine initialValue(){
//			ScriptEngineManager scriptEngineManager = new ScriptEngineManager();  			
//			ScriptEngine engine = scriptEngineManager.getEngineByName("nashorn");	
//			if(engine==null)
//				throw new EngineException("环境缺少nashorn引擎!请升级jdk");
//			logger.info("创建一个新的js执行器:"+engine);
//			return engine;
//		}		
//	};	
//		
//	
//	public static ScriptEngine getCurJsEngine(){
//		return curJsEngine.get();
//	}
//	/**
//	 * 当前线程的线程池 永久利用 小心使用
//	 */
//	private static ThreadLocal<ExecutorService> curExecutorService = new InheritableThreadLocal<ExecutorService>(){
//		@Override
//		protected ExecutorService initialValue(){
//			return new ThreadPoolExecutor(5, 10, 10, 
//					TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
//		}		
//	};	
//	
//	
//	
//	/**
//	 * 当前线程的线程池
//	 * @return
//	 */
//	public static ExecutorService getCurExecutorService(){
//		return curExecutorService.get();
//	}	
	
	/**
	 * 当前线程的查询结果缓存
	 */
//	private static ThreadLocal<EngineQueryCacheMap> curThreadQueryCache = new InheritableThreadLocal<EngineQueryCacheMap>(){
//		@Override
//		protected EngineQueryCacheMap initialValue(){
//			return new EngineQueryCacheMap();
//		}		
//	};
	/**
	 * 当前线程的查询结果缓存
	 * @return
	 */
//	public static EngineQueryCacheMap getCurThreadQueryCache(){
//		return curThreadQueryCache.get();
//	}
	/**
	 * 当前线程的查询项列表，当查询项添加到线程池中则加入，当查询完成后删除，这样当超时的时候查询这个容器中剩余的查询项就知道哪些查询项超时
	 * @return
	 */
//	public static EngineQueryTaskList getCurThreadQueryTaskList(){
//		return curThreadQueryTaskList.get();
//	}

	/**
	 * 统一日志前缀
	 * @param etd
	 * @param business
	 * @return
	 */
	public static String logPrefix(EngineTransferData etd,String business){		
		String flag = StringUtils.abbreviate(etd.getAppRequestId(),60);
		flag = StringUtils.equals(flag, "{}")?"no param":flag; 
		return "("+etd.getProductType()+":"+flag+")"+business+"->";		
	}
	
	public static String logPrefix(String business){
		return business+"->";		
	}	

	public static String transUnixTime2YMD(Object unixtime){
		if(unixtime == null) return null;
		String temp = String.valueOf(unixtime);		
		if(StringUtils.isBlank(temp)) return null;
		Long longtemp =	Long.valueOf(temp+"000");
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMddHHmmss" );		
		return sdf.format(longtemp);
	}
	
//	@Deprecated
//	public static void jsInitContext(Context cx,Scriptable scope){
//		try {
//			ScriptableObject.defineClass(scope, ConsoleScriptable.class);
//		} catch (Exception e) {
//			throw new EngineException("rhino脚本申明类发生异常",e);
//		} 
//		ConsoleScriptable $console = (ConsoleScriptable) cx.newObject(scope, ConsoleScriptable.class.getSimpleName());
//		$console.setPrintStream(System.out);
//        ScriptableObject.putProperty(scope, "$console", $console); //在js中使用$console = System.out
//	}
//	
//	@Deprecated
//	public static void jsBingVar(EngineScriptAssist scriptCpnt, Context cx,Scriptable scope,EngineTransferData etd){
//		//绑定变量
//		cx.evaluateString(scope, "var input = {};var output = {};", null, 1, null);
//		EngineParam params = null;
//		params = etd.getInputParam();		
//		for(String key : scriptCpnt.inputRela()){
//			Object value = params.get(key);
//			EngineAssert.checkNotNull(value, EngineUtil.logPrefix(etd, "绑定变量")+"必要参数缺失,组件名称:%s,字段名:input.%s",scriptCpnt.name(),key);			
//			key = key.trim();
//			scope.put(key, scope, EngineUtil.transferNativeArr(value));
//			cx.evaluateString(scope, "input."+key+" = "+key+";"+key+" = undefined;", null, 1, null);
//			logger.info(EngineUtil.logPrefix(etd, "绑定变量")+"将变量放入js环境中,input类型:{},{},valueType:{}",key,value,value==null?"null":value.getClass().getName());			
//		}
//		
//		params = etd.getOutputParam();
//		for(String key : scriptCpnt.outputRela()){
//			Object value = params.get(key);
//			if(value == null){
//				logger.info(EngineUtil.logPrefix(etd, "绑定变量")+"发现有关联output字段内容为空,字段名:{}",key);	
//				continue;
//			}
//			//EngineAssert.checkNotNull(value, EngineUtil.logPrefix(etd, "绑定变量")+"参数字段的值不存在,组件名称:%s,字段名:output.%s",scriptCpnt.name(),key);
//			key = key.trim();
//			scope.put(key, scope, EngineUtil.transferNativeArr(value));
//			cx.evaluateString(scope, "output."+key+" = "+key+";"+key+" = undefined;", null, 1, null);			
//			logger.info(EngineUtil.logPrefix(etd, "绑定变量")+"将变量放入js环境中,output类型:{},{},valueType:{}",key,value,value==null?"null":value.getClass().getName());
//		}
//	}
//	@Deprecated
//	public static void nashornBingVar(EngineScriptAssist scriptCpnt, ScriptEngine nashorn,EngineTransferData etd) throws ScriptException{
//		//绑定变量
//		StringBuilder sb = new StringBuilder();
//	
//		nashorn.eval("var input = {};var output = {};");
//		EngineParam params = null;
//		params = etd.getInputParam();		
//		for(String key : scriptCpnt.inputRela()){
//			Object value = params.get(key);
//			EngineAssert.checkNotNull(value, EngineUtil.logPrefix(etd, "绑定变量")+"必要参数缺失,组件名称:%s,字段名:input.%s",scriptCpnt.name(),key);			
//			key = key.trim();
//			nashorn.put(key, value);
//			//nashorn.eval("input."+key+" = "+key+";");
//			sb.append("input."+key+" = "+key+";");
//			
//			logger.info(EngineUtil.logPrefix(etd, "绑定变量")+"将变量放入js环境中,input类型:{},{},valueType:{}",key,value,value==null?"null":value.getClass().getName());			
//		}
//		
//		params = etd.getOutputParam();
//		for(String key : scriptCpnt.outputRela()){
//			Object value = params.get(key);
//			if(value == null){
//				logger.info(EngineUtil.logPrefix(etd, "绑定变量")+"发现有关联output字段内容为空,字段名:{}",key);	
//				continue;
//			}
//			//EngineAssert.checkNotNull(value, EngineUtil.logPrefix(etd, "绑定变量")+"参数字段的值不存在,组件名称:%s,字段名:output.%s",scriptCpnt.name(),key);
//			key = key.trim();
//			nashorn.put(key, value);
//			sb.append("output."+key+" = "+key+";");
//			//nashorn.eval("output."+key+" = "+key+";");			
//			logger.info(EngineUtil.logPrefix(etd, "绑定变量")+"将变量放入js环境中,output类型:{},{},valueType:{}",key,value,value==null?"null":value.getClass().getName());
//		}
//		
//		nashorn.eval(sb.toString());
//	}	
	
	
	
	/**
	 * ScriptEngine上运行函数
	 * @param engine
	 * @param etd
	 * @param functions
	 * @throws ScriptException
	 */
	public static String getFunctionStr(EngineTransferData etd,List<EngineScriptFunction> functions){
		StringBuilder sb = new StringBuilder();
		String functionStr = null;
		if(!CollectionUtils.isEmpty(functions)){
			for(EngineScriptFunction function : functions){
				functionStr = function.getFunction();
				sb.append(functionStr+"\n");
			}							
		}		 
		return sb.toString();
	}	

//	@Deprecated
//	public static Object transferNativeArr(Object obj){
//		if(obj instanceof List){
//			@SuppressWarnings("unchecked")
//			List<Object> list = (List<Object>) obj;
//			Object[] arr = list.toArray();
//			NativeArray nativeArray = new NativeArray(arr);
//			return nativeArray;
//		}else
//			return obj;
//	}
	
	public static String get14CurrentDateTime(){
		return DateUtils.convert(new Date(), DateUtils.DATE_TIMESTAMP_SHORT_FORMAT);
	}
	
	public static Map<String,Boolean> relaNonlineal(EngineRelaCpnt cpnt){
		Map<String,Boolean> result = new HashMap<String,Boolean>();
		for(String key : cpnt.relaCpntIds().keySet()){
			result.put(key, false);
		}		
		return result;
	}
	

	public static String getUUID(){
		String uuid = UUID.randomUUID().toString();
		return StringUtils.replace(uuid, "-", "_");
	}
	
	/**
	 * 因为es存储是一个近实时的系统，需要过一秒才能查询到数据
	 */
	public static void waitSecond(){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		
		//可以强制刷新，但暂时不准备使用这种方式
//		IndexResponse response = client.prepareIndex("taojin_test", "ttlTest")
//		        .setSource(testJson.toJSONString())
//		        .get();		
//		response.setForcedRefresh(true);
//		System.out.println("teststet:"+response.forcedRefresh());
	}
	

	/**
	 * @param <T> 
	* @Title: sortMapByKey 
	* @Description: TODO(按map主键进行排序)
	* 参数@param oriMap
	* 参数@return
	* @return Map<String,Object>    返回类型 
	* @throws 
	*/
	public static <T> Map<String, T> sortMapByKey(Map<String, T> oriMap) {  
	    if (oriMap == null || oriMap.isEmpty()) {  
	        return null;  
	    }  
	    Map<String, T> sortedMap = new TreeMap<String, T>(new Comparator<String>() {  
	        public int compare(String key1, String key2) {  
	            int intKey1 = 0, intKey2 = 0;  
	            try {  
	                intKey1 = getInt(key1);  
	                intKey2 = getInt(key2);  
	            } catch (Exception e) {  
	                intKey1 = 0;   
	                intKey2 = 0;  
	            }  
	            return intKey1 - intKey2;  
	        }});  
	    sortedMap.putAll(oriMap);  
	    return sortedMap;  
	} 
	
	private static int getInt(String str) {  
	    int i = 0;  
	    try {  
	        Pattern p = Pattern.compile("^\\d+");  
	        Matcher m = p.matcher(str);  
	        if (m.find()) {  
	            i = Integer.valueOf(m.group());  
	        }  
	    } catch (NumberFormatException e) {  
	        e.printStackTrace();  
	    }  
	    return i;  
	}

	/**
	 * 名称中不能含有<>符号和单引号，双引号
	 * @param name
	 */
	public static void checkNameFormat(String name,String desc){
		EngineAssert.checkNotBlank(name, "name is blank!");
		
		EngineAssert.checkArgument(name.indexOf("<")==-1, desc+"中不能有<符号");
		EngineAssert.checkArgument(name.indexOf(">")==-1, desc+"中不能有>符号");
		
		EngineAssert.checkArgument(name.indexOf("\'")==-1, desc+"中不能有单引号");
		EngineAssert.checkArgument(name.indexOf("\"")==-1, desc+"中不能有双引号");
		
		EngineAssert.checkArgument(name.indexOf("\n")==-1, desc+"中不能有换号符号");
		EngineAssert.checkArgument(name.indexOf("\r")==-1, desc+"中不能有回车符号");
	}
	
	public static void checkNameFormat(String name){
		checkNameFormat(name,"名称");
	}
	
	/**
	 * 名称中不能含有<>符号和单引号，双引号
	 * @param name
	 */
	public static void checkNameFormat(String name,int minLength,int maxLength,String desc){
		checkNameFormat(name,desc);		
		EngineAssert.checkArgument(name.length()>=minLength, desc+"长度必须大等于"+minLength);
		EngineAssert.checkArgument(name.length()<=maxLength, desc+"长度必须小等于"+maxLength);
	}
			
	public static Throwable getBaseCause(Throwable e){
		Throwable cause = e.getCause();
		if(cause != null) 
			return getBaseCause(cause);
		else
			return e;
	}
	
	
	public static void main(String[] args) {
		
		Exception e = new EngineException("上层", new RuntimeException("运行时",new IOException("IO")));
		Throwable t = EngineUtil.getBaseCause(e);
		System.out.println(t);
		System.out.println(t.getClass().getSimpleName());
	}
}
