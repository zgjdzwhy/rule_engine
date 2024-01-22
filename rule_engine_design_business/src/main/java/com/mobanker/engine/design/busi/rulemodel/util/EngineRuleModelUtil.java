package com.mobanker.engine.design.busi.rulemodel.util;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.exception.EngineAssert;

public abstract class EngineRuleModelUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(EngineRuleModelUtil.class);	
	
	/**
	 * 检查组件基本要素
	 * @param json
	 */
	public static void checkCpntBaseInfo(JSONObject json){
		EngineAssert.checkNotNull(json);
		EngineAssert.checkNotBlank(json.getString("id"),"组件属性id不存在");
		EngineAssert.checkNotBlank(json.getString("type"),"组件属性type不存在");		
		EngineAssert.checkNotBlank(json.getString("name"),"组件属性name不存在");
		EngineAssert.checkNotBlank(json.getString("right"),"组件属性right不存在");
		EngineAssert.checkNotBlank(json.getString("updatetime"),"组件属性updatetime不存在");
	}
	
	public static void removeCpnt(List<JSONObject> all,String cpntId){
		Iterator<JSONObject> iterator = all.iterator();
		while(iterator.hasNext()){
			JSONObject one = iterator.next();
			if(StringUtils.equals(one.getString("id"), cpntId)){
				iterator.remove();
			}
		}		
	}

	public static void mergeCpnts(List<JSONObject> srcList,List<JSONObject> addList,String right){
		
		Iterator<JSONObject> iterator = srcList.iterator();
		while(iterator.hasNext()){
			JSONObject one = iterator.next();
			String cpntRight = one.getString("right");			
			if(StringUtils.equals(cpntRight, right)) iterator.remove();			
		}				
		srcList.addAll(addList);		
	}	
	
//	public static List<JSONObject> mergeCpnts(List<JSONObject> srcList,List<JSONObject> addList,String right){
//		
//		List<JSONObject> all = new LinkedList<JSONObject>(srcList);
//		
//		Set<String> needMergeIds = new HashSet<String>();
//		for(JSONObject one : addList){
//			if(StringUtils.isBlank(one.getString("id"))) continue;
//			needMergeIds.add(one.getString("id"));			
//		}
//		logger.info("self ids:{}",needMergeIds);
//		Iterator<JSONObject> iterator = all.iterator();
//		while(iterator.hasNext()){
//			JSONObject one = iterator.next();
//			if(needMergeIds.contains(one.getString("id"))){
//				iterator.remove();
//			}
//		}				
//		all.addAll(addList);
//		
//		return all;
//	}
	
	
	public static JSONObject getCpnt(List<JSONObject> all,String cpntId){
		for(JSONObject one : all){
			if(StringUtils.equals(one.getString("id"), cpntId))
				return one;
		}
		return null;
	}
	
}
