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
import com.mobanker.engine.design.pojo.EngScriptFunction;

@Repository("esCommonFunctionDao")
public class EsCommonFunctionDaoImpl implements EngScriptFunctionDao,InitializingBean{

	private Logger logger = LoggerFactory.getLogger(this.getClass());						
		
	public final String TABLE = "rule_common_function";
	
	private static final int DEFAULT_SIZE = 10000;
	
	@Autowired
	private Client client;	
	
	@Override
	public EngScriptFunction findOne(String productType,String label) {
		GetResponse getResponse = client.prepareGet(TABLE, productType, label).execute().actionGet();	
		return JSONObject.parseObject(getResponse.getSourceAsString(),EngScriptFunction.class);
	}	
	
	@Override
	public void insert(EngScriptFunction engScriptFunction) {
		save(engScriptFunction);
	}	
	
	@Override
	public void update(EngScriptFunction engScriptFunction) {
		save(engScriptFunction);
	}
	
	@Override
	public void delete(String productType, String label) {
		client.prepareDelete(TABLE, productType, label).execute().actionGet().getId();	
	}		
	


	@Override
	public List<EngScriptFunction> findByProduct(String productType) {
		SearchResponse searchresponse = client.prepareSearch(TABLE)
				.setTypes(productType)
                .setSearchType(SearchType.QUERY_AND_FETCH)                
                .setSize(DEFAULT_SIZE)
                .setExplain(true)  
                .execute().actionGet();  
		
		long hitNum = searchresponse.getHits().getTotalHits();
		List<EngScriptFunction> list = new LinkedList<EngScriptFunction>();
		if(hitNum > 0){			
			for(SearchHit hit : searchresponse.getHits().getHits()){						
				list.add(JSONObject.parseObject(hit.getSourceAsString(),EngScriptFunction.class));			
			}
		}
		return list;		
	}

	@Override
	public void deleteByProductType(String productType) {
		throw new RuntimeException("该方法不可用");
	}

	private void save(EngScriptFunction engScriptFunction){
		String productType = engScriptFunction.getProductType();
		String label = engScriptFunction.getLabel();
		Preconditions.checkNotNull(productType);
		Preconditions.checkNotNull(label);
		IndexResponse indexResponse = client.prepareIndex(TABLE, productType, label)
		        .setSource(JSONObject.toJSONString(engScriptFunction))
		        .get();
		
		logger.info("索引函数成功,函数名:"+indexResponse.getId());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		EsUtil.createIndexIfNotExists(client,TABLE);
	}

}
