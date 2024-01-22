package com.mobanker.engine.design.mongo.assist;

import static org.elasticsearch.client.Requests.indicesExistsRequest;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EsUtil {
	
	private static Logger logger = LoggerFactory.getLogger(EsUtil.class);	
	
	public static void createIndexIfNotExists(Client client,String indexName){
		boolean isSuccess;
		if(!client.admin().indices().exists(indicesExistsRequest(indexName)).actionGet().isExists()){
			logger.info("初始化ES索引"+indexName);
			isSuccess = client.admin().indices().create(Requests.createIndexRequest(indexName)).actionGet().isAcknowledged();
			if(!isSuccess){
				throw new RuntimeException("初始es索引失败,indexName:"+indexName);
			}
		}		
	}
}
