package com.mobanker.engine.exec.business.tasktrace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.dianping.cat.message.Transaction;
import com.mobanker.engine.exec.business.snapshot.chartanaly.EngineChartAnaly;
import com.mobanker.engine.exec.business.snapshot.chartanaly.impl.EngineChartAnalyPieRatio;
import com.mobanker.engine.exec.business.snapshot.chartanaly.impl.EngineChartAnalyRowBarAvg;
import com.mobanker.engine.exec.business.snapshot.chartanaly.impl.EngineChartAnalyRowBarNumsum;
import com.mobanker.engine.exec.business.snapshot.chartanaly.impl.EngineChartAnalyRowBarRatiosum;
import com.mobanker.engine.exec.business.snapshot.dto.BatchStatus;
import com.mobanker.engine.exec.pojo.EngineTaskQco;
import com.mobanker.engine.exec.pojo.snapshot.EngineChart;
import com.mobanker.engine.exec.pojo.snapshot.EngineSnapshotTotalCompareDto;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.container.EngineCpntContainer;
import com.mobanker.engine.framkwork.cpnt.flow.EnginePolicyFlow;
import com.mobanker.engine.framkwork.data.EngineRuntimePersistence;
import com.mobanker.engine.framkwork.data.dao.EngineStepInfoDao;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.entry.EngineTaskLauncher;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.exception.EngineRunningException;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.framework.tracking.EE;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年1月23日
 * @version 1.0
 */
@Service
public class EngineTaskBatchRunImpl implements EngineTaskBatchRun{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final static String BUSI_DESC = "模拟调用";
	private final static String NOW_RETURN = "nowReturn";
	private final static String PAST_RETURN = "pastReturn";
	
	private final static Map<String,EngineChartAnaly> analyCpnts = new HashMap<String,EngineChartAnaly>();	
	static{		
		analyCpnts.put(EngineChartAnalyRowBarRatiosum.class.getSimpleName(), new EngineChartAnalyRowBarRatiosum());
		analyCpnts.put(EngineChartAnalyRowBarNumsum.class.getSimpleName(), new EngineChartAnalyRowBarNumsum());
		analyCpnts.put(EngineChartAnalyRowBarAvg.class.getSimpleName(), new EngineChartAnalyRowBarAvg());	
		analyCpnts.put(EngineChartAnalyPieRatio.class.getSimpleName(), new EngineChartAnalyPieRatio());
	}
	
	@Autowired
	private EngineTaskTraceService engineTaskTraceService;
	
	@Autowired
	private EngineTaskLauncher engineTaskLauncher;
	
	@Autowired
	private RedisTemplate<String, String> redisStringTemplate;
	
	/**
	 * 记录批处理状态 0:初始 1:完成 -1:失败
	 */
	@Autowired
	private RedisTemplate<String, BatchStatus> redisBatchStatusTemplate;
	
	@Autowired
	private RedisTemplate<String, EngineTransferData> redisEtdTemplate;
	
	@Autowired
	private RedisTemplate<String, EngineSnapshotTotalCompareDto> redisDisplayChartTemplate;
	
	@Autowired
	private RedisTemplate<String, Set<String>> redisDisplayFieldsTemplate;
	
	@Autowired
	private EngineStepInfoDao engineStepInfoDao;
	
	@Resource(name="engineHbasePersistence")
	private EngineRuntimePersistence engineRuntimePersistence;
	
	private int maxCount = 100000;
	
	private int turnNum = 1000;
	//间隔多少小时才能再次进行测试
	private int intervalHour = 6;
	
	//以下函数存放各种
	private String batchStatusKey(String productType){
		return redisKeyPrefix(productType)+"_status";
	}
	private String batchListContentKey(String productType,String type){
		return redisKeyPrefix(productType)+"_listContent_"+type;
	}
	private String displayChartResultKey(String productType,String fieldKey,String analyType){
		return redisKeyPrefix(productType)+"_displayChartResult_"+fieldKey+analyType;
	}
	private String displayFieldsResultKey(String productType){
		return redisKeyPrefix(productType)+"_displayFieldsResult";
	}
	private String redisKeyPrefix(String productType){
		return "batchRun_"+productType+"_key";
	}

