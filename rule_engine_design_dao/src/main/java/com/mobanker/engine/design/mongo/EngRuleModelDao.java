package com.mobanker.engine.design.mongo;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.design.mongo.assist.RuleModelTable;

public interface EngRuleModelDao {
	
	/**
	 * 
	 * @param productType
	 * @param cpntId
	 * @param jsonObject
	 */
	public void saveRuleModel(String productType,String cpntId,JSONObject jsonObject);
	
	/**
	 * 
	 * @param productType
	 * @param cpntId
	 */
	public void deleteRuleModel(String productType,String cpntId);

	/**
	 * 获取组件信息列表
	 * @param ruleModelTable
	 * @param productType 不可为空
	 * @param username 可为空
	 * @param version 可为空
	 * @return
	 */
	public List<JSONObject> getRuleModel(RuleModelTable ruleModelTable,String productType,String username,String version);
	
	/**
	 * 获取组件信息
	 * @param ruleModelTable
	 * @param productType 不可为空
	 * @param cpntId 不可为空
	 * @return
	 */
	public JSONObject getRuleModel(RuleModelTable ruleModelTable,String productType,String cpntId);
	
	
	/**
	 * 获取历史组件信息
	 * @param ruleModelTable
	 * @param productType 不可为空
	 * @param cpntId 不可为空
	 * @return
	 */
	public JSONObject getHistoryRuleModel(String productType,String cpntId,String version);	
	
	
	/**
	 * 
	 * @param productType 不可为空 
	 * @param username 不可为空
	 */
	public void mergeRuleModel(String productType);
	
	/**
	 * 将对象模型转移到发布区
	 * @param productType 
	 * @return version
	 */
	public String applyReleaseRuleModel(String productType);
	
	/**
	 * 批量插入
	 * @param productType 
	 * @return version
	 */
	public int batchInsert(List<JSONObject> jsonList,RuleModelTable ruleModelTable);
	
	/**
	 * 
	 * @param productType
	 * @param cpntId
	 */
	public void deleteByProductType(String productType,RuleModelTable ruleModelTable);

}
