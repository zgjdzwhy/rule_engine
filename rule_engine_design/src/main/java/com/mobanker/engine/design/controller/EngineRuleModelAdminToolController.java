package com.mobanker.engine.design.controller;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mobanker.engine.common.EngineConst;
import com.mobanker.engine.design.busi.admin.AdminToolService;
import com.mobanker.engine.design.dto.EngineDefineFieldDto;
import com.mobanker.framework.contract.dto.ResponseEntityDto;

/**
 * 规则模型配置组件管理
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月4日
 * @version 1.0
 */
@Controller
@RequestMapping("/ruleModelAdminTool")
public class EngineRuleModelAdminToolController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());	

	@Autowired
	private AdminToolService adminToolService;
	

	
	/**
	 * 获取引擎参数列表
	 * http://localhost:8080/ruleModelAdminTool/ruleModelMongo2Es
	 * @param username
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/ruleModelMongo2Es", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<?> getEngineParamList() {
		ResponseEntityDto<?> ret= new ResponseEntityDto<List<EngineDefineFieldDto>>();
		try{			
			adminToolService.ruleModelMongo2Es();
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("转移异常",e);
			ret = new ResponseEntityDto<List<EngineDefineFieldDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}	
	
	
	/**
	 * 获取引擎参数列表
	 * http://localhost:8080/ruleModelAdminTool/initSystemFunction
	 * @param username
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/initSystemFunction", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<?> initSystemFunction() {
		ResponseEntityDto<?> ret= new ResponseEntityDto<List<EngineDefineFieldDto>>();
		try{			
			adminToolService.initSystemFunction();
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("转移异常",e);
			ret = new ResponseEntityDto<List<EngineDefineFieldDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}		
	
	
	@RequestMapping(value = "/ruleModelEs2Mysql", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<?> ruleModelEs2Mysql() {
		ResponseEntityDto<?> ret= new ResponseEntityDto<List<EngineDefineFieldDto>>();
		try{			
			adminToolService.ruleModelEs2Mysql();
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("转移异常",e);
			ret = new ResponseEntityDto<List<EngineDefineFieldDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}	
	
	@RequestMapping(value = "/functionEs2Hbase", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<?> functionEs2Hbase() {
		ResponseEntityDto<?> ret= new ResponseEntityDto<List<EngineDefineFieldDto>>();
		try{			
			adminToolService.functionEs2Hbase();
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("转移异常",e);
			ret = new ResponseEntityDto<List<EngineDefineFieldDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}	
}
