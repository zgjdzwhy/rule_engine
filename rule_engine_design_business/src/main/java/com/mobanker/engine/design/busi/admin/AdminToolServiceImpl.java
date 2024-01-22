package com.mobanker.engine.design.busi.admin;

import static org.elasticsearch.client.Requests.indicesExistsRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.design.dao.EngProductDao;
import com.mobanker.engine.design.mongo.EngRuleModelDao;
import com.mobanker.engine.design.mongo.EngScriptFunctionDao;
import com.mobanker.engine.design.mongo.EngScriptFunctionDaoImpl;
import com.mobanker.engine.design.mongo.EngScriptTemplateDao;
import com.mobanker.engine.design.mongo.EngScriptTemplateDaoImpl;
import com.mobanker.engine.design.mongo.assist.RuleModelTable;
import com.mobanker.engine.design.pojo.EngProduct;
import com.mobanker.engine.design.pojo.EngScriptFunction;
import com.mobanker.engine.design.pojo.EngScriptTemplate;


/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年1月9日
 * @version 1.0
 */
@Service
public class AdminToolServiceImpl implements AdminToolService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());	
	
//	@Resource(name = "mongoRuleModelDao")
//	private EngRuleModelDao mongoRuleModelDao;
	
	@Resource(name = "esRuleModelDao")
	private EngRuleModelDao esRuleModelDao;
	
	@Resource(name = "esCustomFunctionDao")
	private EngScriptFunctionDao esCustomFunctionDao;
	
	@Resource(name = "esCommonFunctionDao")
	private EngScriptFunctionDao esCommonFunctionDao;
	
	@Resource(name = "hbaseScriptFunctionDao")
	private EngScriptFunctionDao hbaseScriptFunctionDao;
	
	@Resource(name = "hbaseScriptTemplateDao")
	private EngScriptTemplateDao hbaseScriptTemplateDao;

//	@Resource(name = "mongoScriptFunctionDao")
//	private EngScriptFunctionDao mongoScriptFunctionDao;
//	
	@Resource(name = "esScriptTemplateDao")
	private EngScriptTemplateDao esScriptTemplateDao;