	@Override
	public void batchRun(EngineCpntContainer ecc,EngineTaskQco qco) {		
		EngineAssert.checkNotNull(ecc,"EngineCpntContainer为空");
		EngineAssert.checkNotNull(qco,"EngineTaskQco为空");	
		qco.setStatus("2");	//查询已经跑完的数据状态			
		final String productType = qco.getProductType();
		//判断是否已经存在正在运行的测试,如果有则抱错
		if(isFinish(productType)){
			throw new EngineException("该工程需要间隔"+intervalHour+"小时才能测试");
		}
		if(isRunning(productType)){
			throw new EngineException("该工程的快照测试正在进行中");
		}	
		if(isError(productType)){
			throw new EngineException("上一次的快照测试发生了异常，请查看规则配置是否正确以及是否存在快照");
		}	
		
		new Thread(new AsynRunnner(ecc,qco,new Callback(){
			@Override
			public void doWork() {//为了增加体验，事先做好showOutputFields这个步骤
				showOutputFields(productType);
			}
		})).start();		
	}
	
	private class AsynRunnner implements Runnable{		
		private EngineCpntContainer ecc;
		private EngineTaskQco qco;
		private Callback callback;
		
		private AsynRunnner(EngineCpntContainer ecc,EngineTaskQco qco,Callback callback){
			this.ecc = ecc;
			this.qco = qco;
			this.callback = callback;
		}
		
		@Override
		public void run() {
			String productType = qco.getProductType();
			
			//默认最大取10万数据，每批1000一次运行
			int turnCount = maxCount/turnNum;
			int allCount = 0;
			start(productType);
			logger.info("开始模拟运行，工程:{}，批次数:{}",productType,turnCount);
			try{
				for(int i = 0;i < turnCount; i++){
					int pageNum = i+1;
					qco.setPage(pageNum, turnNum);
					List<EngineTaskInfo> taskList = engineTaskTraceService.queryTaskInfo(qco);
					allCount = allCount + taskList.size();
					if(CollectionUtils.isEmpty(taskList)) break;
						
					doTaskList(ecc,taskList);						
					if(taskList.size() < turnNum) break;

					logger.info("开始模拟运行，工程:{}，正在处理中，当前处理批次:{},总处理批次:{}",productType,pageNum,turnCount);
				}
				logger.info("开始模拟运行，工程:{}，处理完成，一共处理了{}",productType,allCount);
				finish(productType);
				
				if(allCount == 0){
					error(productType,"该时间段没有快照数据");
				}
			}catch(Exception e){
				logger.error("快照测试出错,详细请查看cat监控",e);
				error(productType,"处理过程中出错:"+e.getMessage());
			}	
			
			
			callback.doWork();
		}
	}
	
	private interface Callback{		
		void doWork();		
	}

	

	private boolean isRunning(String productType){
		String checkKey = batchStatusKey(productType);
		BatchStatus obj = redisBatchStatusTemplate.opsForValue().get(checkKey);
		if(obj == null) return false;		
		return obj.getStatus()==0;
	}
	
	private boolean isFinish(String productType){
		String checkKey = batchStatusKey(productType);
		BatchStatus obj  = redisBatchStatusTemplate.opsForValue().get(checkKey);
		if(obj == null) return false;		
		return obj.getStatus()==1;
	}
	private boolean isError(String productType){
		String checkKey = batchStatusKey(productType);
		BatchStatus obj  = redisBatchStatusTemplate.opsForValue().get(checkKey);
		if(obj == null) return false;		
		return obj.getStatus()<0;
	}
	
	private void checkNoError(String productType){
		String checkKey = batchStatusKey(productType);
		BatchStatus obj  = redisBatchStatusTemplate.opsForValue().get(checkKey);
		if(obj == null) return;		
		EngineAssert.checkArgument(obj.getStatus()>=0, obj.getMsg());
	}
	
	private void start(String productType){
		String checkKey = batchStatusKey(productType);
		redisBatchStatusTemplate.opsForValue().set(checkKey, new BatchStatus(0,null));
		redisBatchStatusTemplate.expire(checkKey, intervalHour, TimeUnit.HOURS);
	}
	
	private void error(String productType,String errorMsg){
		String checkKey = batchStatusKey(productType);
		redisBatchStatusTemplate.opsForValue().set(checkKey, new BatchStatus(-1,errorMsg));
		redisBatchStatusTemplate.expire(checkKey, intervalHour, TimeUnit.HOURS);
	}
	
