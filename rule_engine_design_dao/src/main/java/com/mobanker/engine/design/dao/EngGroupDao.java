package com.mobanker.engine.design.dao;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import org.springframework.cache.annotation.Cacheable;

import com.mobanker.engine.design.pojo.EngGroup;
import com.mobanker.engine.design.pojo.EngProduct;
/**
 * 
 * <p>Company: mobanker.com</p>
 * @author machine_tao
 * @version 1.0
 */
public interface EngGroupDao{

	public int insert(EngGroup engGroup);
	
	public int batchInsert(List<EngGroup> list);
	
	public int update(EngGroup engGroup);
	
	/**
	 * 如果pojo中的成员变量不为null，则更新
	 * @param EngGroup，其中主键值不能为空
	 * @return
	 */	
	public int updateField(EngGroup engGroup);
	
	public int delete(EngGroup engGroup);
	
	public int deleteById(Long id);
	
	public EngGroup get(Long id);	
	
	public EngGroup queryLastOneByField(EngGroup engGroup);
	
	public List<EngGroup> queryByField(EngGroup engGroup);
	
	public List<EngGroup> queryInField(Map<String,Object> map);
		
	public List<EngGroup> queryByGroupType(String groupType);
	
	public int queryCountByGroupType(String groupType);
	
	@Cacheable(value="rule_default",key="'cacheEngGroup'+#root.args[0]")
	public EngGroup queryLastOneByGroupType(String groupType);
	
	public List<EngGroup> queryBySomeGroupType(List<String> list);
	
	public int queryCountBySomeGroupType(List<String> list);


}
