package com.mobanker.engine.exec.taskget.job;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.dianping.cat.message.Transaction;
import com.mobanker.engine.exec.business.zk.leader.EngineClusterLeaderService;
import com.mobanker.engine.exec.business.zk.taskassign.EngineTaskExecSwitchWatcher;
import com.mobanker.engine.exec.rabbitmq.assign.EngineTaskAssignSender;
import com.mobanker.engine.framkwork.data.dao.EngineTaskInfoDao;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.framework.tracking.EE;

/**
 * 将任务批量发送到消息队列中，如果消息队列中未被启动的任务过多，则停止发送任务
 * <p>Title: TaskSend2MqJob.java<／p>
 * <p>Description: <／p>
 * <p>Company: mobanker.com<／p>
 * @author taojinn
 * @date 2016年2月6日
 * @version 1.0
 */

@Service("engineTaskSendJob")
public class EngineTaskSendJob{	
	private static Logger logger = LoggerFactory.getLogger(EngineTaskSendJob.class);	
	
	private final static String BUSI_DESC_SENDTASK = "【发送任务至消息队列】";
	private final static String BUSI_DESC_RESETFAIL = "【使失败任务重跑】";
	private final static String BUSI_DESC_INTERRUPT = "【使被中断任务重跑】";

	
	/**
	 * 是否正在执行分配任务的标志
	 */
	private static volatile boolean isAssigningFlag = false;
	
	@Autowired
	private EngineTaskInfoDao engineTaskInfoDao;
	
	@Autowired
	private EngineTaskAssignSender engineTaskAssignSender;
	
	@Autowired
	private EngineClusterLeaderService engineClusterLeaderService;
	
	private final static int assignWeight = 50;
	
	
	
	//@EETransaction(type="Job",name="发送任务至消息队列")
	@Transactional(rollbackFor=Exception.class)
	public void sendTask(){		
		if(!engineClusterLeaderService.isLeader()){
			logger.info(BUSI_DESC_SENDTASK+"机器不是leader，不执行任务分配");
			return;
		}		
		if(isAssigningFlag){
			logger.info(BUSI_DESC_SENDTASK+"上一次的分配任务还没有结束");
			return;
		}
		isAssigningFlag = true;
		
		
		Transaction trans = EE.newTransaction("Job", BUSI_DESC_SENDTASK);
		try{
			trans.setStatus(Transaction.SUCCESS);
			EE.logEvent("Job", BUSI_DESC_SENDTASK);
			long timeFlag = System.currentTimeMillis();
			
			logger.info(BUSI_DESC_SENDTASK+"开始执行...");			
			int assigningCount = engineTaskInfoDao.queryCountByAssignStep(1);
			if(assigningCount >= assignWeight){
				logger.info(BUSI_DESC_SENDTASK+"被分配的数量太多，不在继续分配 assigningCount:"+assigningCount);
				return;
			}							
			logger.info(BUSI_DESC_SENDTASK+"查询分配任务...");
			List<EngineTaskInfo> taskList = engineTaskInfoDao.getNeedAssignTaskList(assignWeight);
			if(CollectionUtils.isEmpty(taskList)){
				logger.info(BUSI_DESC_SENDTASK+"没有需要分配的任务");
				return;
			}			
			logger.info(BUSI_DESC_SENDTASK+"给分配的任务打上被分配中标记...");
			int updateCount = engineTaskInfoDao.updateTaskAssigningFlag(taskList);
			if(updateCount == 0)//impossible
				throw new RuntimeException("逻辑异常，没有更新被分配中标记");
			logger.info(BUSI_DESC_SENDTASK+"给分配的任务打上被分配中标记，标记数量:"+updateCount);
			
			for(EngineTaskInfo task : taskList){
				logger.info(BUSI_DESC_SENDTASK+"开始发送任务至mq ==> "+task);
				engineTaskAssignSender.send(task);
			}					
			logger.info(BUSI_DESC_SENDTASK+"执行结束...耗费时间（毫秒）:"+ (System.currentTimeMillis() - timeFlag));		
		}catch(Exception e){
			String errorMsg = EngineUtil.logPrefix(BUSI_DESC_SENDTASK)+"发生错误";
			trans.setStatus(e);
			EE.logError(errorMsg,e);
			throw e;
		}finally{
			logger.info(BUSI_DESC_SENDTASK+"重置分配任务进行中标志，使得下次分配能正常进行");
			isAssigningFlag = false;
			trans.complete();		
		}
	}
	

	/**
	 * 失败任务再次重新启动
	 */
	//@EETransaction(type="Job",name="失败任务重新启动")
	public void resetFailTask(){
		
		if(!engineClusterLeaderService.isLeader()){
			logger.info(BUSI_DESC_RESETFAIL+"机器不是leader，不执行");
			return;
		}
		Transaction trans = EE.newTransaction("Job", BUSI_DESC_RESETFAIL);
		try{
			trans.setStatus(Transaction.SUCCESS);
			EE.logEvent("Job", BUSI_DESC_RESETFAIL);		
			long timeFlag = System.currentTimeMillis();
			
			logger.info(BUSI_DESC_RESETFAIL+"开始执行...");
			int count = engineTaskInfoDao.updateFailTaskAssignFlag();
			logger.info(BUSI_DESC_RESETFAIL+"共"+count+"笔失败任务状态被重置");
			
			logger.info(BUSI_DESC_RESETFAIL+"执行结束...话费时间（毫秒）:"+ (System.currentTimeMillis() - timeFlag));		
		}catch(Exception e){
			String errorMsg = EngineUtil.logPrefix(BUSI_DESC_RESETFAIL)+"发生错误";
			trans.setStatus(e);
			EE.logError(errorMsg,e);
			throw e;
		}finally{
			trans.complete();
		}
	}	
	
	
	/**
	 * 使被中断任务重跑
	 */
	//@EETransaction(type="Job",name="中断任务重新启动")
	public void resetInterruptTask(){
		if(!engineClusterLeaderService.isLeader()){
			logger.info(BUSI_DESC_INTERRUPT+"机器不是leader，不执行");
			return;
		}
		Transaction trans = EE.newTransaction("Job", BUSI_DESC_INTERRUPT);
		try{
			trans.setStatus(Transaction.SUCCESS);
			EE.logEvent("Job", BUSI_DESC_INTERRUPT);		
			long timeFlag = System.currentTimeMillis();
			
			logger.info(BUSI_DESC_INTERRUPT+"开始执行...");
			int count = engineTaskInfoDao.updateInterruptTaskAssignFlag();
			logger.info(BUSI_DESC_INTERRUPT+"共"+count+"笔被中断任务状态被重置");
			
			logger.info(BUSI_DESC_INTERRUPT+"执行结束...话费时间（毫秒）:"+ (System.currentTimeMillis() - timeFlag));
		}catch(Exception e){
			String errorMsg = EngineUtil.logPrefix(BUSI_DESC_INTERRUPT)+"发生错误";
			trans.setStatus(e);
			EE.logError(errorMsg,e);
			throw e;
		}finally{
			trans.complete();
		}
		
		
	}


	
	
}
