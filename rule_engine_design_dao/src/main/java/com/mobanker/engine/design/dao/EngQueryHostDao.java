package com.mobanker.engine.design.dao;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import com.mobanker.engine.design.pojo.EngQueryHost;
/**
 * 
 * <p>Company: mobanker.com</p>
 * @author machine_tao
 * @version 1.0
 */
public interface EngQueryHostDao{

	public int insert(EngQueryHost engQueryHost);
	
	public int batchInsert(List<EngQueryHost> list);
	
	public int update(EngQueryHost engQueryHost);
	
	/**
	 * 如果pojo中的成员变量不为null，则更新
	 * @param EngQueryHost，其中主键值不能为空
	 * @return
	 */	
	public int updateField(EngQueryHost engQueryHost);
	
	/**
	 * 注意map.src如果没有成员变量，则条件为1!=1，详情查看mybatis xml
	 * @param map.src(作为条件) map.des(作为结果) 
	 * @return
	 */	
	public int updateFieldByField(Map<String,EngQueryHost> map);
	
	public int delete(EngQueryHost engQueryHost);
	
	public int deleteById(Long id);
	
	public EngQueryHost get(Long id);	
	
	public EngQueryHost queryLastOneByField(EngQueryHost engQueryHost);
	
	public List<EngQueryHost> queryByField(EngQueryHost engQueryHost);
	
	public List<EngQueryHost> queryInField(Map<String,Object> map);
		
	public List<EngQueryHost> queryByProductType(String productType);
	
	public int queryCountByProductType(String productType);
	
	public EngQueryHost queryLastOneByProductType(String productType);
	
	public List<EngQueryHost> queryBySomeProductType(List<String> list);
	
	public int queryCountBySomeProductType(List<String> list);


}
