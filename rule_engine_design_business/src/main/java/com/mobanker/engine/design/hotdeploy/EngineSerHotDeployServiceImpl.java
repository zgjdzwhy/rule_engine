package com.mobanker.engine.design.hotdeploy;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

import com.mobanker.engine.design.hotdeploy.file.EngineFileContent;
import com.mobanker.engine.design.mongo.EngSerDao;
import com.mobanker.engine.design.pojo.EngSerInfo;
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
@Deprecated
public class EngineSerHotDeployServiceImpl implements EngineSerHotDeployService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());		
		
	@Autowired
	private EngSerDao engSerDao;

	/**
	 * 临时做法 益百利的东西搞的太复杂，配置太多 先写死
	 */
	private final static Map<String,String> SER_PATH =  new HashMap<String,String>();
	static{
		SER_PATH.put("shoujidai", "/sdsser/shoujidai");//暂时只有手机贷		
		SER_PATH.put("xinshen", "/sdsser/xinshen");//暂时只有手机贷
	}
	

	/**
	 * zk服务器地址
	 */
	private String zkAddress;
	private int zkTimeout = 20000;

	/**
	 * ser的本地保存目录
	 */
	//private String localSerMainDir = System.getProperty("user.dir")+File.separator+"serupload";
	//private String localSerMainDir = System.getProperty("java.io.tmpdir")+File.separator+"serupload";

//	public EngineSerHotDeployServiceImpl(){
//		logger.info("localSerMainDir :" + localSerMainDir);
//	} 
//	
	


	public void sendSerFile(String productType,EngineFileContent serFile) {
		
		logger.info("zk send start");
		String zkDirPath = SER_PATH.get(productType);
		if(StringUtils.isBlank(zkDirPath))
			throw new RuntimeException("请先配置yyd_rca_engine_ser_config");
		
		String node = zkDirPath + "/" + serFile.getFileName();
		logger.info("zk send node:"+node);
		CuratorFramework client = null;
		try {
			logger.info("zk client start connect,zkAddress:{}",zkAddress);
			
			client = CuratorFrameworkFactory.builder()
					.connectString(zkAddress).sessionTimeoutMs(zkTimeout)
					.connectionTimeoutMs(zkTimeout)
					.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
			client.start();
			logger.info("zk client conected!");
			// 先判断节点是否存在
			Stat stat = client.checkExists().forPath(node);
			// 如果不存在则新建
			if (stat == null) {
				logger.info("zk create node start,node :" + node);
				client.create().creatingParentsIfNeeded()
						.forPath(node, serFile.getBytes());
			} else {
				logger.info("zk update node start,node :" + node);
				client.setData().forPath(node, serFile.getBytes());
			}
		} catch (Exception e) {
			throw new RuntimeException("发送至zk服务器失败", e);
		}finally{
			if(client!=null) 
				client.close();
		}
		
		

	}



	public String getZkAddress() {
		return zkAddress;
	}

	@Required
	public void setZkAddress(String zkAddress) {
		this.zkAddress = zkAddress;
	}


	private void getFiles(List<String> filelist,String filePath) {
		File root = new File(filePath);
		File[] files = root.listFiles();
		
		if(files == null){
			logger.warn("目录下没有文件:"+filePath);
			return;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				/*
				 * 递归调用
				 */
				getFiles(filelist,file.getAbsolutePath());
			} else {
				String absPath = file.getAbsolutePath();
				filelist.add(absPath);
			}
		}
	}
	
	public static void main(String[] args) {
		String aa = "engineser\\rca\\20160426190748_test.txt";
		System.out.println(StringUtils.contains(aa, "20"));
		
	}


	@Override
	public Collection<EngineFileContent> getSerfiles(String productType) {
		Collection<EngineFileContent> files = new LinkedList<EngineFileContent>();			
		
		logger.info("zk send start");
		String zkDirPath = SER_PATH.get(productType);
		if(StringUtils.isBlank(zkDirPath))
			throw new RuntimeException("请先配置");
		
		CuratorFramework client = null;
		try{
			logger.info("zk client start connect,zkAddress:{}",zkAddress);
			
			client = CuratorFrameworkFactory.builder()
					.connectString(zkAddress).sessionTimeoutMs(zkTimeout)
					.connectionTimeoutMs(zkTimeout)
					.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
			client.start();
			logger.info("zk client conected!");
			// 先判断节点是否存在
			Stat stat = client.checkExists().forPath(zkDirPath);
			// 如果不存在则新建
			if (stat == null) {
				logger.info("不存在该目录:{}"+zkDirPath);				
			} else {
				List<String> nodes = client.getChildren().forPath(zkDirPath);
				for(String node : nodes){
					String fileNode = zkDirPath + "/" + node;
					byte[] bytes = client.getData().forPath(fileNode);
					EngineFileContent efc = new EngineFileContent(node,bytes);
					files.add(efc);
				}					
				return files;
			}
		} catch (Exception e) {
			throw new RuntimeException("操作zk服务器失败", e);
		}finally{
			if(client!=null) 
				client.close();
		}		
		return files;
	}

	public int getZkTimeout() {
		return zkTimeout;
	}

	public void setZkTimeout(int zkTimeout) {
		this.zkTimeout = zkTimeout;
	}

	@Override
	public void backupSer(String productType,String version) {		
		EngineAssert.checkNotBlank(productType, "备份ser发生错误,productType是空值");
		EngineAssert.checkNotBlank(version, "备份ser发生错误,version是空值");
		
		Collection<EngineFileContent> files = this.getSerfiles(productType);
		if(CollectionUtils.isEmpty(files)) return;
				
		for(EngineFileContent efc : files){
			EngSerInfo engSerInfo = new EngSerInfo();
			engSerInfo.setBytes(efc.getBytes());
			engSerInfo.setFileName(efc.getFileName());
			engSerInfo.setProductType(productType);
			engSerInfo.setVersion(version);
			engSerDao.saveSer(engSerInfo);
		}		
		
		logger.info("备份ser包完成");
	}



	@Override
	public void rollbackSer(String productType, String version) {
		List<EngSerInfo> serInfos = engSerDao.findSer(productType, version);
		
		logger.info("获取备份的ser包数量：{}",serInfos.size());
		for(EngSerInfo serInfo : serInfos){
			EngineFileContent efc = new EngineFileContent();
			efc.setBytes(serInfo.getBytes());
			efc.setFileName(serInfo.getFileName());
			this.sendSerFile(productType, efc);
		}
	}
	
	
	
}
