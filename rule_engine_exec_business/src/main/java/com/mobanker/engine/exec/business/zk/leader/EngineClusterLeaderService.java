package com.mobanker.engine.exec.business.zk.leader;

/**
 * 集群机器 选举服务
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月14日
 * @version 1.0
 */
public interface EngineClusterLeaderService {	
	public boolean isLeader();		
}
