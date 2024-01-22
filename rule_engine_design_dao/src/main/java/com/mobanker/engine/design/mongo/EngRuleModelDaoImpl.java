package com.mobanker.engine.design.mongo;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.common.EngineCommonUtil;
import com.mobanker.engine.design.mongo.assist.RuleModelTable;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

//@Repository("mongoRuleModelDao")
public class EngRuleModelDaoImpl implements EngRuleModelDao{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());						
	
	private final String BUSI_DESC = "持久化对象模型";
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	
	
	@Override
	public void saveRuleModel(String productType, String cpntId, 
			JSONObject jsonObject) {				
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(RuleModelTable.SELF.getName());
		
		jsonObject.put("productType", productType);
		//先删除
		BasicDBObject query = new BasicDBObject("productType",productType);
		query.put("id", cpntId);
		table.remove(query);
		logger.info("remove self cpnt,id:{}",cpntId);		
		
		//再插入
		DBObject dbObject = (DBObject) JSON.parse(jsonObject.toJSONString());
		table.insert(dbObject);
	}

	@Override
	public void deleteRuleModel(String productType, String cpntId) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(RuleModelTable.SELF.getName());
		BasicDBObject query = new BasicDBObject("productType",productType);
		query.put("id", cpntId);
		table.remove(query);
	}
	
	
	@Override
	public List<JSONObject> getRuleModel(RuleModelTable ruleModelTable,
			String productType, String username, String version) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(ruleModelTable.getName());
		BasicDBObject query = new BasicDBObject("productType",productType);
		
		if(StringUtils.isNotBlank(username)){
			query.put("right", username);
		}
		if(StringUtils.isNotBlank(version)){
			query.put("version", version);
		}
		DBCursor cursor = table.find(query);		
		List<JSONObject> list = new LinkedList<JSONObject>();
		
		if(cursor != null){			
			logger.info(BUSI_DESC+"cursor hasNext:{}",cursor.hasNext());		
			while (cursor.hasNext()) {   
				String jsonText = cursor.next().toString();
				list.add(JSONObject.parseObject(jsonText));				
			} 			
		}else{
			logger.warn("游标不存在");
		}
		return list;
	}

	@Override
	public JSONObject getRuleModel(RuleModelTable ruleModelTable,
			String productType, String cpntId) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(ruleModelTable.getName());
		BasicDBObject query = new BasicDBObject("productType",productType);
		query.put("id", cpntId);
		
		DBCursor cursor = table.find(query);			
		if(cursor != null){			
			logger.info(BUSI_DESC+"cursor hasNext:{}",cursor.hasNext());
			while (cursor.hasNext()) {   
				String jsonText = cursor.next().toString();
				JSONObject result = JSONObject.parseObject(jsonText);
				return result;
			} 			
		}else{
			logger.warn("游标不存在");
		}
		return null;
	}	
	

	@Override
	public void mergeRuleModel(String productType) {
		List<JSONObject> list = getRuleModel(RuleModelTable.SELF,productType,null,null);
		if(CollectionUtils.isEmpty(list)){			
			logger.warn("self list is empty,productType:{}",productType);
			return;
		}
		logger.info("merge list size:{},productType:{}",list.size(),productType);	
		
		
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(RuleModelTable.MERGE.getName());
		
		//删除该用户
		BasicDBObject query = new BasicDBObject("productType",productType);		
		table.remove(query);
		logger.info("remove merge,productType:{}",productType);	
		int i=0;
		for(JSONObject one : list){
			DBObject dbObject = (DBObject) JSON.parse(one.toJSONString());
			table.insert(dbObject);
			i++;
		}		
		logger.info("insert merge,productType:{},count:{}",productType,i);
	}

	@Override
	public String applyReleaseRuleModel(String productType) {
		List<JSONObject> list = getRuleModel(RuleModelTable.MERGE,productType,null,null);
				
		String version = EngineCommonUtil.get14CurrentDateTime();
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(RuleModelTable.RELEASE.getName());
		
		for(JSONObject one : list){
			one.put("version", version);
			one.remove("_id");
			DBObject dbObject = (DBObject) JSON.parse(one.toJSONString());
			table.insert(dbObject);
		}	
		
		return version;
	}

	@Override
	public int batchInsert(List<JSONObject> jsonList,RuleModelTable ruleModelTable) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(ruleModelTable.getName());
		int count = 0;
		for(JSONObject json : jsonList){
			DBObject dbObject = (DBObject) JSON.parse(json.toJSONString());
			table.insert(dbObject);
			count++;
		}
		return count;
	}

	@Override
	public void deleteByProductType(String productType,RuleModelTable ruleModelTable) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(ruleModelTable.getName());
		BasicDBObject query = new BasicDBObject("productType",productType);
		table.remove(query);
	}

	
	@Override
	public JSONObject getHistoryRuleModel(
			String productType, String cpntId, String version) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(RuleModelTable.RELEASE.getName());
		BasicDBObject query = new BasicDBObject("productType",productType);
		query.put("id", cpntId);
		query.put("version", version);
		
		DBCursor cursor = table.find(query);			
		if(cursor != null){			
			logger.info(BUSI_DESC+"cursor hasNext:{}",cursor.hasNext());
			while (cursor.hasNext()) {   
				String jsonText = cursor.next().toString();
				JSONObject result = JSONObject.parseObject(jsonText);
				return result;
			} 			
		}else{
			logger.warn("游标不存在");
		}
		return null;
	}



}
