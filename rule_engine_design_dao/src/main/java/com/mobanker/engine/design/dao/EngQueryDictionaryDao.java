package com.mobanker.engine.design.dao;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import com.mobanker.engine.design.pojo.EngQueryDictionary;
/**
 * 
 * <p>Company: mobanker.com</p>
 * @author machine_tao
 * @version 1.0
 */
public interface EngQueryDictionaryDao{

	public int insert(EngQueryDictionary engQueryDictionary);
	
	public int batchInsert(List<EngQueryDictionary> list);
	
	public int update(EngQueryDictionary engQueryDictionary);
	
	/**
	 * 如果pojo中的成员变量不为null，则更新
	 * @param EngQueryDictionary，其中主键值不能为空
	 * @return
	 */	
	public int updateField(EngQueryDictionary engQueryDictionary);
	
	/**
	 * 注意map.src如果没有成员变量，则条件为1!=1，详情查看mybatis xml
	 * @param map.src(作为条件) map.des(作为结果) 
	 * @return
	 */	
	public int updateFieldByField(Map<String,EngQueryDictionary> map);
	
	public int delete(EngQueryDictionary engQueryDictionary);
	
	public int deleteById(Long id);
	
	public EngQueryDictionary get(Long id);	
	
	public EngQueryDictionary queryLastOneByField(EngQueryDictionary engQueryDictionary);
	
	public List<EngQueryDictionary> queryByField(EngQueryDictionary engQueryDictionary);
	
	public List<EngQueryDictionary> queryInField(Map<String,Object> map);
		
	public List<EngQueryDictionary> queryByQueryId(String queryId);
	
	public int queryCountByQueryId(String queryId);
	
	public EngQueryDictionary queryLastOneByQueryId(String queryId);
	
	public List<EngQueryDictionary> queryBySomeQueryId(List<String> list);
	
	public int queryCountBySomeQueryId(List<String> list);

	public List<EngQueryDictionary> queryByProductType(String productType);
	
	public int queryCountByProductType(String productType);
	
	public EngQueryDictionary queryLastOneByProductType(String productType);
	
	public List<EngQueryDictionary> queryBySomeProductType(List<String> list);
	
	public int queryCountBySomeProductType(List<String> list);

	/*------------------------------------------------------------------------------------------------------------*/
	
	public int deleteByProductType(String productType);

}
