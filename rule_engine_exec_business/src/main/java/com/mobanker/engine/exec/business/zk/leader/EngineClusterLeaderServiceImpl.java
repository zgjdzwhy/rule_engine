package com.mobanker.engine.exec.business.zk.leader;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.mobanker.engine.common.EngineConst;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 这个类必须是单例
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年10月8日
 * @version 1.0
 */
public class EngineClusterLeaderServiceImpl implements
		EngineClusterLeaderService,InitializingBean {
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	private final static String BUSI_DESC = "集群选举";
	
	/**
	 * zk服务器地址
	 */
	private String zkAddress;
	private int zkTimeout = 20000;
	
	
	private String zkDirPath = EngineConst.ZK_DIR.TASK_LEADER;
	
	private String ipAddress;
	
	private boolean isLeader = false;
	private CuratorFramework client;
	private LeaderLatch leader;
	
	public EngineClusterLeaderServiceImpl(){
		try {
			InetAddress addr = InetAddress.getLocalHost();
			ipAddress=addr.getHostAddress().toString();
			logger.info("ipAddress:"+ipAddress);
		} catch (UnknownHostException e) {
			logger.info("获取IP地址失败，这个不影响正常流程");
		}	
	}
	
	@Override
	public boolean isLeader() {		
		return isLeader;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"zk client start connect,zkAddress:{}",zkAddress);
		
		client = CuratorFrameworkFactory.builder()
				.connectString(zkAddress).sessionTimeoutMs(40000)
				.connectionTimeoutMs(zkTimeout)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"zk client conected!");			
			
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"启动选举启动器:{}",ipAddress);
		leader=new LeaderLatch(client,zkDirPath);
		leader.addListener(new LeaderLatchListener(){
            @Override
            public void isLeader() {
            	logger.info(EngineUtil.logPrefix(BUSI_DESC)+"选举成为leader,ip:{}",ipAddress);
            	isLeader = true;
            }

            @Override
            public void notLeader() {	            
            	logger.info(EngineUtil.logPrefix(BUSI_DESC)+"没有选举成功,ip:{}",ipAddress);
            	isLeader = false;
            }}
		);
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"配置选举监听:{}",ipAddress);
		
		client.start();		
		leader.start();
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"启动选举监听完成:{}",ipAddress);
		
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

	public String getZkDirPath() {
		return zkDirPath;
	}

	public void setZkDirPath(String zkDirPath) {
		this.zkDirPath = zkDirPath;
	}
	
	

}
