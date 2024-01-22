package com.mobanker.engine.framkwork.parse.manager;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineCpntIsEmptyException;
import com.mobanker.engine.framkwork.exception.EngineCpntParseException;
import com.mobanker.engine.framkwork.parse.EngineCpntParser;
import com.mobanker.engine.framkwork.parse.impl.EngineStandardParser;
import com.mobanker.engine.framkwork.util.EngineUtil;

public class EngineParseManager {

	private static final String BUSI_DESC = "规则模型组件解析";
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	private Collection<EngineCpnt> externalCpntList = new LinkedList<EngineCpnt>();
	
	public <T extends EngineCpnt> void addExternalCpnt(Collection<T> cpntList){
		externalCpntList.addAll(cpntList);
	}

	
	public Map<String,EngineCpnt> parse(List<JSONObject> jsonList){				
		return parseNeeded(jsonList,null);
	}	
	
	public Map<String,EngineCpnt> parse(List<JSONObject> jsonList,String... cpntIds){
		Set<String> needParseSet = new HashSet<String>(Arrays.asList(cpntIds));		
		return parseNeeded(jsonList,needParseSet);
	}	
	
	/**
	 * 解析整个流程
	 * @param jsonList
	 * @return
	 * @throws StackOverflowError
	 */
	private Map<String,EngineCpnt> parseNeeded(List<JSONObject> jsonList,Set<String> needParseSet){				
		if(CollectionUtils.isEmpty(jsonList))
			throw new EngineCpntIsEmptyException(EngineUtil.logPrefix(BUSI_DESC)+"组件数量为空");		
		
		//判断模型中id是否重复
		duplicationId(jsonList);
		
		EngineCpntParser engineCpntParser = new EngineStandardParser(jsonList);
		if(!CollectionUtils.isEmpty(externalCpntList)){//首先注册查询组件
			engineCpntParser.addExternalCpnt(externalCpntList);
		}				
		
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"组件数量:"+jsonList.size());
					
		for(JSONObject oneJson : jsonList){
			String cpntId = oneJson.getString("id");
			String cpntName = oneJson.getString("name");
			String cpntType = oneJson.getString("type");
			if(StringUtils.isBlank(cpntId))
				throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"组件节点没有ID属性:"+oneJson.toJSONString());
						
			if(needParseSet != null){
				if(!needParseSet.contains(cpntId)){
					logger.info(EngineUtil.logPrefix(BUSI_DESC)+"该组件ID不需要被解析,cpntName:"+cpntName+",cpntId:"+cpntId);
					continue;
				}				
			}
			
			EngineCpnt cpnt = null;
			try{
				cpnt = engineCpntParser.parse(cpntId, EngineCpnt.class);
			}catch(Exception e){
				throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"解析组件失败,组件名称:"+cpntName+",组件类型:"+cpntType+",原因:"+e.getMessage(),e);
			}catch(StackOverflowError e){
				throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"解析组件失败,组件中存在无限死循环，请查看相关组件,cpntName:"+cpntName+",cpntType:"+cpntType,e);
			}
			
			if(cpnt == null)
				throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"没有解析出组件,组件名称:"+cpntName);
			
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"完成"+cpnt.id()+"-"+cpnt.name()+"的组件解析工作");
		}
		
		Map<String,EngineCpnt> allCpnt = engineCpntParser.getParsedCpnt();
		return allCpnt;
	}	
	
	
	
	
	/**
	 * 解析整个流程
	 * @param jsonList
	 * @return
	 * @throws StackOverflowError
	 */
//	public Map<String,EngineCpnt> parse(List<JSONObject> jsonList){				
//		if(CollectionUtils.isEmpty(jsonList))
//			throw new EngineCpntIsEmptyException(EngineUtil.logPrefix(BUSI_DESC)+"组件数量为空");		
//		
//		//判断模型中id是否重复
//		duplicationId(jsonList);
//		
//		EngineCpntParser engineCpntParser = new EngineStandardParser(jsonList);
//		if(!CollectionUtils.isEmpty(externalCpntList)){//首先注册查询组件
//			engineCpntParser.addExternalCpnt(externalCpntList);
//		}				
//		
//		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"组件数量:"+jsonList.size());
//		
//		for(JSONObject oneJson : jsonList){
//			String cpntId = oneJson.getString("id");
//			String cpntName = oneJson.getString("name");
//			String cpntType = oneJson.getString("type");
//			if(StringUtils.isBlank(cpntId))
//				throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"组件节点没有ID属性:"+oneJson.toJSONString());
//						
//			EngineCpnt cpnt = null;
//			try{
//				cpnt = engineCpntParser.parse(cpntId, EngineCpnt.class);
//			}catch(Exception e){
//				throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"解析组件失败,组件名称:"+cpntName+",组件类型:"+cpntType+",原因:"+e.getMessage(),e);
//			}catch(StackOverflowError e){
//				throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"解析组件失败,组件中存在无限死循环，请查看相关组件,cpntName:"+cpntName+",cpntType:"+cpntType,e);
//			}
//			
//			if(cpnt == null)
//				throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"没有解析出组件,组件名称:"+cpntName);
//			
//			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"完成"+cpnt.id()+"-"+cpnt.name()+"的组件解析工作");
//		}
//		
//		Map<String,EngineCpnt> allCpnt = engineCpntParser.getParsedCpnt();
//		return allCpnt;
//	}
	
	/**
	 * 解析某个组件
	 * @param jsonList
	 * @param cpntId
	 * @return
	 * @throws StackOverflowError
	 */
//	public Map<String,EngineCpnt> parseOne(List<JSONObject> jsonList,String cpntId){		
//		if(CollectionUtils.isEmpty(jsonList))
//			throw new EngineCpntIsEmptyException(EngineUtil.logPrefix(BUSI_DESC)+"组件数量为空");		
//		EngineAssert.checkNotBlank(cpntId, EngineUtil.logPrefix(BUSI_DESC)+"组件id为空");
//		
//		//判断模型中id是否重复
//		duplicationId(jsonList);
//		
//		EngineCpntParser engineCpntParser = new EngineStandardParser(jsonList);
//		if(!CollectionUtils.isEmpty(externalCpntList)){//首先注册查询组件
//			engineCpntParser.addExternalCpnt(externalCpntList);
//		}
//
//		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"生产组件解析器成功");
//		
//		if(CollectionUtils.isEmpty(jsonList))
//			throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"组件数量为空");
//		
//		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"组件数量:"+jsonList.size());
//		
//		EngineCpnt cpnt = null;
//		try{
//			cpnt = engineCpntParser.parse(cpntId, EngineCpnt.class);
//		}catch(Exception e){
//			throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"解析组件失败,cpntId:"+cpntId+",原因:"+e.getMessage(),e);
//		}
//		
//		if(cpnt == null)
//			throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"没有解析出组件,cpntId:"+cpntId);
//		Map<String,EngineCpnt> cpnts = engineCpntParser.getParsedCpnt();
//		
//		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"完成"+cpnt.id()+"-"+cpnt.name()+"的组件解析工作，总共涉及到组件数量:{}",cpnts.size());
//		
//		return cpnts;
//	}
		
	
	private void duplicationId(List<JSONObject> jsonList){
		Collection<String> exists = new HashSet<String>();
		for(JSONObject one : jsonList){
			String id = one.getString("id");
			EngineAssert.checkNotBlank(id);			
			EngineAssert.checkArgument(!exists.contains(id), "规则模型id重复!id:%s",id);			
			exists.add(id);
		}		
	}
	
}
