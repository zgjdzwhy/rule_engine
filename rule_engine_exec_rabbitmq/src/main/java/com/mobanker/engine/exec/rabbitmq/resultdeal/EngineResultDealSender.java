package com.mobanker.engine.exec.rabbitmq.resultdeal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobanker.engine.framkwork.api.params.EngineParam;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.framework.utils.HttpClientUtils;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月14日
 * @version 1.0
 */

public class EngineResultDealSender {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());	

	private final static String QUEUE_PRE = "engine_result_deal";
	
	@Autowired
	private RabbitTemplate amqpTemplate;
	
	private static final Map<String,Object> httpConfig = new HashMap<String,Object>();
	static{
		httpConfig.put(HttpClientUtils.CONNECT_TIMEOUT, 3000);
		httpConfig.put(HttpClientUtils.SOCKET_TIMEOUT, 3000);
	}
	

	public void sendMq(String productType,String appRequestId,EngineParam output) {
		amqpTemplate.convertAndSend(QUEUE_PRE+"_"+productType, 
				QUEUE_PRE+"_"+productType, 
				output.toJSONString());
//		amqpTemplate.convertAndSend("engine_task_assign", 
//				"engine_task_assign", 
//				output.toJSONString());
		
		logger.info("mq发送结果给业务系统成功,productType:{},output:{}",productType,output);
		
	}
	
	public void sendHttp(String productType,String appRequestId,EngineParam output,String httpUrl) throws IOException {
		Map<String,String> urlParamSend = new HashMap<String,String>();		
		urlParamSend.put("productType", productType);
		urlParamSend.put("appRequestId", appRequestId);
		urlParamSend.put("output", output.toJSONString());
		
		HttpClientUtils.setConfig(httpConfig);
		
	
		logger.info("产品线:{},流水号:{},准备回调接口:{},{}",productType,appRequestId,httpUrl,urlParamSend);
		String resStr = HttpClientUtils.doPost(httpUrl, urlParamSend);
		logger.info("http发送结果给业务系统成功,productType:{},output:{},res:{}",productType,output,resStr);
	
	
		
	}	
	
}
