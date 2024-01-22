package com.mobanker.engine.design.dao;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import com.mobanker.engine.design.pojo.EngUserLog;
/**
 * 
 * <p>Company: mobanker.com</p>
 * @author machine_tao
 * @version 1.0
 */
public interface EngUserLogDao{

	public int insert(EngUserLog engUserLog);
	
	public int batchInsert(List<EngUserLog> list);
	
	public int update(EngUserLog engUserLog);
	
	/**
	 * 如果pojo中的成员变量不为null，则更新
	 * @param EngUserLog，其中主键值不能为空
	 * @return
	 */	
	public int updateField(EngUserLog engUserLog);
	
	/**
	 * 注意map.src如果没有成员变量，则条件为1!=1，详情查看mybatis xml
	 * @param map.src(作为条件) map.des(作为结果) 
	 * @return
	 */	
	public int updateFieldByField(Map<String,EngUserLog> map);
	
	public int delete(EngUserLog engUserLog);
	
	public int deleteById(Long id);
	
	public EngUserLog get(Long id);	
	
	public EngUserLog queryLastOneByField(EngUserLog engUserLog);
	
	public List<EngUserLog> queryByField(EngUserLog engUserLog);
	
	public List<EngUserLog> queryInField(Map<String,Object> map);
		

}
