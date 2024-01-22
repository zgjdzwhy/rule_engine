package com.mobanker.engine.exec.taskget.controller;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.exec.business.zk.leader.EngineClusterLeaderService;
import com.mobanker.engine.exec.pojo.EngineTaskDto;
import com.mobanker.engine.exec.rabbitmq.taskget.EngineTaskGetSender;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.framework.contract.dto.ResponseEntityDto;


@Controller
@RequestMapping("taskTest")
public class EngineTaskTestController {
	
	private Logger logger = LoggerFactory.getLogger(EngineTaskTestController.class);	

	@Autowired
	private EngineTaskGetSender engineTaskGetSender;
	
	@Autowired
	private EngineClusterLeaderService engineClusterLeaderService;
	
	@Autowired
	private ConnectionFactory connectionFactory;

	
	/**
	 * http://192.168.1.167:8090/taskTest/batchTestSendTask?count=10
	 * 通过调用Dubbo服务获取提额状态列表
	 * @return ResponseEntityDto
	 */
	@RequestMapping(value = "/batchTestSendTask", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<?> batchTestSendTask(int count,String execTime,Integer priority) {
		for(int i = 0;i<count;i++){
			EngineTaskDto engineTaskDto = new EngineTaskDto();
			engineTaskDto.setProductType("leadDemo");
			engineTaskDto.setAppName("testRca");
			engineTaskDto.setAppRequestId(EngineUtil.get14CurrentDateTime()+i);
			//engineTaskDto.setAppCallBack("http://localhost:8080/ruleModelConfig/insertDefineField");
			
			JSONObject json = new JSONObject();
			json.put("age", (int)(Math.random()*50));
			json.put("job_office", "经理");
			json.put("userId", "234344");
			json.put("home_address_city", "上海");
			json.put("income", 10000);
			json.put("shop_times", (int)(Math.random()*20));
			json.put("shop_times", 25);
			json.put("entry_times", 14);
			
			//{"company_address_check_name":[0,0,0,0,0],"company_name_check_address":1}
			engineTaskDto.setAppParams(json.toJSONString());			
			if(StringUtils.isNotBlank(execTime))
				engineTaskDto.setExecTime(execTime);			
			if(priority != null)
				engineTaskDto.setPriority(priority);
			
			engineTaskGetSender.send(engineTaskDto);
			
			logger.info("enter sendTask send:{}",i);
		}
		
		ResponseEntityDto<String> ret= new ResponseEntityDto<String>();
		ret.setData("ok");
		return ret;
	}

	/**
	 * 模拟入口
	 * http://192.168.1.167:8090/taskTest/testSendOneTask
	 * productType=shoujidai&priority=1&appName=testwxx&appParams=??
	 * @return ResponseEntityDto
	 */
	@RequestMapping(value = "/testSendOneTask", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<String> testSendOneTask(@ModelAttribute EngineTaskDto engineTaskDto) {		
		engineTaskDto.setAppRequestId(EngineUtil.get14CurrentDateTime()+new java.util.Random().nextInt(100));
		engineTaskGetSender.send(engineTaskDto);		
		logger.info("enter sendTask send:ok");				
		ResponseEntityDto<String> ret= new ResponseEntityDto<>();				
		return ret;
	}	
	
	
	/**
	 * 判断是否是master机器
	 * http://192.168.1.167:8090/tasktest/isLeader
	 * @return ResponseEntityDto
	 */
	@RequestMapping(value = "/isLeader", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> isLeader() {		
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<>();
		ret.setData(engineClusterLeaderService.isLeader());		
		return ret;
	}	
	
	
	/**
	 * 判断是否是master机器
	 * http://192.168.1.167:8090/taskTest/rabbitmqInfo
	 * @return ResponseEntityDto
	 */
	@RequestMapping(value = "/rabbitmqInfo", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<String> rabbitmqInfo() {		
		ResponseEntityDto<String> ret= new ResponseEntityDto<>();
		ret.setData(connectionFactory.getHost());		
		return ret;
	}

	
}
