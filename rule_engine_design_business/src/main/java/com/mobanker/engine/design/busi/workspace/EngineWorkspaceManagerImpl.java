package com.mobanker.engine.design.busi.workspace;

import java.io.IOException;
import java.text.Collator;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import pl.gildur.jshint4j.Error;

import com.alibaba.fastjson.JSONObject;
import com.dianping.cat.message.Transaction;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Ordering;
import com.mobanker.engine.common.EngineConst;
import com.mobanker.engine.design.busi.configma.EngineConfigManageService;
import com.mobanker.engine.design.busi.rulemodel.util.EngineRuleModelUtil;
import com.mobanker.engine.design.busi.user.EngineUserService;
import com.mobanker.engine.design.busi.workspace.util.EngineZipFileUtil;
import com.mobanker.engine.design.dao.EngBusiOperatorDao;
import com.mobanker.engine.design.dao.EngFatTestDao;
import com.mobanker.engine.design.dao.EngGroupDao;
import com.mobanker.engine.design.dao.EngProductDao;
import com.mobanker.engine.design.dao.EngProductReleaseFlowDao;
import com.mobanker.engine.design.dao.EngQueryDictionaryDao;
import com.mobanker.engine.design.dao.EngQueryFieldDao;
import com.mobanker.engine.design.dto.EngineCpntCompareDto;
import com.mobanker.engine.design.dto.EngineCpntCompareGroupDto;
import com.mobanker.engine.design.dto.EngineCpntListDisplay;
import com.mobanker.engine.design.dto.EngineDefineFieldDto;
import com.mobanker.engine.design.dto.EngineGroupDto;
import com.mobanker.engine.design.dto.EngineProductDto;
import com.mobanker.engine.design.dto.EngineScriptSyntaxErr;
import com.mobanker.engine.design.dto.EngineTestReturnDto;
import com.mobanker.engine.design.dto.EngineTestZkDto;
import com.mobanker.engine.design.hotdeploy.EngineRuleModelHotDeployService;
import com.mobanker.engine.design.hotdeploy.EngineTestRuleHotDeployService;
import com.mobanker.engine.design.hotdeploy.file.EngineFileContent;
import com.mobanker.engine.design.mongo.EngRuleModelDao;
import com.mobanker.engine.design.mongo.EngScriptFunctionDao;
import com.mobanker.engine.design.mongo.assist.RuleModelTable;
import com.mobanker.engine.design.pojo.EngBusiOperator;
import com.mobanker.engine.design.pojo.EngFatTest;
import com.mobanker.engine.design.pojo.EngGroup;
import com.mobanker.engine.design.pojo.EngProduct;
import com.mobanker.engine.design.pojo.EngProductReleaseFlow;
import com.mobanker.engine.design.pojo.EngQueryDictionary;
import com.mobanker.engine.design.pojo.EngQueryField;
import com.mobanker.engine.design.pojo.EngScriptFunction;
import com.mobanker.engine.framkwork.api.params.EngineParam;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineScriptAssist;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineSelfCheck;
import com.mobanker.engine.framkwork.cpnt.container.EngineCpntContainer;
import com.mobanker.engine.framkwork.cpnt.flow.EnginePolicyFlow;
import com.mobanker.engine.framkwork.cpnt.judge.EngineJudge;
import com.mobanker.engine.framkwork.cpnt.judge.impl.EngineScriptJudge;
import com.mobanker.engine.framkwork.cpnt.query.assist.EngineFieldDefine;
import com.mobanker.engine.framkwork.cpnt.query.assist.EngineQueryDefine;
import com.mobanker.engine.framkwork.cpnt.stage.EngineStage;
import com.mobanker.engine.framkwork.cpnt.stage.EngineStandardStage;
import com.mobanker.engine.framkwork.cpnt.step.EngineStep;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineScriptStep;
import com.mobanker.engine.framkwork.data.EngineDoNothingPersistence;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineCpntParseException;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.exception.EngineFieldExistException;
import com.mobanker.engine.framkwork.manager.EngineStepProcessor;
import com.mobanker.engine.framkwork.manager.EngineStepTaskProcessor;
import com.mobanker.engine.framkwork.parse.manager.EngineParseManager;
import com.mobanker.engine.framkwork.relation.EngineDependField;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;
import com.mobanker.engine.framkwork.relation.EngineRelaField;
import com.mobanker.engine.framkwork.script.executer.EngineScriptThreadLocal;
import com.mobanker.engine.framkwork.script.function.EngineScriptFunction;
import com.mobanker.engine.framkwork.script.syntax.EngineJavaScriptSyntaxCheck;
import com.mobanker.engine.framkwork.serialize.EngineKryoSerialize;
import com.mobanker.engine.framkwork.serialize.EnginePolicyFlowSerialize;
import com.mobanker.engine.framkwork.test.EngineExecMode;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.engine.inner.call.api.EngineSnapshotBatchRun;
import com.mobanker.engine.inner.call.dto.SnapshotBatchRunDto;
import com.mobanker.framework.contract.dto.ResponseEntityDto;
import com.mobanker.framework.tracking.EE;
import com.mobanker.framework.tracking.EETransaction;
import com.mobanker.framework.utils.HttpClientUtils;

/**
 * 工作区间管理器
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月4日
 * @version 1.0
 */
@Service
public class EngineWorkspaceManagerImpl implements EngineWorkspaceManager,BeanFactoryAware{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	private final static String BUSI_DESC = "工作区间管理器";
	private final static String BUSI_DESC_SHOWPRODUCT = "显示产品信息";
	private final static String CPNT_LOCK_KEY = "cpnt_lock";
	private final static String FIELDLIST_KEY = "fieldList";
	private final static String FUNCTIONS_KEY = "functions";
	
	private BeanFactory beanFactory;
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
	@Autowired
	private EngBusiOperatorDao engBusiOperatorDao;
	
	@Autowired
	private EngProductDao engProductDao;
	
	@Autowired
	private EngGroupDao engGroupDao;
	
	@Autowired
	private EngQueryDictionaryDao engQueryDictionaryDao;
	
	@Autowired
	private EngQueryFieldDao engQueryFieldDao;
	
	//@Autowired
	//private EngQueryHostDao engQueryHostDao;
	
	@Autowired
	private EngFatTestDao engFatTestDao;
	
	@Autowired
	private EngProductReleaseFlowDao engProductReleaseFlowDao;
	
	@Resource(name = "engineRuleModelHotDeployService")
	private EngineRuleModelHotDeployService engineRuleModelHotDeployService;
		
	//@Resource(name = "testRuleModelHotDeployService")
	//private EngineRuleModelHotDeployService testRuleModelHotDeployService;
	
	@Resource(name = "mysqlRuleModelDao")
	private EngRuleModelDao engRuleModelDao;
	
	@Resource(name = "hbaseScriptFunctionDao")
	private EngScriptFunctionDao engScriptFunctionDao;

	@Resource(name = "hbaseScriptFunctionDao")
	private EngScriptFunctionDao engCommonFunctionDao;
	
	@Autowired
	private EngineConfigManageService engineConfigManageService;
	
	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	
	@Autowired
	private RedisTemplate<String,Object> redisTemplate;
	
	@Autowired
	private EngineSnapshotBatchRun engineSnapshotBatchRun;
	
	@Resource(name = "engineTestRuleHotDeployService")
	private EngineTestRuleHotDeployService engineTestRuleHotDeployService;
	
	@Autowired
	private EngineUserService engineUserService;
	
	
	@Cacheable(value="rule_default",key="'showProductList'+#username")
	public List<EngineProductDto> showProductList(String username){
		
		logger.info(EngineUtil.logPrefix(BUSI_DESC_SHOWPRODUCT)+"用户名:{}",username);
		List<EngineProductDto> result = new LinkedList<EngineProductDto>();
		
		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
					
		String productIds = engBusiOperator.getProductIds();
		EngineAssert.checkNotBlank(productIds, "操作员所拥有的产品信息为空");
		//逗号分隔
		String[] productArr = StringUtils.split(productIds, ',');
		for(String productId : productArr){
			EngProduct engProduct = engProductDao.get(Long.valueOf(productId));
			if(engProduct == null) continue;			
			result.add(new EngineProductDto(engProduct.getId(),engProduct.getName(),engProduct.getProductType(),engProduct.getGroupId()));	
		}
		
		return result;
	}
	@Override
	public List<EngineProductDto> showProductListByGroup(String groupId){
		
		logger.info(EngineUtil.logPrefix(BUSI_DESC_SHOWPRODUCT)+"分组id:{}",groupId);
		List<EngineProductDto> result = new LinkedList<EngineProductDto>();
		
		EngGroup engGroup=engGroupDao.get(Long.valueOf(groupId));
		EngineAssert.checkNotNull(engGroup,"分组不存在,groupId:%s",groupId);
		EngProduct engProduct=new EngProduct();
		engProduct.setGroupId(groupId);
		List<EngProduct> productList=engProductDao.queryByField(engProduct);
		for(EngProduct product:productList){
			result.add(new EngineProductDto(product.getId(),product.getName(),product.getProductType(),product.getGroupId()));
		}
		
		return result;
	}
	
	/**
	 * 检查字段，和其中文名 是否符合规范
	 * @param productType
	 */
	private void checkProductType(String productType){
		String regex = "^(\\w){1,50}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(productType);
		if(!matcher.matches()){
			throw new EngineFieldExistException("productType必须是由字母，数字和下划线组成，并且长度小于50个字符:"+productType);
		}		
	}
	
	
	@Transactional(propagation=Propagation.REQUIRED)
	@CacheEvict(value="rule_default",key="'showProductList'+#username")
	@Override
	public void createProduct(String username,String productType,String productName,String groupId){
		EngineAssert.checkNotBlank(username);
		EngineAssert.checkNotBlank(productType);
		EngineAssert.checkNotBlank(productName);
		EngineAssert.checkNotBlank(groupId);
		
		EngineUtil.checkNameFormat(productName);
		
		//addby-zjj 因其他系统接入，增加校验，防止输入有误
		checkProductType(productType);
		
		EngBusiOperator engOperator=engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);				
  		EngineAssert.checkArgument(engProductDao.queryLastOneByProductType(productType)==null, "该产品名已经存在");
  		
  		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
  		
  		//记录产品信息
  		EngProduct engProduct = new EngProduct();
  		engProduct.setName(productName);
  		engProduct.setCreatorUser(username);  		
  		engProduct.setProductType(productType);
  		engProduct.setGroupId(groupId);
  		
  		EngineAssert.checkArgument(engProductDao.insert(engProduct)==1, "插入产品数据失败");
  		
  		engProduct=engProductDao.queryLastOneByProductType(productType);
  		
		//新增成功后,同步加入当前操作员权限
  		String pid = engProduct.getId().toString();
//  		String productIds=addProductId(engOperator.getProductIds(), pid);
//		
//		engOperator.setProductIds(productIds);
//		engineUserService.updateUserInfo(engOperator);
		
		EngBusiOperator queryOperator=new EngBusiOperator();
		List<EngBusiOperator> operatorList=engBusiOperatorDao.queryByField(queryOperator);
		//并对 项目总监 和 当前组发布人员 加入权限 
		for(EngBusiOperator operator:operatorList){
			if(StringUtils.isNotBlank(operator.getRole())){
				operator.setProductIds(addProductId(operator.getProductIds(), pid));
				for(String roleId:operator.getRole().split(",")){
					if(StringUtils.endsWith(operator.getGroupId(), groupId)){
						if(StringUtils.endsWith(roleId, EngineConst.ROLE_3)){
							//防止数量增长，超出范围
							if(operator.getProductIds().length()<495){
								engineUserService.updateUserInfo(operator);
							}
							continue;
						}
					}
					if(StringUtils.endsWith(roleId, EngineConst.ROLE_7)){
						//防止数量增长，超出范围
						if(operator.getProductIds().length()<495){
							engineUserService.updateUserInfo(operator);
						}
					}
				}
			}
		}
		
	}
	
	private String addProductId(String productIds,String pid){
		if(!StringUtils.isBlank(productIds)){
			productIds+=",";
		}
		productIds+=pid;
		return productIds;
	}

	public EngineGroupDto showGroup(String username){
		
		logger.info(EngineUtil.logPrefix(BUSI_DESC_SHOWPRODUCT)+"用户名:{}",username);
		EngineGroupDto result = null;
		
		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
					
		String groupId = engBusiOperator.getGroupId();
		EngineAssert.checkNotBlank(groupId, "操作员所拥有的分组为空");
		
		EngGroup engGroup=engGroupDao.get(Long.valueOf(groupId));
		
		if(engGroup!=null)result=new EngineGroupDto(engGroup.getId(),engGroup.getGroupName(),engGroup.getGroupType());

		return result;
	}
	
		

	public void createGroup(String username,String groupType,String groupName){
		EngineAssert.checkNotBlank(username);
		EngineAssert.checkNotBlank(groupType);
		EngineAssert.checkNotBlank(groupName);
		
		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);				
  		EngineAssert.checkArgument(engGroupDao.queryLastOneByGroupType(groupType)==null, "该分组名已经存在");
  		
  		//记录分组信息
  		EngGroup engGroup = new EngGroup();
  		engGroup.setGroupName(groupName);
  		engGroup.setCreatorUser(username);  		
  		engGroup.setGroupType(groupType);
		
  		EngineAssert.checkArgument(engGroupDao.insert(engGroup)==1, "插入分组数据失败");
	}
	
	
