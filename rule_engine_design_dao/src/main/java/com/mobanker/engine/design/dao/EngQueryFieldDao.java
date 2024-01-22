package com.mobanker.engine.design.dao;

import java.util.List;
import java.util.Map;

import com.mobanker.engine.design.pojo.EngQueryField;
/**
 * 
 * <p>Company: mobanker.com</p>
 * @author machine_tao
 * @version 1.0
 */
public interface EngQueryFieldDao{

	public int insert(EngQueryField engQueryField);
	
	public int batchInsert(List<EngQueryField> list);
	
	public int update(EngQueryField engQueryField);
	
	/**
	 * 如果pojo中的成员变量不为null，则更新
	 * @param EngQueryField，其中主键值不能为空
	 * @return
	 */	
	public int updateField(EngQueryField engQueryField);
	
	/**
	 * 注意map.src如果没有成员变量，则条件为1!=1，详情查看mybatis xml
	 * @param map.src(作为条件) map.des(作为结果) 
	 * @return
	 */	
	public int updateFieldByField(Map<String,EngQueryField> map);
	
	public int delete(EngQueryField engQueryField);
	
	public int deleteById(Long id);
	
	public EngQueryField get(Long id);	
	
	public EngQueryField queryLastOneByField(EngQueryField engQueryField);
	
	public List<EngQueryField> queryByField(EngQueryField engQueryField);
	
	public List<EngQueryField> queryInField(Map<String,Object> map);
		
	public List<EngQueryField> queryByQueryId(String queryId);
	
	public int queryCountByQueryId(String queryId);
	
	public EngQueryField queryLastOneByQueryId(String queryId);
	
	public List<EngQueryField> queryBySomeQueryId(List<String> list);
	
	public int queryCountBySomeQueryId(List<String> list);

	public List<EngQueryField> queryByFieldKey(String fieldKey);
	
	public int queryCountByFieldKey(String fieldKey);
	
	public EngQueryField queryLastOneByFieldKey(String fieldKey);
	
	public List<EngQueryField> queryBySomeFieldKey(List<String> list);
	
	public int queryCountBySomeFieldKey(List<String> list);

	public List<EngQueryField> queryByFieldName(String fieldName);
	
	public int queryCountByFieldName(String fieldName);
	
	public EngQueryField queryLastOneByFieldName(String fieldName);
	
	public List<EngQueryField> queryBySomeFieldName(List<String> list);
	
	public int queryCountBySomeFieldName(List<String> list);

	/*$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	public EngQueryField queryLastOneByFieldKeyAndProductType(String fieldKey,String productType);
	public EngQueryField queryLastOneByFieldNameAndProductType(String fieldName,String productType);
	public int deleteByProductType(String productType);	
	public List<EngQueryField> queryByQueryIdAndProductType(String queryId,String productType);
}
