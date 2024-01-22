package com.mobanker.democenter.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobanker.framework.rabbitmq.MQBuilder;
import com.mobanker.framework.rabbitmq.MQConsumer;
import com.mobanker.framework.rabbitmq.MQItem;
import com.mobanker.framework.rabbitmq.MQResponse;

/**
 * 
 * @author lait.zhang@gmail.com
 * @Date Sep 2, 2016 9:43:24 AM
 * @Description:<br>
 * 					</br>
 *
 */
@Service
public class UserMessageConsumer {

	private static final String EXCHANGE = UserMQDefine.EXCHANGE_TOPIC;
	private static final String ROUTING = UserMQDefine.ROUTING_TOPIC;
	private static final String QUEUE = UserMQDefine.QUEUE_TOPIC;

	// @Autowired
	// MQBuilder mqBuilder ;

	@Autowired
	private ConnectionFactory connectionFactory;

	private MQConsumer mqConsumer;

	@PostConstruct
	public void init() throws IOException, TimeoutException {
		MQBuilder mqBuilder = new MQBuilder(connectionFactory);
		MQItem mqItem = new MQItem().setExchangeType("topic").setExchange(EXCHANGE).setRoutingKey(ROUTING).setQueue(QUEUE);
		mqConsumer = mqBuilder.buildMessageConsumer(mqItem, new UserMessageProcess());
	}

	public MQResponse consume() {
		return mqConsumer.consume();
	}
}
