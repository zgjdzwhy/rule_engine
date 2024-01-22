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
public interface EngineTestRuleHotDeployService {

	void deployRuleModel(List<EngineFileContent> ruleModelFileList,
			String zkAddress);

	String isProductEnviro();

	void checkZkAddress(String zkAddress);

}
