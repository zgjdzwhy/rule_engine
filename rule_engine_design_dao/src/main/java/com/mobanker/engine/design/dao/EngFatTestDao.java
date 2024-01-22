package com.mobanker.engine.design.dao;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import com.mobanker.engine.design.pojo.EngFatTest;
/**
 * 
 * <p>Company: mobanker.com</p>
 * @author machine_tao
 * @version 1.0
 */
public interface EngFatTestDao{

	public int insert(EngFatTest engFatTest);
	
	public int batchInsert(List<EngFatTest> list);
	
	public int update(EngFatTest engFatTest);
	
	/**
	 * 如果pojo中的成员变量不为null，则更新
	 * @param EngFatTest，其中主键值不能为空
	 * @return
	 */	
	public int updateField(EngFatTest engFatTest);
	
	/**
	 * 注意map.src如果没有成员变量，则条件为1!=1，详情查看mybatis xml
	 * @param map.src(作为条件) map.des(作为结果) 
	 * @return
	 */	
	public int updateFieldByField(Map<String,EngFatTest> map);
	
	public int delete(EngFatTest engFatTest);
	
	public int deleteById(Long id);
	
	public EngFatTest get(Long id);	
	
	public EngFatTest queryLastOneByField(EngFatTest engFatTest);
	
	public List<EngFatTest> queryByField(EngFatTest engFatTest);
	
	public List<EngFatTest> queryInField(Map<String,Object> map);
		
	public List<EngFatTest> queryByProductType(String productType);
	
	public int queryCountByProductType(String productType);
	
	public EngFatTest queryLastOneByProductType(String productType);
	
	public List<EngFatTest> queryBySomeProductType(List<String> list);
	
	public int queryCountBySomeProductType(List<String> list);


}
