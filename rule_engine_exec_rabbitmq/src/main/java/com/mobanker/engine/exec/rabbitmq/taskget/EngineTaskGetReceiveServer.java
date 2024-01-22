package com.mobanker.engine.exec.rabbitmq.taskget;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobanker.engine.exec.pojo.EngineTaskDto;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.framework.rabbitmq.MQBuilder;
import com.mobanker.framework.rabbitmq.MQConstants;
import com.mobanker.framework.rabbitmq.MQItem;
import com.mobanker.framework.rabbitmq.MQProcess;
import com.mobanker.framework.rabbitmq.ThreadPoolConsumer;

/**
 * 接受外部应用调用，可以说是决策引擎平台的最初入口
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月14日
 * @version 1.0
 */
@Service
public class EngineTaskGetReceiveServer implements InitializingBean{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private final static String BUSI_DESC = "获取外部任务接收器";
	
	private static final String EXCHANGETYPE = EngineTaskGetDefine.EXCHANGETYPE;
	private static final String EXCHANGE = EngineTaskGetDefine.EXCHANGE;
	private static final String ROUTING = EngineTaskGetDefine.ROUTING;
	private static final String QUEUE = EngineTaskGetDefine.QUEUE;

	@Autowired
	private ConnectionFactory connectionFactory;
	@Autowired
	private MQProcess<EngineTaskDto> engineTaskGetProcess;
	
	private ThreadPoolConsumer<EngineTaskDto> threadPoolConsumer;

	@Override
	public void afterPropertiesSet() {
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"准备连接MQ,host:{}",connectionFactory.getHost());
		MQBuilder mqBuilder = new MQBuilder(connectionFactory);
		MQItem mqItem = new MQItem().setExchangeType(EXCHANGETYPE).setExchange(EXCHANGE).setRoutingKey(ROUTING)
				.setQueue(QUEUE);
		threadPoolConsumer = new ThreadPoolConsumer.ThreadPoolConsumerBuilder<EngineTaskDto>()
				.setThreadCount(EngineTaskGetDefine.RECEIVE_THREAD_COUNT).setIntervalMils(MQConstants.INTERVAL_MILS).setMQItem(mqItem)
				.setMQBuilder(mqBuilder).setMessageProcess(engineTaskGetProcess).build();
		
		try {
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"启动");
			threadPoolConsumer.start();
		} catch (IOException | TimeoutException e) {
			logger.error(EngineUtil.logPrefix(BUSI_DESC)+"初始化开启闸道异常");
		}
	}

	
	public void receiveStart() throws IOException, TimeoutException {
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"闸道开启");
		threadPoolConsumer.reStart();
	}

	public void receiveStop() {
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"闸道关闭");
		threadPoolConsumer.stop();
	}




}
