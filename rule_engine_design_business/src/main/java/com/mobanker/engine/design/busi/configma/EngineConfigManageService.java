package com.mobanker.engine.design.busi.configma;

import java.util.List;

import com.mobanker.engine.design.dto.EngineDefineFieldDto;
import com.mobanker.engine.design.dto.EngineGroupDto;
import com.mobanker.engine.design.dto.EngineMemberDto;
import com.mobanker.engine.design.dto.EngineProductDto;
import com.mobanker.engine.design.dto.EngineQueryModelDto;
import com.mobanker.engine.design.dto.EngineRuleAllConfigDto;
import com.mobanker.engine.design.dto.EngineTestZkDto;
import com.mobanker.engine.design.pojo.EngFatTest;
import com.mobanker.engine.design.pojo.EngScriptFunction;
import com.mobanker.engine.design.pojo.EngScriptTemplate;


/**
 * 配置管理服务
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月5日
 * @version 1.0
 */
public interface EngineConfigManageService {
	
	/**
	 * 查询根据操作员查询组件
	 * @return
	 */
	@Deprecated
	public List<EngineQueryModelDto> getQueryModelList(String username,String productType);
	@Deprecated
	public List<EngineQueryModelDto> getQueryModelList(String productType);
	
	/**
	 * 查询引擎参数列表
	 * @param username
	 * @param productType
	 * @return
	 */
	public List<EngineDefineFieldDto> getEngineParamList(String username,String productType);
	public List<EngineDefineFieldDto> getEngineParamList(String productType);

	public List<EngineDefineFieldDto> getDefineFieldList(String username,String productType);
	public List<EngineDefineFieldDto> getDefineFieldList(String productType);
	public EngineDefineFieldDto getDefineField(String productType,String fieldKey);
	
	public void insertDefineField(String username,String productType,EngineDefineFieldDto dto);
	public void updateDefineField(String username,String productType,EngineDefineFieldDto dto);
	public void importDefineField(String username,String productType,List<EngineDefineFieldDto> dtoList);
	public void deleteDefineField(String username,String productType,Long id);
	
	/**
	 * 显示脚本标签提示信息
	 * @return
	 */
	public List<EngScriptTemplate> showScriptTemplate(String productType);		
	
	public void addScriptTemplate(EngScriptTemplate entity);
	public void modScriptTemplate(EngScriptTemplate entity);
	public void delScriptTemplate(String username,String productType,String label);
	
	
	/**
	 * 显示函数列表信息
	 * @return
	 */
	public List<EngScriptFunction> showScriptFunction(String productType);
	public List<EngScriptFunction> showCommonFunction();
	public void addScriptFunction(EngScriptFunction entity);
	public void modScriptFunction(EngScriptFunction entity);
	public void delScriptFunction(String username,String productType,String label);
	
	/**
	 * 显示成员列表
	 * @param username
	 * @return
	 */
	public List<EngineMemberDto> showMemberInfo();

	/**
	 * 显示所有产品列表
	 * @param username
	 * @return
	 */
	public List<EngineProductDto> showAllProduct();	
	
	/**
	 * 配置fat测试地址
	 * @param username
	 * @return
	 */
	public void configFatTestAddress(Long id,String productType,String url);
	
	/**
	 * 
	 * @param id
	 * @param productType
	 * @param url
	 * @return mapKey,productType,url
	 */
	public List<EngFatTest> showFatTestConfig(String productType);	
	

	/**
	 * 导出产品线所有配置信息
	 * @param productType
	 * @return
	 */
	@Deprecated
	public EngineRuleAllConfigDto exportProduct(String productType);
	/**
	 * 导入产品线所有配置信息，如果该产品线已经存在则删除原有的产品线
	 * @param dto
	 * @return
	 */
	@Deprecated
	public void importProduct(String username,EngineRuleAllConfigDto dto,boolean isReplace);
	
	/** 
	* @Title: showAllGroup 
	* @Description: TODO(获取所有组) 
	* 参数@return
	* @return List<EngineGroupDto>    返回类型 
	* @throws 
	*/
	public List<EngineGroupDto> showAllGroup();
	
	/** 
	* @Title: updateTestZk 
	* @Description: 修改zk配置信息 
	* 参数@param id
	* 参数@param zkName
	* 参数@param zkUrl
	* 参数@param status
	* 参数@param memo
	* @return void    返回类型 
	* @throws 
	*/
	void updateTestZk(String id, String zkName, String zkUrl, String status,
			String memo);
	
	/** 
	* @Title: insertTestZk 
	* @Description: 新增zk配置信息 
	* 参数@param username
	* 参数@param zkName
	* 参数@param zkUrl
	* 参数@param memo
	* @return void    返回类型 
	* @throws 
	*/
	void insertTestZk(String username, String zkName, String zkUrl, String memo);
	
	/** 
	* @Title: queryTestZkById 
	* @Description: 根据id查询zk配置信息
	* 参数@param id
	* 参数@return
	* @return EngineTestZkDto    返回类型 
	* @throws 
	*/
	EngineTestZkDto queryTestZkById(String id);
	
	/** 
	* @Title: showAllTestZk 
	* @Description: 查询所有zk配置信息
	* 参数@return
	* @return List<EngineTestZkDto>    返回类型 
	* @throws 
	*/
	List<EngineTestZkDto> showAllTestZk(String status);
	
	/** 
	* @Title: deleteTestZk 
	* @Description: 删除
	* 参数@param id
	* @return void    返回类型 
	* @throws 
	*/
	void deleteTestZk(String id);
	
	
}

