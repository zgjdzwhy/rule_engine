package com.mobanker.democenter.rabbitmq;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobanker.framework.rabbitmq.MQBuilder;
import com.mobanker.framework.rabbitmq.MQItem;
import com.mobanker.framework.rabbitmq.MQResponse;
import com.mobanker.framework.rabbitmq.MQSender;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 
 * @author lait.zhang@gmail.com
 * @Date 2016年8月19日-下午5:38:22
 * @Description:<br>
 * 					</br>
 *
 */
@Service
public class UserMessageSender {

	private static final String EXCHANGE = UserMQDefine.EXCHANGE_TOPIC;
	private static final String ROUTING = UserMQDefine.ROUTING_TOPIC;
	private static final String QUEUE = UserMQDefine.QUEUE_TOPIC;

	//@Autowired
	//private MQBuilder mqBuilder;
	
	private MQSender mqSender;

	@Autowired
	ConnectionFactory connectionFactory;

	@PostConstruct
	public void init() throws IOException, TimeoutException {
		MQBuilder mqBuilder = new MQBuilder(connectionFactory);
		MQItem mqItem = new MQItem().setExchangeType("topic").setExchange(EXCHANGE).setRoutingKey(ROUTING).setQueue(QUEUE);
		mqSender = mqBuilder.buildMessageSender(mqItem);
	}

	public MQResponse send(UserMessage userMessage) {
		return mqSender.send(userMessage);
	}
}
