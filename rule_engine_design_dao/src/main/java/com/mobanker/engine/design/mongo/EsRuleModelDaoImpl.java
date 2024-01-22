package com.mobanker.engine.design.mongo;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.common.EngineCommonUtil;
import com.mobanker.engine.design.mongo.assist.EsUtil;
import com.mobanker.engine.design.mongo.assist.RuleModelTable;
import com.mobanker.engine.design.rule.encrypt.EngineRuleEngcrypt;

public class EsRuleModelDaoImpl implements EngRuleModelDao,InitializingBean{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());								
	
	private static final int DEFAULT_SIZE = 10000;
	
	@Autowired
	private Client client;	
		
	private EngineRuleEngcrypt engineRuleEngcrypt;
	
	@Override
	public void saveRuleModel(String productType, String cpntId, 
			JSONObject jsonObject) {	
		
		String index = RuleModelTable.SELF.getName();
		//先删除
		removeRuleModel(index,productType,cpntId);		
		indexRuleModel(index,productType,jsonObject);		
		//client.admin().indices().prepareRefresh(index).execute();
	}
		
	
	@Override
	public void deleteRuleModel(String productType, String cpntId) {
		String index = RuleModelTable.SELF.getName();		
		long total = removeRuleModel(index,productType,cpntId);
		
		if(total > 1){
			throw new RuntimeException("根据id查询组件时候，发现查询出的数量不只一个,id:"+cpntId);
		}
		if(total == 1){
			//client.admin().indices().prepareRefresh(index).execute();	
		}else
			logger.warn("根据id查询组件时候，没有找到组件,id:"+cpntId);					
	}
	

	
	
	
	@Override
	public List<JSONObject> getRuleModel(RuleModelTable ruleModelTable,
			String productType, String username, String version) {
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		if(StringUtils.isNotBlank(username)){
			qb.must(QueryBuilders.termQuery("right", username.toLowerCase()));
		}
		if(StringUtils.isNotBlank(version)){
			qb.must(QueryBuilders.termQuery("version", version.toLowerCase()));
		}
		
		SearchResponse searchresponse = client.prepareSearch(ruleModelTable.getName())
				.setTypes(productType)
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setQuery(qb)                 
                .setSize(DEFAULT_SIZE)
                .setExplain(true)  
                .execute().actionGet();  
		
		long hitNum = searchresponse.getHits().getTotalHits();
		List<JSONObject> list = new LinkedList<JSONObject>();
		if(hitNum > 0){			
			for(SearchHit hit : searchresponse.getHits().getHits()){		
				JSONObject temp = new JSONObject(hit.getSource());				
				list.add(engineRuleEngcrypt.descrypt(temp));			
			}
		}
		return list;
	}


	

	@Override
	public void mergeRuleModel(String productType) {
		List<JSONObject> list = getRuleModel(RuleModelTable.SELF,productType,null,null);
		if(CollectionUtils.isEmpty(list)){			
			logger.warn("self list is empty,productType:{}",productType);
			return;
		}
		logger.info("merge list size:{},productType:{}",list.size(),productType);	
		
		deleteByProductType(productType,RuleModelTable.MERGE);
		logger.info("remove merge,productType:{}",productType);
		
		int i=0;
		for(JSONObject one : list){
			indexRuleModel(RuleModelTable.MERGE.getName(),productType,one);			
			i++;
		}		
		logger.info("insert merge,productType:{},count:{}",productType,i);						
	}

	@Override
	public String applyReleaseRuleModel(String productType) {
		List<JSONObject> list = getRuleModel(RuleModelTable.MERGE,productType,null,null);				
		String version = EngineCommonUtil.get14CurrentDateTime();	
		for(JSONObject one : list){
			one.put("version", version);
			indexRuleModel(RuleModelTable.RELEASE.getName(),productType,one);					
		}			
		return version;
	}

	@Override
	public int batchInsert(List<JSONObject> jsonList,RuleModelTable ruleModelTable) {
		int count = 0;
		String index = ruleModelTable.getName();
		for(JSONObject json : jsonList){
			String productType = json.getString("productType");
			indexRuleModel(index,productType,json);						
			count++;
		}
		//client.admin().indices().prepareRefresh(index).execute();
		return count;
	}

	@Override
	public void deleteByProductType(String productType,RuleModelTable ruleModelTable) {
		QueryBuilder builder = QueryBuilders.typeQuery(productType);
		DeleteByQueryAction.INSTANCE.newRequestBuilder(client).source(ruleModelTable.getName()).filter(builder).execute().actionGet();	
		//这种方式5.0以后可以直接用DeleteByQueryAction进行删除
//		SearchResponse searchresponse = client.prepareSearch(RuleModelTable.MERGE.getName())
//				.setTypes(productType)
//                .setSearchType(SearchType.QUERY_AND_FETCH)                            
//                .setSize(DEFAULT_SIZE)
//                .setExplain(true)  
//                .execute().actionGet();  
//		
//		long hitNum = searchresponse.getHits().getTotalHits();		
//		if(hitNum > 0){			
//			for(SearchHit hit : searchresponse.getHits().getHits()){		
//				client.prepareDelete(RuleModelTable.MERGE.getName(), productType, hit.getId()).execute();				
//			}
//		}
	}

	@Override
	public JSONObject getRuleModel(RuleModelTable ruleModelTable,
			String productType, String cpntId) {
		return queryOneRuleModel(ruleModelTable, productType, cpntId, null, null);
	}		
	
	
	@Override
	public JSONObject getHistoryRuleModel(
			String productType, String cpntId, String version) {		
		return queryOneRuleModel(RuleModelTable.RELEASE, productType, cpntId, null, version);
	}

		
	private JSONObject queryOneRuleModel(RuleModelTable ruleModelTable,
			String productType, String cpntId, String username, String version) {
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		if(StringUtils.isNotBlank(username)){
			qb.must(QueryBuilders.termQuery("right", username.toLowerCase()));
		}
		if(StringUtils.isNotBlank(version)){
			qb.must(QueryBuilders.termQuery("version", version.toLowerCase()));
		}
		if(StringUtils.isNotBlank(cpntId)){
			qb.must(QueryBuilders.termQuery("id", cpntId.toLowerCase()));
		}
		
		SearchResponse searchresponse = client.prepareSearch(ruleModelTable.getName())
				.setTypes(productType)
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setQuery(qb)                 
                .setSize(DEFAULT_SIZE)
                .setExplain(true)  
                .execute().actionGet();  
		
		long hitNum = searchresponse.getHits().getTotalHits();
		if(hitNum > 1){
			throw new RuntimeException("根据id查询组件时候，发现查询出的数量不只一个,id:"+cpntId);
		}
		if(hitNum == 0){
			logger.warn("根据id查询组件时候，没有找到组件,id:"+cpntId);
			return null;
		}
		SearchHit hit = searchresponse.getHits().getHits()[0];
		JSONObject result = new JSONObject(hit.getSource());
				
		return engineRuleEngcrypt.descrypt(result);
	}	
	
	private void indexRuleModel(String table,String productType, JSONObject jsonObject) {							
		jsonObject = engineRuleEngcrypt.encrypt(jsonObject);		
		IndexResponse indexResponse = client.prepareIndex(table, productType)
		        .setSource(jsonObject.toJSONString())
		        .get();
		
		logger.info("索引数据成功,id:"+indexResponse.getId());
	}	

	
	private long removeRuleModel(String table,String productType,String cpntId) {		
		SearchResponse searchresponse = client.prepareSearch(table)
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setTypes(productType)
                .setQuery(QueryBuilders.termQuery("id", cpntId))  
                .setFrom(0).setSize(DEFAULT_SIZE).setExplain(true)  
                .execute().actionGet();  
		long hitTotal = searchresponse.getHits().getTotalHits();
		
		if(hitTotal>0){
			for(SearchHit hit : searchresponse.getHits().getHits()){
				client.prepareDelete(table, productType, hit.getId()).execute();
			}
		}			
		return hitTotal;
	}	
	

	@Override
	public void afterPropertiesSet() throws Exception {
		//创建好索引
		EsUtil.createIndexIfNotExists(client,RuleModelTable.SELF.getName());
		EsUtil.createIndexIfNotExists(client,RuleModelTable.MERGE.getName());
		EsUtil.createIndexIfNotExists(client,RuleModelTable.RELEASE.getName());	
	}


	public Client getClient() {
		return client;
	}


	public void setClient(Client client) {
		this.client = client;
	}


	public EngineRuleEngcrypt getEngineRuleEngcrypt() {
		return engineRuleEngcrypt;
	}


	public void setEngineRuleEngcrypt(EngineRuleEngcrypt engineRuleEngcrypt) {
		this.engineRuleEngcrypt = engineRuleEngcrypt;
	}	

	
	
}
