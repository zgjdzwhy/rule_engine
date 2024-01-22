package com.mobanker.engine.analy.controller;


import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.mobanker.engine.exec.business.tasktrace.EngineTaskBatchRun;
import com.mobanker.engine.exec.business.tasktrace.EngineTaskTraceService;
import com.mobanker.engine.exec.pojo.EngineTaskQco;
import com.mobanker.engine.exec.pojo.snapshot.EngineSnapshotTotalCompareDto;
import com.mobanker.engine.framkwork.data.entity.EngineStepInfo;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.framework.contract.dto.ResponseEntityDto;

/**
 * 规则模型设计交互
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月4日
 * @version 1.0
 */
@Controller
@RequestMapping("taskAnaly")
public class EngineTaskAnalyController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	@Autowired
	private EngineTaskTraceService engineTaskTraceService;
	
	@Autowired
	private EngineTaskBatchRun engineTaskBatchRun;
	
	/**
	 * http://192.168.1.167:8091/taskAnaly/queryTaskInfo?productType=leadDemo&beginTime=2016-11-13 2013:49:59&endTime=2016-11-13 2013:49:59
	 * 
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/queryTaskInfo", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineTaskInfo>> queryTaskInfo(@ModelAttribute EngineTaskQco qco) {	
		ResponseEntityDto<List<EngineTaskInfo>> ret= new ResponseEntityDto<List<EngineTaskInfo>>();
		try{
			
			List<EngineTaskInfo> list = engineTaskTraceService.queryTaskInfo(qco);
			ret = new ResponseEntityDto<List<EngineTaskInfo>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,
					EngineConst.RET_MSG.OK,list);
		}catch(Exception e){
			logger.error("查询任务信息失败",e);
			ret = new ResponseEntityDto<List<EngineTaskInfo>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}	
		return ret;
	}		
	
	/**
	 * http://192.168.1.167:8091/taskAnaly/queryStepInfo?taskId=140
	 * 
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/queryStepInfo", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineStepInfo>> queryStepInfo(@RequestParam Long taskId) {	
		ResponseEntityDto<List<EngineStepInfo>> ret= new ResponseEntityDto<List<EngineStepInfo>>();
		try{
			
			List<EngineStepInfo> list = engineTaskTraceService.queryStepInfo(taskId);
			ret = new ResponseEntityDto<List<EngineStepInfo>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,
					EngineConst.RET_MSG.OK,list);
		}catch(Exception e){
			logger.error("查询任务信息失败",e);
			ret = new ResponseEntityDto<List<EngineStepInfo>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}	
		return ret;
	}			
	
	/**
	 * 

	 * 
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/queryStepInVar", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Map<String,Object>> queryStepInVar(@RequestParam String productType,@RequestParam String appRequestId,@RequestParam Long stepId) {	
		ResponseEntityDto<Map<String,Object>> ret= new ResponseEntityDto<Map<String,Object>>();
		try{			
			Map<String,Object> json = engineTaskTraceService.queryStepInVar(productType, appRequestId, stepId);
			ret = new ResponseEntityDto<Map<String,Object>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,
					EngineConst.RET_MSG.OK,json);
		}catch(Exception e){
			logger.error("查询任务信息失败",e);
			ret = new ResponseEntityDto<Map<String,Object>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}	
		return ret;
	}		
	/**
	 * http://192.168.1.167:8091/taskAnaly/queryStepOutVar?productType=leadDemo&stepId=8785&appRequestId=201611131349149
	 * 
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/queryStepOutVar", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Map<String,Object>> queryStepOutVar(@RequestParam String productType,@RequestParam String appRequestId,@RequestParam Long stepId) {	
		ResponseEntityDto<Map<String,Object>> ret= new ResponseEntityDto<Map<String,Object>>();
		try{			
			Map<String,Object> json = engineTaskTraceService.queryStepOutVar(productType, appRequestId, stepId);
			ret = new ResponseEntityDto<Map<String,Object>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,
					EngineConst.RET_MSG.OK,json);
		}catch(Exception e){
			logger.error("查询任务信息失败",e);
			ret = new ResponseEntityDto<Map<String,Object>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}	
		return ret;
	}		
	
	/**
	 * http://192.168.1.167:8091/taskAnaly/displayFieldAnalyChart?productType=taojinZy&fieldKey=test&analyType=EngineChartAnalyGroupSum
	 * @param productType
	 * @param fieldKey
	 * @param analyType
	 * @return
	 */
	@RequestMapping(value = "/displayFieldAnalyChart", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<EngineSnapshotTotalCompareDto> displayFieldAnalyChart(@RequestParam String productType,
			@RequestParam String fieldKey,@RequestParam String analyType) {	
		ResponseEntityDto<EngineSnapshotTotalCompareDto> ret= new ResponseEntityDto<EngineSnapshotTotalCompareDto>();
		try{
			EngineSnapshotTotalCompareDto dto = engineTaskBatchRun.displayFieldChartResult(productType, fieldKey, analyType);
			ret = new ResponseEntityDto<EngineSnapshotTotalCompareDto>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,
					EngineConst.RET_MSG.OK,dto);
		}catch(Exception e){
			logger.error("查询任务信息失败",e);
			ret = new ResponseEntityDto<EngineSnapshotTotalCompareDto>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}	
		return ret;
	}	
	
	/**
	 * http://192.168.1.167:8091/taskAnaly/getCustomAnalyType?productType=taojinZy&fieldKey=test
	 * @param productType
	 * @param fieldKey
	 * @param analyType
	 * @return
	 */
	@RequestMapping(value = "/getCustomAnalyType", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<String> getCustomAnalyType(@RequestParam String productType,
			@RequestParam String fieldKey) {	
		ResponseEntityDto<String> ret= new ResponseEntityDto<String>();
		try{
			String result = engineTaskBatchRun.getCustomAnalyType(productType,fieldKey);
			ret = new ResponseEntityDto<String>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,
					EngineConst.RET_MSG.OK,result);
		}catch(Exception e){
			logger.error("查询任务信息失败",e);
			ret = new ResponseEntityDto<String>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}	
		return ret;
	}		
	
	/**
	 * http://192.168.1.167:8091/taskAnaly/clearLastBatchRunInfo
	 * productType=taojinZy4
	 * 
	 * @param productType
	 * @param fieldKey
	 * @param analyType
	 * @return
	 */
	@RequestMapping(value = "/clearLastBatchRunInfo", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> clearLastBatchRunInfo(@RequestParam String productType) {	
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			engineTaskBatchRun.clearLastBatchRunInfo(productType);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,
					EngineConst.RET_MSG.OK,true);
		}catch(Exception e){
			logger.error("删除结果缓存信息失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}	
		return ret;
	}		
	
	
	/**
	 * http://192.168.1.167:8091/taskAnaly/showOutputFields?productType=taojinZy
	 * @param productType
	 * @param fieldKey
	 * @param analyType
	 * @return
	 */
	@RequestMapping(value = "/showOutputFields", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Set<String>> showOutputFields(@RequestParam String productType) {	
		ResponseEntityDto<Set<String>> ret= new ResponseEntityDto<Set<String>>();
		try{
			Set<String> result = engineTaskBatchRun.showOutputFields(productType);
			ret = new ResponseEntityDto<Set<String>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,
					EngineConst.RET_MSG.OK,result);
		}catch(Exception e){
			logger.error("查询任务信息失败",e);
			ret = new ResponseEntityDto<Set<String>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}	
		return ret;
	}			
	
}