//	
//	@Resource(name = "mongoScriptTemplateDao")
//	private EngScriptTemplateDao mongoScriptTemplateDao;	
	
	@Resource(name = "mysqlRuleModelDao")
	private EngRuleModelDao mysqlRuleModelDao;	
	
	@Autowired
	private EngProductDao engProductDao;
	
	//仅仅用于初始，请不要穿透使用，请不要效仿
	/*@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;*/
	
	//仅仅用于初始，请不要穿透使用，请不要效仿
	@Autowired
	private Client client;
	
	@Override
	public void ruleModelEs2Mysql() {		
		
//		List<EngProduct> list = engProductDao.queryByField(new EngProduct());
//		
//		for(EngProduct one : list){		
//			String productType = one.getProductType();		
//			logger.info("开始转移工程:{}",productType);			
//			//转移3张表
//			List<JSONObject> modelList;
//			modelList = esRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, null, null);		
//			modelList = transfer(modelList);
//			mysqlRuleModelDao.batchInsert(modelList, RuleModelTable.SELF);
//			
//			modelList = esRuleModelDao.getRuleModel(RuleModelTable.MERGE, productType, null, null);
//			modelList = transfer(modelList);
//			mysqlRuleModelDao.batchInsert(modelList, RuleModelTable.MERGE);
//
//			modelList = esRuleModelDao.getRuleModel(RuleModelTable.RELEASE, productType, null, null);
//			modelList = transfer(modelList);
//			mysqlRuleModelDao.batchInsert(modelList, RuleModelTable.RELEASE);		
//			
//		}
		
	}
	
	@Override
	public void functionEs2Hbase(){
		List<EngProduct> list = engProductDao.queryByField(new EngProduct());
		for(EngProduct one : list){	
			//转移函数
			List<EngScriptFunction> funcList = esCustomFunctionDao.findByProduct(one.getProductType());
			for(EngScriptFunction func : funcList){
				hbaseScriptFunctionDao.insert(func);	
				logger.info("转移函数中,productType:{},func:{}",one.getProductType(),func.getLabel());		
			}		
		}
		//转移模板
		List<EngScriptTemplate> templateList = esScriptTemplateDao.getAll();
		for(EngScriptTemplate template : templateList){
			hbaseScriptTemplateDao.insert(template);	
			logger.info("转移模板中,template:{}",template.getLabel());		
		}	
	}
	
	@Override
	public void ruleModelMongo2Es() {		
//		initIndex(RuleModelTable.SELF.getName());
//		initIndex(RuleModelTable.MERGE.getName());
//		initIndex(RuleModelTable.RELEASE.getName());	
//		initIndex(EngScriptFunctionDaoImpl.TABLE);
//		initIndex(EngScriptTemplateDaoImpl.TABLE);		
//		
//		List<EngProduct> list = engProductDao.queryByField(new EngProduct());
//		for(EngProduct one : list){			
//			String productType = one.getProductType();		
//			logger.info("开始转移工程:{}",productType);			
//			//转移3张表
//			List<JSONObject> modelList;
//			modelList = mongoRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, null, null);		
//			modelList = transfer(modelList);
//			esRuleModelDao.batchInsert(modelList, RuleModelTable.SELF);
//			
//			modelList = mongoRuleModelDao.getRuleModel(RuleModelTable.MERGE, productType, null, null);
//			modelList = transfer(modelList);
//			esRuleModelDao.batchInsert(modelList, RuleModelTable.MERGE);
//
//			modelList = mongoRuleModelDao.getRuleModel(RuleModelTable.RELEASE, productType, null, null);
//			modelList = transfer(modelList);
//			esRuleModelDao.batchInsert(modelList, RuleModelTable.RELEASE);		
//			
//			//转移函数
//			List<EngScriptFunction> funcList = mongoScriptFunctionDao.findByProduct(productType);
//			for(EngScriptFunction func : funcList){
//				esCustomFunctionDao.insert(func);	
//				logger.info("转移函数中,productType:{},func:{}",productType,func.getLabel());		
//			}										
//		}
//		
//		//转移模板
//		List<EngScriptTemplate> templateList = mongoScriptTemplateDao.getAll();
//		for(EngScriptTemplate template : templateList){
//			esScriptTemplateDao.insert(template);	
//			logger.info("转移模板中,template:{}",template.getLabel());		
//		}			
	}
	
	private void initIndex(String indexName){
		if(client.admin().indices().exists(indicesExistsRequest(indexName)).actionGet().isExists()){
			boolean flag = client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet().isAcknowledged();
			logger.info("删除索引{}，用于初始化，结果:{}",indexName,flag);	
		}
	}
	
	
	private List<JSONObject> transfer(List<JSONObject> modelList){
		List<JSONObject> result = new LinkedList<JSONObject>();
		for(JSONObject model : modelList){
			String srcJson = model.toJSONString();
			String newJson = transfer(srcJson);					
			JSONObject newOne = JSONObject.parseObject(newJson);		
			result.add(newOne);
		}			
		return result;
	} 
	
	private String transfer(String jsonStr){		
		String regex = "[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}";
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(jsonStr);
		while (matcher.find()) {
			String temp = jsonStr.substring(matcher.start(), matcher.end());	
			logger.info("发现id需要格式转换,id : "+temp);
			jsonStr = StringUtils.replace(jsonStr, temp, StringUtils.replace(temp, "-", "_"));
		}
		return jsonStr;
	}
	
	
	
	
	
