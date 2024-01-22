package com.mobanker.engine.design.dao;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import com.mobanker.engine.design.pojo.EngProductReleaseFlow;
/**
 * 
 * <p>Company: mobanker.com</p>
 * @author machine_tao
 * @version 1.0
 */
public interface EngProductReleaseFlowDao{

	public int insert(EngProductReleaseFlow engProductReleaseFlow);
	
	public int batchInsert(List<EngProductReleaseFlow> list);
	
	public int update(EngProductReleaseFlow engProductReleaseFlow);
	
	/**
	 * 如果pojo中的成员变量不为null，则更新
	 * @param EngProductReleaseFlow，其中主键值不能为空
	 * @return
	 */	
	public int updateField(EngProductReleaseFlow engProductReleaseFlow);
	
	/**
	 * 注意map.src如果没有成员变量，则条件为1!=1，详情查看mybatis xml
	 * @param map.src(作为条件) map.des(作为结果) 
	 * @return
	 */	
	public int updateFieldByField(Map<String,EngProductReleaseFlow> map);
	
	public int delete(EngProductReleaseFlow engProductReleaseFlow);
	
	public int deleteById(Long id);
	
	public EngProductReleaseFlow get(Long id);	
	
	public EngProductReleaseFlow queryLastOneByField(EngProductReleaseFlow engProductReleaseFlow);
	
	public List<EngProductReleaseFlow> queryByField(EngProductReleaseFlow engProductReleaseFlow);
	
	public List<EngProductReleaseFlow> queryInField(Map<String,Object> map);
		
	public List<EngProductReleaseFlow> queryByProductType(String productType);
	
	public int queryCountByProductType(String productType);
	
	public EngProductReleaseFlow queryLastOneByProductType(String productType);
	
	public List<EngProductReleaseFlow> queryBySomeProductType(List<String> list);
	
	public int queryCountBySomeProductType(List<String> list);

	public List<EngProductReleaseFlow> queryByApplyUser(String applyUser);
	
	public int queryCountByApplyUser(String applyUser);
	
	public EngProductReleaseFlow queryLastOneByApplyUser(String applyUser);
	
	public List<EngProductReleaseFlow> queryBySomeApplyUser(List<String> list);
	
	public int queryCountBySomeApplyUser(List<String> list);
	
	/****************************************************************************************/

	public List<EngProductReleaseFlow> showList(EngProductReleaseFlow engProductReleaseFlow);
}
