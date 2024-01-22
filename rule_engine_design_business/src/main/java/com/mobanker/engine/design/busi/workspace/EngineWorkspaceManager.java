package com.mobanker.engine.design.busi.workspace;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.design.dto.EngineCpntCompareGroupDto;
import com.mobanker.engine.design.dto.EngineCpntListDisplay;
import com.mobanker.engine.design.dto.EngineDefineFieldDto;
import com.mobanker.engine.design.dto.EngineProductDto;
import com.mobanker.engine.design.dto.EngineScriptSyntaxErr;
import com.mobanker.engine.design.dto.EngineTestReturnDto;
import com.mobanker.engine.design.hotdeploy.file.EngineFileContent;
import com.mobanker.engine.design.mongo.assist.RuleModelTable;
import com.mobanker.engine.design.pojo.EngProductReleaseFlow;
import com.mobanker.engine.framkwork.cpnt.container.EngineCpntContainer;
import com.mobanker.engine.inner.call.dto.SnapshotBatchRunDto;

/**
 * 业务人员发布，保存，以及工作空间相关管理
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月21日
 * @version 1.0
 */
public interface EngineWorkspaceManager {
	
	/**
	 * 显示产品线列表
	 * @param username
	 * @return
	 */
	public List<EngineProductDto> showProductList(String username);
	
	/**
	 * 创建一个产品线
	 * @param username
	 * @param productType
	 * @param productName
	 */
	public void createProduct(String username,String productType,String productName,String groupId);
	
	
	
	/**
	 * 显示组件列表
	 * @param username
	 * @param productType
	 * @param cpntType
	 */
	public List<EngineCpntListDisplay> showCpntList(String username,String productType,String cpntType,RuleModelTable table,String version);
	
	
	/**
	 * 显示分支stage列表
	 * @param username
	 * @return
	 */
	public List<EngineCpntListDisplay> showRouteStageList(String username,String productType);
	
	/**
	 * 下发规则模型对象
	 * @param operatorId
	 * @param productType
	 * @return
	 */
	//public List<JSONObject> downloadRuleModelFile(String username,String productType);
	
	/**
	 * 保存规则模型对象到自己的工作空间
	 * @param operatorId
	 * @param productType
	 * @param newContent
	 */
	//public void saveRuleModel(String username,String productType,JSONObject json);
	
	/**
	 * 新增规则模型对象到自己的工作空间
	 * @param operatorId
	 * @param productType
	 * @param newContent
	 */
	public String insertRuleModel(String username,String productType,JSONObject json);
	
	/**
	 * 更新规则模型对象到自己的工作空间
	 * @param operatorId
	 * @param productType
	 * @param newContent
	 */
	public void updateRuleModel(String username,String productType,JSONObject json);
	
	/**
	 * 删除规则模型对象到自己的工作空间
	 * @param operatorId
	 * @param productType
	 * @param newContent
	 */
	public void removeRuleModel(String username,String productType,String cpntId);
	
	/**
	 * 显示某个组件信息
	 * @param username
	 * @param productType
	 * @param cpntId
	 * @return
	 */
	public JSONObject showOneRuleModel(String username,String productType,String cpntId);
	/**
	 * 显示某个组件信息
	 * @param username
	 * @param productType
	 * @param cpntId
	 * @return
	 */
	public JSONObject showMergeRuleModel(String username,String productType,String cpntId);	
	/**
	 * 显示某个组件信息
	 * @param username
	 * @param productType
	 * @param cpntId
	 * @param version
	 * @return
	 */
	public JSONObject showHistoryRuleModel(String username,String productType,String cpntId,String version);
	
	/**
	 * 将自己的组件合并产品线，同步块
	 * @param operator
	 * @param productType
	 * @param ins
	 */
	public void mergeRuleModel(String username,String productType);
	
	
	/**
	 * 查看某个产品的历史版本
	 * @param productType
	 * @return
	 */
	public List<EngProductReleaseFlow> showRealeaseList(String username,String productType,String status,String matchVersion,String applyUser,Boolean isPrd);
	
	/**
	 * 申请发布
	 * @param username
	 * @param productType
	 */
	public Long applyRelease(String username,String productType,String versionName);
	
	
	/**
	 * 第一层审核
	 * @param username
	 * @param productType
	 */
	@Deprecated
	public void auditRelease1(String username,Long productReleaseFlowId);
	/**
	 * 第二层审核
	 * @param username
	 * @param productType
	 */
	@Deprecated
	public void auditRelease2(String username,Long productReleaseFlowId);
	/**
	 * 发布到生产
	 * @param productType
	 */
	public void release(String username,Long productReleaseFlowId);
	/**
	 * 根据发布记录获取规则内容
	 * @param productType
	 */
	public EngineFileContent getRuleBytesByFlow(String username,Long productReleaseFlowId);
	