//	@Override
//	public void initSystemFunction() {
//		
//		initIndex("rule_common_function");
//		//读取文件
//		org.springframework.core.io.Resource resource = new ClassPathResource("META-INF/sysfuncs");  
//		try {
//			File folder = resource.getFile();		
//			for(File one : folder.listFiles()){
//				String functionStr = FileUtils.readFileToString(one);
//				String fileName = one.getName();
//				
//				EngScriptFunction entity = new EngScriptFunction();
//				entity.setProductType("system");
//				entity.setFunction(functionStr);
//				entity.setName(fileName);
//				entity.setUsername("system");								
//				//entity.checkBaseSyntax();
//				entity.setLabel(parseFunctionName(functionStr));		
//				entity.setTemplate(parseTemplate(functionStr));				
//				esCommonFunctionDao.insert(entity);
//			}
//			
//			
//		} catch (IOException e) {		
//			e.printStackTrace();
//		}
//	}
	
	public static List<String> sysFuncList = new ArrayList<String>();
	static{
		sysFuncList.add("集合包含");
		sysFuncList.add("集合中包含某些关键字");
		sysFuncList.add("转换成时间");
		sysFuncList.add("字符串包含");
		sysFuncList.add("子集包中某一个含在全集中");
		sysFuncList.add("子集全部都包含在全集中");
	}
	@Override
	public void initSystemFunction() {
		initIndex("rule_common_function");
		//读取文件
		for(String funcDefine : sysFuncList){
			String funDir="/META-INF/sysfuncs/"+funcDefine;
			String sampDir="/META-INF/sysfunSample/"+funcDefine;
			
			String functionStr =analyResource(funDir);
			String sampleStr =analyResource(sampDir);
			
			String fileName = funcDefine;
			
			EngScriptFunction entity = new EngScriptFunction();
			entity.setProductType("system");
			entity.setFunction(functionStr);
			entity.setName(fileName);
			entity.setUsername("system");								
			//entity.checkBaseSyntax();
			entity.setLabel(parseFunctionName(functionStr));		
			entity.setTemplate(parseTemplate(functionStr));				
			entity.setSample(sampleStr);
			
			logger.info("转移系统函数中,template:{},sample:{}",entity.getLabel(),sampleStr);	
			hbaseScriptFunctionDao.insert(entity);
	
		}	
	}	
	
	private String analyResource(String dir){
		StringBuffer out = new StringBuffer();
		try {
			InputStream is = this.getClass().getResourceAsStream(dir);
			byte[] b = new byte[4096];
			for (int n; (n = is.read(b)) != -1;) {
				out.append(new String(b, 0, n));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toString();
	}
	
	
	@Override
	@Deprecated
	public void initSystemFunction(String dir) {
		try {
			File folder = new File(dir);	
			for(File one : folder.listFiles()){
				String functionStr = FileUtils.readFileToString(one);
				String fileName = one.getName();
				
				EngScriptFunction entity = new EngScriptFunction();
				entity.setProductType("system");
				entity.setFunction(functionStr);
				entity.setName(fileName);
				entity.setUsername("system");								
				//entity.checkBaseSyntax();
				entity.setLabel(parseFunctionName(functionStr));		
				entity.setTemplate(parseTemplate(functionStr));				
				hbaseScriptFunctionDao.insert(entity);
			}	
		} catch (IOException e) {		
			e.printStackTrace();
		}
		
	}		
	
	
	/**
	 * 获取函数名 如果解析不出 则返回null
	 * @return
	 */
	private String parseFunctionName(String function) {
		if(StringUtils.isBlank(function))
			throw new RuntimeException("函数主体为空");
		
		String regex = "^\\s*function\\s+.*\\(";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(function);
		String functionName = null;
		if(matcher.find()){             
			functionName = matcher.group();       		        
			//去除头和括号
			functionName = StringUtils.replace(functionName, "function", "");
			functionName = StringUtils.replace(functionName, "(", "");
			//去除空格
			functionName = functionName.replaceAll("\\s", "");
		}
		return functionName;
	}
	
	private String parseTemplate(String function) {
		if(StringUtils.isBlank(function))
			throw new RuntimeException("函数主体为空");
		
		String regex = "^\\s*function\\s+.*\\(.*\\)\\s*\\{";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(function);
		String template = null;
		if(matcher.find()){             
			template = matcher.group();       		        
			//去除头和括号
			template = StringUtils.replace(template, "function", "");
			template = StringUtils.replace(template, "{", "");			
			//去除空格
			template = template.replaceAll("\\s", "");
		}
		return template;
	}

	public static void main(String[] args) {
		InputStream is=AdminToolServiceImpl.class.getResourceAsStream("/META-INF/sysfuncs/集合包含");  
		System.out.println(is);
		
	}
	
}
