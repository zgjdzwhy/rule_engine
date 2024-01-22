package com.mobanker.engine.design.hotdeploy;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.common.EngineConst;
import com.mobanker.engine.design.hotdeploy.file.EngineFileContent;
import com.mobanker.engine.framkwork.exception.EngineAssert;

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
public class EngineRuleModelHotDeployServiceImpl implements EngineRuleModelHotDeployService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	private final static String zkDirPath = EngineConst.ZK_DIR.RULE_MODEL;

	/**
	 * zk服务器地址
	 */
	private String zkAddress;
	private int zkTimeout = 20000;

	private final static int byteMaxLength = 1024*1024;//1M
	
	public void deployRuleModel(EngineFileContent ruleModelFile) {		
		logger.info("发布规则模型开始");				
		deployRuleModelByPath(ruleModelFile,zkDirPath);
		logger.info("发布规则模型结束");
	}
	
	@Override
	public void backUpRuleModel() {		
		logger.info("备份规则模型内存开始!");
		copyNodeDir(zkDirPath,EngineConst.ZK_DIR.RULE_MODEL_BAK);
		logger.info("备份规则模型内存完成!");
	}

	@Override
	public void restoreRuleModel() {
		logger.info("恢复规则模型内存开始!");
		copyNodeDir(EngineConst.ZK_DIR.RULE_MODEL_BAK,zkDirPath);
		logger.info("恢复规则模型内存开始!");
	}	
	
	private void deployRuleModelByPath(EngineFileContent ruleModelFile,String nodePath) {
		String node = nodePath + "/" + ruleModelFile.getFileName();
		logger.info("开始推送规则模型，节点地址:"+node);
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
			throw new RuntimeException("发送至zk服务器失败", e);
		}		
	}
	
	@Override
	public List<EngineFileContent> getAllRuleModelBytes() {
		String node = zkDirPath;
		logger.info("查找zk节点:"+node);
		List<EngineFileContent> contentList=new LinkedList<EngineFileContent>();
		try(CuratorFramework client = CuratorFrameworkFactory.builder()
				.connectString(zkAddress).sessionTimeoutMs(20000)
				.connectionTimeoutMs(zkTimeout)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build()) {
			logger.info("zk client start connect,zkAddress:{}",zkAddress);
			
			client.start();
			logger.info("zk client conected!");
			
			//获取子路径列表
			List<String> list = client.getChildren().forPath(node);	
			if(CollectionUtils.isNotEmpty(list)){
				for(String productType : list){
					contentList.add(this.getCurRuleModelBytes(productType));
				}
			}
			
		} catch (Exception e) {
			throw new RuntimeException("获取zk节点信息失败", e);
		}
		return contentList;
	}
	
	@Override
	public EngineFileContent getCurRuleModelBytes(String productType) {
		
		String node = zkDirPath + "/" + productType;
		logger.info("查找zk节点:"+node);
		try(CuratorFramework client = CuratorFrameworkFactory.builder()
				.connectString(zkAddress).sessionTimeoutMs(20000)
				.connectionTimeoutMs(zkTimeout)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build()) {
			logger.info("zk client start connect,zkAddress:{}",zkAddress);
			
			client.start();
			logger.info("zk client conected!");
			
			byte[] ruleModelData = client.getData().forPath(node);
			logger.info("获取zk节点信息成功,产品线:{},字节数:{}",productType,ruleModelData.length);
			return new EngineFileContent(productType,ruleModelData);			
		} catch (Exception e) {
			throw new RuntimeException("获取zk节点信息失败", e);
		}
	}	
	

	
	
	private void copyNodeDir(String fromPath,String toPatch) {
		String node = fromPath;
		logger.info("查找zk节点:"+node);
		try(CuratorFramework client = CuratorFrameworkFactory.builder()
				.connectString(zkAddress).sessionTimeoutMs(20000)
				.connectionTimeoutMs(zkTimeout)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build()) {
			logger.info("zk client start connect,zkAddress:{}",zkAddress);
			
			client.start();
			logger.info("zk client conected!");
			
			//获取子路径列表
			List<String> list = client.getChildren().forPath(node);			
			//备份
			if(CollectionUtils.isNotEmpty(list)){
				for(String productType : list){
					EngineFileContent content = this.getCurRuleModelBytes(productType);
					deployRuleModelByPath(content,toPatch);
				}
				logger.info("拷贝成功,从{}到{},工程模块列表:{}",fromPath,toPatch,list);
			}		
		} catch (Exception e) {
			throw new RuntimeException("获取zk节点信息失败", e);
		}
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