//	public List<JSONObject> downloadRuleModelFile(String username,String productType){
//		//首先检查该产品是否存在
//		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
//		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
//		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
//		
//		String role = engBusiOperator.getRole();
//		EngineAssert.checkNotBlank(role,"用户角色属性不存在,role:%s",role);
//		if(StringUtils.equals(role, "0")){//如果是普通操作员
//			return engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, username, null);
//		}else{
//			//从merge区域拿到该产品的所有组件进行下发
//			logger.warn(EngineUtil.logPrefix(BUSI_DESC)+"用户未管理员，下发所有规则模型");
//			return engRuleModelDao.getRuleModel(RuleModelTable.MERGE, productType, null, null);
//		}									 											
//	}
	

//	public void saveRuleModel(String username,String productType,JSONObject json){
//		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
//		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);
//		
//		EngineRuleModelUtil.checkCpntBaseInfo(json);
//		
//		String cpntId = json.getString("id");			
//		
//		EngineAssert.checkNotBlank(cpntId,"上送的组件id为空,json:%s",json.toJSONString());
//		engRuleModelDao.saveRuleModel(productType, cpntId, json);
//	}
	
	@Override	
	public String insertRuleModel(String username, String productType,
			JSONObject json) {			
		EngineAssert.checkNotNull(json.get("name"));
		EngineUtil.checkNameFormat(json.get("name").toString());		
		checkCpntOperatorLock(username,productType);
		
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);
		
		//生成默认id
		//String cpntId = json.getString("type")+"_"+username+"_"+EngineUtil.get14CurrentDateTime();	
		String cpntId = EngineUtil.getUUID();
				
		json.put("id", cpntId);
		EngineRuleModelUtil.checkCpntBaseInfo(json);
		
		//String cpntId = json.getString("id");			
		JSONObject obj = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, cpntId);
		EngineAssert.checkArgument(obj==null, "该组件id已经被占用:%s,占用者:%s,使用的是UUID,应该是不可能",cpntId,obj);
		
		EngineAssert.checkNotBlank(cpntId,"上送的组件id为空,json:%s",json.toJSONString());
		engRuleModelDao.saveRuleModel(productType, cpntId, json);
		
//		EngineUtil.waitSecond();
		return cpntId;
	}

	/**
	 * 用户锁定该工程模块
	 * @param username
	 * @param productType
	 */
	private void checkCpntOperatorLock(String username,String productType){
		EngineAssert.checkNotBlank(productType);
		String redisKey = CPNT_LOCK_KEY + "_" + productType;			
		Object verifyObj = redisTemplate.opsForValue().get(redisKey);//从redis当中获取权限校验结果
		if(verifyObj != null){
			if(!(verifyObj instanceof String)){
				throw new EngineException("操作员锁定值类型异常["+verifyObj.getClass()+"]");
			}
			String verifyResult = (String) verifyObj;		
			if(StringUtils.isNotBlank(verifyResult)&&!StringUtils.equals(verifyResult, username)){
				throw new EngineException("该工程模块已经被另一个操作员["+verifyResult+"]锁定");
			}		
		}
		redisTemplate.opsForValue().set(redisKey, username);
		redisTemplate.expire(redisKey,1,TimeUnit.HOURS);		
	}
	

	@Override
	public void userUnlockProduct(String username) {
		List<EngineProductDto> myProductList = this.showProductList(username);
		for(EngineProductDto myProduct : myProductList){
			String productType = myProduct.getProductType();
			
			EngineAssert.checkNotBlank(productType);
			String redisKey = CPNT_LOCK_KEY + "_" + productType;				
			Object verifyObj = redisTemplate.opsForValue().get(redisKey);//从redis当中获取权限校验结果
			if(verifyObj != null){
				if(!(verifyObj instanceof String)){
					throw new EngineException("操作员锁定值类型异常["+verifyObj.getClass()+"]");
				}
				String verifyResult = (String) verifyObj;
				if(StringUtils.isNotBlank(verifyResult)&&StringUtils.equals(verifyResult, username)){
					redisTemplate.delete(redisKey);
				}					
			}						
		}		
	}
	
	
	@Override	
	public void updateRuleModel(String username, String productType,
			JSONObject json) {		
		EngineAssert.checkNotNull(json.get("name"));
		EngineUtil.checkNameFormat(json.get("name").toString());
		checkCpntOperatorLock(username,productType);
		
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
		
		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
	
		EngineRuleModelUtil.checkCpntBaseInfo(json);
		
		String cpntId = json.getString("id");			
		JSONObject obj = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, cpntId);
		EngineAssert.checkNotNull(obj, "组件id没有找到!");
		
		//管理员可以修改别人的组件，但不能删除
		//if(StringUtils.equals(engBusiOperator.getRole(), "0")){
		//判断添加者是自己
//		String right = obj.getString("right");
//		EngineAssert.checkArgument(StringUtils.equals(username, right), "你无法修改该组件，添加人为%s",right);
		//}
				
		EngineAssert.checkNotBlank(cpntId,"上送的组件id为空,json:{}",json.toJSONString());
		engRuleModelDao.saveRuleModel(productType, cpntId, json);
		
//		EngineUtil.waitSecond();	
	}		

	
	@Override
	public String setRuleModelFlow(String username, String productType,
			JSONObject json) {
		checkCpntOperatorLock(username,productType);
		
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
		
		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
		
		json.put("name",productType+"规则流");
		String cpntId = json.getString("id");	
		//查看是新增还是修改
		if(StringUtils.isBlank(cpntId)){//如果是新增
			//查看组件库里是否已经有整个决策流，如果有流则报错
			//List<JSONObject> mergeList = engRuleModelDao.getRuleModel(RuleModelTable.MERGE, productType, null, null);
			//List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, username, null);			
			//EngineRuleModelUtil.mergeCpnts(mergeList, selfList, username);
			List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, null, null);
			for(JSONObject one : selfList){
				String type = one.getString("type");
				if(StringUtils.equals(type, EnginePolicyFlow.class.getSimpleName())){
					throw new EngineException("该产品线已经存在决策流，无法新增,创建者:"+one.getString("right"));
				}
			}			
			cpntId=this.insertRuleModel(username, productType, json);
		}else{//如果是修改
			this.updateRuleModel(username, productType, json);			
		}
		return cpntId;
	}	
	
	
	@Override		
	public void removeRuleModel(String username,String productType,String cpntId) {
		checkCpntOperatorLock(username,productType);
		
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
		
		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
		
		JSONObject obj = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, cpntId);
		EngineAssert.checkNotNull(obj, "组件id没有找到!");
		
		//判断添加者是自己
		//String right = obj.getString("right");
		//EngineAssert.checkArgument(StringUtils.equals(username, right), "你无法删除该组件，添加人为%s",right);
		
		//检查依赖组件，如果有依赖不得删除
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"正准备检查依赖组件");		
				
//		List<JSONObject> mergeList = engRuleModelDao.getRuleModel(RuleModelTable.MERGE, productType, null, null);
//		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, username, null);
//		
//		EngineRuleModelUtil.mergeCpnts(mergeList, selfList, username);
		
		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, null, null);
		//排除自己
		//EngineRuleModelUtil.removeCpnt(selfList, cpntId);
		
		//EngineCpntContainer flow = json2Flow(productType,mergeList);
		Map<String,EngineCpnt> cpntMap = null;
		try{
			cpntMap = json2Cpnts(selfList);			
			//container = this.transfer2Container(json2Cpnts(selfList), productType);
		}catch(EngineException e){
			throw new EngineException("在删除组件的过程中，由于存在依赖性，系统会校验该工程，所以请在想删除组件的时候，保证其他组件的正确性。本次删除失败，原因："+e.getMessage(), e);
		}
		
		StringBuilder errorMsg = new StringBuilder();
		
		//查看下有么有组件依赖这个即将被删除的组件
		for(String oneId : cpntMap.keySet()){
			if(StringUtils.equals(oneId, cpntId)) continue;
			EngineCpnt cpnt = cpntMap.get(oneId);
			if(!(cpnt instanceof EngineRelaCpnt)) continue;
			EngineRelaCpnt rela = (EngineRelaCpnt) cpnt;
			
			Map<String,Boolean> relaCpntIds = rela.relaCpntIds();
			
			Boolean isDirct = relaCpntIds.get(cpntId);			
			if(BooleanUtils.isTrue(isDirct)){				
				errorMsg.append("组件["+cpnt.name()+"]依赖了这个组件\n");
			}							
		}
				
		if(errorMsg.length() > 0)
			throw new EngineException("无法删除该组件，原因:\n"+errorMsg.toString());
		
		//都验证没有问题则删除		
		engRuleModelDao.deleteRuleModel(productType, cpntId);
		
//		EngineUtil.waitSecond();		
	}	
	@Override
	public JSONObject showOneRuleModel(String username,String productType,String cpntId){
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
		
		EngineAssert.checkNotBlank(cpntId,"上送参数缺失，cpntId不存在");		
		JSONObject result = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, cpntId);
		EngineAssert.checkNotNull(result,"规则模型对象不存在,id:%s",cpntId);
		
		return result;
	}
	@Override
	public JSONObject showMergeRuleModel(String username,String productType,String cpntId){
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
		
		EngineAssert.checkNotBlank(cpntId,"上送参数缺失，cpntId不存在");		
		JSONObject result = engRuleModelDao.getRuleModel(RuleModelTable.MERGE, productType, cpntId);
		EngineAssert.checkNotNull(result,"规则模型对象不存在,id:%s",cpntId);
		
		return result;
	}	
	
	@Override
	public JSONObject showHistoryRuleModel(String username, String productType,
			String cpntId, String version) {
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
		
		EngineAssert.checkNotBlank(cpntId,"上送参数缺失，cpntId不存在");		
		EngineAssert.checkNotBlank(version,"上送参数缺失，version不存在");
		
		JSONObject result = engRuleModelDao.getHistoryRuleModel(productType, cpntId,version);
		EngineAssert.checkNotNull(result,"规则模型对象不存在,id:%s",cpntId,version);
		
		return result;
	}
	
	@Override
	public JSONObject showHistoryPolicyFlowModel(String username,Long releaseFlowId) {
		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);		
		EngProductReleaseFlow flow = engProductReleaseFlowDao.get(releaseFlowId);
		EngineAssert.checkNotNull(flow,"规则模型对象发布记录不存在!It is impossible,flowId:%s",releaseFlowId);
						
		EngineCpntContainer container = generateContainerByFlow(flow);	
		
		return this.showHistoryRuleModel(username, flow.getProductType(), container.getRoot().getId(), container.getRuleVersion());
	}
	
			
	public void mergeRuleModel(String username,String productType){		
		checkCpntOperatorLock(username,productType);
		
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
		
		String role = engBusiOperator.getRole();
		EngineAssert.checkNotBlank(role,"用户角色属性不存在,role:{}",role);
		
		//规则模型检查
		//从merge区拉一片新的规则和自己的规则合并后进行
//		List<JSONObject> mergeList = engRuleModelDao.getRuleModel(RuleModelTable.MERGE, productType, null, null);
//		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, username, null);
//		
//		EngineRuleModelUtil.mergeCpnts(mergeList, selfList, username);
//		//EngineCpntContainer flow = json2Flow(productType,mergeList);
//		EngineCpntContainer flow = this.transfer2Container(json2Cpnts(mergeList), productType);
		
		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, null, null);
		EngineCpntContainer flow = this.generateContainer(json2Cpnts(selfList), productType);
		//组件自检查
		List<EngineSelfCheck> checkList = flow.getCpntsByType(EngineSelfCheck.class);
		for(EngineSelfCheck selfCheck : checkList){
			selfCheck.checkSelf();
		}
		
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"解析ok,可以进行合并");
		
		engRuleModelDao.mergeRuleModel(productType);													
	}
	
	@Override
	public EngineCpntContainer getProductContainer(RuleModelTable modelTable,
			String productType) {
		List<JSONObject> jsonList = engRuleModelDao.getRuleModel(modelTable, productType, null, null);
		EngineCpntContainer flow = this.generateContainer(json2Cpnts(jsonList), productType);
		return flow;
	}
	
	
	private EngineCpntContainer generateContainer(Map<String,EngineCpnt> cpnts,String productType,String version){
		List<EngineDefineFieldDto> fieldList = getReleasedFieldList(productType,version);
		List<EngineScriptFunction> functions = getReleasedFunctions(productType,version);
		return this.transfer2Container(productType,cpnts,fieldList,functions);
	}
	
	private EngineCpntContainer generateContainer(Map<String,EngineCpnt> cpnts,String productType){
		List<EngineDefineFieldDto> fieldList = engineConfigManageService.getEngineParamList(productType);
		List<EngineScriptFunction> functions = getScriptFunctions(productType);
		return this.transfer2Container(productType,cpnts,fieldList,functions);
	}
	
	private EngineCpntContainer transfer2Container(String productType,Map<String,EngineCpnt> cpnts,List<EngineDefineFieldDto> fieldList,List<EngineScriptFunction> functions){
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"开始组件完善工作,组件数量:{},产品线:{}",cpnts.size(),productType);
		
		EngineCpntContainer cpntContainer = new EngineCpntContainer();
		cpntContainer.setAllCpnt(cpnts);				
		
		//获取数据源信息转化为组件
