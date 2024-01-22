package com.mobanker.engine.design.hotdeploy;

import java.util.List;

import com.mobanker.engine.design.hotdeploy.file.EngineFileContent;

/**
 * 模型对象热发布
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年3月14日
 * @version 1.0
 */
public interface EngineRuleModelHotDeployService {

	/**
	 * 发送至zookeeper服务器
	 * @param serFile 文件名
	 */
	public void deployRuleModel(EngineFileContent ruleModelFile);
	
	/**
	 * 获取当前规则模型
	 * @param productType
	 * @return
	 */
	public EngineFileContent getCurRuleModelBytes(String productType);
	
	/**
	 * 备份所有规则到zk
	 */
	public void backUpRuleModel();
	
	/**
	 * 回复所有规则
	 */
	public void restoreRuleModel();

	
	/** 
	* @Title: getAllRuleModelBytes 
	* @Description: 导出所有规则
	* 参数@return
	* @return List<EngineFileContent>    返回类型 
	* @throws 
	*/
	List<EngineFileContent> getAllRuleModelBytes();
}
