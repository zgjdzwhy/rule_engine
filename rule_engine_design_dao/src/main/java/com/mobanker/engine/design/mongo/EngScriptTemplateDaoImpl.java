package com.mobanker.engine.design.mongo;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.design.pojo.EngScriptTemplate;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

//@Repository("mongoScriptTemplateDao")
public class EngScriptTemplateDaoImpl implements EngScriptTemplateDao{

	private Logger logger = LoggerFactory.getLogger(this.getClass());						
	
	private final String BUSI_DESC = "持久化脚本模板";
	public static final String TABLE = "rule_script_template";
			
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public EngScriptTemplate findOne(String label) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(TABLE);
		
		//先检查是否有重复的
		BasicDBObject query = new BasicDBObject("label",label);
		DBCursor cursor = table.find(query);
		if(cursor != null){			
			logger.info(BUSI_DESC+"cursor hasNext:{}",cursor.hasNext());		
			while (cursor.hasNext()) {   
				String jsonText = cursor.next().toString();
				return JSONObject.parseObject(jsonText,EngScriptTemplate.class);				
			} 			
		}else{
			logger.warn("游标不存在");
		}
		return null;
	}	
	
	@Override
	public void insert(EngScriptTemplate one) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(TABLE);
	
		DBObject dbObject = (DBObject) JSON.parse(JSONObject.toJSONString(one));
		table.insert(dbObject);		
	}	
	
	@Override
	public void update(EngScriptTemplate one) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(TABLE);
		//先删除
		String label = one.getLabel();
		String productType = one.getProductType();
		BasicDBObject query = new BasicDBObject("productType",productType);
		query.put("label", label);
		table.remove(query);
		logger.info("remove,id:{},result:{}",label);		
		
		DBObject dbObject = (DBObject) JSON.parse(JSONObject.toJSONString(one));
		table.insert(dbObject);			
	}

	@Override
	public void delete(String label) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(TABLE);
		//先删除
		BasicDBObject query = new BasicDBObject("label",label);
		table.remove(query);
		logger.info("remove,id:{},result:{}",label);		
	}	
	

	@Override
	public List<EngScriptTemplate> getAll() {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(TABLE);		
		
		DBCursor cursor = table.find();		
		List<EngScriptTemplate> list = new LinkedList<EngScriptTemplate>();
		
		if(cursor != null){			
			logger.info(BUSI_DESC+"cursor hasNext:{}",cursor.hasNext());		
			while (cursor.hasNext()) {   
				String jsonText = cursor.next().toString();
				list.add(JSONObject.parseObject(jsonText,EngScriptTemplate.class));				
			} 			
		}else{
			logger.warn("游标不存在");
		}
		return list;
	}	



	
	
}
