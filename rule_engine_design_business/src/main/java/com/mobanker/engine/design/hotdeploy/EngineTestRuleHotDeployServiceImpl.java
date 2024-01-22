package com.mobanker.engine.design.hotdeploy;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.mobanker.engine.common.EngineConst;
import com.mobanker.engine.design.hotdeploy.file.EngineFileContent;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.exception.EngineRunningException;

/**
 * 
 * <p>
 * Company: mobanker.com
 * </p>
 * 
 * @author taojinn
 * @date 2016年3月14日
 * @version 1.0
 */
public class EngineTestRuleHotDeployServiceImpl implements EngineTestRuleHotDeployService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	private final static String zkDirPath = EngineConst.ZK_DIR.RULE_MODEL;

	private final static String checkConnectPath = EngineConst.ZK_DIR.CHECK_CONNECTIVE;
	
	/**
	 * zk服务器地址
	 */
	private int zkTimeout = 20000;
	private String isProductEnviro;
	
	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	
	
	private final static int byteMaxLength = 1024*1024;//1M
	
	@Override
	public String isProductEnviro(){
		return isProductEnviro;
	}
	
	@Override
	public void deployRuleModel(List<EngineFileContent> ruleModelFileList,String zkAddress) {	
		if(StringUtils.isBlank(isProductEnviro)){
			throw new RuntimeException("未配置是否生产环境标志");
		}else if("0".equals(isProductEnviro)){
			throw new RuntimeException("当前为测试环境无需发送!");
		}
		logger.info("发布测试环境规则模型开始,当前发送zk地址:"+zkAddress);				
		deployRuleModelByPath(ruleModelFileList,zkDirPath,zkAddress);
		logger.info("发布测试环境规则模型结束,当前发送zk地址:"+zkAddress);
	}
	
	@Override
	public void checkZkAddress(String zkAddress){
		//zk重试次数设置不起作用，无奈，暂时先通过线程捕获超时！
	    checkCallable call=new checkCallable(zkAddress);
	    Future<String> future = null;
	    try {  
	        future = threadPoolTaskExecutor.submit(call);  
	        String obj = future.get(1000 * 7, TimeUnit.MILLISECONDS);   
	        
	    } catch (TimeoutException ex) {  
	    	throw new EngineRunningException("发送至zk服务器超时，请检查地址:"+zkAddress); 
	    } catch (Exception e) {  
	    	throw new EngineRunningException("zk联通性测试失败,地址："+zkAddress, e);
	    }finally { 
	    	if(future!=null){
	    		future.cancel(true);  
	    	}
        }    
	}
	
	private class checkCallable implements Callable<String> {
		private String zkAddress;
		public checkCallable(String zkAddress){
			this.zkAddress=zkAddress;
		}
	    @Override
	    public String call() throws Exception {
	    	String node = checkConnectPath;
    		logger.info("开始测试zk联通性，zk地址:"+zkAddress+"，节点地址:"+node);
    		try(CuratorFramework client = CuratorFrameworkFactory.builder()
    				.connectString(zkAddress).sessionTimeoutMs(20000)
    				.connectionTimeoutMs(zkTimeout)
    				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build()){
    			logger.info("zk client start connect,zkAddress:{}",zkAddress);
    			client.start();
    			logger.info("zk client conected!");
    			String test="test";
    			client.inTransaction().check().forPath(node);
    			logger.info("zk client conected!");
    			// 先判断节点是否存在
    			Stat stat = client.checkExists().forPath(node);
    			// 如果不存在则新建
    			if(stat == null) {
    				logger.info("zk create node start,node :" + node);
    				client.create().creatingParentsIfNeeded()
    						.forPath(node, test.getBytes());
    			}else{
    				logger.info("zk update node start,node :" + node);
    				client.setData().forPath(node, test.getBytes());
    			}
    			
    			logger.info("zk联通性测试成功，zk地址{}",zkAddress);
    		} catch (Exception e) {
    			throw new EngineException("zk联通性测试失败,地址："+zkAddress, e);
    		}	
            return "true";  
	    }
	}

	
	private void deployRuleModelByPath(List<EngineFileContent> ruleModelFileList,String nodePath,String zkAddress) {
		for(EngineFileContent ruleModelFile:ruleModelFileList){
			String node = nodePath + "/" + ruleModelFile.getFileName();
			logger.info("开始推送规则模型，zk地址:"+zkAddress+"，节点地址:"+node);
			try(CuratorFramework client = CuratorFrameworkFactory.builder()
					.connectString(zkAddress).sessionTimeoutMs(20000)
					.connectionTimeoutMs(zkTimeout)
					.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build()){
				logger.info("zk client start connect,zkAddress:{}",zkAddress);
				
				
				client.start();
				logger.info("zk client conected!");
				int byteLength = ruleModelFile.getBytes().length;
				logger.info("序列化后的规则模型对象大小:{}",byteLength);
					
				EngineAssert.checkArgument(byteLength<=byteMaxLength, "规则模型打包的内容过大，无法传送至zk服务器上");
				
				// 先判断节点是否存在
				Stat stat = client.checkExists().forPath(node);
				// 如果不存在则新建
				if(stat == null) {
					logger.info("zk create node start,node :" + node);
					client.create().creatingParentsIfNeeded()
							.forPath(node, ruleModelFile.getBytes());
				}else{
					logger.info("zk update node start,node :" + node);
					client.setData().forPath(node, ruleModelFile.getBytes());
				}
				
				logger.info("发送规则模型成功:"+ruleModelFile.getBytes().length);
			} catch (Exception e) {
				throw new EngineException("发送至zk服务器失败", e);
			}		
		}
	}

	
	public int getZkTimeout() {
		return zkTimeout;
	}


	public void setZkTimeout(int zkTimeout) {
		this.zkTimeout = zkTimeout;
	}

	public String getIsProductEnviro() {
		return isProductEnviro;
	}

	public void setIsProductEnviro(String isProductEnviro) {
		this.isProductEnviro = isProductEnviro;
	}
	
	
}
