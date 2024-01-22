package com.mobanker.engine.exec.rabbitmq.taskget;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobanker.engine.exec.pojo.EngineTaskDto;
import com.mobanker.engine.framkwork.data.dao.EngineTaskInfoDao;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.framework.rabbitmq.MQProcess;
import com.mobanker.framework.rabbitmq.MQResponse;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月14日
 * @version 1.0
 */
@Service
public class EngineTaskGetProcess implements MQProcess<EngineTaskDto> {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	private final static String BUSI_DESC = "获取外部任务";
	
	@Autowired
	private EngineTaskInfoDao engineTaskInfoDao;
		
	//@EETransaction(type="RecvMq",name="外部任务信息落地")
	@Override
	public MQResponse process(EngineTaskDto dto) {
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"获取外部任务:{}",dto);
		
		//将数据保存到数据库中
		EngineAssert.checkNotNull(dto);
		EngineAssert.checkNotBlank(dto.getAppName(),BUSI_DESC+"task.appName为空");
		EngineAssert.checkNotBlank(dto.getProductType(),BUSI_DESC+"task.product为空");
		EngineAssert.checkNotBlank(dto.getAppParams(),BUSI_DESC+"task.appParams为空");
		EngineAssert.checkNotBlank(dto.getAppRequestId(),BUSI_DESC+"task.appRequestId为空");
		//dto.getExecTime()可为空
		//dto.getPriorty()可为空
		
		EngineTaskInfo entity = new EngineTaskInfo();
		entity.setProductType(dto.getProductType());
		entity.setAppName(dto.getAppName());		
		entity.setAppParams(dto.getAppParams());
		entity.setAppRequestId(dto.getAppRequestId());
		entity.setAppCallBack(dto.getAppCallBack());
		entity.setExecTime(dto.getExecTime());
		entity.setPriority(dto.getPriority());
				
		if(StringUtils.isBlank(entity.getExecTime())) entity.setExecTime("00000000000000"); 
		if(entity.getPriority() == null) entity.setPriority(1);
		
		entity.setAssignStep(0);
		entity.setStatus("0");
		entity.setChannel("mq");
		entity.setFailTimes(0);
		entity.setSourceDetail("");		
		entity.setBeginTime("");
		entity.setEndTime("");
		entity.setRuleVersion("");
		engineTaskInfoDao.insert(entity);	
		
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"任务数据成功落地:{}",dto);
		
		return new MQResponse(true, "");
	}
	

	
}
