package com.mobanker.engine.design.busi.configma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.gildur.jshint4j.Error;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Collections2;
import com.mobanker.engine.design.busi.workspace.EngineWorkspaceManager;
import com.mobanker.engine.design.dao.EngBusiOperatorDao;
import com.mobanker.engine.design.dao.EngDefineFieldDao;
import com.mobanker.engine.design.dao.EngFatTestDao;
import com.mobanker.engine.design.dao.EngGroupDao;
import com.mobanker.engine.design.dao.EngProductDao;
import com.mobanker.engine.design.dao.EngQueryDictionaryDao;
import com.mobanker.engine.design.dao.EngQueryFieldDao;
import com.mobanker.engine.design.dao.EngQueryHostDao;
import com.mobanker.engine.design.dao.EngTestZkDao;
import com.mobanker.engine.design.dto.EngineDefineFieldDto;
import com.mobanker.engine.design.dto.EngineGroupDto;
import com.mobanker.engine.design.dto.EngineMemberDto;
import com.mobanker.engine.design.dto.EngineProductDto;
import com.mobanker.engine.design.dto.EngineQueryFieldDto;
import com.mobanker.engine.design.dto.EngineQueryModelDto;
import com.mobanker.engine.design.dto.EngineRuleAllConfigDto;
import com.mobanker.engine.design.dto.EngineTestZkDto;
import com.mobanker.engine.design.hotdeploy.EngineTestRuleHotDeployService;
import com.mobanker.engine.design.mongo.EngRuleModelDao;
import com.mobanker.engine.design.mongo.EngScriptFunctionDao;
import com.mobanker.engine.design.mongo.EngScriptTemplateDao;
import com.mobanker.engine.design.mongo.assist.RuleModelTable;
import com.mobanker.engine.design.pojo.EngBusiOperator;
import com.mobanker.engine.design.pojo.EngDefineField;
import com.mobanker.engine.design.pojo.EngFatTest;
import com.mobanker.engine.design.pojo.EngGroup;
import com.mobanker.engine.design.pojo.EngProduct;
import com.mobanker.engine.design.pojo.EngQueryDictionary;
import com.mobanker.engine.design.pojo.EngQueryField;
import com.mobanker.engine.design.pojo.EngQueryHost;
import com.mobanker.engine.design.pojo.EngScriptFunction;
import com.mobanker.engine.design.pojo.EngScriptTemplate;
import com.mobanker.engine.design.pojo.EngTestZk;
import com.mobanker.engine.design.pojo.transfer.EngineQueryFieldDtoFunction;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.container.EngineCpntContainer;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineCpntIsEmptyException;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.exception.EngineFieldExistException;
import com.mobanker.engine.framkwork.relation.EngineDependField;
import com.mobanker.engine.framkwork.script.syntax.EngineJavaScriptSyntaxCheck;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.zkc.cache.CacheManager;

@Service
public class EngineConfigManageServiceImpl implements EngineConfigManageService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	private final static String BUSI_DESC = "配置信息服务";
	
	@Autowired
	private EngQueryDictionaryDao engQueryDictionaryDao;
	
	@Autowired
	private EngQueryFieldDao engQueryFieldDao;
	
	@Autowired
	private EngBusiOperatorDao engBusiOperatorDao;
		
	@Autowired
	private EngProductDao engProductDao;
	
	@Autowired
	private EngGroupDao engGroupDao;
	
	@Autowired
	private EngDefineFieldDao engDefineFieldDao;
	
	@Resource(name = "hbaseScriptTemplateDao")
	private EngScriptTemplateDao engScriptTemplateDao;
	
	@Resource(name = "hbaseScriptFunctionDao")
	private EngScriptFunctionDao engScriptFunctionDao;
	
	@Resource(name = "hbaseScriptFunctionDao")
	private EngScriptFunctionDao engCommonFunctionDao;
	
	@Autowired
	private EngineWorkspaceManager engineWorkspaceManager;
	
	@Autowired
	private EngQueryHostDao engQueryHostDao;
	
	@Resource(name = "mysqlRuleModelDao")
	private EngRuleModelDao engRuleModelDao;
	
	@Autowired
	private EngFatTestDao engFatTestDao;
	
	@Autowired
	private EngTestZkDao engTestZkDao;
	
	@Resource(name = "engineTestRuleHotDeployService")
	private EngineTestRuleHotDeployService engineTestRuleHotDeployService;
	
	@Deprecated
	@Override
	public List<EngineQueryModelDto> getQueryModelList(String username,String productType) {
		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);
		return this.getQueryModelList(productType);
	}
	@Deprecated
	@Override
	public List<EngineQueryModelDto> getQueryModelList(String productType) {
		EngineAssert.checkNotBlank(productType);
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
		
		List<EngineQueryModelDto> result = new LinkedList<EngineQueryModelDto>();
		
		List<EngQueryDictionary> engQueryDictionarylist = engQueryDictionaryDao.queryByProductType(productType);
		for(EngQueryDictionary dict : engQueryDictionarylist){	
			//List<EngQueryField> engQueryFieldList = engQueryFieldDao.queryByDictId(dict.getId());
			List<EngQueryField> engQueryFieldList = engQueryFieldDao.queryByQueryIdAndProductType(dict.getQueryId(), productType);
			Collection<EngineQueryFieldDto> fieldDtoList = Collections2.transform(engQueryFieldList, new EngineQueryFieldDtoFunction()); 
			
			EngineQueryModelDto dto = new EngineQueryModelDto();
			dto.setFieldList(new ArrayList<EngineQueryFieldDto>(fieldDtoList));
			dto.setMemo(dict.getMemo());
			dto.setQueryDomain(dict.getQueryDomain());
			dto.setQueryId(dict.getQueryId());
			dto.setQueryName(dict.getQueryName());
			
			result.add(dto);
		}
		//有可能要把这个人的组件数据抓出output的参数 待定
				
		return result;
	}
	
	

	@Override
	public List<EngineDefineFieldDto> getEngineParamList(String username,
			String productType) {
		EngineAssert.checkNotBlank(productType);
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);
				
		List<EngineDefineFieldDto> resultList = this.getDefineFieldList(username,productType);
		for(EngineDefineFieldDto dto : resultList){
			dto.setUsername(dto.getUsername());
			String addname = dto.getUsername();
			EngBusiOperator operator = engBusiOperatorDao.queryLastOneByUsername(addname);			
			dto.setRealname(operator==null?"未知":operator.getName());
		}
		
