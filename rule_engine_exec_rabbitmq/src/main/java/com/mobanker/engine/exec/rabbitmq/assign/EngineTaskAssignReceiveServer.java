package com.mobanker.engine.exec.rabbitmq.assign;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.framework.rabbitmq.MQBuilder;
import com.mobanker.framework.rabbitmq.MQConstants;
import com.mobanker.framework.rabbitmq.MQItem;
import com.mobanker.framework.rabbitmq.MQProcess;
import com.mobanker.framework.rabbitmq.ThreadPoolConsumer;

/**
 * 接受任务分配信息，并且启动多线程处理这些信息
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月14日
 * @version 1.0
 */
@Service
public class EngineTaskAssignReceiveServer implements InitializingBean{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private final static String BUSI_DESC = "任务分配接受管理器";
	
	private static final String EXCHANGETYPE = EngineTaskAssignDefine.EXCHANGETYPE;
	private static final String EXCHANGE = EngineTaskAssignDefine.EXCHANGE;
	private static final String ROUTING = EngineTaskAssignDefine.ROUTING;
	private static final String QUEUE = EngineTaskAssignDefine.QUEUE;

	@Autowired
	private ConnectionFactory connectionFactory;
	@Autowired
	private MQProcess<EngineTaskInfo> engineTaskAssignProcess;

	
	private ThreadPoolConsumer<EngineTaskInfo> threadPoolConsumer;

	@Override
	public void afterPropertiesSet() {		
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"准备连接MQ,host:{}",connectionFactory.getHost());
		MQBuilder mqBuilder = new MQBuilder(connectionFactory);
		MQItem mqItem = new MQItem().setExchangeType(EXCHANGETYPE).setExchange(EXCHANGE).setRoutingKey(ROUTING)
				.setQueue(QUEUE);
		threadPoolConsumer = new ThreadPoolConsumer.ThreadPoolConsumerBuilder<EngineTaskInfo>()
				.setThreadCount(EngineTaskAssignDefine.RECEIVE_THREAD_COUNT).setIntervalMils(MQConstants.INTERVAL_MILS).setMQItem(mqItem)
				.setMQBuilder(mqBuilder).setMessageProcess(engineTaskAssignProcess).build();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int wait = 10000;
					//由于engineRuntimeProductManager可能还没把所有产品装载到内存中，所以这里需要等待10秒后开启分配任务
					logger.info(EngineUtil.logPrefix(BUSI_DESC)+"等待{}毫秒后开启任务分配",wait);
					Thread.sleep(wait);					
					logger.info(EngineUtil.logPrefix(BUSI_DESC)+"分配任务开启!");
					threadPoolConsumer.start();
				} catch (IOException | TimeoutException | InterruptedException e) {
					logger.error("开启任务分配开关失败，请检查",e);
				}
			}
		}).start();				
	}

	
//	public void receiveStart() throws IOException, TimeoutException {
//		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"闸道开启");
//		threadPoolConsumer.reStart();
//	}
//
//	public void receiveStop() {
//		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"闸道关闭");
//		threadPoolConsumer.stop();
//	}



}
