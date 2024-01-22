package com.mobanker.engine.exec.rabbitmq.assign;

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
 * 单个的接受者 暂时没用
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月14日
 * @version 1.0
 */
@Service
public class EngineTaskAssignConsumer {

	private static final String EXCHANGE = EngineTaskAssignDefine.EXCHANGE;
	private static final String ROUTING = EngineTaskAssignDefine.ROUTING;
	private static final String QUEUE = EngineTaskAssignDefine.QUEUE;

	// @Autowired
	// MQBuilder mqBuilder ;

	@Autowired
	private ConnectionFactory connectionFactory;
	
	@Autowired
	private EngineTaskAssignProcess engineTaskMsgProcess;

	private MQConsumer mqConsumer;

	@PostConstruct
	public void init() throws IOException, TimeoutException {
		MQBuilder mqBuilder = new MQBuilder(connectionFactory);
		MQItem mqItem = new MQItem().setExchangeType(EngineTaskAssignDefine.EXCHANGETYPE).setExchange(EXCHANGE).setRoutingKey(ROUTING).setQueue(QUEUE);
		mqConsumer = mqBuilder.buildMessageConsumer(mqItem, engineTaskMsgProcess);
	}

	/**
	 * 会阻塞
	 * @return
	 */
	public MQResponse consume() {
		return mqConsumer.consume();
	}
}
