package com.mobanker.engine.design.mongo;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.common.EngineCommonUtil;
import com.mobanker.engine.design.dao.EngRuleRepositoryDao;
import com.mobanker.engine.design.mongo.assist.RuleModelTable;
import com.mobanker.engine.design.pojo.EngRuleModel;
import com.mobanker.engine.design.rule.encrypt.EngineRuleEngcrypt;

public class MysqlRuleModelDaoImpl implements EngRuleModelDao{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());								
	
	@Autowired
	private Client client;	
		
	private EngineRuleEngcrypt engineRuleEngcrypt;
	
	@Autowired
	private EngRuleRepositoryDao engRuleRepositoryDao;
	
	@Override
	public void saveRuleModel(String productType, String cpntId, 
			JSONObject jsonObject) {	
		
		String index = RuleModelTable.SELF.getName();
		save(index,productType,jsonObject);		
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
		if(StringUtils.isBlank(productType))throw new RuntimeException("productType为空！");
		if(StringUtils.isBlank(ruleModelTable.getName()))throw new RuntimeException("Table为空！");
		
		EngRuleModel findParams=new EngRuleModel();
		if(StringUtils.isNotBlank(username)){
			findParams.setCreateUser(username);
		}
		if(StringUtils.isNotBlank(version)){
			findParams.setVersion(version);
		}
		findParams.setProductType(productType);
		findParams.setRepositoryType(ruleModelTable.getName());
		
		List<EngRuleModel> ruleList = engRuleRepositoryDao.get(findParams);
		List<JSONObject> list = new LinkedList<JSONObject>();
		if(ruleList!=null && ruleList.size()> 0){			
			for(EngRuleModel engRuleModel : ruleList){		
				list.add(engineRuleEngcrypt.descrypt(this.assembleRuleJson(engRuleModel)));			
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
			save(RuleModelTable.MERGE.getName(),productType,one);			
			i++;
		}		
		logger.info("insert merge,productType:{},count:{}",productType,i);						
	}

	@Override
	public String applyReleaseRuleModel(String productType) {
		List<JSONObject> list = getRuleModel(RuleModelTable.MERGE,productType,null,null);	
		if(list.isEmpty()||list.size()<1){
			throw new RuntimeException("请先在流程设计中提交模块！");
		}
		String version = EngineCommonUtil.get14CurrentDateTime();	
		for(JSONObject one : list){
			one.put("version", version);
			save(RuleModelTable.RELEASE.getName(),productType,one);					
		}			
		return version;
	}

	@Override
	public int batchInsert(List<JSONObject> jsonList,RuleModelTable ruleModelTable) {
		int count = 0;
		String index = ruleModelTable.getName();
		for(JSONObject json : jsonList){
			String productType = json.getString("productType");
			save(index,productType,json);						
			count++;
		}
		//client.admin().indices().prepareRefresh(index).execute();
		return count;
	}

	@Override
	public void deleteByProductType(String productType,RuleModelTable ruleModelTable) {
		if(StringUtils.isBlank(productType))throw new RuntimeException("productType为空！");
		if(StringUtils.isBlank(ruleModelTable.getName()))throw new RuntimeException("Table为空！");
		
		if(StringUtils.equals(ruleModelTable.getName(), ruleModelTable.RELEASE.getName())){
			throw new RuntimeException("不允许批量删除Release区规则！");
		}
		
		EngRuleModel findParams=new EngRuleModel();
		findParams.setProductType(productType);
		findParams.setRepositoryType(ruleModelTable.getName());
		
		List<EngRuleModel> ruleList = engRuleRepositoryDao.get(findParams);
		if(ruleList !=null && ruleList.size()>0){
			for(EngRuleModel engRuleModel:ruleList){
				engRuleRepositoryDao.delete(engRuleModel.getId());
			}
		}
		
		
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
		if(StringUtils.isBlank(productType))throw new RuntimeException("productType为空！");
		if(StringUtils.isBlank(ruleModelTable.getName()))throw new RuntimeException("Table为空！");
		if(StringUtils.isBlank(cpntId))throw new RuntimeException("cpntId为空！");
		
		EngRuleModel findParams=new EngRuleModel();
		if(StringUtils.isNotBlank(username)){
			findParams.setRuleRight(username.toLowerCase());
		}
		if(StringUtils.isNotBlank(version)){
			findParams.setVersion(version.toLowerCase());
		}
		findParams.setRuleId(cpntId.toLowerCase());
		findParams.setProductType(productType);
		findParams.setRepositoryType(ruleModelTable.getName());
		
		List<EngRuleModel> list = engRuleRepositoryDao.get(findParams);
		
		if(list.size() > 1){
			throw new RuntimeException("根据id查询组件时候，发现查询出的数量不只一个,id:"+cpntId);
		}
		if(list==null || list.size() == 0){
			logger.warn("根据id查询组件时候，没有找到组件,id:"+cpntId);
			return null;
		}
		
		JSONObject result = new JSONObject(this.assembleRuleJson(list.get(0)));
				
		return engineRuleEngcrypt.descrypt(result);
	}	
	
	private void save(String table,String productType, JSONObject jsonObject) {			
		if(StringUtils.isBlank(productType))throw new RuntimeException("productType为空！");
		if(StringUtils.isBlank(table))throw new RuntimeException("Table为空！");
		if(jsonObject.isEmpty())throw new RuntimeException("jsonObject为空！");
		if(StringUtils.isBlank(jsonObject.getString("id")))throw new RuntimeException("cpntId为空！");
		
		jsonObject = engineRuleEngcrypt.encrypt(jsonObject);		
		EngRuleModel engRuleModel=new EngRuleModel();
		engRuleModel.setRepositoryType(table);
		engRuleModel.setProductType(productType);
		engRuleModel.setRuleId(jsonObject.getString("id"));
		engRuleModel.setRuleRight(jsonObject.getString("right"));
		engRuleModel.setRuleName(jsonObject.getString("name"));
		engRuleModel.setCpntType(jsonObject.getString("type"));
		engRuleModel.setFreshPoint(jsonObject.getString("updatetime"));
		engRuleModel.setVersion(jsonObject.getString("version"));
		engRuleModel.setStatus("0"); //新建都为初始 0
		engRuleModel.setEncryptKey(jsonObject.getString("businessId"));
		engRuleModel.setContent(jsonObject.getString("content"));
		
		
		EngRuleModel findParams=new EngRuleModel();
		findParams.setRuleId(engRuleModel.getRuleId());
		findParams.setRepositoryType(table);
		if(StringUtils.isNoneBlank(jsonObject.getString("version"))){
			findParams.setVersion(jsonObject.getString("version"));
		}
		EngRuleModel retRuleModel= engRuleRepositoryDao.getOne(findParams);
		if(retRuleModel!=null){
			engRuleModel.setId(retRuleModel.getId());
			engRuleModel.setUpdateUser(engRuleModel.getRuleRight());
			engRuleRepositoryDao.update(engRuleModel);
		}else{
			engRuleModel.setCreateUser(engRuleModel.getRuleRight());
			engRuleRepositoryDao.insert(engRuleModel);
		}
		
	}	

	
	private long removeRuleModel(String table,String productType,String cpntId) {		
		if(StringUtils.endsWith(table,RuleModelTable.RELEASE.name())){
			throw new RuntimeException("无法根据cpntId删除release区域");
		}
		if(StringUtils.isBlank(cpntId))throw new RuntimeException("cpntId为空！");
		
		EngRuleModel findParams=new EngRuleModel();
		findParams.setRuleId(cpntId);
		findParams.setRepositoryType(table);
		List<EngRuleModel> list = engRuleRepositoryDao.get(findParams);
		if(list!=null && list.size()==1){
			for(EngRuleModel engRuleModel:list){
				engRuleRepositoryDao.delete(engRuleModel.getId());
			}
		}
		
		return list==null?0:list.size();
	}	
	
	private JSONObject assembleRuleJson(EngRuleModel engRuleModel){
		JSONObject json=new JSONObject();
		
		json.put("id", engRuleModel.getRuleId());
		json.put("right", engRuleModel.getRuleRight());
		json.put("name", engRuleModel.getRuleName());
		json.put("type", engRuleModel.getCpntType());
		json.put("updatetime", engRuleModel.getFreshPoint());
		json.put("version", engRuleModel.getVersion());
		json.put("businessId", engRuleModel.getEncryptKey());
		json.put("content", engRuleModel.getContent());
		
		return json;
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