//		List<EngineQueryDefine> queryList = this.getQueryItems(productType);
//		EngQueryHost engQueryHost = engQueryHostDao.queryLastOneByProductType(productType);
//			
//		
//		//如果是数据源组件组装url这些东西
//		List<EngineHttpQueryStep> queryStepList = cpntContainer.getCpntsByType(EngineHttpQueryStep.class);
//		for(EngineHttpQueryStep queryStep : queryStepList){
//			EngineAssert.checkNotNull(engQueryHost, "没有配置eng_query_host,组件名称:%s",queryStep.name());
//			queryStep.setUrl(engQueryHost.getUrl());
//			queryStep.setFatalDeal(engQueryHost.getFatalDeal());
//			queryStep.setTimeout(engQueryHost.getTimeout());
//			queryStep.setUrlType(engQueryHost.getUrlType());												
//			
//			Set<String> urlParamKeys = new HashSet<String>();				
//			String paramKeyStr = engQueryHost.getUrlParamKeys();
//			if(StringUtils.isNoneBlank(paramKeyStr)){
//				String[] paramKeyArr = StringUtils.split(paramKeyStr,",");
//				urlParamKeys.addAll(Arrays.asList(paramKeyArr));
//			}										
//			queryStep.setUrlParamKeys(urlParamKeys);
//			
//			Map<String,EngineQueryDefine> queryMap = queryStep.getQueryList();
//			for(String key : queryMap.keySet()){
//				for(EngineQueryDefine queryDefine : queryList){
//					if(StringUtils.equals(queryDefine.getQueryId(), key)){
//						logger.info(EngineUtil.logPrefix(BUSI_DESC)+"补填了查询项,queryId:{}",queryDefine.getQueryId());
//						queryMap.put(key, queryDefine);
//					}					
//				}
//			}			
//			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"成功配置数据源路径,组件名称:{},路径:{},参数:{}",queryStep.name(),queryStep.getUrl(),queryStep.getUrlParamKeys());
//		}

		
		//设置根节点
		List<EnginePolicyFlow> rootCheck = cpntContainer.getCpntsByType(EnginePolicyFlow.class);
		if(!CollectionUtils.isEmpty(rootCheck)){
			EngineAssert.checkArgument(rootCheck.size() <= 1, "该产品线存在多个root节点,数量:%s",rootCheck.size());			
			EnginePolicyFlow root = rootCheck.get(0);
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"设置根节点对象:{}",root);
			cpntContainer.setRoot(root);
		}
		
		//获取申明字段
		//List<EngineDefineFieldDto> fieldList = engineConfigManageService.getEngineParamList(productType);
		Map<String,String> replaceMap = new HashMap<String,String>();
		for(EngineDefineFieldDto field : fieldList){
			replaceMap.put(field.getFieldKey(), field.getFieldName());
		}
		
		//获取函数列表
		//List<EngineScriptFunction> functions = getScriptFunctions(productType);				
		
		//获取脚本组件列表
		List<EngineScriptAssist> scriptList = cpntContainer.getCpntsByType(EngineScriptAssist.class);
		for(EngineScriptAssist one : scriptList){			
			//绑定函数
			one.bindFunctions(functions);
			
			//替换脚本中的中文对应
			one.replaceVar(replaceMap);
			
			//关联需要的字段
			this.scriptRelaField(one,fieldList);
		}
		
		return cpntContainer;
	
	}

	private Map<String,EngineCpnt> json2Cpnts(List<JSONObject> jsonList){
		EngineParseManager engineParseManager = new EngineParseManager();		
		return engineParseManager.parse(jsonList);
	}
	
	private Map<String,EngineCpnt> json2SomeCpnt(List<JSONObject> jsonList,String... cpntIds){
		EngineParseManager engineParseManager = new EngineParseManager();
		return engineParseManager.parse(jsonList, cpntIds);
	}
	
	
	/**
	 * 脚本关联入参和出参,必须放在替换中文字段之后
	 * @param scriptCpnt
	 * @param fieldList
	 */
	private void scriptRelaField(EngineScriptAssist scriptCpnt,List<EngineDefineFieldDto> fieldList){
		List<EngineDefineFieldDto> reverseList = Ordering.from(new Comparator<EngineDefineFieldDto>() {
			@Override
			public int compare(EngineDefineFieldDto o1,
					EngineDefineFieldDto o2) {										
				return Integer.compare(o1.getFieldKey().length(), o2.getFieldKey().length());
			}
		}).reverse().sortedCopy(fieldList);
				
		String script = new String(scriptCpnt.getScript());
		
		script = StringUtils.replace(script, "==", "strEqual");
		script = script.replaceAll("\\s", "");
				
		for(EngineDefineFieldDto defineField : reverseList){
			EngineAssert.checkNotBlank(defineField.getFieldUse());
			EngineAssert.checkNotBlank(defineField.getFieldKey());
			String fieldStr = defineField.getFieldUse()+"."+defineField.getFieldKey();	
			//排除output.xxx = 这种 
			//暂时不排除
			//script = StringUtils.replace(script, fieldStr+"=", "");			
			if(script.indexOf(fieldStr) >= 0){//当发现脚本中存在input.xxx则关联
				scriptCpnt.relaField(defineField.getFieldUse(),defineField.getFieldKey());
				logger.info("脚本成功关联了字段,组件名称:{},字段:{}",scriptCpnt.name(),fieldStr);
				script = StringUtils.replace(script, fieldStr, "");//为了在所有替换后查看是否还有遗漏的input或者output未经过定义
			}					
		}
		//查看是否有遗漏的input或者output未经过定义		
		if((script.indexOf("input.") >= 0)||(script.indexOf("output.") >= 0)){
			throw new EngineCpntParseException("组件名称["+scriptCpnt.name()+"]组件类型["+scriptCpnt.type()+"]发现有未定义的入参或者出参,script:"+script);
		}				
	}
	
	private List<EngineScriptFunction> getScriptFunctions(String productType){
		List<EngineScriptFunction> result = new LinkedList<EngineScriptFunction>();
		//获取用户自定义函数
		List<EngScriptFunction> srcList = engScriptFunctionDao.findByProduct(productType);		
		//获取系统函数
		srcList.addAll(engCommonFunctionDao.findByProduct("system"));		
		for(EngScriptFunction src : srcList){			
			EngineScriptFunction engineScriptFunction = new EngineScriptFunction();
			engineScriptFunction.setName(src.getLabel());
			engineScriptFunction.setFunction(src.getFunction());
			result.add(engineScriptFunction);
		}
		return result;
	}
		
	
	private void release2Zk(EngineRuleModelHotDeployService engineRuleModelHotDeployService,EngineCpntContainer container,String productType) {			
		logger.info("规则模型对象解析成功,准备发布到zk,version:{},root:{},",container.getRuleVersion(),container.getRoot());			
		
		//将flow序列化并且发布到运行模块
		EnginePolicyFlowSerialize serializer = new EngineKryoSerialize();		
		byte[] bytes = serializer.serialize(container);
		//发布到zk上
		logger.info("规则模型对象序列化成功");
		engineRuleModelHotDeployService.deployRuleModel(new EngineFileContent(productType, bytes));				
	}	
	

	@SuppressWarnings("unused")
	@Deprecated
	private List<EngineQueryDefine> getQueryItems(String productType){
		List<EngineQueryDefine> result = new LinkedList<EngineQueryDefine>();
		
		List<EngQueryDictionary> engQueryDictionarylist = engQueryDictionaryDao.queryByProductType(productType);
		for(EngQueryDictionary dict : engQueryDictionarylist){
			//List<EngQueryField> engQueryFieldList = engQueryFieldDao.queryByDictId(dict.getId());
			List<EngQueryField> engQueryFieldList = engQueryFieldDao.queryByQueryIdAndProductType(dict.getQueryId(), productType);
			EngineAssert.checkArgument(!CollectionUtils.isEmpty(engQueryFieldList), "查询字典无对应的查询字段,eng_query_dictionary.id:%s",dict.getId());			
			
			List<EngineFieldDefine> fieldDefineList = new LinkedList<EngineFieldDefine>();
			
			for(EngQueryField engQueryField : engQueryFieldList){
				EngineAssert.checkNotBlank(engQueryField.getFieldKey(),EngineUtil.logPrefix(BUSI_DESC)+"配置查询组件中的fieldKey值为空,eng_query_field.id:%s",engQueryField.getId());
				EngineAssert.checkNotBlank(engQueryField.getFieldType(),EngineUtil.logPrefix(BUSI_DESC)+"配置查询组件中的fieldType值为空,eng_query_field.id:%s",engQueryField.getId());				
					
				EngineFieldDefine engineFieldDefine = new EngineFieldDefine();
				engineFieldDefine.setFieldType(engQueryField.getFieldType());					
				engineFieldDefine.setFieldKey(engQueryField.getFieldKey());
				engineFieldDefine.setDefaultValue(StringUtils.isBlank(engQueryField.getFieldDefaultValue())?"":engQueryField.getFieldDefaultValue());
				engineFieldDefine.setRefValue(engQueryField.getFieldRefValue());					
				fieldDefineList.add(engineFieldDefine);												
			}
			EngineQueryDefine engineQueryDefine = new EngineQueryDefine();
			engineQueryDefine.setQueryId(dict.getQueryId());
			engineQueryDefine.setFieldDefineList(fieldDefineList);
			result.add(engineQueryDefine);
		}		
		
		return result;
	}	
	

	@Override
	public List<EngProductReleaseFlow> showRealeaseList(String username,String productType,String status,final String matchVersion,String applyUser,Boolean isPrd) {
		//List<EngProductReleaseFlow> flowList = engProductReleaseFlowDao.queryByProductType(productType);	
		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
				
		List<EngineProductDto> myProductList = beanFactory.getBean(EngineWorkspaceManager.class).showProductList(username);
		if(CollectionUtils.isEmpty(myProductList)) return Collections.emptyList();
		final Collection<String> myProductStrList = Collections2.transform(myProductList,new Function<EngineProductDto,String>(){
			@Override
			public String apply(EngineProductDto input) {
				return input.getProductType();
			}
		});
		
		
		EngProductReleaseFlow queryBean = new EngProductReleaseFlow();
		queryBean.setProductType(productType);
		queryBean.setStatus(status);
		queryBean.setApplyUser(applyUser);
		List<EngProductReleaseFlow> queryList =  engProductReleaseFlowDao.queryByField(queryBean);
		
		
		Collection<EngProductReleaseFlow> filterList = Collections2.filter(queryList, new Predicate<EngProductReleaseFlow>(){
			@Override
			public boolean apply(EngProductReleaseFlow input) {
				if(StringUtils.isNoneBlank(matchVersion)){
					String version = input.getVersion();
					if(!version.contains(matchVersion)) return false;					
				}									
				if(!myProductStrList.contains(input.getProductType())) return false;				
				return true;
			}
		});
		
		//按时间排序
		List<EngProductReleaseFlow> reverseList = Ordering.from(new Comparator<EngProductReleaseFlow>() {
			@Override
			public int compare(EngProductReleaseFlow o1,
					EngProductReleaseFlow o2) {										
				return Long.compare(o1.getAddtime().getTime(), o2.getAddtime().getTime());
			}
		}).reverse().sortedCopy(filterList);
		
		Map<String,EngProductReleaseFlow> flowMap=new HashMap<String,EngProductReleaseFlow>();
		
		for(EngProductReleaseFlow flow : reverseList){
			//已经按时间最新排序 该产线最新为生产版本
			if(StringUtils.endsWith(flow.getStatus(), "f")&&flowMap.get(flow.getProductType())==null){
				flowMap.put(flow.getProductType(), flow);
			}
			
			flow.setStatus(EngProductReleaseFlow.getStatusText(flow.getStatus()));
			
			String reProductType = flow.getProductType();
			if(StringUtils.isNotBlank(reProductType)){
				EngProduct engProduct = engProductDao.queryLastOneByProductType(reProductType);
				flow.setProductTypeId(reProductType);
				flow.setProductType(engProduct==null?"":engProduct.getName());				
			}
			
			
			String applyUserTemp = flow.getApplyUser();
			if(StringUtils.isNotBlank(applyUserTemp)){
				EngBusiOperator userOper = engBusiOperatorDao.queryLastOneByUsername(applyUserTemp);
				flow.setApplyUser(userOper==null?"":userOper.getName());
			}
				
			String auditUser1 = flow.getAuditUser1();
			if(StringUtils.isNotBlank(auditUser1)){
				EngBusiOperator userOper = engBusiOperatorDao.queryLastOneByUsername(auditUser1);
				flow.setAuditUser1(userOper==null?"":userOper.getName());
			}
			
			String auditUser2 = flow.getAuditUser2();
			if(StringUtils.isNotBlank(auditUser1)){
				EngBusiOperator userOper = engBusiOperatorDao.queryLastOneByUsername(auditUser2);
				flow.setAuditUser2(userOper==null?"":userOper.getName());
			}
			
			String finalAuditUser = flow.getFinalAuditUser();
			if(StringUtils.isNotBlank(finalAuditUser)){
				EngBusiOperator userOper = engBusiOperatorDao.queryLastOneByUsername(finalAuditUser);
				flow.setFinalAuditUser(userOper==null?"":userOper.getName());
			}
			
		}
		
		//保留50个
		int retainSize = 50;
		reverseList = reverseList.subList(0, reverseList.size()<retainSize?reverseList.size():retainSize);
		
		List<EngProductReleaseFlow> prdList = null;
		
		//返回当前生产版本
		if(isPrd.booleanValue()){
			prdList=new LinkedList<EngProductReleaseFlow>();
			for(String key:flowMap.keySet()){
				prdList.add(flowMap.get(key));
			}
			return prdList;
		}
		
		return reverseList;
	}

	public List<EngProductReleaseFlow> showPrdRealeaseList(String username) {
		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
				
		List<EngProductReleaseFlow> flowList = new LinkedList<EngProductReleaseFlow>();
		
		List<EngineProductDto> myProductList = this.showProductList(username);
		for(EngineProductDto dto:myProductList){
			EngProductReleaseFlow flow=this.getCurrentProdReleaseFlow(dto.getProductType());
			if(flow!=null){
				flowList.add(flow);
			}
		}
		
		//按时间排序
		List<EngProductReleaseFlow> reverseList = Ordering.from(new Comparator<EngProductReleaseFlow>() {
			@Override
			public int compare(EngProductReleaseFlow o1,
					EngProductReleaseFlow o2) {										
				return Long.compare(o1.getAddtime().getTime(), o2.getAddtime().getTime());
			}
		}).reverse().sortedCopy(flowList);
		
		return reverseList;
	}
	
	@Override	
	@EETransaction(type = "URL", name = "申请发布")
	public Long applyRelease(String username, String productType,String versionName) {
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);
				
		EngineAssert.checkNotBlank(versionName,"versionName不可为空");
		EngineUtil.checkNameFormat(versionName);
		
		//检查文件是否存在
		EngProductReleaseFlow flow = engProductReleaseFlowDao.queryLastOneByProductType(productType);
		if(flow != null){//如果已经存在产品线记录判断状态
			String status = flow.getStatus();
			EngineAssert.checkNotBlank(status,"该产品线发布记录中，状态值异常:%s",status);
			EngineAssert.checkArgument(status.equals("f")||status.equals("c"), "该产品线已存在未发布的记录，记录状态:%s",status);	
		}		
				
		
		//将规则对象转移到发布区 并设置版本号
		String version = engRuleModelDao.applyReleaseRuleModel(productType);
		
		//将函数和字段发布发布区以便于回滚
		List<EngineDefineFieldDto> fieldList = engineConfigManageService.getEngineParamList(productType);
		List<EngineScriptFunction> functions = getScriptFunctions(productType);
		redisTemplate.opsForValue().set(getReleaseAdditionalDataKey(FIELDLIST_KEY,productType,version), fieldList);
		redisTemplate.opsForValue().set(getReleaseAdditionalDataKey(FUNCTIONS_KEY,productType,version), functions);
		
		//将规则模型发布到fat环境  不需要了
		//List<JSONObject> list = engRuleModelDao.getRuleModel(RuleModelTable.MERGE, productType, null, null);				
		//release2Test(productType,list);		
		
		flow = new EngProductReleaseFlow();		
		flow.init();
		flow.setProductType(productType);
		flow.setApplyTime(EngineUtil.get14CurrentDateTime());
		flow.setApplyUser(username);
		flow.setVersion(version);
		flow.setVersionName(versionName);
		flow.setStatus("0");				
		
		engProductReleaseFlowDao.insert(flow);	
		
		return flow.getId();
	}

	@Override
	@Deprecated
	public void fatTest(final String productType, String username) {
		final List<EngFatTest> fatTestList = engFatTestDao.queryByProductType(productType);
		if(CollectionUtils.isEmpty(fatTestList)){
			throw new EngineException("该工程模块没有配置FAT环境测试地址");
		}
		
		if(!CollectionUtils.isEmpty(fatTestList)){
			threadPoolTaskExecutor.submit(new Runnable() {					
				@Override
				public void run() {
					Transaction trans = EE.newTransaction("FatTest", productType);											
					try {//首先等待10秒，否则fat环境有可能还没加载完规则
						trans.setStatus(Transaction.SUCCESS);
						EE.logEvent("FatTest", productType);
						logger.info("需要触发fat环境测试，等待fat环境加载完规则");		
						Thread.sleep(10000);
						logger.info("开始触发fat环境测试");						
						for(EngFatTest fatTest : fatTestList){
							String url = fatTest.getUrl();
							if(StringUtils.isBlank(url)) continue;
							logger.info("fat入口:{}",url);
							Map<String,Object> config = new HashMap<String,Object>();
							config.put(HttpClientUtils.CONNECT_TIMEOUT,3000);
				 			config.put(HttpClientUtils.SOCKET_TIMEOUT,3000);
				 			HttpClientUtils.setConfig(config); 		
							HttpClientUtils.doGet(url,null);
							logger.info("触发fat环境成功");
						}						
					} catch (InterruptedException | IOException e) {
						trans.setStatus(e);
						EE.logError("触发fat测试异常", e);
						logger.info("触发fat环境测试异常",e);
					} finally {
						trans.complete();
					}				
					
				}
			});			
		}else
			logger.info("没有配置该产品线的fat触发入口，无法触发fat环境测试");	
	}
	
	@Override	
	@Deprecated
	public void auditRelease1(String username, Long productReleaseFlowId) {		
		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);		
		EngProductReleaseFlow flow = engProductReleaseFlowDao.get(productReleaseFlowId);			
		EngineAssert.checkNotNull(flow,"申请记录不存在,productReleaseFlowId:%s",productReleaseFlowId);		
		EngineAssert.checkArgument(StringUtils.equals(flow.getStatus(), "0"),"该申请记录状态异常，已经被初审,status:%s",flow.getStatus());
		flow.setStatus("1");
		flow.setAuditUser1(username);
		flow.setAuditTime1(EngineUtil.get14CurrentDateTime());
		engProductReleaseFlowDao.update(flow);		
	}


	@Override	
	@Deprecated
	public void auditRelease2(String username, Long productReleaseFlowId) {
		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);		
		EngProductReleaseFlow flow = engProductReleaseFlowDao.get(productReleaseFlowId);			
		EngineAssert.checkNotNull(flow,"申请记录不存在,productReleaseFlowId:%s",productReleaseFlowId);
		//EngineAssert.checkArgument(StringUtils.equals(flow.getStatus(), "1"),"该申请记录状态异常，已经被初审,status:%s",flow.getStatus());
		EngineAssert.checkArgument(StringUtils.equals(flow.getStatus(), "1"),"该申请记录状态异常,status:%s",flow.getStatus());
		flow.setStatus("2");
		flow.setAuditUser2(username);
		flow.setAuditTime2(EngineUtil.get14CurrentDateTime());
		engProductReleaseFlowDao.update(flow);		
	}	
	
	@Override
	@EETransaction(type = "URL", name = "发布提交")
	public void release(String username,Long productReleaseFlowId) {
		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);		
		EngProductReleaseFlow flow = engProductReleaseFlowDao.get(productReleaseFlowId);
		EngineAssert.checkNotNull(flow,"规则模型对象发布记录不存在!It is impossible,flowId:%s",productReleaseFlowId);
						
		EngineAssert.checkArgument(StringUtils.equals(flow.getStatus(), "0"),"该申请记录状态异常,status:%s",flow.getStatus());
		
		EngineCpntContainer container = generateContainerByFlow(flow);		
		release2Zk(engineRuleModelHotDeployService,container,flow.getProductType());
		
		flow.setStatus("f");
		flow.setFinalAuditUser(username);
		flow.setFinalAuditTime(EngineUtil.get14CurrentDateTime());	
		flow.setReleaseTime(EngineUtil.get14CurrentDateTime());
		engProductReleaseFlowDao.update(flow);	
	
		//同时将规则同步至所有测试环境,单独线程
		this.threadDepolyTestZk(container, flow.getProductType());	
	}
	
	private void threadDepolyTestZk(EngineCpntContainer container,String productType){
		List<EngineTestZkDto> list=engineConfigManageService.showAllTestZk("1");
		if(!CollectionUtils.isEmpty(list)){
			for(EngineTestZkDto dto:list){
				threadPoolTaskExecutor.submit(new testZkRun(container, productType,dto.getZkUrl()));	
			}
		}
	}
	
	private class testZkRun implements Runnable{
		private EngineCpntContainer container;
		private String productType;
		private String zkAddress;
		
		public testZkRun(EngineCpntContainer container,String productType,String zkAddress){
			this.container=container;
			this.productType=productType;
			this.zkAddress=zkAddress;
		}
		
		@Override
		public void run() {
			EnginePolicyFlowSerialize serializer = new EngineKryoSerialize();		
			byte[] bytes = serializer.serialize(container);
			List<EngineFileContent> ruleContentList=new LinkedList<EngineFileContent>();
			ruleContentList.add(new EngineFileContent(productType,bytes));
			Transaction trans = EE.newTransaction("Deploy2TestZk", productType);											
			try {
				trans.setStatus(Transaction.SUCCESS);
				EE.logEvent("Deploy2TestZk", productType);
				logger.info("开始触发Deploy2TestZk同步规则至测试环境");						
				deployRuleModelByAdress(ruleContentList,zkAddress);					
			} catch (EngineException e) {
				trans.setStatus(e);
				EE.logError("触发Deploy2TestZk同步规则至测试环境异常", e);
				logger.info("触发Deploy2TestZk同步规则至测试环境异常",e);
			} finally {
				trans.complete();
			}				
			
		}
	}
	
	@Override
	public EngineFileContent getRuleBytesByFlow(String username,
			Long productReleaseFlowId) {
		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);		
		EngProductReleaseFlow flow = engProductReleaseFlowDao.get(productReleaseFlowId);
		EngineAssert.checkNotNull(flow,"规则模型对象发布记录不存在!It is impossible,flowId:%s",productReleaseFlowId);
						
		EngineCpntContainer container = generateContainerByFlow(flow);	
		
		EnginePolicyFlowSerialize serializer = new EngineKryoSerialize();		
		byte[] bytes = serializer.serialize(container);
		//发布到zk上
		logger.info("规则模型对象序列化成功");		
		return new EngineFileContent(flow.getProductType(), bytes);
	}
	
	private EngineCpntContainer generateContainerByFlow(EngProductReleaseFlow flow){
		String productType = flow.getProductType();
		String version = flow.getVersion();
		EngineAssert.checkNotBlank(productType,"releaseFlow记录中productType为空，id:%s",flow.getId());
		EngineAssert.checkNotBlank(version,"releaseFlow记录中version为空，id:%s",flow.getId());		
		List<JSONObject> list = engRuleModelDao.getRuleModel(RuleModelTable.RELEASE, productType, null, version);
		
		EngineAssert.checkArgument(!CollectionUtils.isEmpty(list), "组件数量为为空，请检查是否上传了组件");
			
		
		//EngineCpntContainer engineFlow = json2Flow(productType,list);
		//EngineCpntContainer container = this.generateContainer(json2Cpnts(list), productType);
		//原先如果直接从mysql库里走的话，会碰到如果，上传完版本后，在发布前，删除某个重要的字段，最终会导致发布失败。如果从会发生从redis走，就没有这样的问题，因为redis把当时发布的状况（包括规则，函数，字段）都记录下来了
		EngineCpntContainer container = this.generateContainer(json2Cpnts(list), productType, version);
		container.setRuleVersion(flow.getVersion());
		return container;
	}
	
	@Override
	@EETransaction(type = "URL", name = "紧急发布")
	public void urgencyRelease(String username, String productType) {
		List<JSONObject> list = engRuleModelDao.getRuleModel(RuleModelTable.MERGE, productType, null, null);		
		//EngineCpntContainer engineFlow = json2Flow(productType,list);
		EngineCpntContainer container = this.generateContainer(json2Cpnts(list), productType);
		container.setRuleVersion("urgency");
		release2Zk(engineRuleModelHotDeployService,container,productType);
	}	
	
	@Override
	@EETransaction(type = "URL", name = "手动上传发布")
	public void uploadRelease(String username, EngineFileContent ruleModelData) {
		EngineAssert.checkNotBlank(username,"上送参数username不可为空");
		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);						
		engineRuleModelHotDeployService.deployRuleModel(ruleModelData);	
	}
	
	@Override
	@EETransaction(type = "URL", name = "撤销发布")
	public void cancelRelease(String username, Long productReleaseFlowId) {
		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);		
		EngProductReleaseFlow flow = engProductReleaseFlowDao.get(productReleaseFlowId);			
		EngineAssert.checkNotNull(flow,"申请记录不存在,productReleaseFlowId:%s",productReleaseFlowId);		
		EngineAssert.checkArgument(!StringUtils.equals(flow.getStatus(), "f"),"该申请记录已经审核无法撤销,status:%s",flow.getStatus());
		flow.setStatus("c");
		flow.setFinalAuditUser(username);
		flow.setFinalAuditTime(EngineUtil.get14CurrentDateTime());
		engProductReleaseFlowDao.update(flow);
	}	
	
	
