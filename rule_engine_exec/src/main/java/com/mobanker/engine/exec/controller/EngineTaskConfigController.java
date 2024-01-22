package com.mobanker.engine.exec.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mobanker.engine.common.EngineConst;
import com.mobanker.engine.exec.business.snapshot.EngineParamSnapshotService;
import com.mobanker.engine.exec.business.zk.taskassign.EngineTaskExecSwitchWatcher;
import com.mobanker.framework.contract.dto.ResponseEntityDto;

/**
 * 规则模型设计交互
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月4日
 * @version 1.0
 */
@Controller
@RequestMapping("taskConfig")
public class EngineTaskConfigController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	@Autowired
	private EngineTaskExecSwitchWatcher engineTaskExecSwitchWatcher;
	
	@Autowired
	private EngineParamSnapshotService engineParamSnapshotService;
	
	/**
	 * http://192.168.1.167:8081/taskConfig/execSwitchOpen?productType=taojinZy2
	 *
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/execSwitchOpen", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> execSwitchOpen(@RequestParam String productType) {	
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{			
			logger.warn("开启产品线:{}",productType);			
			engineTaskExecSwitchWatcher.open(productType);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,
					EngineConst.RET_MSG.OK,true);
		}catch(Exception e){
			logger.error("远程调用字段异常",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		return ret;
	}	
	
	/**
	 * http://192.168.1.167:8081/taskConfig/execSwitchClose?productType=taojinZy2
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/execSwitchClose", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> execSwitchClose(@RequestParam String productType) {	
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			
			logger.warn("关闭产品线:{}",productType);			
			engineTaskExecSwitchWatcher.close(productType);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,
					EngineConst.RET_MSG.OK,true);
		}catch(Exception e){
			logger.error("远程调用字段异常",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		return ret;
	}		
	
	
	/**
	 * http://192.168.1.167:8081/taskConfig/startConsume
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/startConsume", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> startConsume() {	
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{			
			engineParamSnapshotService.startConsume();
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,
					EngineConst.RET_MSG.OK,true);
		}catch(Exception e){
			logger.error("远程调用字段异常",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		return ret;
	}		
}
