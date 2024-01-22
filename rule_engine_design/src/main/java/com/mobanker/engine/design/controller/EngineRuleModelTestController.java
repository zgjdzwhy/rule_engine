package com.mobanker.engine.design.controller;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mobanker.engine.common.EngineConst;
import com.mobanker.engine.design.busi.admin.AdminToolService;
import com.mobanker.engine.design.busi.configma.EngineConfigManageService;
import com.mobanker.engine.design.busi.user.EngineUserService;
import com.mobanker.engine.design.busi.workspace.EngineWorkspaceManagerImpl;
import com.mobanker.engine.design.dto.EngineTestReturnDto;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.engine.inner.call.dto.SnapshotBatchRunDto;
import com.mobanker.framework.contract.dto.ResponseEntityDto;

/**
 * 测试用
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月4日
 * @version 1.0
 */
@Controller
@RequestMapping("ruleModelTest")
public class EngineRuleModelTestController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());	

	@Autowired
	private EngineWorkspaceManagerImpl engineWorkspaceManager;
	
	@Autowired
	private EngineConfigManageService engineQueryModelService;
	
	@Autowired
	private EngineUserService engineUserService;

	@Autowired
	private AdminToolService adminToolService;
	/**
	 * http://localhost:8080/ruleModelTest/urgencyRelease?username=taojin&productType=shoujidai
	 * @param username
	 * @param productType
	 * @param cpntId
	 * @param json
	 * @return
	 */
	@RequestMapping(value = "/urgencyRelease", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> urgencyRelease(@RequestParam String username,@RequestParam String productType) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();						
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotNull(productType,"上送参数productType不可为空");						
								
			engineWorkspaceManager.urgencyRelease(username, productType);
			ret.setData(true);	
		}catch(Exception e){
			logger.error("紧急发布失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		return ret;
	}		
		
	
	/**
	 * http://localhost:8080/ruleModelTest/moduleTest?
	 * username=taojin&productType=shoujidai&cpntId=520d1de8-0e73-48e9-b8b5-c3ddbdfca2f2&input={'user_flag0':1,'phone':'13524366905'}&output={'score':11}
	 * 
	 * 
	 * 测试某个组件
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/moduleTest", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineTestReturnDto>> moduleTest(@RequestParam String username,@RequestParam String productType,
			@RequestParam String cpntId,@RequestParam String input,@RequestParam String output) {
		ResponseEntityDto<List<EngineTestReturnDto>> ret= new ResponseEntityDto<List<EngineTestReturnDto>>();		
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");						
			EngineAssert.checkNotBlank(cpntId,"上送参数cpntId不可为空");
			
			List<EngineTestReturnDto> returnInfo = engineWorkspaceManager.moduleTest(username, productType, cpntId, input,output);
			ret.setData(returnInfo);
		}catch(Exception e){			
			logger.error("模块测试失败",e);
			Throwable cause = EngineUtil.getBaseCause(e);
//			StringWriter sw = new StringWriter();
//			PrintWriter pw = new PrintWriter(sw);
//			e.printStackTrace(pw);
			
			
			//ret = new ResponseEntityDto<List<EngineTestReturnDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,sw.toString(),null);
			ret = new ResponseEntityDto<List<EngineTestReturnDto>>(EngineConst.RET_STATUS.FAIL,cause.getClass().getName(),cause.getMessage(),null);							
		}	
		
		return ret;
	}	
	

	/**
	 * 
	 * http://localhost:8080/ruleModelTest/getModuleTestCustomParam?cpntId=xxxxx&productType=shoujidai
	 * 获取用户上次的测试参数数据
	 * @param productType
	 * @param cpntId
	 * @return
	 */
	@RequestMapping(value = "/getModuleTestCustomParam", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Map<String,Map<String,Object>>> getModuleTestCustomParam(@RequestParam String productType,@RequestParam String cpntId) {
		ResponseEntityDto<Map<String,Map<String,Object>>> ret= new ResponseEntityDto<Map<String,Map<String,Object>>>();						
		try{			
			EngineAssert.checkNotNull(productType,"上送参数productType不可为空");						
			EngineAssert.checkNotNull(cpntId,"上送参数cpntId不可为空");							
			Map<String,Map<String,Object>> result = engineWorkspaceManager.getModuleTestCustomParam(productType, cpntId);			
			ret = new ResponseEntityDto<Map<String,Map<String,Object>>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,result);
		}catch(Exception e){
			logger.error("获取单元测试客户习惯失败",e);
			ret = new ResponseEntityDto<Map<String,Map<String,Object>>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		return ret;
	}
	
	
	
	/**
	 * http://localhost:8080/ruleModelTest/wholeTest
	 * username=taojin&productType=shoujidai&cpntId=dqdStartStage&input={'user_flag0':1,'phone':'13524366905','company_name_check_address':'1','company_address_check_name':[1,0,1]}&output={'score':11}
	 * 
	 * 整体测试
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/wholeTest", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineTestReturnDto>> wholeTest(@RequestParam String username,@RequestParam String productType,
			@RequestParam String input,@RequestParam String output) {
		ResponseEntityDto<List<EngineTestReturnDto>> ret= new ResponseEntityDto<List<EngineTestReturnDto>>();		
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");						
			
			List<EngineTestReturnDto> returnInfo = engineWorkspaceManager.wholeTest(username, productType, input,output);
			ret = new ResponseEntityDto<List<EngineTestReturnDto>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,returnInfo);		
		}catch(Exception e){
			logger.error("整体测试失败",e);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			ret = new ResponseEntityDto<List<EngineTestReturnDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,sw.toString(),null);	
		}	
		
		return ret;
	}		
	
	/**
	 * http://localhost:8080/ruleModelTest/demoRelease
	 * 
	 * 
	 * 整体测试
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/demoRelease", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> demoRelease() {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();		
		try{
			engineWorkspaceManager.demoRelease();
			ret.setData(true);	
		}catch(Exception e){
			logger.error("整体测试失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,"demo fail",null);	
		}	
		
		return ret;
	}		
		
	/**
	 * http://localhost:8080/ruleModelTest/mongo2es
	 * 
	 * 
	 * 整体测试
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/mongo2es", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> mongo2es() {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();		
		try{
			adminToolService.ruleModelMongo2Es();
			ret.setData(true);	
		}catch(Exception e){
			logger.error("mongo转es失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,"mongo2es fail",null);	
		}	
		
		return ret;
	}		
	
	
	/**
	 * http://localhost:8080/ruleModelTest/snapshotBatchRun
	 * username=taojin&productType=shoujidai&beginTime=2012-01-13 13:45:23&endTime=2018-01-13 13:45:23
	 * 
	 * 整体测试
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/snapshotBatchRun", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> snapshotBatchRun(@ModelAttribute SnapshotBatchRunDto dto) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();		
		try{
			EngineAssert.checkNotNull(dto, "上送参数为空");
			EngineAssert.checkNotBlank(dto.getUsername(),"上送参数username不可为空");
			EngineAssert.checkNotBlank(dto.getProductType(),"上送参数productType不可为空");
			EngineAssert.checkNotBlank(dto.getBeginTime(),"上送参数beginTime不可为空");
			EngineAssert.checkNotBlank(dto.getEndTime(),"上送参数endTime不可为空");		
			engineWorkspaceManager.snapshotBatchRun(dto);
			ret.setData(true);
		}catch(Exception e){
			logger.error("快照测试失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}	
		
		return ret;
	}		
}
