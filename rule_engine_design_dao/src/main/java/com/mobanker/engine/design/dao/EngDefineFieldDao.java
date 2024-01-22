package com.mobanker.engine.design.dao;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import com.mobanker.engine.design.pojo.EngDefineField;
/**
 * 
 * <p>Company: mobanker.com</p>
 * @author machine_tao
 * @version 1.0
 */
public interface EngDefineFieldDao{

	public int insert(EngDefineField engDefineField);
	
	public int batchInsert(List<EngDefineField> list);
	
	public int update(EngDefineField engDefineField);
	
	/**
	 * 如果pojo中的成员变量不为null，则更新
	 * @param EngDefineField，其中主键值不能为空
	 * @return
	 */	
	public int updateField(EngDefineField engDefineField);
	
	/**
	 * 注意map.src如果没有成员变量，则条件为1!=1，详情查看mybatis xml
	 * @param map.src(作为条件) map.des(作为结果) 
	 * @return
	 */	
	public int updateFieldByField(Map<String,EngDefineField> map);
	
	public int delete(EngDefineField engDefineField);
	
	public int deleteById(Long id);
	
	public EngDefineField get(Long id);	
	
	public EngDefineField queryLastOneByField(EngDefineField engDefineField);
	
	public List<EngDefineField> queryByField(EngDefineField engDefineField);
	
	public List<EngDefineField> queryInField(Map<String,Object> map);
		
	public List<EngDefineField> queryByFieldKey(String fieldKey);
	
	public int queryCountByFieldKey(String fieldKey);
	
	public EngDefineField queryLastOneByFieldKey(String fieldKey);
	
	public List<EngDefineField> queryBySomeFieldKey(List<String> list);
	
	public int queryCountBySomeFieldKey(List<String> list);

	public List<EngDefineField> queryByFieldName(String fieldName);
	
	public int queryCountByFieldName(String fieldName);
	
	public EngDefineField queryLastOneByFieldName(String fieldName);
	
	public List<EngDefineField> queryBySomeFieldName(List<String> list);
	
	public int queryCountBySomeFieldName(List<String> list);

	public List<EngDefineField> queryByFieldUse(String fieldUse);
	
	public int queryCountByFieldUse(String fieldUse);
	
	public EngDefineField queryLastOneByFieldUse(String fieldUse);
	
	public List<EngDefineField> queryBySomeFieldUse(List<String> list);
	
	public int queryCountBySomeFieldUse(List<String> list);

	public List<EngDefineField> queryByUsername(String username);
	
	public int queryCountByUsername(String username);
	
	public EngDefineField queryLastOneByUsername(String username);
	
	public List<EngDefineField> queryBySomeUsername(List<String> list);
	
	public int queryCountBySomeUsername(List<String> list);

	public List<EngDefineField> queryByProductType(String productType);
	
	public int queryCountByProductType(String productType);
	
	public EngDefineField queryLastOneByProductType(String productType);
	
	public List<EngDefineField> queryBySomeProductType(List<String> list);
	
	public int queryCountBySomeProductType(List<String> list);

	/*$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	public EngDefineField queryLastOneByFieldKeyAndProductType(String fieldKey,String productType);
	public EngDefineField queryLastOneByFieldNameAndProductType(String fieldName,String productType);
	public int deleteByProductType(String productType);	
}
