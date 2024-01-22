package com.mobanker.engine.exec.business.zk.taskassign;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.dianping.cat.message.Transaction;
import com.mobanker.engine.common.EngineConst;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.framework.tracking.EE;

public class EngineTaskExecSwitchWatcherImpl implements
		EngineTaskExecSwitchWatcher,InitializingBean {

	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	private final static String BUSI_DESC = "任务执行开关";
	
	private final static String zkDirPath = EngineConst.ZK_DIR.TASK_EXEC_SWITCH;	
	/**
	 * zk服务器地址
	 */
	private String zkAddress;
	private int zkTimeout = 20000;
	
	private ExecutorService pool = Executors.newSingleThreadExecutor();

	private CuratorFramework client;
	private PathChildrenCache childrenCache;
	/**
	 * productType,true or false
	 */
	private Map<String,Boolean> openFlag = new ConcurrentHashMap<String, Boolean>();
	
	
	@Override
	public void open(String productType) {
		control(productType,true);
	}

	@Override
	public void close(String productType) {
		control(productType,false);
	}


	private boolean isOpen(String productType) {
		Boolean flag = openFlag.get(productType);
		return flag==null?true:flag;
	}

	@Override
	public boolean isClose(String productType) {		
		return !isOpen(productType);
	}	
	
	private void control(String productType,Boolean flag) {
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"准备打开");
		String node = zkDirPath + "/" + productType;
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"node:"+node);
		
		CuratorFramework client = null;
		try {
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"zk client start connect,zkAddress:{}",zkAddress);
			
			client = CuratorFrameworkFactory.builder()
					.connectString(zkAddress).sessionTimeoutMs(20000)
					.connectionTimeoutMs(zkTimeout)
					.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
			client.start();
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"zk client conected!");			
				
			// 先判断节点是否存在
			Stat stat = client.checkExists().forPath(node);
			// 如果不存在则新建
			if (stat == null) {
				logger.info(EngineUtil.logPrefix(BUSI_DESC)+"zk create node start,node :" + node);
				client.create().creatingParentsIfNeeded()
						.forPath(node, flag.toString().getBytes());
			}else{
				logger.info(EngineUtil.logPrefix(BUSI_DESC)+"zk update node start,node :" + node);
				client.setData().forPath(node, flag.toString().getBytes());
			}
		} catch (Exception e) {
			throw new RuntimeException(EngineUtil.logPrefix(BUSI_DESC)+"发送至zk服务器失败", e);
		}finally{
			if(client!=null) 
				client.close();
		}
	}
	
	
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info(EngineUtil.logPrefix(BUSI_DESC)
				+ "开始监听,zkAddress:{},node:{}", zkAddress, zkDirPath);
		client = CuratorFrameworkFactory.builder().connectString(zkAddress)
				.sessionTimeoutMs(5000).connectionTimeoutMs(zkTimeout)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		logger.info(EngineUtil.logPrefix(BUSI_DESC) + "zk client start");

		childrenCache = new PathChildrenCache(client,zkDirPath, true);
		childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
		childrenCache.getListenable().addListener(
				new PathChildrenCacheListener() {
					public void childEvent(CuratorFramework client,
							PathChildrenCacheEvent event) throws Exception {					
					
						Transaction trans = EE.newTransaction("ZkListener", BUSI_DESC);
						String productType = null;
						try {
							trans.setStatus(Transaction.SUCCESS);									
							byte[] bytes;
							switch (event.getType()) {
							case CHILD_ADDED:
								logger.warn(EngineUtil.logPrefix(BUSI_DESC)+"规则模型对象被创建,zkPath:{}",event.getData().getPath());		
								productType = getProductType(event.getData().getPath());
								EE.logEvent("ZkListener", BUSI_DESC+"创建"+"-->"+productType);																
								bytes = event.getData().getData();																
								modSwitchFlag(productType,bytes);
								break;
							case CHILD_REMOVED:
								logger.warn(EngineUtil.logPrefix(BUSI_DESC)+"不应该出现此操作");
								break;
							case CHILD_UPDATED:		
								logger.warn(EngineUtil.logPrefix(BUSI_DESC)+"规则模型对象被修改,zkPath:{}",event.getData().getPath());		
								productType = getProductType(event.getData().getPath());
								EE.logEvent("ZkListener", BUSI_DESC+"修改"+"-->"+productType);								
								bytes = event.getData().getData();
								modSwitchFlag(productType,bytes);
								break;
							default:
								break;
							}
						} catch (Exception e) {
							String msg = "反序列化规则模型失败，产品线:"+productType;
							trans.setStatus(e);
							EE.logError(msg,e);
							throw new EngineException(msg, e);		
						} finally{
							trans.complete();
						}
					}
				}, pool);
	}	
	
	private void modSwitchFlag(String productType,byte[] bytes){
		String flag = new String(bytes);
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"任务执行开关标志被修改，值:" + flag);
		if(StringUtils.equals(flag, "true")){
			openFlag.put(productType, true);
		}else if(StringUtils.equals(flag, "false")){
			openFlag.put(productType, false);
		}else
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"任务执行开关标志被需改，但是值不正确:{}",flag);
	}
	
	private String getProductType(String zkFilePath){
		String str = StringUtils.replace(zkFilePath, zkDirPath, "");
		return StringUtils.replace(str, "/", "");
	}

	public String getZkAddress() {
		return zkAddress;
	}

	public void setZkAddress(String zkAddress) {
		this.zkAddress = zkAddress;
	}

	public int getZkTimeout() {
		return zkTimeout;
	}

	public void setZkTimeout(int zkTimeout) {
		this.zkTimeout = zkTimeout;
	}	
	
	
	
}
