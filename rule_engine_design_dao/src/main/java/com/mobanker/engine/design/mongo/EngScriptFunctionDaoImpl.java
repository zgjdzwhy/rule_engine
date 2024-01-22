package com.mobanker.engine.design.mongo;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.design.pojo.EngScriptFunction;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

//@Repository("mongoScriptFunctionDao")
public class EngScriptFunctionDaoImpl implements EngScriptFunctionDao{

	private Logger logger = LoggerFactory.getLogger(this.getClass());						
	
	private final String BUSI_DESC = "持久化函数";
	public static final String TABLE = "rule_script_function";
	
	@Autowired
	private MongoTemplate mongoTemplate;	
	
	@Override
	public EngScriptFunction findOne(String productType,String label) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(TABLE);
		
		//先检查是否有重复的
		BasicDBObject query = new BasicDBObject("label",label);
		query.put("productType", productType);
		DBCursor cursor = table.find(query);
		if(cursor != null){			
			logger.info(BUSI_DESC+"cursor hasNext:{}",cursor.hasNext());		
			while (cursor.hasNext()) {   
				String jsonText = cursor.next().toString();
				return JSONObject.parseObject(jsonText,EngScriptFunction.class);				
			} 			
		}else{
			logger.warn("游标不存在");
		}
		return null;
	}	
	
	@Override
	public void insert(EngScriptFunction engScriptFunction) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(TABLE);
	
		DBObject dbObject = (DBObject) JSON.parse(JSONObject.toJSONString(engScriptFunction));
		table.insert(dbObject);		
	}	
	
	@Override
	public void update(EngScriptFunction engScriptFunction) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(TABLE);
		//先删除
		String label = engScriptFunction.getLabel();
		String productType = engScriptFunction.getProductType();
		BasicDBObject query = new BasicDBObject("productType",productType);
		query.put("label", label);
		table.remove(query);
		logger.info("remove self cpnt,id:{},result:{}",label);		
		
		DBObject dbObject = (DBObject) JSON.parse(JSONObject.toJSONString(engScriptFunction));
		table.insert(dbObject);			
	}

	@Override
	public void delete(String productType, String label) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(TABLE);
		//先删除
		BasicDBObject query = new BasicDBObject("productType",productType);
		query.put("label", label);
		table.remove(query);
		logger.info("remove,id:{},result:{}",label);		
	}		
	


	@Override
	public List<EngScriptFunction> findByProduct(String productType) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(TABLE);
		BasicDBObject query = new BasicDBObject("productType",productType);

		DBCursor cursor = table.find(query);		
		List<EngScriptFunction> list = new LinkedList<EngScriptFunction>();
		
		if(cursor != null){			
			logger.info(BUSI_DESC+"cursor hasNext:{}",cursor.hasNext());		
			while (cursor.hasNext()) {   
				String jsonText = cursor.next().toString();
				list.add(JSONObject.parseObject(jsonText,EngScriptFunction.class));				
			} 			
		}else{
			logger.warn("游标不存在");
		}
		return list;
	}

	@Override
	public void deleteByProductType(String productType) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(TABLE);
		//先删除
		BasicDBObject query = new BasicDBObject("productType",productType);
		table.remove(query);	
	}



}
