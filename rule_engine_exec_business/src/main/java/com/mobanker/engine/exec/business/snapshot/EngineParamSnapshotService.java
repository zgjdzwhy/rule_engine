package com.mobanker.engine.exec.business.snapshot;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.cat.message.Transaction;
import com.mobanker.engine.exec.business.snapshot.dto.EngineParamSnapShot;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.framework.tracking.EE;

/**
 * 参数快照服务
 * 基于队列形式批量记录快照
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年1月22日
 * @version 1.0
 */

public class EngineParamSnapshotService implements InitializingBean{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	@Autowired
	private Client client;
	
	private final int MAX_SIZE = 100000;
	
	//一轮大小，当队列里达到这个数值的时候将写入es中
	private int turnSize = 1000;
	
	private final BlockingQueue<EngineParamSnapShot> queue = new LinkedBlockingQueue<EngineParamSnapShot>(MAX_SIZE);
	
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
		
	public void snapshot(String table,EngineTransferData etd){		
		boolean isSuccess = queue.offer(new EngineParamSnapShot(table,etd));
		if(!isSuccess)
			logger.info(EngineUtil.logPrefix(etd, "记录快照")+"快照队列数量已满，此次快照被丢弃");			
	}

	
	@Override
	public void afterPropertiesSet() throws Exception {
//		startConsume();				
	}
	

	public void startConsume(){
		logger.info("启动一个消费者去消费快照缓存记录");
		executorService.execute(new Runnable() {		
			@Override
			public void run() {				
				String busi = "参数快照";
				int count = 0;
				for(;;){					
					int size = queue.size();
					if(size >= turnSize){//如果查过阀值则快照入es
						logger.info("满足批量插入条件，开始记录快照，当前队列数量:{}",size);

						Transaction trans = EE.newTransaction("SnapshotRecord", busi);
						try{
							trans.setStatus(Transaction.SUCCESS);
							EE.logEvent("SnapshotRecord", busi);
							BulkRequestBuilder bulkRequest = client.prepareBulk();  
							//去除这些值批量插入es
							for(int i = 0;i < size;i++){
								EngineParamSnapShot oneSnapshot = queue.poll();
								if(oneSnapshot == null){
									logger.warn("从快照缓存列表里获取数据为空，不可能啊!");	
									continue;
								}
								String etdContent = oneSnapshot.getEtdContent();
								String table = oneSnapshot.getTable();							
								String productType = oneSnapshot.getProductType();
								//logger.info("ES批量操作存放数据:{}",etdContent);
								bulkRequest.add(client.prepareIndex(table, productType).setSource(etdContent));												
							}						
							BulkResponse bulkResponse = bulkRequest.execute().actionGet();
							logger.info("批量插入快照完成，数量:{}",size);
							//如果有异常先不管
							if(bulkResponse.hasFailures()){
								logger.warn("ES批量操作返回异常，原因:{}",bulkResponse.buildFailureMessage());									
							}											
						}catch(Exception e){
							logger.error("记录快照异常",e);
							trans.setStatus(e);
							EE.logError("记录快照异常",e);							
						}finally{
							trans.complete();
						}				
					}
					count++;
					//logger.info("等待{}毫秒，再次进行轮询快照队列",sleepInterval);
					if(count % 3600 == 0){
						EE.logEvent("SnapshotRecord", "快照队列消费者健康");
						logger.info("快照队列消费者健康");
						count = 0;
					}		
					try {
						int sleepInterval = 1000;
						Thread.sleep(sleepInterval);											
					} catch (InterruptedException e) {
						logger.error("It is impossible",e);
					} 
					
				}				
			}
		});		
	}


	public int getTurnSize() {
		return turnSize;
	}


	public void setTurnSize(int turnSize) {
		this.turnSize = turnSize;
	}
	
	
	
}
