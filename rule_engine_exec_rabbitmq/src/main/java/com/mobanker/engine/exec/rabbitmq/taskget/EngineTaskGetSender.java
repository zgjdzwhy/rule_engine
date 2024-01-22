package com.mobanker.engine.exec.rabbitmq.taskget;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobanker.engine.exec.pojo.EngineTaskDto;
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
public class EngineTaskGetSender {

	private static final String EXCHANGE = EngineTaskGetDefine.EXCHANGE;
	private static final String ROUTING = EngineTaskGetDefine.ROUTING;
	private static final String QUEUE = EngineTaskGetDefine.QUEUE;

	//@Autowired
	//private MQBuilder mqBuilder;
	
	private MQSender mqSender;

	@Autowired
	private ConnectionFactory connectionFactory;

	@PostConstruct
	public void init() throws IOException, TimeoutException {
		MQBuilder mqBuilder = new MQBuilder(connectionFactory);
		MQItem mqItem = new MQItem().setExchangeType(EngineTaskGetDefine.EXCHANGETYPE).setExchange(EXCHANGE).setRoutingKey(ROUTING).setQueue(QUEUE);
		mqSender = mqBuilder.buildMessageSender(mqItem);
	}

	public MQResponse send(EngineTaskDto engineTaskDto) {
		return mqSender.send(engineTaskDto);
	}
}
