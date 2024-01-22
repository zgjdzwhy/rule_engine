package com.mobanker.democenter.rabbitmq;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.mobanker.framework.rabbitmq.MQBuilder;
import com.mobanker.framework.rabbitmq.MQItem;
import com.mobanker.framework.rabbitmq.MQProcess;
import com.mobanker.framework.rabbitmq.ThreadPoolConsumer;
import com.mobanker.framework.rabbitmq.MQConstants;

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
public class MQExample {

	private static final String EXCHANGE = UserMQDefine.EXCHANGE_TOPIC;
	private static final String ROUTING = UserMQDefine.ROUTING_TOPIC;
	private static final String QUEUE = UserMQDefine.QUEUE_TOPIC;

	@Autowired
	private ConnectionFactory connectionFactory;
	@Autowired
	private UserMessageSender userMessageSender;
	@Autowired
	private UserMessageConsumer userMessageConsumer;

	private ThreadPoolConsumer<UserMessage> threadPoolConsumer;

	@PostConstruct
	public void init() {
		MQBuilder mqBuilder = new MQBuilder(connectionFactory);
		MQItem mqItem = new MQItem().setExchangeType("topic").setExchange(EXCHANGE).setRoutingKey(ROUTING)
				.setQueue(QUEUE);
		MQProcess<UserMessage> mqProcess = new UserMessageProcess();
		threadPoolConsumer = new ThreadPoolConsumer.ThreadPoolConsumerBuilder<UserMessage>()
				.setThreadCount(MQConstants.THREAD_COUNT).setIntervalMils(MQConstants.INTERVAL_MILS).setMQItem(mqItem)
				.setMQBuilder(mqBuilder).setMessageProcess(mqProcess).build();
	}

	
	public void receive() throws IOException, TimeoutException {
		threadPoolConsumer.start();
	}

	public void stop() {
		threadPoolConsumer.stop();
	}

	/**
	 * 
	 * @param obj 发送数据
	 * @param count 发送数量
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public void send(final Object obj,final int count,final long time) throws IOException, TimeoutException {
		
		
		// 消费
		// start();
		new Thread(new Runnable() {
			int id = 0;
			boolean stopSend = false;
			// 发送
			@Override
			public void run() {
				while (!stopSend) {
					userMessageSender.send(new UserMessage(id++, obj.toString()));
					if(id == count){
						stopSend = true;
					}
					try {
						Thread.sleep(time);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

}
