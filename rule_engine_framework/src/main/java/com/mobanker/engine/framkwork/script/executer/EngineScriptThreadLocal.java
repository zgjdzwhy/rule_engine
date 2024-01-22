package com.mobanker.engine.framkwork.script.executer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineException;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年2月15日
 * @version 1.0
 */
public abstract class EngineScriptThreadLocal{
	
	private final static Logger logger = LoggerFactory.getLogger(EngineScriptThreadLocal.class);			

	private static Class<?> clazz;
	
	static{//静态域用于判断class是否存在
		try {
			clazz = Class.forName("com.mobanker.engine.framkwork.script.executer.impl.EngineScriptExecuterImpl");
			logger.info("找到了js引擎类:"+clazz);
		} catch (ClassNotFoundException e) {
			throw new EngineException("严重错误，找不到js引擎的实现类！");
		}		
	}
	
	private static ThreadLocal<EngineScriptExecuter> curThreadEngine = new ThreadLocal<EngineScriptExecuter>(){
		
		@Override
		protected EngineScriptExecuter initialValue(){
			EngineAssert.checkNotNull(clazz,"严重错误，找不到js引擎的实现类！");
			Object obj;
			try {
				obj = clazz.newInstance();						
			} catch (InstantiationException | IllegalAccessException e) {
				throw new EngineException("严重错误，无法实例化js引擎对象！请查看EngineScriptExecuterImpl类是否支持空参数的初始化方法");
			}
			if(!(obj instanceof EngineScriptExecuter))
				throw new EngineException("严重错误，js引擎对象没有实现EngineScriptExecuter接口！");													
			EngineScriptExecuter engine = (EngineScriptExecuter) obj;
			logger.info("当前线程的js执行器创建成功");	
			
			try {
				logger.info("js引擎，初始化绑定原型方法{}",SYSTEM_SCRIPT);	
				engine.eval(SYSTEM_SCRIPT);
			} catch (Exception e) {
				throw new EngineException("严重错误，js引擎绑定原型方法失败！");		
			}
			
			return engine;
		}		
	};	
			
	public static EngineScriptExecuter getCurThreadEngine(){
		return curThreadEngine.get();
	}
	
	/**
	 * 用于测试用
	 */
	public static void initCurEngine(){
		curThreadEngine.remove();
	}
	
	//加减乘除方法  js小数位使用，避免精度丢失
	private static final String SYSTEM_SCRIPT = "//\u52A0\u6CD5   \nNumber.prototype.add = function(arg){   \n  var r1,r2,m; "
			+ "  \n  try{r1=this.toString().split(\".\")[1].length}catch(e){r1=0}   \n  try{r2=arg.toString().split(\".\")[1].length}"
			+ "catch(e){r2=0}   \n  m=Math.pow(10,Math.max(r1,r2))   \n  return (this*m+arg*m)/m   \n}  \n//\u51CF\u6CD5   "
			+ "\nNumber.prototype.sub = function (arg){   \n    return this.add(-arg);   \n}   \n\n//\u4E58\u6CD5   "
			+ "\nNumber.prototype.mul = function (arg)   \n{   \n    var m=0,s1=this.toString(),s2=arg.toString();   \n    "
			+ "try{m+=s1.split(\".\")[1].length}catch(e){}   \n    try{m+=s2.split(\".\")[1].length}catch(e){}   \n    "
			+ "return Number(s1.replace(\".\",\"\"))*Number(s2.replace(\".\",\"\"))/Math.pow(10,m)   \n}   \n\n//\u9664\u6CD5  "
			+ " \nNumber.prototype.div = function (arg){   \n    var t1=0,t2=0,r1,r2;   \n    "
			+ "try{t1=this.toString().split(\".\")[1].length}catch(e){}   \n    "
			+ "try{t2=arg.toString().split(\".\")[1].length}catch(e){}   \n    with(Math){   \n        "
			+ "r1=Number(this.toString().replace(\".\",\"\"))   \n        r2=Number(arg.toString().replace(\".\",\"\"))   \n     "
			+ "   return (r1/r2)*pow(10,t2-t1);   \n    }   \n}";
	
	
}