//		List<EngineQueryModelDto> queryList = this.getQueryModelList(username, productType);
//		for(EngineQueryModelDto query : queryList){						
//			Collection<EngineQueryFieldDto> fieldList = query.getFieldList();
//			for(EngineQueryFieldDto field : fieldList){
//				EngineDefineFieldDto dto = new EngineDefineFieldDto();
//				dto.setFieldKey(field.getFieldKey());
//				dto.setFieldName(field.getFieldName());
//				dto.setFieldRefValue(field.getFieldRefValue());
//				dto.setFieldType(field.getFieldType());
//				dto.setFieldUse("input");
//				dto.setIsArr(field.getIsArr());
//				dto.setUsername("查询项");				
//				resultList.add(dto);
//			}
//		}
		
		return resultList;
	}	
	 
	@Cacheable(value="rule_default",key="'takeEngineParamList'+#productType")
	@Override
	public List<EngineDefineFieldDto> getEngineParamList(String productType) {
		EngineAssert.checkNotBlank(productType);
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
				
		List<EngineDefineFieldDto> resultList = this.getDefineFieldList(productType);
		for(EngineDefineFieldDto dto : resultList){
			String addname = dto.getUsername();
			EngBusiOperator operator = engBusiOperatorDao.queryLastOneByUsername(addname);
			dto.setUsername(operator==null?"未知":operator.getName());
		}
		

//		List<EngineQueryModelDto> queryList = this.getQueryModelList(productType);
//		for(EngineQueryModelDto query : queryList){						
//			Collection<EngineQueryFieldDto> fieldList = query.getFieldList();
//			for(EngineQueryFieldDto field : fieldList){
//				EngineDefineFieldDto dto = new EngineDefineFieldDto();
//				dto.setFieldKey(field.getFieldKey());
//				dto.setFieldName(field.getFieldName());
//				dto.setFieldRefValue(field.getFieldRefValue());
//				dto.setFieldType(field.getFieldType());
//				dto.setFieldUse("input");
//				dto.setIsArr(field.getIsArr());
//				dto.setUsername("查询项");				
//				resultList.add(dto);
//			}
//		}
		
		return resultList;
	}	
		
	
	
	@Override
	public List<EngineDefineFieldDto> getDefineFieldList(String username,
			String productType) {		
		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);	
		return this.getDefineFieldList(productType);
	}

	@Override
	public List<EngineDefineFieldDto> getDefineFieldList(
			String productType) {
		EngineAssert.checkNotBlank(productType);
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
		
		List<EngineDefineFieldDto> result = new LinkedList<EngineDefineFieldDto>();
		
		List<EngDefineField> engOutputFieldList = engDefineFieldDao.queryByProductType(productType);
		for(EngDefineField one : engOutputFieldList){				
			EngineDefineFieldDto dto = new EngineDefineFieldDto();
			dto.setId(one.getId());
			dto.setFieldKey(one.getFieldKey());
			dto.setFieldName(one.getFieldName());
			dto.setFieldType(one.getFieldType());
			dto.setIsArr(one.getIsArr());
			dto.setFieldUse(one.getFieldUse());			
			dto.setFieldRefValue(one.getFieldRefValue());
			dto.setUsername(one.getUsername());
			result.add(dto);
		}
		
		
		return result;
	}	
	
	/**
	 * 检查字段，和其中文名 是否符合规范
	 * @param fieldKey
	 * @param fieldName
	 */
	private void checkFieldContent(String fieldKey,String fieldName){
		String regex = "^(\\w){1,50}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(fieldKey);
		if(!matcher.matches()){
			throw new EngineFieldExistException("fieldKey必须是由字母，数字和下划线组成，并且长度小于50个字符:"+fieldKey);
		}		
		
		//fieldName不能有空格和符号#
		if(fieldName.indexOf(" ")>=0){
			throw new EngineFieldExistException("fieldName不能存在空格:"+fieldName);
		}
		if(fieldName.indexOf("#")>=0){
			throw new EngineFieldExistException("fieldName不能存在#符号:"+fieldName);
		}
	}
	
	@CacheEvict(value="rule_default",key="'takeEngineParamList'+#productType")
	@Override
	public void insertDefineField(String username,String productType,EngineDefineFieldDto dto) {		
		EngineAssert.checkNotNull(dto,"上传的dto为空");
		EngineAssert.checkNotBlank(dto.getFieldKey(),"上传的fieldKey为空");
		EngineAssert.checkNotBlank(dto.getFieldName(),"上传的fieldName为空");
		EngineAssert.checkNotBlank(dto.getFieldType(),"上传的fieldType为空");
		EngineAssert.checkNotBlank(dto.getIsArr(),"上传的isArr为空");
		EngineAssert.checkNotBlank(dto.getFieldUse(),"上传的fieldUse为空");
		EngineAssert.checkNotBlank(dto.getFieldRefValue(),"上传的fieldRefValue为空");
					
		EngineUtil.checkNameFormat(dto.getFieldKey(),3,50,"字段名（英文）");
		EngineUtil.checkNameFormat(dto.getFieldName(),1,50,"字段名（中文）");
		
		if(StringUtils.isNumeric(dto.getFieldKey().substring(0, 1))){
			throw new EngineFieldExistException("fieldKey的首字符不能是数字，fieldKey:"+dto.getFieldKey());
		}
		checkFieldContent(dto.getFieldKey(),dto.getFieldName());					
		
		//检查申明项中是否有字段，和字段名是被占用的
		EngDefineField engDefineField = engDefineFieldDao.queryLastOneByFieldKeyAndProductType(dto.getFieldKey(),productType);
		if(engDefineField != null)
			throw new EngineFieldExistException("数据库中已经存在一条相同fieldKey的记录，记录id:"+engDefineField.getId()+",key:"+dto.getFieldKey());
		engDefineField = engDefineFieldDao.queryLastOneByFieldNameAndProductType(dto.getFieldName(),productType);
		if(engDefineField != null)
			throw new EngineFieldExistException("数据库中已经存在一条相同fieldName的记录，记录id:"+engDefineField.getId()+",name:"+dto.getFieldName());
		
		//检查查询项中是否有字段，和字段名是被占用的
		EngQueryField engQueryField = engQueryFieldDao.queryLastOneByFieldKeyAndProductType(dto.getFieldKey(),productType);
		if(engQueryField != null)
			throw new EngineFieldExistException("查询项中已经存在一条相同fieldKey的记录，记录id:"+engQueryField.getId()+",key:"+dto.getFieldKey());
		engQueryField = engQueryFieldDao.queryLastOneByFieldNameAndProductType(dto.getFieldName(),productType);
		if(engQueryField != null)
			throw new EngineFieldExistException("查询项中已经存在一条相同fieldName的记录，记录id:"+engQueryField.getId()+",name:"+dto.getFieldName());
	
		
		EngDefineField one = new EngDefineField();
		one.setFieldKey(dto.getFieldKey());
		one.setFieldName(dto.getFieldName());
		one.setFieldType(dto.getFieldType());
		one.setIsArr(dto.getIsArr());
		one.setFieldUse(dto.getFieldUse());
		one.setFieldRefValue(dto.getFieldRefValue());
		one.setProductType(productType);
		one.setUsername(username);		
		engDefineFieldDao.insert(one);
	}

	@Override
	@Transactional
	@CacheEvict(value="rule_default",key="'takeEngineParamList'+#productType")
	public void importDefineField(String username, String productType,
			List<EngineDefineFieldDto> dtoList) {
		for(EngineDefineFieldDto dto : dtoList){
			this.insertDefineField(username, productType, dto);
		}		
		logger.info("总共倒入{}条",dtoList.size());
	}	
	
	
	@Override
	@CacheEvict(value="rule_default",key="'takeEngineParamList'+#productType")
	public void updateDefineField(String username,String productType,EngineDefineFieldDto dto) {
		EngineAssert.checkNotNull(dto,"上传的dto为空");
		EngineAssert.checkNotNull(dto.getId(),"上传的id为空");		
		EngineAssert.checkNotBlank(dto.getFieldKey(),"上传的fieldKey为空");
		EngineAssert.checkNotBlank(dto.getFieldName(),"上传的fieldName为空");
		EngineAssert.checkNotBlank(dto.getFieldType(),"上传的fieldType为空");
		EngineAssert.checkNotBlank(dto.getIsArr(),"上传的isArr为空");
		EngineAssert.checkNotBlank(dto.getFieldRefValue(),"上传的fieldRefValue为空");
		EngineAssert.checkNotBlank(dto.getFieldUse(),"上传的fieldUse为空");
		
		EngineAssert.checkArgument(dto.getFieldKey().length()<=50, "字段英文名最大接受50个英文字母");
		EngineAssert.checkArgument(dto.getFieldName().length()<=50, "字段中文名最大接受50个汉字");
				
		EngineUtil.checkNameFormat(dto.getFieldKey());
		EngineUtil.checkNameFormat(dto.getFieldName());		
		
		if(StringUtils.isNumeric(dto.getFieldKey().substring(0, 1))){
			throw new EngineFieldExistException("fieldKey的首字符不能是数字，fieldKey:"+dto.getFieldKey());
		}
		checkFieldContent(dto.getFieldKey(),dto.getFieldName());
		
		EngDefineField one = engDefineFieldDao.get(dto.getId());
		
		EngineAssert.checkNotNull(one,"根据id没有找到该记录:%s",dto.getId());			
		EngineAssert.checkArgument(StringUtils.equals(one.getProductType(), productType),
				"根据对应的产品信息不相符,记录:%s,本次提交:%s",one.getProductType(), productType);
//		EngineAssert.checkArgument(StringUtils.equals(one.getUsername(), username),
//				"只有是创建该输出字段的用户才有权限修改该字段,记录:%s,本次提交:%s",one.getUsername(), username);

		boolean isFieldChange = false;
		//如果fieldKey变化了
		if(!StringUtils.equals(dto.getFieldKey(), one.getFieldKey())){
			//检查申明项中是否有字段，和字段名是被占用的
			isFieldChange = true;
			EngDefineField engDefineField = engDefineFieldDao.queryLastOneByFieldKeyAndProductType(dto.getFieldKey(),productType);
			if(engDefineField != null&&!engDefineField.getId().equals(dto.getId()))
				throw new EngineException("数据库中已经存在一条相同fieldKey的记录，记录id:"+engDefineField.getId());					
		}
		if(!StringUtils.equals(dto.getFieldName(), one.getFieldName())){
			//检查申明项中是否有字段，和字段名是被占用的
			isFieldChange = true;
			EngDefineField engDefineField = engDefineFieldDao.queryLastOneByFieldNameAndProductType(dto.getFieldName(),productType);
			if(engDefineField != null&&!engDefineField.getId().equals(dto.getId()))
				throw new EngineException("数据库中已经存在一条相同fieldName的记录，记录id:"+engDefineField.getId());					
		}
		if(isFieldChange){
			//检查该字段是否被引用
			String fieldKey = one.getFieldKey();
			checkRelaParams(RuleModelTable.SELF,productType,fieldKey);
			checkRelaParams(RuleModelTable.MERGE,productType,fieldKey);					
		}
//		
//		//检查查询项中是否有字段，和字段名是被占用的
//		EngQueryField engQueryField = engQueryFieldDao.queryLastOneByFieldKeyAndProductType(dto.getFieldKey(),productType);
//		if(engQueryField != null)
//			throw new EngineException("查询项中已经存在一条相同fieldKey的记录，记录id:"+engQueryField.getId());
//		engQueryField = engQueryFieldDao.queryLastOneByFieldNameAndProductType(dto.getFieldName(),productType);
//		if(engQueryField != null)
//			throw new EngineException("查询项中已经存在一条相同fieldName的记录，记录id:"+engQueryField.getId());				
		
		//FIXME 检查组件中是否有用到的 这个还没做				
		one.setFieldKey(dto.getFieldKey());
		one.setFieldName(dto.getFieldName());				
		one.setFieldType(dto.getFieldType());
		one.setIsArr(dto.getIsArr());
		//one.setFieldUse(dto.getFieldUse());
		one.setFieldRefValue(dto.getFieldRefValue());
		try{
			engDefineFieldDao.update(one);
		}catch(DuplicateKeyException e){
			throw new EngineException("字段名重复了，请检查是否已经存在一个名为"+dto.getFieldKey()+"的字段名", e);
		}
	}

	@Override
	@CacheEvict(value="rule_default",key="'takeEngineParamList'+#productType")
	public void deleteDefineField(String username, String productType, Long id) {
		EngDefineField one = engDefineFieldDao.get(id);
		
		EngineAssert.checkNotNull(one,"根据id没有找到该记录:%s",id);			
		EngineAssert.checkArgument(StringUtils.equals(one.getProductType(), productType),
				"根据对应的产品信息不相符,记录:%s,本次提交:%s",one.getProductType(), productType);
		
		String fieldKey = one.getFieldKey();
		
		checkRelaParams(RuleModelTable.SELF,productType,fieldKey);
		checkRelaParams(RuleModelTable.MERGE,productType,fieldKey);
		
		engDefineFieldDao.delete(one);
	}
	
	private void checkRelaParams(RuleModelTable table,String productType,String fieldKey){
		try{
			EngineCpntContainer container = engineWorkspaceManager.getProductContainer(table,productType);
			//找出有关联的字段				
			List<EngineDependField> dependList = container.getCpntsByType(EngineDependField.class);
			for(EngineDependField depend : dependList){
				if(depend.dependFields().keySet().contains(fieldKey)){
					EngineCpnt cpnt = (EngineCpnt) depend;
					throw new EngineException(cpnt.type()+"["+cpnt.name()+"]引用了这个字段无法删除，所属域:"+table.getName());
				}		
			}					
		}catch(EngineCpntIsEmptyException e){//如果没有组件则没问题
			logger.warn("该工程还没有组件，工程:"+productType+"，所属域:"+table.getName());
			return;
		}	
	}

	@Override
	public List<EngScriptTemplate> showScriptTemplate(String productType) {
		EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");			
		//获取模板
		//List<EngScriptTemplate> templateList = engScriptTemplateDao.findByProduct(productType);
		//模板不根据产品线了 通用
		List<EngScriptTemplate> templateList = engScriptTemplateDao.getAll();
		
		return templateList;
	}

	@Override
	public void addScriptTemplate(EngScriptTemplate entity) {
		EngineAssert.checkNotNull(entity,"上送参数entity不可为空");			
		EngineAssert.checkNotBlank(entity.getLabel(),"上送参数label不可为空");
		EngineAssert.checkNotBlank(entity.getName(),"上送参数name不可为空");
		EngineAssert.checkNotBlank(entity.getProductType(),"上送参数productType不可为空");
		EngineAssert.checkNotBlank(entity.getTemplate(),"上送参数template不可为空");
		EngineAssert.checkNotBlank(entity.getUsername(),"上送参数username不可为空");
		
		EngScriptTemplate checkFlag = engScriptTemplateDao.findOne(entity.getLabel());
		if(checkFlag != null)
			throw new EngineException("该label已经被占用");
		engScriptTemplateDao.insert(entity);				
	}

	@Override
	public void modScriptTemplate(EngScriptTemplate entity) {
		EngineAssert.checkNotNull(entity,"上送参数entity不可为空");			
		EngineAssert.checkNotBlank(entity.getLabel(),"上送参数label不可为空");
		EngineAssert.checkNotBlank(entity.getName(),"上送参数name不可为空");
		EngineAssert.checkNotBlank(entity.getProductType(),"上送参数productType不可为空");
		EngineAssert.checkNotBlank(entity.getTemplate(),"上送参数template不可为空");
		EngineAssert.checkNotBlank(entity.getUsername(),"上送参数username不可为空");				
		
		EngScriptTemplate one = engScriptTemplateDao.findOne(entity.getLabel());
		EngineAssert.checkArgument(StringUtils.equals(one.getUsername(), entity.getUsername()),"不可以修改别人添加的模板,添加人:%s",one.getUsername());
		
		engScriptTemplateDao.update(entity);
	}

	@Override
	public void delScriptTemplate(String username,
			String productType, String label) {
		EngScriptTemplate one = engScriptTemplateDao.findOne(label);
		EngineAssert.checkArgument(StringUtils.equals(one.getUsername(), username),"不可以删除别人添加的模板,添加人:%s",one.getUsername());				
		engScriptTemplateDao.delete(label);
	}
	
	
	
	@Override
	public List<EngScriptFunction> showScriptFunction(String productType) {
		EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");			
		//获取函数
		List<EngScriptFunction> functionList = engScriptFunctionDao.findByProduct(productType);						
		return functionList;
	}
	
	@Override
	public List<EngScriptFunction> showCommonFunction() {
		//获取函数
		List<EngScriptFunction> functionList = engCommonFunctionDao.findByProduct("system");						
		return functionList;
	}

	@Override
	public void addScriptFunction(EngScriptFunction entity) {
		EngineAssert.checkNotNull(entity,"上送参数entity不可为空");			
		//EngineAssert.checkNotBlank(entity.getLabel(),"上送参数label不可为空");
		EngineAssert.checkNotBlank(entity.getName(),"上送参数name不可为空");
		EngineAssert.checkNotBlank(entity.getProductType(),"上送参数productType不可为空");
		EngineAssert.checkNotBlank(entity.getFunction(),"上送参数function不可为空");
		EngineAssert.checkNotBlank(entity.getUsername(),"上送参数username不可为空");
				
		EngineUtil.checkNameFormat(entity.getName());
		
		entity.checkBaseSyntax();
		entity.setLabel(entity.parseFunctionName());		
		entity.setTemplate(entity.parseTemplate());
		
		EngScriptFunction checkFlag = engScriptFunctionDao.findOne(entity.getProductType(), entity.getLabel());
		if(checkFlag != null)
			throw new EngineException("函数名已经被占用");
		
		
		List<Error> errList = EngineJavaScriptSyntaxCheck.getInstance().checkSyntax(entity.getFunction());		
		if(errList.size() > 0){
			String checkResult = errList.toString();
			throw new EngineException("检测到函数语法错误->"+checkResult);
		}
			
		engScriptFunctionDao.insert(entity);	
		EngineUtil.waitSecond();
	}

	@Override
	public void modScriptFunction(EngScriptFunction entity) {
		EngineAssert.checkNotNull(entity,"上送参数entity不可为空");			
		EngineAssert.checkNotBlank(entity.getLabel(),"上送参数label不可为空");
		EngineAssert.checkNotBlank(entity.getName(),"上送参数name不可为空");
		EngineAssert.checkNotBlank(entity.getProductType(),"上送参数productType不可为空");
		EngineAssert.checkNotBlank(entity.getFunction(),"上送参数function不可为空");
		EngineAssert.checkNotBlank(entity.getUsername(),"上送参数username不可为空");				
		
		EngineUtil.checkNameFormat(entity.getName());
		
		entity.checkBaseSyntax();				
		
		EngScriptFunction record = engScriptFunctionDao.findOne(entity.getProductType(), entity.getLabel());
		EngineAssert.checkNotNull(record,"原记录不存在:%s",entity.getLabel());
		EngineAssert.checkArgument(StringUtils.equals(record.getUsername(), entity.getUsername()),"不可以修改别人添加的函数,添加人:%s",record.getUsername());	
		
		if(!StringUtils.equals(entity.getLabel(), entity.parseFunctionName())){//如果函数名被修改,则需要检查该函数名是否在系统中已经存在，以免重复			
			EngScriptFunction checkFlag = engScriptFunctionDao.findOne(entity.getProductType(), entity.parseFunctionName());
			if(checkFlag != null)
				throw new EngineException("函数名已经被占用");
			
		}					
				
		List<Error> errList = EngineJavaScriptSyntaxCheck.getInstance().checkSyntax(entity.getFunction());		
		if(errList.size() > 0){
			String checkResult = errList.toString();
			throw new EngineException("检测到函数语法错误->"+checkResult);
		}		
		
		//删除原来的函数记录
		engScriptFunctionDao.delete(entity.getProductType(), entity.getLabel());
		entity.setLabel(entity.parseFunctionName());		
		entity.setTemplate(entity.parseTemplate());
		engScriptFunctionDao.insert(entity);
		
		EngineUtil.waitSecond();
	}

	@Override
	public void delScriptFunction(String username, String productType,
			String label) {
		EngScriptFunction one = engScriptFunctionDao.findOne(productType, label);
		EngineAssert.checkArgument(StringUtils.equals(one.getUsername(), username),"不可以删除别人添加的函数,添加人:%s",one.getUsername());				
		engScriptFunctionDao.delete(productType, label);
		
		EngineUtil.waitSecond();
	}
	
	@Override
	public List<EngineMemberDto> showMemberInfo() {
		
		List<EngBusiOperator> list = engBusiOperatorDao.queryByField(new EngBusiOperator());
		
		List<EngineMemberDto> result = new LinkedList<EngineMemberDto>();
		for(EngBusiOperator one : list){
			EngineMemberDto dto = new EngineMemberDto();
			dto.setRealname(one.getName());
			dto.setRole(one.getRole());
			dto.setUsername(one.getUsername());
			dto.setRoleDesc(one.roleDesc());	
			result.add(dto);
		}
		return result;
	}
	
	@Override
	@Deprecated
	public EngineRuleAllConfigDto exportProduct(String productType) {
		EngineRuleAllConfigDto result = new EngineRuleAllConfigDto();
		
		//处理eng_product
		EngProduct engProduct = engProductDao.queryLastOneByProductType(productType);
		EngineAssert.checkNotNull(engProduct,"没有找到eng_product记录");
		result.setEngProduct(engProduct);
		logger.info("导出了eng_product");
		
		//处理eng_query_host
		EngQueryHost engQueryHost = engQueryHostDao.queryLastOneByProductType(productType);
		result.setEngQueryHost(engQueryHost);
		logger.info("导出了eng_query_host");
		
		//处理eng_query_dictionary和eng_query_field
		List<EngineQueryModelDto> queryModelDtoList = this.getQueryModelList(productType);
		result.setEngineQueryModelList(queryModelDtoList);	
		logger.info("导出了eng_query_dictionary和eng_query_field");
		
		//处理eng_define_field
		List<EngDefineField> engDefineFieldList = engDefineFieldDao.queryByProductType(productType);
		result.setEngDefineFieldList(engDefineFieldList);
		logger.info("导出了eng_define_field");
		
		//处理rule_model_merge
		List<JSONObject> mergeList = engRuleModelDao.getRuleModel(RuleModelTable.MERGE, productType, null, null);
		result.setRuleModelMerge(mergeList);
		logger.info("导出了rule_model_merge");
		
		//处理rule_model_self
		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, null, null);
		result.setRuleModelSelf(selfList);
		logger.info("导出了rule_model_self");
		
		//处理rule_script_template
		//List<EngScriptTemplate> templateList = engScriptTemplateDao.findByProduct(productType);
		//result.setRuleScriptTemplate(templateList);
		//logger.info("导出了rule_script_template");
		
		//处理rule_script_function
		List<EngScriptFunction> functionList = engScriptFunctionDao.findByProduct(productType);
		result.setRuleScriptFunction(functionList);
		logger.info("导出了rule_script_function");
		
		return result;
	}
	
	@Override
	@Deprecated
	public void importProduct(String username,EngineRuleAllConfigDto dto,boolean isReplace) {
		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
		
		String productType = dto.getEngProduct().getProductType();
		//处理eng_product
		EngProduct engProduct = engProductDao.queryLastOneByProductType(productType);		
		if(engProduct != null){
			if(!isReplace){
				throw new EngineException("原产品已经存在，无法导入");
			}
			
			logger.info("删除原产品eng_product");
			engProductDao.delete(engProduct);
		}		
		engProduct = dto.getEngProduct();
		int count = engProductDao.insert(engProduct);
		logger.info("插入eng_product记录数:{}",count);
		
		//处理eng_query_host
		EngQueryHost engQueryHost = engQueryHostDao.queryLastOneByProductType(productType);
		if(engQueryHost != null){
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"删除原产品eng_query_host");
			engQueryHostDao.delete(engQueryHost);
		}	
		engQueryHost = dto.getEngQueryHost();
		count = engQueryHostDao.insert(engQueryHost);
		logger.info("插入eng_query_host记录数:{}",count);
		
		//处理eng_query_dictionary和eng_query_field
		engQueryDictionaryDao.deleteByProductType(productType);
		engQueryFieldDao.deleteByProductType(productType);
				
		List<EngineQueryModelDto> queryModelDtoList = dto.getEngineQueryModelList();
		for(EngineQueryModelDto queryModelDto : queryModelDtoList){
			EngQueryDictionary one = new EngQueryDictionary();
			one.setMemo(queryModelDto.getMemo());
			one.setProductType(productType);
			one.setQueryDomain(queryModelDto.getQueryDomain());
			one.setQueryId(queryModelDto.getQueryId());
			one.setQueryName(queryModelDto.getQueryName());
			engQueryDictionaryDao.insert(one);
			logger.info("插入eng_query_dictionary记录:{}",one.getQueryId());
			
			Collection<EngineQueryFieldDto> queryFieldList = queryModelDto.getFieldList();
			List<EngQueryField> engQueryFieldList = new LinkedList<EngQueryField>();
			for(EngineQueryFieldDto queryFieldDto : queryFieldList){
				EngQueryField engQueryField = new EngQueryField();
				engQueryField.setQueryId(one.getQueryId());
				engQueryField.setProductType(productType);
				engQueryField.setFieldDefaultValue(queryFieldDto.getFieldDefaultValue());
				engQueryField.setFieldKey(queryFieldDto.getFieldKey());
				engQueryField.setFieldName(queryFieldDto.getFieldName());
				engQueryField.setFieldRefValue(queryFieldDto.getFieldRefValue());
				engQueryField.setFieldType(queryFieldDto.getFieldType());
				engQueryField.setIsArr(queryFieldDto.getIsArr());
				engQueryFieldList.add(engQueryField);
			}			
			count = engQueryFieldDao.batchInsert(engQueryFieldList);
			logger.info("插入eng_query_field记录数:{},{}",one.getQueryId(),count);
		}
		
		//处理eng_define_field
		engDefineFieldDao.deleteByProductType(productType);		
		List<EngDefineField> engDefineFieldList = dto.getEngDefineFieldList();		
		logger.info("插入eng_define_field记录数:{}",engDefineFieldDao.batchInsert(engDefineFieldList));
		
		//处理rule_model_merge
		engRuleModelDao.deleteByProductType(productType, RuleModelTable.MERGE);
		List<JSONObject> mergeList = dto.getRuleModelMerge();
		clearMongoId(mergeList);
		logger.info("插入rule_model_merge记录数:{}",engRuleModelDao.batchInsert(mergeList,RuleModelTable.MERGE));
		
		//处理rule_model_self
		engRuleModelDao.deleteByProductType(productType, RuleModelTable.SELF);
		List<JSONObject> selfList = dto.getRuleModelSelf();
		clearMongoId(selfList);
		logger.info("插入rule_model_self记录数:{}",engRuleModelDao.batchInsert(selfList,RuleModelTable.SELF));
		
		//处理rule_script_template
