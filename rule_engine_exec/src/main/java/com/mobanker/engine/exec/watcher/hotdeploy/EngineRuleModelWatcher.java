package com.mobanker.engine.exec.watcher.hotdeploy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import com.dianping.cat.message.Transaction;
import com.mobanker.engine.common.EngineConst;
import com.mobanker.engine.framkwork.cpnt.container.EngineCpntContainer;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineHttpQueryStep;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineCpntParseException;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.promanager.EngineRuntimeProductManager;
import com.mobanker.engine.framkwork.serialize.EngineKryoSerialize;
import com.mobanker.engine.framkwork.serialize.EnginePolicyFlowSerialize;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.framework.tracking.EE;
import com.mobanker.zkc.cache.CacheManager;

/**
 * 引擎包文件监控
 * <p>
 * Company: mobanker.com
 * </p>
 * 
 * @author taojinn
 * @date 2016年3月11日
 * @version 1.0
 */
public class EngineRuleModelWatcher implements InitializingBean {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());		
	
	private final static String BUSI_DESC = "规则模型变化监听";
	
	private EngineRuntimeProductManager engineRuntimeProductManager;
	
	/**
	 * zk服务器地址
	 */
	private String zkAddress;
	private int zkTimeout = 20000;
	
	private final static String zkDirPath = EngineConst.ZK_DIR.RULE_MODEL;

	private ExecutorService pool = Executors.newSingleThreadExecutor();

	private CuratorFramework client;
	private PathChildrenCache childrenCache;

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"开始监听,zkAddress:{},zkParentNode:{}",zkAddress,zkDirPath);
		client = CuratorFrameworkFactory.builder()
				.connectString(zkAddress).sessionTimeoutMs(5000)
				.connectionTimeoutMs(zkTimeout)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"zk client start");

		childrenCache = new PathChildrenCache(client,zkDirPath, true);
		childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
		childrenCache.getListenable().addListener(
				new PathChildrenCacheListener() {
					public void childEvent(CuratorFramework client,
							PathChildrenCacheEvent event) throws Exception {					
						String productType = null;
						byte[] bytes;
						try {																		
							switch (event.getType()) {
								case CHILD_ADDED:
									logger.warn(EngineUtil.logPrefix(BUSI_DESC)+"规则模型对象被创建,zkPath:{}",event.getData().getPath());		
									productType = getProductType(event.getData().getPath());							
									bytes = event.getData().getData();
									updateRuleFlow(productType,bytes);
									break;
								case CHILD_REMOVED:
									logger.warn(EngineUtil.logPrefix(BUSI_DESC)+"不应该出现此操作");
									break;
								case CHILD_UPDATED:		
									logger.warn(EngineUtil.logPrefix(BUSI_DESC)+"规则模型对象被修改,zkPath:{}",event.getData().getPath());		
									productType = getProductType(event.getData().getPath());
									bytes = event.getData().getData();
									updateRuleFlow(productType,bytes);
									break;
								default:
									break;
							}
						} catch (Exception e) {
							logger.error("发生未知异常",e);
							EE.logError("发生未知异常",e);		
						} 
					}
				}, pool);
		
	}

	private void updateRuleFlow(String productType,byte[] bytes){
		Transaction trans = EE.newTransaction("RuleRelease", productType);
		try{
			trans.setStatus(Transaction.SUCCESS);	
			EE.logEvent("ZkListener", BUSI_DESC+"-->"+productType);
			logger.warn(EngineUtil.logPrefix(BUSI_DESC)+"开始反序列化,产品:{},length:{}",productType,bytes.length);				
			EnginePolicyFlowSerialize serializer = new EngineKryoSerialize();
			EngineCpntContainer container = serializer.unserialize(bytes);
			logger.warn(EngineUtil.logPrefix(BUSI_DESC)+"开始反序列成功,产品:{},root:{}",productType,container.getRoot());
			//replaceQueryUrl(container,productType);		
			engineRuntimeProductManager.putFlow(productType, container);			
		}catch (Exception e) {
			String msg = "反序列化规则模型失败，产品线:"+productType;
			trans.setStatus(e);
			EE.logError(msg,e);
			throw new EngineException(msg, e);		
		} finally{
			trans.complete();
		}
		
		
		
	}
	
	/**
	 * 原先因为要支持EngineHttpQueryStep组件，不得不由于环境的不同，而替换不同的URL
	 * @param container
	 * @param productType
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void replaceQueryUrl(EngineCpntContainer container,String productType){
		List<EngineHttpQueryStep> list = container.getCpntsByType(EngineHttpQueryStep.class);
		if(CollectionUtils.isEmpty(list)) return;
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"该产品存在数据源组件,产品:{}",productType);		
		
		String nid = productType+"_url";				
		String queryUrl = CacheManager.getActiveMapValue(nid);
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"环境替代URL:{}",queryUrl);				
		for(EngineHttpQueryStep step : list){
			if(StringUtils.isNotBlank(queryUrl)){				
				step.setUrl(queryUrl);
				logger.info("query url被重新设置，产品{}->{}",productType,queryUrl);
			}
			if(StringUtils.isBlank(step.getUrl()))
				throw new EngineCpntParseException("该产品存在数据源组件，但是没有在配置系统中配置查询地址");			
		}
		
	}
	
	
	
	public String getZkAddress() {
		return zkAddress;
	}

	@Required
	public void setZkAddress(String zkAddress) {
		this.zkAddress = zkAddress;
	}


	
	private String getProductType(String zkFilePath){
		String str = StringUtils.replace(zkFilePath, zkDirPath, "");
		return StringUtils.replace(str, "/", "");
	}

	public int getZkTimeout() {
		return zkTimeout;
	}

	public void setZkTimeout(int zkTimeout) {
		this.zkTimeout = zkTimeout;
	}

	public EngineRuntimeProductManager getEngineRuntimeProductManager() {
		return engineRuntimeProductManager;
	}

	public void setEngineRuntimeProductManager(
			EngineRuntimeProductManager engineRuntimeProductManager) {
		this.engineRuntimeProductManager = engineRuntimeProductManager;
	}


	
	
	
}
