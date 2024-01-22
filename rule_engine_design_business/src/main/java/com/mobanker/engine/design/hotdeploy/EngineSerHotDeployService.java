package com.mobanker.engine.design.hotdeploy;

import java.util.Collection;
import java.util.List;

import com.mobanker.engine.design.hotdeploy.file.EngineFileContent;

/**
 * ser引擎包热部署服务
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年3月14日
 * @version 1.0
 */
@Deprecated
public interface EngineSerHotDeployService {


	/**
	 * 从zk上获取所有的ser包
	 * @param productType
	 * @return
	 */
	public Collection<EngineFileContent> getSerfiles(String productType);
	
	/**
	 * 发送至zookeeper服务器
	 * @param serFile 文件名
	 */
	public void sendSerFile(String productType,EngineFileContent serFile);
	
	/**
	 * 回滚引擎包
	 * @param 
	 * @return
	 */
	public void rollbackSer(String productType,String version);
	
	/**
	 * 备份ser包
	 * @param productType
	 * @param version
	 */
	public void backupSer(String productType,String version);
		
	
}
