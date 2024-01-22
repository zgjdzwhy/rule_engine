package com.mobanker.engine.design.mongo;

import java.util.LinkedList;
import java.util.List;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.mobanker.engine.design.mongo.assist.EsUtil;
import com.mobanker.engine.design.pojo.EngScriptTemplate;

@Repository("esScriptTemplateDao")
public class EsScriptTemplateDaoImpl implements EngScriptTemplateDao,InitializingBean{

	private Logger logger = LoggerFactory.getLogger(this.getClass());						
		
	public static final String TABLE = "rule_script_template";
			
	private static final int DEFAULT_SIZE = 10000;
	
	private static final String TYPE = "defaultType";
	
	@Autowired
	private Client client;	

	@Override
	public EngScriptTemplate findOne(String label) {
		GetResponse getResponse = client.prepareGet(TABLE, TYPE, label).execute().actionGet();	
		return JSONObject.parseObject(getResponse.getSourceAsString(),EngScriptTemplate.class);
	}	
	
	@Override
	public void insert(EngScriptTemplate one) {
		save(one);
	}	
	
	@Override
	public void update(EngScriptTemplate one) {
		save(one);	
	}

	@Override
	public void delete(String label) {
		client.prepareDelete(TABLE, TYPE, label).execute().actionGet().getId();		
	}	
	

	@Override
	public List<EngScriptTemplate> getAll() {
		SearchResponse searchresponse = client.prepareSearch(TABLE)
				.setTypes(TYPE)
                .setSearchType(SearchType.QUERY_AND_FETCH)                
                .setSize(DEFAULT_SIZE)
                .setExplain(true)  
                .execute().actionGet();  
		
		long hitNum = searchresponse.getHits().getTotalHits();
		List<EngScriptTemplate> list = new LinkedList<EngScriptTemplate>();
		if(hitNum > 0){			
			for(SearchHit hit : searchresponse.getHits().getHits()){						
				list.add(JSONObject.parseObject(hit.getSourceAsString(),EngScriptTemplate.class));			
			}
		}
		return list;			
	}	

	private void save(EngScriptTemplate engScriptTemplate){
		String productType = TYPE;
		String label = engScriptTemplate.getLabel();
		Preconditions.checkNotNull(productType);
		Preconditions.checkNotNull(label);
		IndexResponse indexResponse = client.prepareIndex(TABLE, productType, label)
		        .setSource(JSONObject.toJSONString(engScriptTemplate))
		        .get();
		
		logger.info("索引模板成功,模板名:"+indexResponse.getId());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		EsUtil.createIndexIfNotExists(client,TABLE);
	}	
	
}
