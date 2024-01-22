package com.mobanker.engine.exec.taskget.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mobanker.engine.exec.pojo.EngineTaskDto;
import com.mobanker.engine.exec.rabbitmq.taskget.EngineTaskGetProcess;
import com.mobanker.framework.contract.dto.ResponseEntityDto;


@Controller
@RequestMapping("taskGet")
public class EngineTaskGetController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());	

	@Autowired
	private EngineTaskGetProcess engineTaskGetProcess;
	
	
	/**
	 * http://192.168.1.167:8090/taskGet/sendTask
	 * {'productType':'xxx','appRequestId':'xxxxxxxx',}
	 * 通过调用Dubbo服务获取提额状态列表
	 * @return ResponseEntityDto
	 */
	@RequestMapping(value = "/sendTask", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<?> sendTask(@ModelAttribute EngineTaskDto dto) {
		engineTaskGetProcess.process(dto);		
		ResponseEntityDto<String> ret= new ResponseEntityDto<String>();
		ret.setData("ok");
		return ret;
	}
	
}
