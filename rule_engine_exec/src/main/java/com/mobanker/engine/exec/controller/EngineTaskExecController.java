package com.mobanker.engine.exec.controller;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.common.EngineConst;
import com.mobanker.engine.exec.business.invoke.EngineSynInvokeEntry;
import com.mobanker.engine.exec.business.invoke.EngineSynInvokeType;
import com.mobanker.engine.framkwork.test.EngineExecMode;
import com.mobanker.engine.rpc.dto.EngineCallDto;
import com.mobanker.engine.rpc.dto.EngineHttpDto;
import com.mobanker.framework.contract.dto.ResponseEntityDto;

/**
 * 规则模型设计交互
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月4日
 * @version 1.0
 */
@Controller
@RequestMapping("taskExec")
public class EngineTaskExecController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	//@Autowired
	//private EngineTaskAssignSender engineTaskAssignSender;
	
	@Autowired
	private EngineSynInvokeEntry engineSynInvokeEntry;
	
	/**
	 * http://192.168.1.167:8081/taskExec/testSender?count=5
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/testSender", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<?> testSender(int count) {	
		logger.info("任务分配测试发送");
		for(long i=0;i<count;i++){
			sendOne(i);
		}
		return new ResponseEntityDto<>();
	}	
	
	
	private void sendOne(long taskId){
//		EngineTaskInfo task = new EngineTaskInfo();
//		task.setId(taskId);
//		task.setAppName("testApp");
//		task.setAppParams("{userId:'123132',borrowNid:'P123123125234'}");
//		task.setProductType("shoujidai");
//		task.setAppRequestId("T"+taskId);
//		task.setStatus("0");
//		task.setAssignStep(1);
//		//logger.info("测试发送"+task);		
//		engineTaskAssignSender.send(task);
	}
	
	/**
	 * http://192.168.1.167:8081/taskExec/rpcInvoke
	 * productType=shoujidai&appName=test&appParams={'userId':'52343','zm_score':711,is_zm_auth:1,'zm_score_time':'20161031121212'}
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/rpcInvoke", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Map<String,Object>> rpcInvoke(@ModelAttribute EngineHttpDto dto) {	
		ResponseEntityDto<Map<String,Object>> ret= new ResponseEntityDto<Map<String,Object>>();
		try{
			
			logger.info("远程调用开始，dto:{}",dto);			
			
			EngineCallDto invo = new EngineCallDto();
			invo.setAppName(dto.getAppName());
			invo.setProductType(dto.getProductType());
			invo.setAppParams(JSONObject.parseObject(dto.getAppParams()));
			invo.setExecMode(EngineExecMode.NO_TEST.getValue());
			
			ret = new ResponseEntityDto<Map<String,Object>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,
					EngineConst.RET_MSG.OK,engineSynInvokeEntry.invoke(EngineSynInvokeType.HTTP, invo));
		}catch(Exception e){
			logger.error("远程调用字段异常",e);
			ret = new ResponseEntityDto<Map<String,Object>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}		
}
