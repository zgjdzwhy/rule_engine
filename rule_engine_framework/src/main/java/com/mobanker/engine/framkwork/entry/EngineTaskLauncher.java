package com.mobanker.engine.framkwork.entry;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.flow.EnginePolicyFlow;
import com.mobanker.engine.framkwork.data.EngineRuntimePersistence;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineParamMissException;
import com.mobanker.engine.framkwork.exception.EngineRunningException;
import com.mobanker.engine.framkwork.exception.monitor.EngineMonitorTransaction;
import com.mobanker.engine.framkwork.exception.monitor.EngineStaticMonitorEnvironment;
import com.mobanker.engine.framkwork.manager.EngineStepProcessor;
import com.mobanker.engine.framkwork.manager.EngineStepTaskProcessor;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 任务执行器
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年10月28日
 * @version 1.0
 */
public class EngineTaskLauncher{
	private Logger logger = LoggerFactory.getLogger(this.getClass());						
	
	private final String BUSI_DESC = "任务执行";

	private EngineRuntimePersistence engineRuntimePersistence;
	//private BeanFactory beanFactory;	
	
	private String ipAddress;
			
	
	public EngineTaskLauncher(){
		try {
			InetAddress addr = InetAddress.getLocalHost();
			ipAddress = addr.getHostAddress().toString();
		} catch (UnknownHostException e) {			
			logger.info("获取IP地址失败，这个不影响正常流程");
		}	
	}
	
