package com.mobanker.engine.exec.rabbitmq.assign;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.cat.message.Transaction;
import com.mobanker.engine.exec.business.zk.taskassign.EngineTaskExecSwitchWatcher;
import com.mobanker.engine.exec.rabbitmq.resultdeal.EngineResultDealSender;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.container.EngineCpntContainer;
import com.mobanker.engine.framkwork.cpnt.flow.EnginePolicyFlow;
import com.mobanker.engine.framkwork.data.dao.EngineTaskInfoDao;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.entry.EngineTaskLauncher;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.exception.EngineProductCloseException;
import com.mobanker.engine.framkwork.promanager.EngineRuntimeProductManager;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.framework.rabbitmq.MQProcess;
import com.mobanker.framework.rabbitmq.MQResponse;
import com.mobanker.framework.tracking.EE;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月14日
 * @version 1.0
 */
@Service
public class EngineTaskAssignProcess implements MQProcess<EngineTaskInfo> {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	private final static String BUSI_DESC = "被分配任务";
	
	@Autowired
	private EngineRuntimeProductManager engineRuntimeProductManager;
	
	@Autowired
	private EngineTaskLauncher etl;
	
	@Autowired
	private EngineResultDealSender engineResultDealSender;
	
	@Autowired
	private EngineTaskExecSwitchWatcher engineTaskExecSwitchWatcher;
	
	@Autowired
	private EngineTaskInfoDao engineTaskInfoDao;
	
	private ExecutorService service = new ThreadPoolExecutor(EngineTaskAssignDefine.RECEIVE_THREAD_COUNT, 
			EngineTaskAssignDefine.RECEIVE_THREAD_COUNT+5, 10, 
			TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	
	@Override
	public MQResponse process(EngineTaskInfo task) {
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+":获取任务"+task);		
		
		
		Transaction trans = EE.newTransaction("RecvMq", BUSI_DESC);
		try {
			trans.setStatus(Transaction.SUCCESS);
			EngineAssert.checkNotNull(task);
			EngineAssert.checkNotNull(task.getId(),BUSI_DESC+"task.id为空");
			String product = task.getProductType();
			EngineAssert.checkNotBlank(product,BUSI_DESC+"task.product为空");			
			
			
			if(engineTaskExecSwitchWatcher.isClose(product)){
				EngineTaskInfo taskInfo = engineTaskInfoDao.get(task.getId());
				EngineAssert.checkNotNull(task,BUSI_DESC+"根据task.id没有找到taskinfo,id:%s",task.getId());
				//修改任务状态为停止
				taskInfo.setStatus("9");
				engineTaskInfoDao.update(taskInfo);
				throw new EngineProductCloseException("该产品线已经被关闭，产品线:"+product);
			}
			
			EE.logEvent("RecvMq", BUSI_DESC+"-->"+product);
			
			
			EngineAssert.checkNotBlank(task.getAppRequestId(),BUSI_DESC+"task.appRequestId为空");
			
			EngineCpntContainer container = engineRuntimeProductManager.getFlow(product);
			EngineAssert.checkNotNull(container,"对应的产品规则模型不存在!%s",product);
			
			task.setRuleVersion(container.getRuleVersion());
			
			EnginePolicyFlow flow = container.getRoot();
			EngineAssert.checkNotNull(container,"对应的产品规则模型不存在root节点!%s",flow);
			
			//etl.runTask(task);//原有的处理方式有弊端，一旦该线程挂了 就再也起不来了
			Future<EngineTransferData> future = service.submit(new SlaveRunner(etl,task,flow));
			//10分钟 超过了执行下一个任务
			EngineTransferData etd = future.get(10, TimeUnit.MINUTES);
			
			//查看应用方有没有callback地址,如果有则回调callback地址，否则发送mq;
			if(StringUtils.isNotBlank(task.getAppCallBack())){				
				engineResultDealSender.sendHttp(task.getProductType(), task.getAppRequestId(), etd.getOutputParam(), task.getAppCallBack());
			}else{
				//将结果发送至mq
				engineResultDealSender.sendMq(task.getProductType(), task.getAppRequestId(), etd.getOutputParam());
			}		
		}catch(ExecutionException|EngineException|InterruptedException|TimeoutException e){
			String errMsg = "执行任务发生严重异常:"+task.getAppName()+"->"+task.getAppParams();			
			Throwable throwable = e;
			if(!(e instanceof ExecutionException)){//执行异常已经在EngineTaskLauncher这个类被打印过了，所以不想在打印一次
				EE.logError(errMsg,throwable);
				logger.error(errMsg,throwable);
			}							
			trans.setStatus(throwable);
		}catch(Exception e){
			String errMsg = "执行任务发生未知异常:"+task.getAppName()+"->"+task.getAppParams();		
			EE.logError(errMsg,e);
			logger.error(errMsg,e);
			trans.setStatus(e);
		}finally{
			trans.complete();
		}	
		//无论如何都仍未是成功的 因为要回应给mq ack确认
		return new MQResponse(true, "");
	}
	
	/**
	 * 为了让主线程不死
	 * <p>Company: mobanker.com</p>
	 * @author taojinn
	 * @date 2016年9月14日
	 * @version 1.0
	 */
	private class SlaveRunner implements Callable<EngineTransferData>{

		private EngineTaskLauncher etl;
		private EngineTaskInfo task;
		private EnginePolicyFlow flow;
		
		public SlaveRunner(EngineTaskLauncher etl,EngineTaskInfo task,EnginePolicyFlow flow){
			this.etl = etl;
			this.task = task;
			this.flow = flow;
		}
		
		@Override
		public EngineTransferData call() throws Exception {						
			return etl.runTask(task,flow);		
		}
	}
		
	
}