//		engScriptTemplateDao.deleteByProductType(productType);
//		List<EngScriptTemplate> scriptTemplateList = dto.getRuleScriptTemplate();
//		for(EngScriptTemplate one : scriptTemplateList){
//			engScriptTemplateDao.insert(one);
//		}
//		logger.info("插入rule_script_template记录数:{}",scriptTemplateList.size());
		
		//处理rule_script_function
		engScriptFunctionDao.deleteByProductType(productType);
		List<EngScriptFunction> scriptFunctionList = dto.getRuleScriptFunction();
		for(EngScriptFunction one : scriptFunctionList){
			engScriptFunctionDao.insert(one);
		}
		logger.info("插入rule_script_function记录数:{}",scriptFunctionList.size());
		
		logger.info("导入完成");
	}

	private void clearMongoId(List<JSONObject> jsonList){
		for(JSONObject json : jsonList){
			json.remove("_id");
		}			
	}
	@Override
	public List<EngineProductDto> showAllProduct() {
		List<EngineProductDto> result = new LinkedList<EngineProductDto>();				
		List<EngProduct> productList = engProductDao.queryByField(new EngProduct());
		//逗号分隔
		for(EngProduct engProduct : productList){						
			result.add(new EngineProductDto(engProduct.getId(),engProduct.getName(),engProduct.getProductType(),engProduct.getGroupId()));	
		}		
		return result;
	}
	
	@Override
	public List<EngineGroupDto> showAllGroup() {
		List<EngineGroupDto> result = new LinkedList<EngineGroupDto>();				
		List<EngGroup> groupList = engGroupDao.queryByField(new EngGroup());
		//逗号分隔
		for(EngGroup engGroup : groupList){						
			result.add(new EngineGroupDto(engGroup.getId(),engGroup.getGroupName(),engGroup.getGroupType()));	
		}		
		return result;
	}
	
	@Override
	public void configFatTestAddress(Long id, String productType, String url) {
		EngFatTest engFatTest = new EngFatTest();
		engFatTest.setProductType(productType);
		engFatTest.setUrl(url);
		if(id == null||id == 0L){			
			engFatTestDao.insert(engFatTest);
		}else{
			engFatTest.setId(id);
			engFatTestDao.update(engFatTest);
		}
		
	}
	@Override
	public List<EngFatTest> showFatTestConfig(String productType) {		
		if(StringUtils.isBlank(productType)) productType = null;
		EngFatTest queryBean = new EngFatTest();
		queryBean.setProductType(productType);		
		return engFatTestDao.queryByField(queryBean);
	}
	@Override
	public EngineDefineFieldDto getDefineField(String productType,
			String fieldKey) {		
		EngDefineField one = engDefineFieldDao.queryLastOneByFieldKeyAndProductType(fieldKey, productType);
		EngineDefineFieldDto dto = new EngineDefineFieldDto();
		dto.setId(one.getId());
		dto.setFieldKey(one.getFieldKey());
		dto.setFieldName(one.getFieldName());
		dto.setFieldType(one.getFieldType());
		dto.setIsArr(one.getIsArr());
		dto.setFieldUse(one.getFieldUse());			
		dto.setFieldRefValue(one.getFieldRefValue());
		dto.setUsername(one.getUsername());
		return dto;
	}

	@Override
	public void insertTestZk(String username,String zkName,String zkUrl,String memo){
		
		EngineAssert.checkNotBlank(username,"用户名为空");			
		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);
		EngineAssert.checkNotBlank(zkName,"zk名称为空");
		EngineAssert.checkNotBlank(zkUrl,"zk地址为空");
		EngTestZk queryZk=new EngTestZk();
		queryZk.setZkUrl(zkUrl);
		if(engTestZkDao.queryLastOneByField(queryZk)!=null){
			throw new EngineException("数据库中已经存在一条相同zk地址记录:"+queryZk);
		}
		String prdUrl=CacheManager.getStaticMapValue("dubbo.zk.url");
		String[] prdUrlArr=StringUtils.split(prdUrl, ",");
		String[] zkUrlArr=StringUtils.split(zkUrl, ",");
		for(String pSplUrl:prdUrlArr){
			for(String zkSplUrl:zkUrlArr){
				if(StringUtils.equals(pSplUrl.trim(), zkSplUrl.trim())){
					throw new EngineException("配置地址不允许与当前环境zk地址相同！"+pSplUrl);
				}
			}
			
		}
		
		//测试联通性
		engineTestRuleHotDeployService.checkZkAddress(zkUrl);
		
		EngTestZk engTestZk=new EngTestZk();
		engTestZk.setUsername(username);
		engTestZk.setZkName(zkName);
		engTestZk.setZkUrl(zkUrl);
		engTestZk.setMemo(memo);
		engTestZk.setStatus("1");
		engTestZkDao.insert(engTestZk);
		
	}
	
	
	@Override
	public void updateTestZk(String id,String zkName,String zkUrl,String status,String memo){
		EngineAssert.checkNotBlank(id,"id为空");
		EngineAssert.checkNotBlank(zkName,"zk名称为空");
		EngineAssert.checkNotBlank(zkUrl,"zk地址为空");
		EngineAssert.checkNotBlank(status,"zk状态为空");
	
		String prdUrl=CacheManager.getStaticMapValue("dubbo.zk.url");
		String[] prdUrlArr=StringUtils.split(prdUrl, ",");
		String[] zkUrlArr=StringUtils.split(zkUrl, ",");
		for(String pSplUrl:prdUrlArr){
			for(String zkSplUrl:zkUrlArr){
				if(StringUtils.equals(pSplUrl.trim(), zkSplUrl.trim())){
					throw new EngineException("配置地址不允许与当前环境zk地址相同！"+pSplUrl);
				}
			}
			
		}
		//测试联通性
		engineTestRuleHotDeployService.checkZkAddress(zkUrl);
		
		EngTestZk engTestZk=new EngTestZk();
		engTestZk.setId(Long.valueOf(id));
		engTestZk.setZkName(zkName);
		engTestZk.setZkUrl(zkUrl);
		engTestZk.setMemo(memo);
		engTestZk.setStatus(status);
		engTestZkDao.updateField(engTestZk);
		
	}
	
	@Override
	public void deleteTestZk(String id){
		EngineAssert.checkNotBlank(id,"id为空");
		engTestZkDao.deleteById(Long.valueOf(id));
	}
	
	@Override
	public EngineTestZkDto queryTestZkById(String id) {
		EngTestZk engTestZk= engTestZkDao.get(Long.valueOf(id));
		if(engTestZk==null){
			throw new EngineException("未查询到该zk信息！id："+id);
		}
		return new EngineTestZkDto(engTestZk);
	}
	
	@Override
	public List<EngineTestZkDto> showAllTestZk(String status) {
		List<EngineTestZkDto> result = new LinkedList<EngineTestZkDto>();	
		EngTestZk queryBean=new EngTestZk();
		queryBean.setStatus(status);
		List<EngTestZk> testZkList = engTestZkDao.queryByField(queryBean);
		//逗号分隔
		for(EngTestZk engTestZk : testZkList){						
			result.add(new EngineTestZkDto(engTestZk));	
		}		
		return result;
	}
	
	
}