	private void finish(String productType){
		String checkKey = batchStatusKey(productType);
		redisBatchStatusTemplate.opsForValue().set(checkKey, new BatchStatus(1,null));
		redisBatchStatusTemplate.expire(checkKey, intervalHour, TimeUnit.HOURS);
	}
	
	private void putResult2redis(String resultKey,EngineTransferData etd){
		if(etd == null) return;
		redisEtdTemplate.opsForList().leftPush(resultKey, etd);
		redisEtdTemplate.expire(resultKey, intervalHour, TimeUnit.HOURS);
	}		
	
	
	
	private void doTaskList(EngineCpntContainer ruleModel,List<EngineTaskInfo> taskList){
		for(EngineTaskInfo taskInfo : taskList){
			String productType = taskInfo.getProductType();
			//用新规则跑出来的返回值
			EngineTransferData nowReturn = doTask(ruleModel,taskInfo);
			logger.info("模拟运行，新规则的结果:"+nowReturn.getOutputParam());				
			putResult2redis(batchListContentKey(productType,NOW_RETURN),nowReturn);
			
			//当时跑出来的返回值 
			EngineTransferData pastReturn = getPastResult(taskInfo);
			if(pastReturn != null){
				logger.info("模拟运行，旧规则的结果:"+pastReturn.getOutputParam());
				putResult2redis(batchListContentKey(productType,PAST_RETURN),pastReturn);
			}else//由于生产数据可能因为重启而导致数据缺失所以这里做了容错
				logger.warn("模拟运行，旧规则的结果缺失");
		}				
	}

	private EngineTransferData getPastResult(EngineTaskInfo taskInfo){
		return engineRuntimePersistence.querySnapshotByTask(taskInfo.getProductType(), taskInfo.getAppRequestId(), taskInfo.getId());					
//		List<EngineStepInfo> stepList = engineStepInfoDao.queryByTaskId(taskInfo.getId());
//		if(CollectionUtils.isEmpty(stepList)) return Collections.emptyMap();
//		
//		List<EngineStepInfo> reverseList = Ordering.from(new Comparator<EngineStepInfo>() {
//			@Override
//			public int compare(EngineStepInfo o1,
//					EngineStepInfo o2) {										
//				return o1.getId().compareTo(o2.getId());
//			}
//		}).reverse().sortedCopy(stepList);
//		
//		EngineStepInfo lastStep = reverseList.get(0);
//		if(lastStep == null) return Collections.emptyMap();
//		String productType = taskInfo.getProductType();
//		String appRequestId = taskInfo.getAppRequestId();
//				
//		EngineTransferData etd = engineRuntimePersistence.queryLogOutEtdByStep(productType, appRequestId, lastStep.getId());
//		if(etd == null) return Collections.emptyMap();
//		return etd.getOutputParam();
	}
	
	

	
	private EngineTransferData doTask(EngineCpntContainer ruleModel,EngineTaskInfo taskInfo){
		String productType = taskInfo.getProductType();		
		Transaction trans = EE.newTransaction("FakeInvoke",BUSI_DESC+"-->"+productType);
		try {
			EngineAssert.checkNotBlank(productType,"productType为空");				
			EE.logEvent("FakeInvoke", BUSI_DESC+"-->"+productType);		
			trans.setStatus(Transaction.SUCCESS);
			
			EnginePolicyFlow execFlow = ruleModel.getRoot();
			EngineAssert.checkNotNull(execFlow, EngineUtil.logPrefix(BUSI_DESC)+"该产品线没有root节点,productType:%s",productType);			
						
			EngineTransferData etd = engineTaskLauncher.runTask(taskInfo, ruleModel.getRoot());		
			return etd;
		} catch (EngineRunningException e) {//由于engineTaskLauncher里面已经做了cat错误日志埋点，所以这里不用再次埋了
			String msg = "模拟调用"+productType+"产品线失败";
			throw new EngineException(msg+"=>"+e.getMessage(), e);		
		}catch (Exception e) {
			String msg = "模拟调用"+productType+"产品线失败，发生了未知异常";
			trans.setStatus(e);
			EE.logError(msg+"=>"+e.getMessage(),e);
			throw new EngineException(msg+"=>"+e.getMessage(), e);		
		} finally{
			trans.complete();
		}
	}

	
	//@Cacheable(value="snapShotInterval",key="'showOutputFields_'+#productType")
	@Override
	public Set<String> showOutputFields(String productType) {	
		//判断缓存
		String cacheKey = displayFieldsResultKey(productType);
		Set<String> result = redisDisplayFieldsTemplate.opsForValue().get(cacheKey);
		if(result != null) return result;	
		
		if(isRunning(productType))
			throw new EngineException("快照测试正在进行!");			
		checkNoError(productType);
		
		EngineAssert.checkArgument(isFinish(productType), "请先进行快照测试!");
		
		List<EngineTransferData> oldList = redisEtdTemplate.opsForList().range(batchListContentKey(productType,PAST_RETURN), 0, -1);
		List<EngineTransferData> newList = redisEtdTemplate.opsForList().range(batchListContentKey(productType,NOW_RETURN), 0, -1);
				
		result = new HashSet<String>();
		logger.info("开始提炼旧版本数据中的字段集合");
		result.addAll(extractField(oldList));
		logger.info("开始提炼新版本数据中的字段集合");
		result.addAll(extractField(newList));
				
		//记录缓存
		redisDisplayFieldsTemplate.opsForValue().set(cacheKey, result);
		redisDisplayFieldsTemplate.expire(cacheKey, intervalHour, TimeUnit.HOURS);
		
		return result;
	}	
	
