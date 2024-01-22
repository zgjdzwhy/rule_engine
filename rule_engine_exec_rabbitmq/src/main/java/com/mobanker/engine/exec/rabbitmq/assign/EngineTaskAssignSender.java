package com.mobanker.engine.exec.rabbitmq.assign;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.framework.rabbitmq.MQBuilder;
import com.mobanker.framework.rabbitmq.MQItem;
import com.mobanker.framework.rabbitmq.MQResponse;
import com.mobanker.framework.rabbitmq.MQSender;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月14日
 * @version 1.0
 */
@Service
public class EngineTaskAssignSender {

	private static final String EXCHANGE = EngineTaskAssignDefine.EXCHANGE;
	private static final String ROUTING = EngineTaskAssignDefine.ROUTING;
	private static final String QUEUE = EngineTaskAssignDefine.QUEUE;

	
	private MQSender mqSender;

	@Autowired
	private ConnectionFactory connectionFactory;
	
	//@Autowired
	//private RabbitTemplate amqpTemplate;

	@PostConstruct
	public void init() throws IOException, TimeoutException {
		MQBuilder mqBuilder = new MQBuilder(connectionFactory);
		MQItem mqItem = new MQItem().setExchangeType(EngineTaskAssignDefine.EXCHANGETYPE).setExchange(EXCHANGE).setRoutingKey(ROUTING).setQueue(QUEUE);
		mqSender = mqBuilder.buildMessageSender(mqItem);
	}

	public MQResponse send(EngineTaskInfo engineTaskInfo) {
		return mqSender.send(engineTaskInfo);
		//amqpTemplate.convertAndSend(ROUTING,engineTaskInfo);
		//return new MQResponse(true,"");
	}
}
