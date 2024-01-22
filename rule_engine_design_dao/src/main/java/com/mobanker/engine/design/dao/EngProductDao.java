package com.mobanker.engine.design.dao;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import org.springframework.cache.annotation.Cacheable;

import com.mobanker.engine.design.pojo.EngProduct;
/**
 * 
 * <p>Company: mobanker.com</p>
 * @author machine_tao
 * @version 1.0
 */
public interface EngProductDao{

	public int insert(EngProduct engProduct);
	
	public int batchInsert(List<EngProduct> list);
	
	public int update(EngProduct engProduct);
	
	/**
	 * 如果pojo中的成员变量不为null，则更新
	 * @param EngProduct，其中主键值不能为空
	 * @return
	 */	
	public int updateField(EngProduct engProduct);
	
	/**
	 * 注意map.src如果没有成员变量，则条件为1!=1，详情查看mybatis xml
	 * @param map.src(作为条件) map.des(作为结果) 
	 * @return
	 */	
	public int updateFieldByField(Map<String,EngProduct> map);
	
	public int delete(EngProduct engProduct);
	
	public int deleteById(Long id);
	
	public EngProduct get(Long id);	
	
	public EngProduct queryLastOneByField(EngProduct engProduct);
	
	public List<EngProduct> queryByField(EngProduct engProduct);
	
	public List<EngProduct> queryInField(Map<String,Object> map);
		
	public List<EngProduct> queryByProductType(String productType);
	
	public int queryCountByProductType(String productType);
	
	@Cacheable(value="rule_default",key="'cacheEngProduct'+#root.args[0]")
	public EngProduct queryLastOneByProductType(String productType);
	
	public List<EngProduct> queryBySomeProductType(List<String> list);
	
	public int queryCountBySomeProductType(List<String> list);


}
