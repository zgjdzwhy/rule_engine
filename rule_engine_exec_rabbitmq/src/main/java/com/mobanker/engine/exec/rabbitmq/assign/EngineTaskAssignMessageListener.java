package com.mobanker.engine.exec.rabbitmq.assign;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.framework.rabbitmq.MQProcess;

@Deprecated()
public class EngineTaskAssignMessageListener implements MessageListener,InitializingBean{

	private static Logger logger = LoggerFactory.getLogger(EngineTaskAssignMessageListener.class);
	private final static String BUSI_DESC = "任务分配接受管理器";
	
	@Autowired
	private MQProcess<EngineTaskInfo> engineTaskAssignProcess;
	
	public static Charset charset = Charset.forName("UTF-8");
	
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			int wait = 5000;
			//由于engineRuntimeProductManager可能还没把所有产品装载到内存中，所以这里需要等待10秒后开启分配任务
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"等待{}毫秒后开启任务分配",wait);
			Thread.sleep(wait);					
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"准备开启任务分配!");
		} catch (InterruptedException e) {
			logger.error("开启任务分配开关失败，请检查",e);
		}
	}		
		
	
	@Override
	public void onMessage(Message message) {		
		String str = new String(message.getBody(),charset);
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"接受到消息提:{}",str);
		EngineTaskInfo entity = JSONObject.parseObject(str, EngineTaskInfo.class);		
		engineTaskAssignProcess.process(entity);	
	}
	

}