	/**
	 * 没有流程的紧急发布，发布最新的merge区域,慎用
	 * @param username
	 * @param productType
	 */
	public void urgencyRelease(String username,String productType);
	/**
	 * 上传到运行环境，只能用于生产上下载后倒入到sat环境中
	 * @param productType
	 */
	public void uploadRelease(String username,EngineFileContent ruleModelData);
	/**
	 * 将最新的规则发布到生产 用于上线时候
	 * @param productType
	 */
	public void currentRelease();
	
	/**
	 * 回滚
	 * @param username
	 * @param productReleaseFlowId
	 */
	public void rollbackRelease(String username,Long productReleaseFlowId);
	
	/**
	 * 撤销发布
	 * @param username
	 * @param productReleaseFlowId
	 */
	public void cancelRelease(String username,Long productReleaseFlowId);
	
	
	/**
	 * 获取查询组件信息
	 * @param productType
	 * @return
	 */
	//public List<EngineQuery> getQueryCpnts(String productType);
	
	/**
	 * 测试解析
	 * @param ruleModelTable
	 * @param productType
	 */
	@Deprecated
	public EngineCpntContainer parseProduct(RuleModelTable ruleModelTable,String productType);
	
	
	
	/**
	 * 显示树结构
	 * @param productType
	 * @return
	 */
	public JSONObject showEngineFlow(String username,String productType);
	
	/**
	 * 语法检查
	 * @param productType
	 * @param cpntId
	 * @return
	 */
	public List<EngineScriptSyntaxErr> checkScriptSyntax(String productType,String cpntId);
	
	/**
	 * 模块测试
	 * @param username
	 * @param productType
	 * @param cpntId
	 * @param json 参数，以json格式提现
	 * @return
	 */
	public List<EngineTestReturnDto> moduleTest(String username,String productType,String cpntId,String input,String output);
	
	/**
	 * 获取客户单元测试习惯参数
	 * @param productType
	 * @param cpntId
	 * @return
	 */
	public Map<String,Map<String,Object>> getModuleTestCustomParam(String productType,String cpntId);
	
	/**
	 * 整体测试
	 * @param username
	 * @param productType
	 * @param cpntId
	 * @param input
	 * @param output
	 * @return
	 */
	public List<EngineTestReturnDto> wholeTest(String username,String productType,String input,String output);
	
	
	/**
	 * 复制组件（深克隆）
	 * @param username
	 * @param fromProduct
	 * @param toProduct
	 */
	public List<EngineCpntListDisplay> copyCpnt(String username,String fromProduct,String toProduct,String... cpntIds);
	
	/**
	 * 
	 * @param username
	 * @param fromProduct
	 * @param toProduct
	 * @return 
	 */
	public String setRuleModelFlow(String username,String productType,JSONObject json);
	
	/**
	 * 获取相关字段列表
	 * @param username
	 * @param productType
	 * @param cpntId
	 * @return
	 */
	public List<EngineDefineFieldDto> getRelaDefineField(String username,String productType,String cpntId);
	
	/**
	 * 比较两个版本有变化的组件
	 * @param productType
	 * @param oneVersion
	 * @param anotherVersion
	 * @return
	 */
	public List<EngineCpntCompareGroupDto> compareCpntByVersionAndVersion(String productType,String oneVersion,String anotherVersion);
	
	/**
	 * 比较当前版本和过去的某版本
	 * @param productType
	 * @param anotherVersion
	 * @return
	 */
	public List<EngineCpntCompareGroupDto> compareCpntByCurrentAndVersion(String productType,String username, String anotherVersion);
	
	/**
	 * 比较当前版本和生产的
	 * @param productType
	 * @param anotherVersion
	 * @return
	 */
	public List<EngineCpntCompareGroupDto> compareCpntByCurrentAndProd(String productType,String username);
	
	
	/**
	 * 比较两个版本有变化的组件
	 * @param productType
	 * @param oneVersion
	 * @param anotherVersion
	 * @return
	 */
	public List<EngineCpntCompareGroupDto> compareCpntByVersionAndProd(String productType,String username,String newVersion);
	
	/**
	 * 进行fat环境测试
	 * @param productType
	 * @param username
	 */
	public void fatTest(String productType,String username);
	
	
	/**
	 * 用户解锁工程模块
	 * @param productType
	 * @param username
	 */
	public void userUnlockProduct(String username);	
	
	
	public EngineCpntContainer getProductContainer(RuleModelTable modelTable,String productType);
	
	/**
	 * 发一个demo版本
	 */
	public void demoRelease();
	
	/**
	 * 模拟生产数据进行测试
	 */
	public void snapshotBatchRun(SnapshotBatchRunDto dto);

	/**
	 * 新建分组
	 */
	public void createGroup(String username, String groupType, String groupName);

	/**
	 * 通过分组查询工程模块
	 */
	public List<EngineProductDto> showProductListByGroup(String groupId);

	byte[] getRuleZipBytes();

	String deployRuleModel2Test(List<EngineFileContent> ruleContentList);

	void deployRuleModel2Test(String zkAddress);

	void zipFileUpload(MultipartFile multiFile);

	JSONObject showHistoryPolicyFlowModel(String username,
			Long releaseFlowId);

	JSONObject showEngineFlowByVersion(String username, String productType,
			String version);

}