	/**
	 * 执行任务
	 * @param taskInfo
	 */	
	public EngineTransferData runTask(EngineTaskInfo taskInfo,EnginePolicyFlow flow){	
		EngineAssert.checkNotNull(taskInfo);
		EngineAssert.checkNotNull(taskInfo.getId(),"task.id不可为空");
		EngineAssert.checkNotBlank(taskInfo.getProductType(),"task.productType不可为空");
		EngineAssert.checkNotBlank(taskInfo.getAppName(),"task.appName不可为空");
		EngineAssert.checkNotBlank(taskInfo.getAppRequestId(),"task.appRequestId不可为空");
		EngineAssert.checkNotBlank(taskInfo.getAppParams(),"task.appParams不可为空");
		EngineAssert.checkNotBlank(taskInfo.getStatus(),"task.status不可为空");
		EngineAssert.checkNotNull(taskInfo.getAssignStep(),"task.assignStep不可为空");		
		EngineAssert.checkNotNull(flow,"流程无root节点");
		
		if(StringUtils.isBlank(taskInfo.getRuleVersion())) taskInfo.setRuleVersion("no version");
			

		//long taskId = taskInfo.getId();		
		//String status = taskInfo.getStatus();			  
		String productType = taskInfo.getProductType();
		
		EngineTransferData etd = EngineTransferData.createByDefault(taskInfo);			
		etd.setIpAddress(ipAddress);
		
		EngineMonitorTransaction trans = EngineStaticMonitorEnvironment.create("TaskRun",productType);
		//EngineTransaction trans = EE.newTransaction("TaskRun"+"-->"+productType, BUSI_DESC);
		try{
			trans.setStatus(EngineMonitorTransaction.SUCCESS);
			//EE.logEvent("TaskRun"+"-->"+productType, BUSI_DESC);
			
			//`task_step` '0:未被分配,1:被分配中,2:已被分配  如果失败后再次被分配则2->1',
			//`status` '0:初始,1:进行中,2:完成,3:失败 如果失败后再次被分配则3->1',
			//更新task表状态为2,1		
			engineRuntimePersistence.taskStart(etd);
			
//			List<EngineStep> needExeSteps = new LinkedList<EngineStep>();
			
			//检查任务是否是初始的，如果不是初始的找到上次执行失败的步骤与参数开始执行，否则从头开始执行
//			if(status.equals("3")||status.equals("1")){
//				logger.info(EngineUtil.logPrefix(etd,BUSI_DESC)+"该进件状态为失败，开始寻找上次最后失败的步骤并执行");
//				//查找上次执行失败的步骤集合
//				List<EngineStepInfo> failSteps = engineRuntimePersistence.queryLastFailTaskSteps(taskId);		
//				if(CollectionUtils.isEmpty(failSteps)){//impossible									
//					throw new EngineException(EngineUtil.logPrefix(etd,BUSI_DESC)+"重跑阶段没有发现异常步骤");
//				}
//				
//				//需要重新回到第一步开始操作的标记，一般来说失败的话是从中断出开始的，但由于一些异常导致无法从中断部分开始，我这里做了防灾错误，让该任务从头开始执行
//				boolean resetStartFlag = false; 
//				//for(TaskStepInfo failStep : failSteps){
//				for(int i=0;i<failSteps.size();i++){
//					EngineStepInfo failStep = failSteps.get(i);			
//					String stepBean = failStep.getStepBean();
//										
//					EngineStep step = ruleModel.getOneCpnt(stepBean, EngineStep.class);
//					if(step == null){//当业务人员修改规则模型，且删除了某一个步骤，则可能走到这里
//						logger.warn(EngineUtil.logPrefix(etd,BUSI_DESC)+"该步骤不存在了！从头开始执行");
//						resetStartFlag = true;
//						continue;
//					}
//					
//					needExeSteps.add(step);
//					//如果是发生错误第一步 则把mongo中的参数快照转换成etd;
//					if(i == 0){
//						Long stepId = failStep.getId();							
//						logger.info(EngineUtil.logPrefix(etd,BUSI_DESC)+"该进件上次最后失败步骤ID为:{},开始寻找该步骤参数快照",stepId);
//						EngineTransferData failEtd = engineRuntimePersistence.queryEtdByStep(stepId);
//						//原本是考虑找不到步骤信息直接跑错，为了程序可用性更高改成了 如果没有找到步骤的参数信息（照例说不会），则从头开始执行
////							if(failEtd == null){
////								throw new EngineException(EngineUtil.logPrefix(etd,BUSI_DESC)
////										+"没有找到步骤的参数,stepId:"+stepId+",stepBean:"+stepBean);
////							}
//						if(failEtd != null){//从中断位置开始执行
//							logger.info(EngineUtil.logPrefix(etd,BUSI_DESC)+"成功获取参数快照");
//							etd.setBaseParam(failEtd.getBaseParam());
//							etd.setInputParam(failEtd.getInputParam());
//							etd.setOutputParam(failEtd.getOutputParam());
//						}else{//从头开始执行
//							logger.warn(EngineUtil.logPrefix(etd,BUSI_DESC)+"未能成功获取参数快照，从头开始执行");
//							resetStartFlag = true;
//							continue;
//						}							
//					}												
//				}		
//				if(resetStartFlag){
//					needExeSteps.clear();
//					needExeSteps.addAll(ruleModel.getRoot().getList());
//				}								
//			}else if(status.equals("0")){
//				//如果正常执行
//				logger.info(EngineUtil.logPrefix(etd,BUSI_DESC)+"该进件状态为初始，开始加载初始步骤");
//				needExeSteps.addAll(ruleModel.getRoot().getList());
//			}else{				
//				throw new EngineException(EngineUtil.logPrefix(etd,BUSI_DESC)+"任务状态异常,status:"+status);
//			}
			//检查入参是否缺失
			checkInputParamMiss(flow,etd);
			
			EngineStepProcessor processor = EngineStepTaskProcessor.create(engineRuntimePersistence,etd);			
			//EnginePolicyFlow flow = ruleModel.getRoot();
			flow.run(processor, etd);
			
			//更新task表状态为2,2
			engineRuntimePersistence.taskFinish(etd);
			
			trans.logEvent("EndPoint"+"-->"+productType, etd.getCurStepName());
		}catch(Throwable e){//方式异常则执行异常步骤			
			String errorMsg = EngineUtil.logPrefix(etd,BUSI_DESC)+"执行步骤发生了异常";
			trans.setStatus(e,errorMsg);			
			logger.error(errorMsg,e);
			try{
				//更新task表状态为2,3 不影响正常流程所以try包裹
				engineRuntimePersistence.taskFail(etd);
			}catch(Exception e1){
				logger.error(EngineUtil.logPrefix(etd,BUSI_DESC)+"更新任务表信息为异常失败",e1);
			}			
			throw new EngineRunningException(EngineUtil.logPrefix(etd,BUSI_DESC)+"执行任务异常,原因:"+e.getMessage(), e);
		}finally{
			trans.complete();
		}
		
		return etd;
	}
	

	public EngineRuntimePersistence getEngineRuntimePersistence() {
		return engineRuntimePersistence;
	}

	public void setEngineRuntimePersistence(
			EngineRuntimePersistence engineRuntimePersistence) {
		this.engineRuntimePersistence = engineRuntimePersistence;
	}

	public void checkInputParamMiss(EnginePolicyFlow flow,EngineTransferData etd){
		//需要的入参
		Set<String> inputRela = new HashSet<String>(flow.inputRela());	
		Set<String> inputParamKeys = etd.getInputParam().keySet();
		
		for(String key : inputParamKeys){
			inputRela.remove(key);
		}
		
		if(inputRela.size() > 0){
			throw new EngineParamMissException("必要入参缺失,缺失入参:"+inputRela);
		}
	}
	
	
}