//	@SuppressWarnings("unused")
//	@Deprecated
//	private void release2Test(String productType,List<JSONObject> list) {		
//		EngineCpntContainer engineFlow = this.generateContainer(json2Cpnts(list), productType);
//		release2Zk(testRuleModelHotDeployService,engineFlow,productType);	
//	}

	
	@Override
	public List<EngineCpntListDisplay> showCpntList(String username, String productType,
			String cpntType,RuleModelTable table,String version) {		
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
								
		List<EngineCpntListDisplay> reverseList = new LinkedList<EngineCpntListDisplay>();
		
		//List<JSONObject> list = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, username, null);						
		List<JSONObject> list = engRuleModelDao.getRuleModel(table, productType, null, version);
								
		for(JSONObject json : list){
			String id = json.getString("id");
			String name = json.getString("name");
			String type = json.getString("type");
			String right = json.getString("right");
			String updatetime = json.getString("updatetime");
			
			EngineAssert.checkNotBlank(id, "规则对象id不存在,json:%s",json.toJSONString());
			EngineAssert.checkNotBlank(name, "规则对象name不存在,json:%s",json.toJSONString());
			EngineAssert.checkNotBlank(type, "规则对象type不存在,json:%s",json.toJSONString());
			EngineAssert.checkNotBlank(right, "规则对象right不存在,json:%s",json.toJSONString());
			EngineAssert.checkNotBlank(updatetime, "规则对象updatetime不存在,json:%s",json.toJSONString());		
			
			if(StringUtils.isNotBlank(cpntType)){//cpntType:Stage,Step,Judge			
				//结尾不是								
				if(!StringUtils.endsWith(type, cpntType.trim())) continue;
			}
			
			reverseList.add(new EngineCpntListDisplay(id,name,type,right,updatetime));
		}
		
		List<EngineCpntListDisplay> reslut = Ordering.from(new Comparator<EngineCpntListDisplay>() {
			//按中文规则
			Collator cmp = Collator.getInstance(java.util.Locale.CHINA); 
			@Override
			public int compare(EngineCpntListDisplay o1,
					EngineCpntListDisplay o2) {										
		        if (cmp.compare(o1.getName(), o2.getName())>0){  
		            return 1;  
		        }else if (cmp.compare(o1.getName(), o2.getName())<0){  
		            return -1;  
		        }  
		        return 0;
			}
			//正序排列
		}).sortedCopy(reverseList);
		
		return reslut;
	}
	

	@Override
	public List<EngineCpntListDisplay> showRouteStageList(String username,
			String productType) {
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
								
		List<EngineCpntListDisplay> result = new LinkedList<EngineCpntListDisplay>();
		
//		List<JSONObject> mergelist = engRuleModelDao.getRuleModel(RuleModelTable.MERGE, productType, null, null);						
//		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, username, null);		
//		EngineRuleModelUtil.mergeCpnts(mergelist, selfList, username);
		
		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, null, null);
		for(JSONObject json : selfList){
			String id = json.getString("id");
			String name = json.getString("name");
			String type = json.getString("type");
			String right = json.getString("right");
			String updatetime = json.getString("updatetime");
			
			EngineAssert.checkNotBlank(id, "规则对象id不存在,json:%s",json.toJSONString());
			EngineAssert.checkNotBlank(name, "规则对象name不存在,json:%s",json.toJSONString());
			EngineAssert.checkNotBlank(type, "规则对象type不存在,json:%s",json.toJSONString());
			EngineAssert.checkNotBlank(right, "规则对象right不存在,json:%s",json.toJSONString());
			EngineAssert.checkNotBlank(updatetime, "规则对象updatetime不存在,json:%s",json.toJSONString());		
					
			//结尾不是								
			if(!StringUtils.endsWith(type, "Stage")) continue;						
			result.add(new EngineCpntListDisplay(id,name,type,right,updatetime));
		}

		return result;
	}	


	@Override
	public EngineCpntContainer parseProduct(RuleModelTable ruleModelTable,
			String productType) {
		List<JSONObject> list = engRuleModelDao.getRuleModel(ruleModelTable, productType, null, null);
		//EngineCpntContainer engineFlow = json2Flow(productType,list);
		EngineCpntContainer engineFlow = this.generateContainer(json2Cpnts(list), productType);
		logger.info("解析成功:{}",engineFlow.getAllCpnt());
		return engineFlow;
	}

	
	@Override
	@EETransaction(type = "URL", name = "规则回滚")
	public void rollbackRelease(String username, Long productReleaseFlowId) {
		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);		
		EngProductReleaseFlow flow = engProductReleaseFlowDao.get(productReleaseFlowId);			
		EngineAssert.checkNotNull(flow,"申请记录不存在,productReleaseFlowId:%s",productReleaseFlowId);		
		EngineAssert.checkArgument(StringUtils.equals(flow.getStatus(), "f"),"该申请记录已经未正式上线，无法用这个版本回滚,status:%s",flow.getStatus());
		
		String productType = flow.getProductType();
		String version = flow.getVersion();
		EngineAssert.checkNotBlank(version,"申请记录的版本号为空,productReleaseFlowId:%s",productReleaseFlowId);
		
		//恢复ser包
		//engineSerFileHotDeployService.rollbackSer(flow.getProductType(), flow.getVersion());
				
		//恢复ruleModel
		List<JSONObject> list = engRuleModelDao.getRuleModel(RuleModelTable.RELEASE, productType, null, version);
		//EngineCpntContainer engineFlow = json2Flow(productType,list);
				
		//后去当时存储的字段数据和函数数据				
		List<EngineDefineFieldDto> fieldList = getReleasedFieldList(productType,version);
		List<EngineScriptFunction> functions = getReleasedFunctions(productType,version);
		
		EngineCpntContainer container = this.transfer2Container(productType,json2Cpnts(list),fieldList,functions);		
		container.setRuleVersion(flow.getVersion());
		
		//更新发布时间
		flow.setReleaseTime(EngineUtil.get14CurrentDateTime());
		engProductReleaseFlowDao.update(flow);		
		
		release2Zk(engineRuleModelHotDeployService,container,flow.getProductType());		
	}

	@SuppressWarnings("unchecked")
	private List<EngineDefineFieldDto> getReleasedFieldList(String productType,String version){
		List<EngineDefineFieldDto> fieldList = null;
		Object fieldListObj = redisTemplate.opsForValue().get(getReleaseAdditionalDataKey(FIELDLIST_KEY,productType,version));
		if(fieldListObj != null){
			logger.info("发现当时发布时候的字段数据，使用当时字段数据去组装规则内存，工程:{}",productType);
			fieldList = (List<EngineDefineFieldDto>) fieldListObj;		
		}else{
			logger.info("由于没有找到当时发布时候的字段数据，使用当前库里的字段数据去组装规则内存，工程:{}",productType);
			fieldList = engineConfigManageService.getEngineParamList(productType);						
		}
		return fieldList;
	} 
	
	@SuppressWarnings("unchecked")
	private List<EngineScriptFunction> getReleasedFunctions(String productType,String version){
		List<EngineScriptFunction> functions = null;
		Object functionsObj = redisTemplate.opsForValue().get(getReleaseAdditionalDataKey(FUNCTIONS_KEY,productType,version));
		if(functionsObj != null){
			logger.info("发现当时发布时候的函数数据，使用当时函数数据去组装规则内存，工程:{}",productType);
			functions = (List<EngineScriptFunction>) functionsObj;
		}else{
			logger.info("由于没有找到当时发布时候的函数数据，使用当前库里的函数数据去组装规则内存，工程:{}",productType);
			functions = getScriptFunctions(productType);
		}
		return functions;
	} 
	
	/**
	 * 获取当前生产的发布记录
	 * @return
	 */
	private EngProductReleaseFlow getCurrentProdReleaseFlow(String productType){
		//用发布时间进行倒叙		
		PageHelper.startPage(1, 1, "release_time desc");		
		
		EngProductReleaseFlow query = new EngProductReleaseFlow();
		query.setStatus("f");
		query.setProductType(productType);
		List<EngProductReleaseFlow> releaseList = engProductReleaseFlowDao.queryByField(query);
		if(!CollectionUtils.isEmpty(releaseList)){
			return releaseList.get(0);			
		}
		return null;
	}
	
	@Override
	@EETransaction(type = "URL", name = "将最新的规则发布到生产")
	public void currentRelease() {		
		
		//PageHelper.startPage(1, 1, "release_time desc");		
		//找到每个产品线当前线上的版本
		List<EngProduct> productList = engProductDao.queryByField(new EngProduct());
		int count = 0;
		for(EngProduct product : productList){
			String productType = product.getProductType();							
			Transaction trans = EE.newTransaction("RefreshRule", productType);
			try{
				trans.setStatus(Transaction.SUCCESS);
				EE.logEvent("RefreshRule", productType);
			
				EngProductReleaseFlow releaseFlow = getCurrentProdReleaseFlow(productType);
				if(releaseFlow != null){				
					String version = releaseFlow.getVersion();
					logger.warn("重置规则模型开始,productType:{},version:{}",productType,version);
					EngineAssert.checkNotBlank(version,"申请记录的版本号为空,productReleaseFlowId:%s",releaseFlow.getId());
					
					List<JSONObject> list = engRuleModelDao.getRuleModel(RuleModelTable.RELEASE, productType, null, version);
					if(CollectionUtils.isEmpty(list)){//万一数据库被删了
						logger.warn("重置规则模型，模型配置内容为空,productType:{},version:{}",productType,version);
						continue;
					}
					//EngineCpntContainer engineFlow = json2Flow(productType,list);
					
					List<EngineDefineFieldDto> fieldList = getReleasedFieldList(productType,version);
					List<EngineScriptFunction> functions = getReleasedFunctions(productType,version);
					EngineCpntContainer container = this.transfer2Container(productType,json2Cpnts(list),fieldList,functions);		
					container.setRuleVersion(version);
					//发布
					release2Zk(engineRuleModelHotDeployService,container,productType);					
					logger.warn("重置规则模型成功,productType:{},version:{},versionName:{}",productType,version,releaseFlow.getVersionName());
					count++;
				}
			}catch(Exception e){
				trans.setStatus(e);
				EE.logError("刷新规则内容失败["+productType+"]",e);
				logger.error("刷新规则内容失败["+productType+"]",e);
			}finally{
				trans.complete();
			}
			
		}		
		logger.warn("重置规则模型完成，一共重置了{}个",count);
	}

	
	
	@Override
	public JSONObject showEngineFlow(String username,String productType) {		
//		List<JSONObject> mergeList = engRuleModelDao.getRuleModel(RuleModelTable.MERGE, productType, null, null);
//		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, username, null);		
//		EngineRuleModelUtil.mergeCpnts(mergeList, selfList, username);
		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, null, null);
		JSONObject result = null;
		for(JSONObject one : selfList){
			String type = one.getString("type");
			//判断一个产品线是否有多个决策流
			if(result != null){
				if(StringUtils.equals(type, EnginePolicyFlow.class.getSimpleName())){
					throw new EngineException("该产品线有多个决策流,请联系管理员检查");
				}
			}
			
			if(StringUtils.equals(type, EnginePolicyFlow.class.getSimpleName())){
				result = one;
			}						
		}
		return result;
	}

	@Override
	public JSONObject showEngineFlowByVersion(String username,String productType,String version) {		
		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.RELEASE, productType, null, version);
		JSONObject result = null;
		for(JSONObject one : selfList){
			String type = one.getString("type");
			//判断一个产品线是否有多个决策流
			if(result != null){
				if(StringUtils.equals(type, EnginePolicyFlow.class.getSimpleName())){
					throw new EngineException("该产品线有多个决策流,请联系管理员检查");
				}
			}
			
			if(StringUtils.equals(type, EnginePolicyFlow.class.getSimpleName())){
				result = one;
			}						
		}
		return result;
	}


	@Override
	public List<EngineScriptSyntaxErr> checkScriptSyntax(String productType, String script) {
		List<EngineDefineFieldDto> fieldList = engineConfigManageService.getEngineParamList(productType);
		Map<String,String> replaceMap = new HashMap<String,String>();
		for(EngineDefineFieldDto field : fieldList){
			replaceMap.put(field.getFieldKey(), field.getFieldName());
		}
		
		for(String key : replaceMap.keySet()){
			String name = replaceMap.get(key);
			EngineAssert.checkNotBlank(key);
			EngineAssert.checkNotBlank(name);			
			script = StringUtils.replace(script, "#"+name+"#", key);
		}
				
		logger.debug(EngineUtil.logPrefix(BUSI_DESC)+"开始校验:{}",script);
		List<Error> errList = EngineJavaScriptSyntaxCheck.getInstance().checkSyntax(script);
		
		List<EngineScriptSyntaxErr> result = new LinkedList<EngineScriptSyntaxErr>();
		 		
		if(!CollectionUtils.isEmpty(errList)){
			for(Error err : errList){
				EngineScriptSyntaxErr dto = new EngineScriptSyntaxErr();
				dto.setId(err.getId());
				dto.setRaw(err.getRaw());
				dto.setCode(err.getCode());
				dto.setEvidence(err.getEvidence());
				dto.setLine(err.getLine());
				dto.setCharacter(err.getCharacter());
				dto.setScope(err.getScope());
				dto.setReason(err.getReason());
				result.add(dto);
			}
		}
		
		return result;
	}




	
	@Override
	@EETransaction(type = "URL", name = "组件单元测试")
	public List<EngineTestReturnDto> moduleTest(String username,String productType,String cpntId,
			String input,String output) {		
		//从merge区拉一片新的规则和自己的规则合并后进行
//		List<JSONObject> mergeList = engRuleModelDao.getRuleModel(RuleModelTable.MERGE, productType, null, null);
//		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, username, null);
		
//		EngineRuleModelUtil.mergeCpnts(mergeList, selfList, username);
		
		//初始化js引擎中的线程局部变量		
		EngineScriptThreadLocal.initCurEngine();
		
		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, null, null);
		//EngineCpntContainer flow = json2Flow(productType,mergeList);
		EngineCpntContainer container = this.generateContainer(json2SomeCpnt(selfList,cpntId), productType);
		
		EngineCpnt cpnt = container.getOneCpnt(cpntId);
		EngineAssert.checkNotNull(cpnt, "没有找到该组件,cpntId:%s",cpntId);
		
		if(cpnt instanceof EngineRelaField){
			EngineRelaField rela = (EngineRelaField) cpnt;
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"该组件涉及相关input:{}",rela.inputRela());
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"该组件涉及相关output:{}",rela.outputRela());
		}
		if(cpnt instanceof EngineRelaCpnt){
			EngineRelaCpnt rela = (EngineRelaCpnt) cpnt;
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"该组件涉及相关组件:{}",rela.relaCpntIds());			
		}
		
		//将数据缓存到单元测试里，以便记忆后 下次获取
		String inputKey = moduleTestKey(productType,cpntId,"input");
		String outputKey = moduleTestKey(productType,cpntId,"output");
		redisTemplate.opsForValue().set(inputKey, input);
		redisTemplate.opsForValue().set(outputKey, output);
		
		return transfer2TestReturn(productType,this.simulateTest(cpnt, productType, input, output, EngineExecMode.MODULE_TEST));
	}
	
	private String moduleTestKey(String productType,String cpntId,String type){
		return "moduleTest_"+productType+cpntId+type;
	}
	
	public Map<String,Map<String,Object>> getModuleTestCustomParam(String productType,String cpntId){
		String inputKey = moduleTestKey(productType,cpntId,"input");
		String outputKey = moduleTestKey(productType,cpntId,"output");
		
		Object inputCache = redisTemplate.opsForValue().get(inputKey);
		Object outputCache = redisTemplate.opsForValue().get(outputKey);
		
		Map<String,Map<String,Object>> result = new HashMap<String,Map<String,Object>>();
		if(inputCache != null){
			JSONObject json = JSONObject.parseObject(inputCache.toString());
			result.put("input", json);
		}else{
			Map<String,Object> temp = Collections.emptyMap();
			result.put("input", temp);
		}
			
		if(outputCache != null){
			JSONObject json = JSONObject.parseObject(outputCache.toString());
			result.put("output", json);
		}else{
			Map<String,Object> temp = Collections.emptyMap();
			result.put("output", temp);
		}

		return result;
	}

	@Override
	public List<EngineTestReturnDto> wholeTest(String username, String productType,
			String input, String output) {
		//从merge区拉一片新的规则和自己的规则合并后进行
//		List<JSONObject> mergeList = engRuleModelDao.getRuleModel(RuleModelTable.MERGE, productType, null, null);
//		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, username, null);
//		
//		EngineRuleModelUtil.mergeCpnts(mergeList, selfList, username);
		//EngineCpntContainer flow = json2Flow(productType,mergeList);
		EngineScriptThreadLocal.initCurEngine();
		
		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, null, null);
		EngineCpntContainer flow = this.generateContainer(json2Cpnts(selfList), productType);
		
		EnginePolicyFlow root = flow.getRoot();
		EngineAssert.checkNotNull(root, "该产品流程没有root节点");
		
		return transfer2TestReturn(productType,this.simulateTest(root, productType, input, output, EngineExecMode.WHOLE_TEST));
	}

	
	private List<EngineTestReturnDto> transfer2TestReturn(String productType,Map<String, Object> returnData){
		List<EngineTestReturnDto> result = new LinkedList<EngineTestReturnDto>();
		
		List<EngineDefineFieldDto> paramDefineList = engineConfigManageService.getDefineFieldList(productType);
		
		for(String key : returnData.keySet()){			 			
			EngineTestReturnDto one = new EngineTestReturnDto();
			one.setKey(key);
			
			for(EngineDefineFieldDto paramDefine : paramDefineList){
				if(StringUtils.equals(paramDefine.getFieldKey(), key)){
					one.setName(paramDefine.getFieldName());
					break;
				}
			}			
			
			if("_result".equals(key)){
				one.setName("判断结果");
			}
			
			one.setValue(returnData.get(key));		
			result.add(one);
		}
		return result;
	}
	
	private Map<String, Object> simulateTest(EngineCpnt cpnt,String productType,
			String input,String output,EngineExecMode testMode) {
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+StringUtils.center("装载组件完成，开始测试", 120, '*'));
		
		//模拟task
		EngineTaskInfo taskInfo = new EngineTaskInfo();
		taskInfo.setId(0L);
		taskInfo.setProductType(productType);
		taskInfo.setAppName("testApp");
		taskInfo.setAppRequestId("testRequestId");
		taskInfo.setAssignStep(1);//设置成已被分配
		taskInfo.setStatus("1");//设置成处理中
		taskInfo.setAppParams(input);
		taskInfo.setRuleVersion("test");
		EngineTransferData etd = EngineTransferData.createByDefault(taskInfo);
		
		Map<String,Object> outputParams = JSONObject.parseObject(output);
		etd.getOutputParam().putAll(outputParams);
		
		//校验参数数据类型		
		checkParam(etd.getInputParam(),productType);
		checkParam(etd.getOutputParam(),productType);
		
		Map<String, Object> result = new HashMap<String, Object>();
		if(cpnt instanceof EnginePolicyFlow){
			EnginePolicyFlow flow = (EnginePolicyFlow) cpnt;		
			EngineStepProcessor processor = EngineStepTaskProcessor.create(new EngineDoNothingPersistence(),etd);
			flow.run(processor, etd);
			result.putAll(etd.getOutputParam());			
		}else if(cpnt instanceof EngineStage){
			EngineStage stage = (EngineStage) cpnt;		
			EngineStepProcessor processor = EngineStepTaskProcessor.create(new EngineDoNothingPersistence(),etd);
			processor.addStepList(stage.getList());
			processor.launch();		
			result.putAll(etd.getOutputParam());			
		}else if(cpnt instanceof EngineStep){
			EngineStep step = (EngineStep) cpnt;			
			step.execute(etd);
			result.putAll(etd.getOutputParam());
		}else if(cpnt instanceof EngineJudge){
			EngineJudge judge = (EngineJudge) cpnt;			
			boolean judgeResult = judge.judge(etd);
			result.put("_result", judgeResult);
		}else
			throw new EngineException("It is impossible");
		
		return result;
	}
	
	private void checkParam(EngineParam param,String productType){
		for(String fieldKey : param.keySet()){
			Object value = param.get(fieldKey);			
			EngineDefineFieldDto defineDto = engineConfigManageService.getDefineField(productType, fieldKey);
			//如果没有定义则不管
			if(defineDto == null) continue;			
			checkParamType(fieldKey,value,defineDto.getFieldType(),StringUtils.equals(defineDto.getIsArr(), "1"));
		}
	}
	
	private void checkParamType(String fieldKey,Object obj,String type,boolean isArr){
		EngineAssert.checkNotNull(obj,"测试上送参数不能为空!");
		EngineAssert.checkNotNull(type,"参数定义中的type不能为空!");
		
		if(isArr){
			EngineAssert.checkArgument(obj instanceof List, fieldKey+"必须是数组类");
			List<?> list = (List<?>) obj;
			if(list.size() == 0) return;
			for(Object one : list){
				if(type.toLowerCase().equals("string")){
					EngineAssert.checkArgument(one instanceof String, fieldKey+"必须是字符串数组类");
				}else if(type.toLowerCase().equals("bigdecimal")||type.toLowerCase().equals("long")){
					EngineAssert.checkArgument(one instanceof Number, fieldKey+"必须是数值数组类");
				}
			}			
		}else{
			if(type.toLowerCase().equals("string")){
				EngineAssert.checkArgument(obj instanceof String, fieldKey+"必须是字符串类");
			}else if(type.toLowerCase().equals("bigdecimal")||type.toLowerCase().equals("long")){
				EngineAssert.checkArgument(obj instanceof Number, fieldKey+"必须是数值类");
			}
		}
		
	}
	

	
	
	@EETransaction(type = "URL", name = "复制规则模型")
	@Override
	public List<EngineCpntListDisplay> copyCpnt(String username, String fromProduct, String toProduct, 
			String... cpntIds) {
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(fromProduct),"该产品不存在,来源产品线:%s",fromProduct);
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(toProduct),"该产品不存在,toProduct:%s",toProduct);
		EngineAssert.checkNotNull(engBusiOperatorDao.queryLastOneByUsername(username),"用户不存在,username:%s",username);		
		
		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, fromProduct, null, null);		
		EngineCpntContainer flow = this.generateContainer(json2SomeCpnt(selfList,cpntIds), fromProduct);		
		//检查一下依赖字段有没有缺失
		//首先找到依赖字段
		Set<String> inputSet = new HashSet<String>();
		Set<String> outputSet = new HashSet<String>();
		List<EngineRelaField> relaFields = flow.getCpntsByType(EngineRelaField.class);
		for(EngineRelaField relaField : relaFields){
			inputSet.addAll(relaField.inputRela());
			outputSet.addAll(relaField.outputRela());
		}
		List<EngineDependField> dependFields = flow.getCpntsByType(EngineDependField.class);
		for(EngineDependField dependField : dependFields){
			Map<String,String> oneCpntDependField = dependField.dependFields();
			for(String key : oneCpntDependField.keySet()){
				String putType = oneCpntDependField.get(key);
				if(StringUtils.equals(putType, "input")) inputSet.add(key);
				if(StringUtils.equals(putType, "output")) outputSet.add(key);
				
			}						
		}
		//查看新工程里是不是有这些字段
		List<EngineDefineFieldDto> toProductFields = engineConfigManageService.getDefineFieldList(toProduct);
		Set<String> toProductInputFields = new HashSet<String>();
		Set<String> toProductOutputFields = new HashSet<String>();
		for(EngineDefineFieldDto dto : toProductFields){
			if(StringUtils.equals(dto.getFieldUse(), "input")) toProductInputFields.add(dto.getFieldKey());
			if(StringUtils.equals(dto.getFieldUse(), "output")) toProductOutputFields.add(dto.getFieldKey());
		}		
		for(String field : inputSet){
			EngineAssert.checkArgument(toProductInputFields.contains(field), "新工程缺少input字段["+field+"]");
		}
		for(String field : outputSet){
			EngineAssert.checkArgument(toProductOutputFields.contains(field), "新工程缺少output字段["+field+"]");
		}
		//检查新工程是否缺少函数 比较难判断暂时不管了
		
		
		//需要检测涉及到哪些组件id 并把组件id的那些json复制到新的产品线中
		Set<String> relaCpntIds = new HashSet<String>();		
		for(String cpntId : cpntIds){
			//如果已经存在了则不在查找了
			if(relaCpntIds.contains(cpntId)) continue;			
			EngineCpnt cpnt = flow.getOneCpnt(cpntId);
			if(cpnt instanceof EngineRelaCpnt){
				EngineRelaCpnt rela = (EngineRelaCpnt) cpnt;
				relaCpntIds.addAll(rela.relaCpntIds().keySet());
				relaCpntIds.add(cpnt.id());
			}
		}
		
		List<EngineCpntListDisplay> result = new LinkedList<EngineCpntListDisplay>();
		
		//对应的新的组件ID列表
		Map<String,String> relaNewCpntIdMap = new HashMap<String, String>();
		for(String oldId : relaCpntIds){
			String newId = EngineUtil.getUUID();			
			relaNewCpntIdMap.put(oldId, newId);
		}
		
		List<JSONObject> insertList = new LinkedList<JSONObject>();
		

		for(JSONObject cpntJson : selfList){
			String cpntId = cpntJson.getString("id");
			if(!relaCpntIds.contains(cpntId)) continue;
		
			//复制好的新组件，本意是用全新的id去标示，但是
			//由于依赖组件的ID很不好替换，这里只能先通过文本替换了
			//因为id号现在是uuid，所以基本问题也不大
			JSONObject newJson = replaceNewId(cpntJson, relaNewCpntIdMap);
			
			newJson.remove("_id");
			newJson.put("right", username);
			newJson.put("productType", toProduct);
			newJson.put("updatetime", EngineUtil.get14CurrentDateTime());
			insertList.add(newJson);
			
			EngineCpntListDisplay display = new EngineCpntListDisplay(
					cpntJson.getString("id"),
					cpntJson.getString("name"),
					cpntJson.getString("type"),
					cpntJson.getString("right"),
					cpntJson.getString("updatetime")
					);
			result.add(display);
		}
		
		engRuleModelDao.batchInsert(insertList,RuleModelTable.SELF);		
		return result;
	}

	private JSONObject replaceNewId(JSONObject oldJson,Map<String,String> relaNewCpntIdMap){		
		String oldStr = oldJson.toJSONString();
		String newStr = oldStr;
		for(String oldId:relaNewCpntIdMap.keySet()){
			String newid = relaNewCpntIdMap.get(oldId);
			newStr = StringUtils.replace(newStr, oldId, newid);			
		}				
		
		JSONObject newJson = JSONObject.parseObject(newStr);
		return newJson;
	}

	@Override
	public void demoRelease() {
		EngineScriptJudge judgeAge = new EngineScriptJudge();
		judgeAge.setScript("_result = input.age >= 18;");
				
		EngineScriptStep scriptStep1 = new EngineScriptStep();
		scriptStep1.setScript("output.result = true;");
		
		EngineScriptStep scriptStep2 = new EngineScriptStep();
		scriptStep2.setScript("output.result = false;");
		
		EngineStandardStage stage1 = new EngineStandardStage();
		List<EngineStep> stepList1 = new LinkedList<EngineStep>();
		stepList1.add(scriptStep1);
		stage1.setStepList(stepList1);
		
		EngineStandardStage stage2 = new EngineStandardStage();
		List<EngineStep> stepList2 = new LinkedList<EngineStep>();
		stepList2.add(scriptStep2);
		stage2.setStepList(stepList2);
		
		EnginePolicyFlow.EngineFlowNode root = new EnginePolicyFlow.EngineFlowNode();
		
		EnginePolicyFlow.EngineFlowNode node1 = new EnginePolicyFlow.EngineFlowNode();
		node1.setStage(stage1);
		EnginePolicyFlow.EngineFlowNode node2 = new EnginePolicyFlow.EngineFlowNode();
		node2.setStage(stage2);
				
		root.addBranch(judgeAge, true, node1);
		root.setDefaultNode(node2);
		
		EnginePolicyFlow flow = new EnginePolicyFlow();
		flow.setRoot(root);
		
		EngineCpntContainer container = new EngineCpntContainer();
		container.setRoot(flow);
		container.setRuleVersion("demoRelease");
		
		release2Zk(engineRuleModelHotDeployService,container,"xxxxxxxxxxxx");		
	}

	@Override
	public List<EngineDefineFieldDto> getRelaDefineField(String username,
			String productType, String cpntId) {
		EngineAssert.checkNotNull(engProductDao.queryLastOneByProductType(productType),"该产品不存在,productType:%s",productType);
		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
		
		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, null, null);
		EngineCpntContainer container = this.generateContainer(json2SomeCpnt(selfList,cpntId), productType);
		
		EngineCpnt cpnt = container.getOneCpnt(cpntId);
		EngineAssert.checkNotNull(cpnt, "没有找到该组件,cpntId:%s",cpntId);
		
		
		List<EngineDefineFieldDto> resultList = new LinkedList<EngineDefineFieldDto>();
		if(cpnt instanceof EngineRelaField){
			EngineRelaField rela = (EngineRelaField) cpnt;
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"该组件涉及相关input:{}",rela.inputRela());
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"该组件涉及相关output:{}",rela.outputRela());
			
			List<EngineDefineFieldDto> paramList = engineConfigManageService.getEngineParamList(productType);
			for(EngineDefineFieldDto one : paramList){
				for(String input : rela.inputRela()){
					if("input".equals(one.getFieldUse())&&StringUtils.equals(input, one.getFieldKey())){
						resultList.add(one);
					}
				}
				for(String output : rela.outputRela()){
					if("output".equals(one.getFieldUse())&&StringUtils.equals(output, one.getFieldKey())){
						resultList.add(one);
					}
				}
			}
		}
		
		return resultList;
	}

	@Override
	public List<EngineCpntCompareGroupDto> compareCpntByVersionAndVersion(
			String productType, String oneVersion, String anotherVersion) {

		List<JSONObject> oneList = engRuleModelDao.getRuleModel(RuleModelTable.RELEASE, productType, null, oneVersion);		
		EngineAssert.checkArgument(!CollectionUtils.isEmpty(oneList), "组件数量为为空，无法比较，version:%s",oneVersion);					
		
		List<JSONObject> anotherList = engRuleModelDao.getRuleModel(RuleModelTable.RELEASE, productType, null, anotherVersion);		
		EngineAssert.checkArgument(!CollectionUtils.isEmpty(anotherList), "组件数量为为空，无法比较，version:%s",anotherVersion);			

		return compareCpntByByJson(productType,oneVersion,anotherVersion,oneList,anotherList);
	}

	@Override
	public List<EngineCpntCompareGroupDto> compareCpntByCurrentAndVersion(
			String productType, String username, String anotherVersion) {
		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"用户不存在,username:%s",username);
		
