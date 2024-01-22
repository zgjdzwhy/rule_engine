package com.mobanker.engine.design.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.common.EngineConst;
import com.mobanker.engine.design.busi.configma.EngineConfigManageService;
import com.mobanker.engine.design.busi.user.EngineUserService;
import com.mobanker.engine.design.busi.workspace.EngineWorkspaceManager;
import com.mobanker.engine.design.dto.EngineCpntCompareGroupDto;
import com.mobanker.engine.design.dto.EngineCpntListDisplay;
import com.mobanker.engine.design.dto.EngineDefineFieldDto;
import com.mobanker.engine.design.dto.EngineProductDto;
import com.mobanker.engine.design.dto.EngineQueryModelDto;
import com.mobanker.engine.design.dto.EngineScriptSyntaxErr;
import com.mobanker.engine.design.dto.EngineUserLoginDto;
import com.mobanker.engine.design.mongo.assist.RuleModelTable;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.framework.contract.dto.ResponseEntityDto;

/**
 * 规则模型设计交互
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月4日
 * @version 1.0
 */
@Controller
@RequestMapping("/ruleModelDesign")
public class EngineRuleModelDesignController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());	


	@Autowired
	private EngineWorkspaceManager engineWorkspaceManager;
	
	@Autowired
	private EngineConfigManageService engineConfigManageService;
	
	@Autowired
	private EngineUserService engineUserService;
	
	
	/**
	 * http://localhost:8080/ruleModelDesign/loginIn
	 * 登录
	 * @param operatorId
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/loginIn", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<EngineUserLoginDto> loginIn(@RequestParam String username,@RequestParam String password,@RequestParam String sessionId) {
		ResponseEntityDto<EngineUserLoginDto> ret= new ResponseEntityDto<EngineUserLoginDto>();
		try{	
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(password,"上送参数password不可为空");			
			EngineUserLoginDto dto = engineUserService.loginIn(username, password,sessionId);			
			ret = new ResponseEntityDto<EngineUserLoginDto>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,dto);		
		}catch(Exception e){
			logger.error("登录异常",e);
			ret = new ResponseEntityDto<EngineUserLoginDto>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		
		return ret;
	}		
	
	/**
	 * http://localhost:8080/ruleModelDesign/loginCheck?username=zhujunjie&sessionId=123456
	 * 登录校验
	 * @param operatorId
	 * @param modelType
	 * @return
	 */
	@RequestMapping(value = "/loginCheck", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<EngineUserLoginDto> loginCheck(HttpServletRequest request,@RequestParam String sessionId) {
		ResponseEntityDto<EngineUserLoginDto> ret= new ResponseEntityDto<EngineUserLoginDto>();
		try{	
			EngineAssert.checkNotBlank(sessionId,"上送参数sessionId不可为空");
			
			EngineUserLoginDto dto=engineUserService.getUserInfoBySeesionId(sessionId);
			
			ret = new ResponseEntityDto<EngineUserLoginDto>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,dto);		
		}catch(Exception e){
			logger.error("校验异常",e);
			ret = new ResponseEntityDto<EngineUserLoginDto>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;
	}	
	
	/**
	 * http://localhost:8080/ruleModelDesign/loginOut?username=zhujunjie&sessionId=123456
	 * 登录
	 * @param operatorId
	 * @param modelType
	 * @return
	 */
	@RequestMapping(value = "/loginOut", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> loginOut(HttpServletRequest request,@RequestParam String username,@RequestParam String sessionId) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{	
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(sessionId,"上送参数sessionId不可为空");
			
			engineUserService.userLoginOut(username, sessionId);
			
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("校验异常",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		
		return ret;
	}	
	
	/**
	 * http://localhost:8080/ruleModelDesign/createProduct
	 * username=taojin&productType=shoujidai
	 * 创建一个产品线
	 * @param operatorId
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/createProduct", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> createProduct(@RequestParam String username,@RequestParam String productType
			,@RequestParam String productName,@RequestParam String groupId) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			EngineAssert.checkNotBlank(productName,"上送参数productName不可为空");
			EngineAssert.checkNotBlank(groupId,"上送参数groupId不可为空");
			engineWorkspaceManager.createProduct(username,productType,productName,groupId);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("创建一个产品线异常",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		
		engineUserService.operatorLog(username, productType, "createProduct",ret);
		return ret;
	}	
	
	/**
	 * http://localhost:8080/ruleModelDesign/showProductList?username=taojin
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/showProductList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineProductDto>> showProductList(@RequestParam String username) {
		ResponseEntityDto<List<EngineProductDto>> ret= new ResponseEntityDto<List<EngineProductDto>>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			List<EngineProductDto> productList = engineWorkspaceManager.showProductList(username);
			ret = new ResponseEntityDto<List<EngineProductDto>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,productList);		
		}catch(Exception e){
			logger.error("显示产品列表异常",e);
			ret = new ResponseEntityDto<List<EngineProductDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}	
	
	/**
	 * http://localhost:8080/ruleModelDesign/showProductListByGroup?groupId=1
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/showProductListByGroup", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineProductDto>> showProductListByGroup(@RequestParam String groupId) {
		ResponseEntityDto<List<EngineProductDto>> ret= new ResponseEntityDto<List<EngineProductDto>>();
		try{
			EngineAssert.checkNotBlank(groupId,"上送参数groupId不可为空");
			List<EngineProductDto> productList = engineWorkspaceManager.showProductListByGroup(groupId);
			ret = new ResponseEntityDto<List<EngineProductDto>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,productList);		
		}catch(Exception e){
			logger.error("显示产品列表异常",e);
			ret = new ResponseEntityDto<List<EngineProductDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}
		
	/**
	 * http://localhost:8080/ruleModelDesign/createGroup
	 * username=taojin&productType=shoujidai
	 * 创建一个分组
	 * @param operatorId
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/createGroup", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> createGroup(@RequestParam String username,@RequestParam String groupType,@RequestParam String groupName) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(groupType,"上送参数groupType不可为空");
			EngineAssert.checkNotBlank(groupName,"上送参数groupName不可为空");
			engineWorkspaceManager.createGroup(username,groupType,groupName);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("创建一个产品线异常",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		
		engineUserService.operatorLog(username, groupType, "createGroup",ret);
		return ret;
	}
	
	/**
	 * http://localhost:8080/ruleModelDesign/downloadQueryModel?username=taojin&productType=taojinZy2
	 * 下发查询组件
	 */
	@Deprecated
	@RequestMapping(value = "/downloadQueryModel", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineQueryModelDto>> downloadQueryModel(@RequestParam String username,@RequestParam String productType) {
		ResponseEntityDto<List<EngineQueryModelDto>> ret= new ResponseEntityDto<List<EngineQueryModelDto>>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			List<EngineQueryModelDto> list = engineConfigManageService.getQueryModelList(username,productType);
			ret = new ResponseEntityDto<List<EngineQueryModelDto>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,null);
			ret.setData(list);
		}catch(Exception e){
			logger.error("下发查询项异常",e);
			ret = new ResponseEntityDto<List<EngineQueryModelDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;
	}	
	
	/**
	 * http://localhost:8080/ruleModelDesign/showCpntList?username=taojin&productType=shoujidai&cpntType=Step
	 * 
	 * 下发规则模型对象
	 * @return
	 */
	@RequestMapping(value = "/showCpntList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineCpntListDisplay>> showCpntList(@RequestParam String username,@RequestParam String productType,@RequestParam String cpntType) {
		ResponseEntityDto<List<EngineCpntListDisplay>> ret= new ResponseEntityDto<List<EngineCpntListDisplay>>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");

			List<EngineCpntListDisplay> list = engineWorkspaceManager.showCpntList(username, productType, cpntType, RuleModelTable.SELF, null);
	
			logger.info("下发规则模型对象数量:{}",list.size());		
			ret = new ResponseEntityDto<List<EngineCpntListDisplay>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,list);	
		}catch(Exception e){
			logger.error("下发规则模型对象列表异常",e);
			ret = new ResponseEntityDto<List<EngineCpntListDisplay>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
			
		return ret;
	}

	/**
	 * http://localhost:8080/ruleModelDesign/showMergeCpntList?username=taojin&productType=shoujidai&cpntType=Step
	 * 
	 * 下发规则模型对象
	 * @return
	 */
	@RequestMapping(value = "/showMergeCpntList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineCpntListDisplay>> showMergeCpntList(@RequestParam String username,@RequestParam String productType,
			@RequestParam String cpntType) {
		ResponseEntityDto<List<EngineCpntListDisplay>> ret= new ResponseEntityDto<List<EngineCpntListDisplay>>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");

			List<EngineCpntListDisplay> list = engineWorkspaceManager.showCpntList(username, productType, cpntType, RuleModelTable.MERGE, null);
			logger.info("下发规则模型对象数量:{}",list.size());		
			ret = new ResponseEntityDto<List<EngineCpntListDisplay>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,list);	
		}catch(Exception e){
			logger.error("下发规则模型对象列表异常",e);
			ret = new ResponseEntityDto<List<EngineCpntListDisplay>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
			
		return ret;
	}	
	
	/**
	 * http://localhost:8080/ruleModelDesign/showHistoryCpntList?username=taojin&productType=shoujidai&cpntType=Step
	 * 
	 * 下发规则模型对象
	 * @return
	 */
	@RequestMapping(value = "/showHistoryCpntList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineCpntListDisplay>> showHistoryCpntList(@RequestParam String username,@RequestParam String productType,
			@RequestParam String cpntType,@RequestParam String version) {
		ResponseEntityDto<List<EngineCpntListDisplay>> ret= new ResponseEntityDto<List<EngineCpntListDisplay>>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");

			List<EngineCpntListDisplay> list = engineWorkspaceManager.showCpntList(username, productType, cpntType, RuleModelTable.RELEASE, version);
	
			logger.info("下发规则模型对象数量:{}",list.size());		
			ret = new ResponseEntityDto<List<EngineCpntListDisplay>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,list);	
		}catch(Exception e){
			logger.error("下发规则模型对象列表异常",e);
			ret = new ResponseEntityDto<List<EngineCpntListDisplay>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
			
		return ret;
	}	
	
	/**
	 * http://localhost:8080/ruleModelDesign/showRouteStageList?username=taojin&productType=shoujidai
	 * 
	 * 下发stage列表（产品级）
	 * @return
	 */
	@RequestMapping(value = "/showRouteStageList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineCpntListDisplay>> showRouteStageList(@RequestParam String username,@RequestParam String productType) {
		ResponseEntityDto<List<EngineCpntListDisplay>> ret= new ResponseEntityDto<List<EngineCpntListDisplay>>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");

			List<EngineCpntListDisplay> list = engineWorkspaceManager.showRouteStageList(username, productType);
	
			logger.info("下发STAGE数量:{}",list.size());		
			ret = new ResponseEntityDto<List<EngineCpntListDisplay>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,list);	
		}catch(Exception e){
			logger.error("下发STAGE对象异常",e);
			ret = new ResponseEntityDto<List<EngineCpntListDisplay>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;
	}
	
	
	
	/**
	 * http://localhost:8080/ruleModelDesign/insertRuleModel
	 * username=taojin&productType=shoujidai&json={'id':'dqdZr1ScriptStep','type':'EngineScriptStep','right':'taojin','name':'脚本处理1','updatetime':'20160801125321','script':'_result.phone = 13524366905;'}
	 * username=taojin&productType=shoujidai&json={'id':'dqdZr1ScriptStep','type':'EngineScriptStep','right':'taojin','name':'脚本处理1','updatetime':'20160801125321','script':'_result.phone = 13524366905;'}
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/insertRuleModel", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<String> insertRuleModel(@RequestParam String username,@RequestParam String productType,@RequestParam String json) throws Exception {						
		ResponseEntityDto<String> ret;
		try{		
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			EngineAssert.checkNotBlank(json,"上送参数json不可为空");
					
			logger.info("对象模型str:{}",json);
			JSONObject jsonObject = JSONObject.parseObject(json);			
			String cpntId = engineWorkspaceManager.insertRuleModel(username, productType, jsonObject);
			ret = new ResponseEntityDto<String>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,cpntId);
		}catch(Exception e){
			logger.error("新增规则模型对象失败",e);
			ret = new ResponseEntityDto<String>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		engineUserService.operatorLog(username, productType, "insertRuleModel",ret);
		return ret;
	}	
	
	/**
	 * http://localhost:8080/ruleModelDesign/insertRuleModel
	 * username=taojin&productType=shoujidai&json={'id':'dqdZr1ScriptStep','type':'EngineScriptStep','right':'taojin','name':'脚本处理1','updatetime':'20160801125321','script':'_result.phone = 13524366905;'}
	 * username=taojin&productType=shoujidai&json={'id':'dqdZr1ScriptStep','type':'EngineScriptStep','right':'taojin','name':'脚本处理1','updatetime':'20160801125321','script':'_result.phone = 13524366905;'}
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/updateRuleModel", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<?> updateRuleModel(@RequestParam String username,@RequestParam String productType,@RequestParam String json) throws Exception {						
		ResponseEntityDto<?> ret;
		try{		
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			EngineAssert.checkNotBlank(json,"上送参数json不可为空");
					
			logger.info("对象模型str:{}",json);
			JSONObject jsonObject = JSONObject.parseObject(json);			
			engineWorkspaceManager.updateRuleModel(username, productType, jsonObject);
			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,null);
		}catch(Exception e){
			logger.error("更新规则模型对象失败",e);
			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		engineUserService.operatorLog(username, productType, "updateRuleModel",ret);
		return ret;
	}	
	
	
	/**
	 * http://localhost:8080/ruleModelDesign/setRuleModelFlow
	 * username=taojin&productType=shoujidai&json={'id':'dqdZr1ScriptStep','type':'EngineScriptStep','right':'taojin','name':'脚本处理1','updatetime':'20160801125321','script':'_result.phone = 13524366905;'}
	 * username=taojin&productType=shoujidai&json={'id':'dqdZr1ScriptStep','type':'EngineScriptStep','right':'taojin','name':'脚本处理1','updatetime':'20160801125321','script':'_result.phone = 13524366905;'}
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/setRuleModelFlow", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<?> setRuleModelFlow(@RequestParam String username,@RequestParam String productType,@RequestParam String json) throws Exception {						
		ResponseEntityDto<?> ret;
		try{		
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			EngineAssert.checkNotBlank(json,"上送参数json不可为空");
					
			logger.info("对象模型str:{}",json);
			JSONObject jsonObject = JSONObject.parseObject(json);			
			String cpntId = engineWorkspaceManager.setRuleModelFlow(username, productType, jsonObject);
			ret = new ResponseEntityDto<String>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,cpntId);
		}catch(Exception e){
			logger.error("设置规则流",e);
			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		engineUserService.operatorLog(username, productType, "setRuleModelFlow",ret);
		return ret;
	}	
	
	/**
	 * http://localhost:8080/ruleModelDesign/removeRuleModel
	 * username=taojin&productType=shoujidai&cpntId=1
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/removeRuleModel", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<?> removeRuleModel(@RequestParam String username,@RequestParam String productType,@RequestParam String cpntId) throws Exception {						
		ResponseEntityDto<?> ret;
		try{		
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			EngineAssert.checkNotBlank(cpntId,"上送参数cpntId不可为空");
							
			engineWorkspaceManager.removeRuleModel(username, productType,cpntId);
			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,null);
		}catch(Exception e){
			logger.error("删除规则模型对象失败",e);
			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		engineUserService.operatorLog(username, productType, "removeRuleModel",ret);
		return ret;
	}		
	
	
	/**
	 * http://localhost:8080/ruleModelDesign/showOneRuleModel?username=taojin&productType=shoujidai&cpntId=dqdZr1ScriptStep 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/showOneRuleModel", method=RequestMethod.GET)
	@ResponseBody	
	public ResponseEntityDto<JSONObject> showOneRuleModel(@RequestParam String username,@RequestParam String productType,@RequestParam String cpntId) throws Exception {						
		ResponseEntityDto<JSONObject> ret;
		try{		
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			EngineAssert.checkNotBlank(cpntId,"上送参数cpntId不可为空");
								
			ret = new ResponseEntityDto<JSONObject>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,
					engineWorkspaceManager.showOneRuleModel(username, productType, cpntId));
		}catch(Exception e){
			logger.error("显示模型对象失败",e);
			ret = new ResponseEntityDto<JSONObject>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;
	}	
	
	/**
	 * http://localhost:8080/ruleModelDesign/showMergeRuleModel?username=taojin&productType=shoujidai&cpntId=dqdZr1ScriptStep 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/showMergeRuleModel", method=RequestMethod.GET)
	@ResponseBody	
	public ResponseEntityDto<JSONObject> showMergeRuleModel(@RequestParam String username,@RequestParam String productType,@RequestParam String cpntId) throws Exception {						
		ResponseEntityDto<JSONObject> ret;
		try{		
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			EngineAssert.checkNotBlank(cpntId,"上送参数cpntId不可为空");
								
			ret = new ResponseEntityDto<JSONObject>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,
					engineWorkspaceManager.showMergeRuleModel(username, productType, cpntId));
		}catch(Exception e){
			logger.error("显示模型对象失败",e);
			ret = new ResponseEntityDto<JSONObject>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;
	}		
	
	/**
	 * http://localhost:8080/ruleModelDesign/showHistoryRuleModel?
	 * username=taojin&productType=leadDemo&cpntId=b601031a-8876-4b56-803b-19f260267d13&version=20161118154826
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/showHistoryRuleModel", method=RequestMethod.GET)
	@ResponseBody	
	public ResponseEntityDto<JSONObject> showHistoryRuleModel(@RequestParam String username,@RequestParam String productType,
			@RequestParam String cpntId,@RequestParam String version) throws Exception {						
		ResponseEntityDto<JSONObject> ret;
		try{		
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			EngineAssert.checkNotBlank(cpntId,"上送参数cpntId不可为空");
			EngineAssert.checkNotBlank(version,"上送参数version不可为空");
								
			ret = new ResponseEntityDto<JSONObject>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,
					engineWorkspaceManager.showHistoryRuleModel(username, productType, cpntId,version));
		}catch(Exception e){
			logger.error("显示模型对象失败",e);
			ret = new ResponseEntityDto<JSONObject>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;
	}	
		
	/**
	 * http://localhost:8080/ruleModelDesign/showHistoryPolicyFlowModel?
	 * username=taojin&releaseFlowId=245
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/showHistoryPolicyFlowModel", method=RequestMethod.GET)
	@ResponseBody	
	public ResponseEntityDto<JSONObject> showHistoryPolicyFlowModel(@RequestParam String username,
			@RequestParam Long releaseFlowId) throws Exception {						
		ResponseEntityDto<JSONObject> ret;
		try{		
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(releaseFlowId,"上送参数releaseFlowId不可为空");
								
			ret = new ResponseEntityDto<JSONObject>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,
					engineWorkspaceManager.showHistoryPolicyFlowModel(username, releaseFlowId));
		}catch(Exception e){
			logger.error("显示模型对象失败",e);
			ret = new ResponseEntityDto<JSONObject>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;
	}	
	
	
	/**
	 * http://localhost:8080/ruleModelDesign/mergeRuleModel
	 * username=taojin&productType=shoujidai
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/mergeRuleModel", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<?> mergeRuleModel(@RequestParam String username,@RequestParam String productType) throws Exception {						
		ResponseEntityDto<?> ret;
		try{		
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");			
			engineWorkspaceManager.mergeRuleModel(username, productType);
			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,null);
		}catch(Exception e){
			logger.error("上传规则模型对象失败",e);
			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		engineUserService.operatorLog(username, productType, "mergeRuleModel",ret);
		return ret;
	}	
	

	
	/**
	 * 显示整个决策树
	 * http://localhost:8080/ruleModelDesign/showEngineFlow?username=taojin&productType=shoujidai
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/showEngineFlow", method=RequestMethod.GET)
	@ResponseBody	
	public ResponseEntityDto<JSONObject> showEngineFlow(@RequestParam String username,@RequestParam String productType) throws Exception {						
		ResponseEntityDto<JSONObject> ret;
		try{		
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");			
			JSONObject dto = engineWorkspaceManager.showEngineFlow(username,productType);
			ret = new ResponseEntityDto<JSONObject>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,dto);
		}catch(Exception e){
			logger.error("显示整个决策树异常",e);
			ret = new ResponseEntityDto<JSONObject>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;
	}		
	
	
	/**
	 * 显示整个决策树
	 * http://localhost:8080/ruleModelDesign/showEngineFlow?username=taojin&productType=shoujidai
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/showEngineFlowByVersion", method=RequestMethod.GET)
	@ResponseBody	
	public ResponseEntityDto<JSONObject> showEngineFlowByVersion(@RequestParam String username,@RequestParam String productType,String version) throws Exception {						
		ResponseEntityDto<JSONObject> ret;
		try{		
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");			
			JSONObject dto = engineWorkspaceManager.showEngineFlowByVersion(username, productType, version);
			ret = new ResponseEntityDto<JSONObject>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,dto);
		}catch(Exception e){
			logger.error("显示整个决策树异常",e);
			ret = new ResponseEntityDto<JSONObject>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;
	}	
	
	/**
	 * 检查某个组件的结构
	 * http://localhost:8080/ruleModelDesign/checkScriptSyntax
	 * username=taojin&productType=shoujidai&script=???
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/checkScriptSyntax", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<List<EngineScriptSyntaxErr>> checkScriptSyntax(@RequestParam String username,
			@RequestParam String productType,@RequestParam String script) throws Exception {						
		ResponseEntityDto<List<EngineScriptSyntaxErr>> ret;
		try{		
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");			
			EngineAssert.checkNotBlank(script,"上送参数script不可为空");
						
			ret = new ResponseEntityDto<List<EngineScriptSyntaxErr>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,
					engineWorkspaceManager.checkScriptSyntax(productType, script));
		}catch(Exception e){
			logger.error("检查脚本失败",e);
			ret = new ResponseEntityDto<List<EngineScriptSyntaxErr>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}

		return ret;
	}	
	
	/**
	 * http://localhost:8080/ruleModelDesign/copyCpnt
	 * username=liangxiaojing&fromProduct=leadDemo&toProduct=shoujidai&cpntIds=xxxx,xxxx,xxx
	 * 
	 * @param username
	 * @param fromProduct
	 * @param toProduct
	 * @param toUser
	 * @param cpntIds 以逗号分隔
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/copyCpnt", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<List<EngineCpntListDisplay>> copyCpnt(
			@RequestParam String username,
			@RequestParam String fromProduct,
			@RequestParam String toProduct,
			@RequestParam String cpntIds) throws Exception {						
		ResponseEntityDto<List<EngineCpntListDisplay>> ret;
		try{		
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(fromProduct,"上送参数fromProduct不可为空");			
			EngineAssert.checkNotBlank(toProduct,"上送参数toProduct不可为空");		
			EngineAssert.checkNotBlank(cpntIds,"上送参数cpntIds不可为空");
			String[] cpntIdArr = StringUtils.split(cpntIds, ',');
			
			ret = new ResponseEntityDto<List<EngineCpntListDisplay>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,
					engineWorkspaceManager.copyCpnt(username, fromProduct, toProduct, cpntIdArr));
		}catch(Exception e){
			logger.error("检查脚本失败",e);
			ret = new ResponseEntityDto<List<EngineCpntListDisplay>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}

		engineUserService.operatorLog(username, fromProduct, "copyCpnt",ret);
		return ret;
	}		
	
	/**
	 * 获取引擎参数列表
	 * http://localhost:8080/ruleModelDesign/getRelaDefineField?username=taojin&productType=shoujidai&cpntId=fecf1539-2064-434c-8038-c45fbf4f65a1
	 * @param username
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/getRelaDefineField", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineDefineFieldDto>> getRelaDefineField(@RequestParam String username,
			@RequestParam String productType,@RequestParam String cpntId) {
		ResponseEntityDto<List<EngineDefineFieldDto>> ret= new ResponseEntityDto<List<EngineDefineFieldDto>>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数cpntId不可为空");
			List<EngineDefineFieldDto> list = engineWorkspaceManager.getRelaDefineField(username, productType, cpntId);
			ret = new ResponseEntityDto<List<EngineDefineFieldDto>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,list);		
		}catch(Exception e){
			logger.error("查询关联字段异常",e);
			ret = new ResponseEntityDto<List<EngineDefineFieldDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}	
	
	
	/**
	 * 获取当前版本和某个版本的比较信息
	 * http://localhost:8080/ruleModelDesign/compareCpntByCurrentAndVersion?username=liangxiaojing&productType=leadDemo&version=20161116130838
	 * @param username
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/compareCpntByCurrentAndVersion", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineCpntCompareGroupDto>> compareCpntByCurrentAndVersion(@RequestParam String username,
			@RequestParam String productType,@RequestParam String version) {
		ResponseEntityDto<List<EngineCpntCompareGroupDto>> ret= new ResponseEntityDto<List<EngineCpntCompareGroupDto>>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			List<EngineCpntCompareGroupDto> list = engineWorkspaceManager.compareCpntByCurrentAndVersion(productType, username, version);
			ret = new ResponseEntityDto<List<EngineCpntCompareGroupDto>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,list);		
		}catch(Exception e){
			logger.error("查询关联字段异常",e);
			ret = new ResponseEntityDto<List<EngineCpntCompareGroupDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}	
	
	/**
	 * 获取当前版本和生产版本的比较信息
	 * http://localhost:8080/ruleModelDesign/compareCpntByCurrentAndProd?username=liangxiaojing&productType=leadDemo
	 * @param username
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/compareCpntByCurrentAndProd", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineCpntCompareGroupDto>> compareCpntByCurrentAndProd(@RequestParam String username,
			@RequestParam String productType) {
		ResponseEntityDto<List<EngineCpntCompareGroupDto>> ret= new ResponseEntityDto<List<EngineCpntCompareGroupDto>>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			List<EngineCpntCompareGroupDto> list = engineWorkspaceManager.compareCpntByCurrentAndProd(productType, username);
			ret = new ResponseEntityDto<List<EngineCpntCompareGroupDto>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,list);		
		}catch(Exception e){
			logger.error("查询关联字段异常",e);
			ret = new ResponseEntityDto<List<EngineCpntCompareGroupDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		return ret;
	}	
	
	/**
	 * 获取当前版本和生产版本的比较信息
	 * http://localhost:8080/ruleModelDesign/compareCpntByVersionAndVersion
	 * @param username
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/compareCpntByVersionAndProd", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineCpntCompareGroupDto>> compareCpntByVersionAndProd(@RequestParam String username,
			@RequestParam String productType,@RequestParam String newVersion) {
		ResponseEntityDto<List<EngineCpntCompareGroupDto>> ret= new ResponseEntityDto<List<EngineCpntCompareGroupDto>>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			EngineAssert.checkNotBlank(newVersion,"上送参数newVersion不可为空");
			
			
			List<EngineCpntCompareGroupDto> list = engineWorkspaceManager.compareCpntByVersionAndProd(productType, username, newVersion);
			ret = new ResponseEntityDto<List<EngineCpntCompareGroupDto>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,list);		
		}catch(Exception e){
			logger.error("查询关联字段异常",e);
			ret = new ResponseEntityDto<List<EngineCpntCompareGroupDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		return ret;
	}	
	
	
	
	/**
	 * 用户解锁工程模块
	 * http://localhost:8080/ruleModelDesign/userUnlockProduct
	 * username=taojin
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/userUnlockProduct", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<?> userUnlockProduct(@RequestParam String username) throws Exception {						
		ResponseEntityDto<?> ret;
		try{		
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");			
			engineWorkspaceManager.userUnlockProduct(username);
			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,null);
		}catch(Exception e){
			logger.error("用户解锁工程模块失败",e);
			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}		
		return ret;
	}	
	
	/**
	 * http://localhost:8080/ruleModelDesign/save1
	 * @return
	 */
	@RequestMapping("/save1")
	public ModelAndView save1() {
		ModelAndView mav = new ModelAndView("save1");
		return mav;
	}

	@RequestMapping("/save2")
	public ModelAndView save2() {
		ModelAndView mav = new ModelAndView("save2");
		return mav;
	}
	
	
	
}