	private Set<String> extractField(List<EngineTransferData> returnList){
		Set<String> result = new HashSet<String>();
		for(EngineTransferData returnObj : returnList){			
			result.addAll(returnObj.getOutputParam().keySet());
		}	
		return result;
	} 
		
	@Override
	public EngineSnapshotTotalCompareDto displayFieldChartResult(String productType,String fieldKey,String analyType) {
		//记录用户习惯		
		redisStringTemplate.opsForValue().set(customAnalyType(productType,fieldKey), analyType);
		
		//判断缓存
		String cacheKey = displayChartResultKey(productType,fieldKey,analyType);
		EngineSnapshotTotalCompareDto result = redisDisplayChartTemplate.opsForValue().get(cacheKey);
		if(result != null) return result;		

		EngineAssert.checkArgument(isFinish(productType), "请先进行快照测试!");
		EngineChartAnaly engineChartAnaly = analyCpnts.get(analyType);
		EngineAssert.checkNotNull(engineChartAnaly,"根据类型没有找到图标分析引擎:%s",analyType);
		String redisKey;
		List<EngineTransferData> data;		
		//分析旧数据
		logger.info("开始分析之前的数据,工程:{},字段:{},分析类型:{}",productType,fieldKey,analyType);
		redisKey = batchListContentKey(productType,PAST_RETURN);		
		data = redisEtdTemplate.opsForList().range(redisKey, 0, -1);
		EngineChart oldChart = engineChartAnaly.generate(data,fieldKey);
		oldChart.setSize(data.size());
		//分析新数据
		logger.info("开始分析现在的数据,工程:{},字段:{},分析类型:{}",productType,fieldKey,analyType);
		redisKey = batchListContentKey(productType,NOW_RETURN);		
		data = redisEtdTemplate.opsForList().range(redisKey, 0, -1);
		EngineChart newChart = engineChartAnaly.generate(data,fieldKey);
		newChart.setSize(data.size());				
		
		result = new EngineSnapshotTotalCompareDto(oldChart,newChart);
		//记录缓存
		redisDisplayChartTemplate.opsForValue().set(cacheKey, result);
		redisDisplayChartTemplate.expire(cacheKey, intervalHour, TimeUnit.HOURS);
		
		return result;
	}
	
	private String customAnalyType(String productType,String fieldKey){
		return "customAnalyType_"+productType+"_"+fieldKey;
	}

	@Override
	public String getCustomAnalyType(String productType,String fieldKey) {		
		return redisStringTemplate.opsForValue().get(customAnalyType(productType,fieldKey));
	}
	
	@Override
	public void clearLastBatchRunInfo(String productType) {
		if(isRunning(productType))
			throw new EngineException("快照测试正在进行!需要等该次批处理结束才能清除!");
		
		String keyPatten = redisKeyPrefix(productType)+"*";
		Set<String> keys = redisBatchStatusTemplate.keys(keyPatten);
		if(CollectionUtils.isNotEmpty(keys)){
			logger.info("删除批处理结果缓存,keys:{}",keys);
			redisBatchStatusTemplate.delete(keys);
		}				
	}
	
}
