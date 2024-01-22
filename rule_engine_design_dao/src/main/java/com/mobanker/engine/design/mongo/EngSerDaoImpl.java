package com.mobanker.engine.design.mongo;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.design.pojo.EngSerInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@Deprecated
public class EngSerDaoImpl implements EngSerDao {

	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	private final String BUSI_DESC = "持久化ser包文件";
	
	public static final String SERTABLE = "rule_ser_release";
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void saveSer(EngSerInfo engSerInfo) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(SERTABLE);

		DBObject dbObject = (DBObject) JSON.parse(JSONObject.toJSONString(engSerInfo));
		table.insert(dbObject);		
	}

	@Override
	public List<EngSerInfo> findSer(String productType, String version) {
		DB db = mongoTemplate.getDb();
		DBCollection table = db.getCollection(SERTABLE);
		BasicDBObject query = new BasicDBObject("productType",productType);
		
		if(StringUtils.isNotBlank(version)){
			query.put("version", version);
		}
		DBCursor cursor = table.find(query);		
		List<EngSerInfo> list = new LinkedList<EngSerInfo>();
		
		if(cursor != null){			
			logger.info(BUSI_DESC+"cursor hasNext:{}",cursor.hasNext());		
			while (cursor.hasNext()) {   
				String jsonText = cursor.next().toString();
				list.add(JSONObject.parseObject(jsonText,EngSerInfo.class));				
			} 			
		}else{
			logger.warn("游标不存在");
		}
		return list;
	}

}
