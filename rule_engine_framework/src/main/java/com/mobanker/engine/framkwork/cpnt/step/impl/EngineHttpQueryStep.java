package com.mobanker.engine.framkwork.cpnt.step.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.api.params.EngineParam;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.query.assist.EngineFieldDefine;
import com.mobanker.engine.framkwork.cpnt.query.assist.EngineQueryDefine;
import com.mobanker.engine.framkwork.cpnt.query.dto.EngineResponseEntity;
import com.mobanker.engine.framkwork.cpnt.step.EngineAbstractStep;
import com.mobanker.engine.framkwork.cpnt.step.EngineStep;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;
import com.mobanker.engine.framkwork.relation.EngineRelaField;
import com.mobanker.engine.framkwork.util.EngineUtil;


/**
 * 获取数据，根据数据库配置去获取哪些要查询的项目
 * <p>Title: EngineTestStep.java<／p>
 * <p>Description: <／p>
 * <p>Company: mobanker.com<／p>
 * @author taojinn
 * @date 2016年1月27日
 * @version 1.0
 */

@Deprecated
public class EngineHttpQueryStep extends EngineAbstractStep implements EngineStep,EngineRelaCpnt,EngineRelaField {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(EngineHttpQueryStep.class);	
	private final String BUSI_DESC = "数据源";
	/**
	 * 0-中断程序 1-按默认值继续执行
	 */
	private String fatalDeal;
	private String url;
	private Set<String> urlParamKeys;
	private String urlType;
	private int timeout = 60000;
	
	private Map<String,EngineQueryDefine> queryList = new HashMap<String,EngineQueryDefine>();
	//在解析的时候占位置用，以后在替换掉
	private final static EngineQueryDefine staticDefine = new EngineQueryDefine();
	
	@Override
	public Map<String,Boolean> relaCpntIds() {
		return Collections.emptyMap();
	}
	@Override
	public String type() {
		return BUSI_DESC;
	}		
	
	
	@Override
	protected void run(EngineTransferData etd) {		
		if(MapUtils.isEmpty(queryList)){
			logger.warn(EngineUtil.logPrefix(etd, BUSI_DESC)+"获取数据源没有配置查询项，请管理员查看数据源配置,组件名称:%s",name());
			return;
		}		
		//EngineAssert.checkArgument(MapUtils.isNotEmpty(queryList), EngineUtil.logPrefix(etd, BUSI_DESC)+"获取数据源没有配置查询项，请管理员查看数据源配置,组件名称:%s",name());
		
		Set<String> monitorSet = new HashSet<String>();
		
		StringBuilder queryIds = new StringBuilder();
		for(EngineQueryDefine queryDefine: queryList.values()){
			queryIds.append(","+queryDefine.getQueryId());
			for(EngineFieldDefine fieldDefine : queryDefine.getFieldDefineList()){
				String fieldKey = fieldDefine.getFieldKey(); 	
				monitorSet.add(fieldKey);
			}
		}
						
		Set<String> inputKeySet = etd.getInputParam().keySet();		
		if(inputKeySet.containsAll(monitorSet)){//如果全部包含了 则认为不用再次查询
			logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"所需特征指标数据已经存在，无需再次查询!fieldKeys:{}",monitorSet);
			return;
		}
		//如果是测试模式，则不查询数据源直接报错，一般测试模式应该已经在input选项中已经填充好所有的指标数据了
//		if(etd.modeIsTest()){
//			logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"测试模式，不进行查询!fieldKeys:{}",monitorSet);
//			return;
//		}
		//	throw new EngineTestModeException(EngineUtil.logPrefix(etd, BUSI_DESC)+"测试模式中测试指标数据模拟的不完整,需要模拟的字段:"+monitorSet);
		
		
		Map<String,String> urlParamSend = new HashMap<String,String>();
		EngineParam etdParams = etd.getInputParam();
		for(String key : urlParamKeys){
			if(urlParamSend.containsKey(key)) continue;		
			Object value = etdParams.get(key);
			if(value != null){
				urlParamSend.put(key, JSONObject.toJSONString(value));				
			}
		}
		etdParams = etd.getOutputParam();
		for(String key : urlParamKeys){
			if(urlParamSend.containsKey(key)) continue;			
			Object value = etdParams.get(key);
			if(value != null){
				urlParamSend.put(key, JSONObject.toJSONString(value));
			}
		}
		
