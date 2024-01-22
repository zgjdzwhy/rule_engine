package com.mobanker.engine.framkwork.parse.factory;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 已经解析过的组件都注册到这里
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年11月3日
 * @version 1.0
 */
public class EngineCpntRegisterFactory{

	private static final String BUSI_DESC = "组件注册工厂";
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	/**
	 * 注册工厂
	 */
	private Map<String,EngineCpnt> registerFactory = new HashMap<String, EngineCpnt>();
	
	/**
	 * 注册组件
	 * @param cpnt
	 */
	public void register(EngineCpnt cpnt){
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"注册组件:{},{}",cpnt.id(),cpnt.name());
		registerFactory.put(cpnt.id(), cpnt);
	}
	
	/**
	 * 获取已经注册过的组件
	 * @param cpntId
	 * @param clazz
	 * @return 如果该组件ID没有注册过，则返回null
	 */
	public <T extends EngineCpnt> T getExistCpnt(String cpntId,Class<T> clazz){
		EngineCpnt cpnt = registerFactory.get(cpntId);
		if(cpnt == null) return null;
		try{
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"找到存在的组件:{},{}",cpnt.id(),cpnt.name());
			@SuppressWarnings("unchecked")
			T result = (T) cpnt;  
			return result;
		}catch(ClassCastException e){
			throw new EngineException(EngineUtil.logPrefix(BUSI_DESC)+"组件转换类型错误,id:"+cpntId+" class:"+clazz.getSimpleName()+" object:"+cpnt.getClass().getSimpleName(),e);
		}
	}

	public Map<String, EngineCpnt> getRegisterCpnt() {
		return registerFactory;
	}
}
