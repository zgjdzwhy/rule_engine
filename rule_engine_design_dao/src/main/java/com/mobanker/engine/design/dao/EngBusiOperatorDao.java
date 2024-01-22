package com.mobanker.engine.design.dao;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.mobanker.engine.design.pojo.EngBusiOperator;
import com.mobanker.framework.tracking.EETransaction;
/**
 * 
 * <p>Company: mobanker.com</p>
 * @author machine_tao
 * @version 1.0
 */
public interface EngBusiOperatorDao{

	public int insert(EngBusiOperator engBusiOperator);
	
	public int batchInsert(List<EngBusiOperator> list);
	
	@CacheEvict(value="rule_default",key="'cacheEngBusiOperator'+#root.args[0].getUsername()")
	public int update(EngBusiOperator engBusiOperator);
	
	/**
	 * 如果pojo中的成员变量不为null，则更新
	 * @param EngBusiOperator，其中主键值不能为空
	 * @return
	 */	
	public int updateField(EngBusiOperator engBusiOperator);
	
	/**
	 * 注意map.src如果没有成员变量，则条件为1!=1，详情查看mybatis xml
	 * @param map.src(作为条件) map.des(作为结果) 
	 * @return
	 */	
	public int updateFieldByField(Map<String,EngBusiOperator> map);
	
	public int delete(EngBusiOperator engBusiOperator);
	
	public int deleteById(Long id);
	
	public EngBusiOperator get(Long id);	
	
	public EngBusiOperator queryLastOneByField(EngBusiOperator engBusiOperator);
	
	public List<EngBusiOperator> queryByField(EngBusiOperator engBusiOperator);
	
	public List<EngBusiOperator> queryInField(Map<String,Object> map);
		
	public List<EngBusiOperator> queryByUsername(String username);
	
	public int queryCountByUsername(String username);
	
	@Cacheable(value="rule_default",key="'cacheEngBusiOperator'+#root.args[0]")
	public EngBusiOperator queryLastOneByUsername(String username);
	
	public List<EngBusiOperator> queryBySomeUsername(List<String> list);
	
	public int queryCountBySomeUsername(List<String> list);

	public List<EngBusiOperator> queryByProductIds(String productIds);
	
	public int queryCountByProductIds(String productIds);
	
	public EngBusiOperator queryLastOneByProductIds(String productIds);
	
	public List<EngBusiOperator> queryBySomeProductIds(List<String> list);
	
	public int queryCountBySomeProductIds(List<String> list);


}