		EngineAssert.checkArgument(urlParamKeys.size() == urlParamSend.size(), 
				"发送的请求参数缺失,需要：%s,实际：%s",urlParamKeys,urlParamSend.keySet());
		
						
		urlParamSend.put("queryIds", queryIds.toString().substring(1));
		EngineParam result = etd.getInputParam();
		
		try{			
			Map<String,Object> config = new HashMap<String,Object>();
			//config.put(HttpClientUtils.CONNECT_TIMEOUT,3000);
 			//config.put(HttpClientUtils.SOCKET_TIMEOUT,timeout);
 			//HttpClientUtils.setConfig(config); 			
 			
 			logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"准入访问:{},{}",url,urlParamSend);
 			String resStr = null;
 			if(StringUtils.equals(urlType, "POST")){
 				//resStr = HttpClientUtils.doPost(url, urlParamSend);
 			}else
 				//resStr = HttpClientUtils.doGet(url, urlParamSend);
			
			EngineAssert.checkNotBlank(resStr,EngineUtil.logPrefix(etd,BUSI_DESC)+"基本参数不存在，queryId:"+name());
			
			logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"返回内容:{}",resStr);
			
			EngineResponseEntity entity = JSONObject.parseObject(resStr,EngineResponseEntity.class);
			EngineAssert.checkNotNull(entity,EngineUtil.logPrefix(etd,BUSI_DESC)+"解析数据失败，queryId:"+name());			
			EngineAssert.checkArgument(StringUtils.equals(entity.getStatus(), "1"),
					EngineUtil.logPrefix(etd,BUSI_DESC)+"数据源故障，queryId:"+id()+"error:"+entity.getError());
			Map<String,Object> data = entity.getData();
			
			//获取这个字段详细信息，进行转换
			for(String key : data.keySet()){
				Object value = data.get(key);	
				if(value == null) continue;//如果是返回是空的话，就使用默认值
				
				logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"获取数据放入key:{},value:{},type:{}",key,value,value.getClass().getName());
				result.put(key, value);
				
				
			}
		}catch(Exception e){
			if(!StringUtils.equals(fatalDeal, "1")){
				throw new EngineException(EngineUtil.logPrefix(etd,BUSI_DESC)+"调用数据源失败,key:"+name(),e);
			}else{
				logger.warn(EngineUtil.logPrefix(etd, BUSI_DESC)+"调用数据源失败,但因为重要性比较小,继续执行,key:"+name());
			}						
		}
				
			
		
	}


	public void configQuery(String queryItem){
		queryList.put(queryItem, staticDefine);
	}

	

	public String getFatalDeal() {
		return fatalDeal;
	}



	public void setFatalDeal(String fatalDeal) {
		this.fatalDeal = fatalDeal;
	}



	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
	}



	public Set<String> getUrlParamKeys() {
		return urlParamKeys;
	}



	public void setUrlParamKeys(Set<String> urlParamKeys) {
		this.urlParamKeys = urlParamKeys;
	}



	public String getUrlType() {
		return urlType;
	}



	public void setUrlType(String urlType) {
		this.urlType = urlType;
	}



	public int getTimeout() {
		return timeout;
	}



	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}



	public Map<String, EngineQueryDefine> getQueryList() {
		return queryList;
	}



	public void setQueryList(Map<String, EngineQueryDefine> queryList) {
		this.queryList = queryList;
	}



	@Override
	public Set<String> inputRela() {	
		Set<String> result = new HashSet<String>();
		for(EngineQueryDefine define : queryList.values()){
			List<EngineFieldDefine> fieldList = define.getFieldDefineList();
			for(EngineFieldDefine field : fieldList){
				result.add(field.getFieldKey());
			}						
		}				
		return result;
	}



	@Override
	public Set<String> outputRela() {
		return Collections.emptySet();
	}


	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof EngineHttpQueryStep)) return false;
		EngineHttpQueryStep another = (EngineHttpQueryStep) obj;			
		if(!this.queryList.keySet().equals(another.queryList.keySet())) return false;		
		
		return true;
	}	
	



	
	


}