//		List<JSONObject> mergeList = engRuleModelDao.getRuleModel(RuleModelTable.MERGE, productType, null, null);
//		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, productType, username, null);		
//		EngineRuleModelUtil.mergeCpnts(mergeList, selfList, username);
		List<JSONObject> mergeList = engRuleModelDao.getRuleModel(RuleModelTable.MERGE, productType, null, null);
		
		List<JSONObject> anotherList = engRuleModelDao.getRuleModel(RuleModelTable.RELEASE, productType, null, anotherVersion);		
		EngineAssert.checkArgument(!CollectionUtils.isEmpty(anotherList), "组件数量为为空，无法比较，version:%s",anotherVersion);			
		
		return compareCpntByByJson(productType,anotherVersion,null,anotherList,mergeList);
	}

	/* (非 Javadoc) 
	* <p>Title: compareCpntByCurrentAndProd</p> 
	* <p>Description: 当前本地提交版本与生产版本对比 </p> 
	* @param productType
	* @param username
	* @return 
	* @see com.mobanker.engine.design.busi.workspace.EngineWorkspaceManager#compareCpntByCurrentAndProd(java.lang.String, java.lang.String) 
	*/
	@Override
	public List<EngineCpntCompareGroupDto> compareCpntByCurrentAndProd(
			String productType, String username) {
		//获取生产的list
		EngProductReleaseFlow flow = getCurrentProdReleaseFlow(productType);
		if(flow == null){
			logger.info("工程模型:{},第一次比较",productType);
			List<JSONObject> mergeList = engRuleModelDao.getRuleModel(RuleModelTable.MERGE, productType, null, null);
			EngineCpntContainer newContainer = this.generateContainer(json2Cpnts(mergeList), productType);			
			//弄一个空的hashMap
			EngineCpntContainer oldContainer = new EngineCpntContainer();
			oldContainer.setAllCpnt(new HashMap<String, EngineCpnt>());			
			return compareCpntByByContainer(null,null,oldContainer,newContainer);			
		}else{
			logger.info("工程模型:{}找到最新的版本记录,{}",productType,flow);
			return this.compareCpntByCurrentAndVersion(productType, username, flow.getVersion());	
		}					
	}	
	
	
	/* (非 Javadoc) 
	* <p>Title: compareCpntByVersionAndProd</p> 
	* <p>Description: 已发布区，根据版本号与当前生产版本比对</p> 
	* @param productType
	* @param username
	* @param newVersion
	* @return 
	* @see com.mobanker.engine.design.busi.workspace.EngineWorkspaceManager#compareCpntByVersionAndProd(java.lang.String, java.lang.String, java.lang.String) 
	*/
	@Override
	public List<EngineCpntCompareGroupDto> compareCpntByVersionAndProd(String productType, String username,
			String newVersion) {
		//获取生产的list
		EngProductReleaseFlow flow = getCurrentProdReleaseFlow(productType);
		if(flow == null){
			logger.info("工程模型:{},第一次比较",productType);
			List<JSONObject> newList = engRuleModelDao.getRuleModel(RuleModelTable.RELEASE, productType, null, newVersion);		
			EngineCpntContainer newContainer = this.generateContainer(json2Cpnts(newList), productType);			
			//弄一个空的hashMap
			EngineCpntContainer oldContainer = new EngineCpntContainer();
			oldContainer.setAllCpnt(new HashMap<String, EngineCpnt>());			
			return compareCpntByByContainer(null,null,oldContainer,newContainer);						
		}else{
			logger.info("工程模型:{}找到最新的版本记录,{}",productType,flow);
			return this.compareCpntByVersionAndVersion(productType, flow.getVersion(), newVersion);	
		}		
	}
	
	
	private List<EngineCpntCompareGroupDto> compareCpntByByJson(
			String productType, String oneVersion, String anotherVersion,List<JSONObject> oneList, List<JSONObject> anotherList) {
		EngineCpntContainer oneContainer;
		EngineCpntContainer anotherContainer;
		if(StringUtils.isNoneBlank(oneVersion)){
			logger.info("版本对比,从版本数据中获取字段和函数列表(oneVersion)，工程号:{},版本号:{}",productType,oneVersion);
			oneContainer = this.generateContainer(json2Cpnts(oneList), productType, oneVersion);
		}else{
			logger.info("版本对比,从当前数据库中获取字段和函数列表(oneVersion)，工程号:{},版本号:{}",productType,oneVersion);
			oneContainer = this.generateContainer(json2Cpnts(oneList), productType);
		}
			
		if(StringUtils.isNoneBlank(anotherVersion)){
			logger.info("版本对比,从版本数据中获取字段和函数列表(anotherVersion)，工程号:{},版本号:{}",productType,anotherVersion);
			anotherContainer = this.generateContainer(json2Cpnts(anotherList), productType, anotherVersion);
		}else{
			logger.info("版本对比,从当前数据库中获取字段和函数列表(anotherVersion)，工程号:{},版本号:{}",productType,anotherVersion);
			anotherContainer = this.generateContainer(json2Cpnts(anotherList), productType);
		}
		
		return compareCpntByByContainer(oneVersion,anotherVersion,oneContainer,anotherContainer);
	}
	
	private  List<EngineCpntCompareGroupDto> compareCpntByByContainer(String oneVersion, String anotherVersion, EngineCpntContainer oneContainer, EngineCpntContainer anotherContainer) {
		List<EngineCpntCompareGroupDto> result = new LinkedList<EngineCpntCompareGroupDto>();
		
		for(String cpntId : oneContainer.getAllCpnt().keySet()){
			EngineCpnt oneCpnt = oneContainer.getAllCpnt().get(cpntId);
			
			EngineCpntCompareDto oneDto = new EngineCpntCompareDto();
			oneDto.setCpntId(oneCpnt.id());
			oneDto.setCpntName(oneCpnt.name());
			oneDto.setType(oneCpnt.getClass().getSimpleName());
			oneDto.setVersion(oneVersion);
			//如果是另一个版本没有的
			if(anotherContainer.getAllCpnt().get(cpntId)==null){								
				result.add(new EngineCpntCompareGroupDto(oneDto,null));				
				continue;
			}
			
			EngineCpnt anotherCpnt = anotherContainer.getAllCpnt().get(cpntId);			
			if(!oneCpnt.equals(anotherCpnt)){
				EngineCpntCompareDto anotherDto = new EngineCpntCompareDto();
				anotherDto.setCpntId(anotherCpnt.id());
				anotherDto.setCpntName(anotherCpnt.name());
				anotherDto.setType(anotherCpnt.getClass().getSimpleName());
				anotherDto.setVersion(anotherVersion);
				result.add(new EngineCpntCompareGroupDto(oneDto,anotherDto));				
				continue;
			}
		}
		
		
		for(String cpntId : anotherContainer.getAllCpnt().keySet()){
			EngineCpnt anotherCpnt = anotherContainer.getAllCpnt().get(cpntId);
			
			EngineCpntCompareDto anotherDto = new EngineCpntCompareDto();
			anotherDto.setCpntId(anotherCpnt.id());
			anotherDto.setCpntName(anotherCpnt.name());
			anotherDto.setType(anotherCpnt.getClass().getSimpleName());
			anotherDto.setVersion(anotherVersion);
			if(oneContainer.getAllCpnt().get(cpntId)==null){								
				result.add(new EngineCpntCompareGroupDto(null,anotherDto));				
				continue;
			}
		}

		return result;
	}


	private String getReleaseAdditionalDataKey(String keyType,String productType,String version){
		return keyType+"_"+productType+"_"+version;
	}



	@Override
	public void snapshotBatchRun(SnapshotBatchRunDto dto) {		
		EngineAssert.checkNotNull(dto.getReleaseId());
		EngineAssert.checkNotBlank(dto.getBeginTime());
		EngineAssert.checkNotBlank(dto.getEndTime());
		
//		EngProductReleaseFlow query = new EngProductReleaseFlow();
//		query.setStatus("0");
//		query.setProductType(productType);
//		List<EngProductReleaseFlow> applyList = engProductReleaseFlowDao.queryByField(query);
//		if(CollectionUtils.isEmpty(applyList)){
//			throw new EngineException("请先上传一个待发布的规则模型");
//		}		
//		EngProductReleaseFlow applyFlow = applyList.get(applyList.size()-1);				
		
		EngProductReleaseFlow applyFlow = engProductReleaseFlowDao.get(dto.getReleaseId());
		EngineAssert.checkNotNull("根据发布id没有找到记录,id:%s",dto.getReleaseId());		
		String productType = applyFlow.getProductType();
		
		//获取该工程最后一次未发布的					
		Transaction trans = EE.newTransaction("SimulateBatchRun", productType);
		try{
			trans.setStatus(Transaction.SUCCESS);
			EE.logEvent("SimulateBatchRun", productType);
			
			String version = applyFlow.getVersion();
			logger.warn("测试规则模型开始,productType:{},version:{}",productType,version);
			EngineAssert.checkNotBlank(version,"申请记录的版本号为空,productReleaseFlowId:%s",applyFlow.getId());
			
			List<JSONObject> list = engRuleModelDao.getRuleModel(RuleModelTable.RELEASE, productType, null, version);
			if(CollectionUtils.isEmpty(list)){//万一数据库被删了
				logger.warn("测试规则模型，模型配置内容为空,productType:{},version:{}",productType,version);
				throw new EngineException("规则模型的组件数量为零，请查看规则仓库");
			}
			//EngineCpntContainer engineFlow = json2Flow(productType,list);
			
			List<EngineDefineFieldDto> fieldList = getReleasedFieldList(productType,version);
			List<EngineScriptFunction> functions = getReleasedFunctions(productType,version);
			EngineCpntContainer container = this.transfer2Container(productType,json2Cpnts(list),fieldList,functions);		
			container.setRuleVersion(version);
			//发布							
			ResponseEntityDto<String> res = engineSnapshotBatchRun.batchRun(container, dto);
			if(!StringUtils.equals(res.getStatus(), "1")){
				throw new EngineException(res.getMsg());
			}			
			logger.info(res.getMsg());			
		}catch(Exception e){
			trans.setStatus(e);
			EE.logError("测试规则内容失败["+productType+"]",e);
			logger.error("测试规则内容失败["+productType+"]",e);
			throw new EngineException(e.getMessage(),e);
		}finally{
			trans.complete();
		}
		
		
	}
	
	/* (非 Javadoc) 
	* <p>Title: getRuleZipBytes</p> 
	* <p>Description: 导出规则zip文件流</p> 
	* @return 
	* @see com.mobanker.engine.design.busi.workspace.EngineWorkspaceManager#getRuleZipBytes() 
	*/
	@Override
	public byte[] getRuleZipBytes() {
		List<EngineFileContent> list=engineRuleModelHotDeployService.getAllRuleModelBytes();
		return EngineZipFileUtil.zip(list);
	}
	
	/** 
	* @Title: deployRuleModel2Test 
	* @Description: 同步当前环境所有规则至所有测试环境
	* 参数@return
	* @return String    返回类型 
	* @throws 
	*/
	@Override
	@EETransaction(type = "URL", name = "同步所有测试环境")
	public String deployRuleModel2Test(List<EngineFileContent> ruleContentList){
		
		List<EngineTestZkDto> list=engineConfigManageService.showAllTestZk("1");
		
		String errorMsg="";
		if(!CollectionUtils.isEmpty(list)){
			Map<String,Future<Integer>> futureList= new HashMap<String,Future<Integer>>();
			for(EngineTestZkDto dto:list){
				callDepolyTestZk call=new callDepolyTestZk(ruleContentList,dto.getZkUrl());
			    Future<Integer> future = threadPoolTaskExecutor.submit(call); 
			    futureList.put(dto.getZkUrl(),future);
			}
			
			if(futureList != null && futureList.size() > 0){
				//上面线程池任务未完成会一直阻塞
				for(String url:futureList.keySet()){
					Future<Integer> future=futureList.get(url);
				    try {  
				        
				    	future.get(4, TimeUnit.MINUTES);   
				        
				    } catch (TimeoutException ex) {  
				    	logger.error("发送至zk测试服务器超时，请检查地址:{}",url);
				    	errorMsg+="环境同步失败,发送超时！请检查地址:"+url+"<br />";
				    } catch (Exception e) {  
				    	logger.error("发送至zk测试服务器失败,地址："+url, e);
				    	errorMsg+="环境同步失败！请检查地址:"+url+"<br />";
				    }finally { 
				    	if(future!=null){
				    		future.cancel(true);  
				    	}
			        } 
				}
			}
		}
		if(!StringUtils.isBlank(errorMsg)){
			throw new EngineException(errorMsg);
		}
		return errorMsg;
	}
	
	private class callDepolyTestZk implements Callable<Integer> {
		private String zkAddress;
		private List<EngineFileContent> ruleContentList;
		
		public callDepolyTestZk(List<EngineFileContent> ruleContentList,String zkAddress){
			this.zkAddress=zkAddress;
			this.ruleContentList=ruleContentList;
		}
	    @Override
	    public Integer call() throws Exception {
    		try{
    			deployRuleModelByAdress(ruleContentList,zkAddress);
    		} catch (Exception e) {
				logger.error("规则同步测试环境失败,zk地址{}",zkAddress,e);
    			throw new EngineException("规则同步测试环境失败,地址："+zkAddress, e);
    		}	
            return 1;  
	    }
	}
	
	/** 
	* @Title: deployRuleModel2Test 
	* @Description:  同步当前环境所有规则至 传入地址zk
	* 参数@param zkAddress
	* @return void    返回类型 
	* @throws 
	*/
	@Override
	@EETransaction(type = "URL", name = "同步单个测试环境")
	public void deployRuleModel2Test(String zkAddress){
		List<EngineFileContent> ruleContentList=engineRuleModelHotDeployService.getAllRuleModelBytes();
		this.deployRuleModelByAdress(ruleContentList, zkAddress);
	}
	
	/** 
	* @Title: zipFileUpload 
	* @Description: 规则批量导入
	* 参数@param multiFile
	* @return void    返回类型 
	* @throws 
	*/
	@Override
	public void zipFileUpload(MultipartFile multiFile){
		if(!StringUtils.equals(engineTestRuleHotDeployService.isProductEnviro(),"1")){
			List<EngineFileContent> ruleContentList=this.decompressingZipFile(multiFile);
			for(EngineFileContent ruleModelData:ruleContentList){
				engineRuleModelHotDeployService.deployRuleModel(ruleModelData);	
			}
		}else{
			throw new EngineException("生产环境不支持批量导入功能!");
		}
	}
	
	private void deployRuleModelByAdress(List<EngineFileContent> ruleContentList,String zkAddress){
		engineTestRuleHotDeployService.deployRuleModel(ruleContentList, zkAddress);
	}
	
	private List<EngineFileContent> decompressingZipFile(MultipartFile multiFile) {
		List<EngineFileContent> list=EngineZipFileUtil.decompressingZipFile(multiFile);
		return list;
	}



}
