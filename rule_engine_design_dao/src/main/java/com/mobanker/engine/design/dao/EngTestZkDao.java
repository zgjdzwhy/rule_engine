package com.mobanker.engine.design.dao;

import java.util.List;
import java.util.Map;

import com.mobanker.engine.design.pojo.EngTestZk;
/**
 * 
 * <p>Company: mobanker.com</p>
 * @author machine_tao
 * @version 1.0
 */
public interface EngTestZkDao{

	public int insert(EngTestZk engTestZk);
	
	public int batchInsert(List<EngTestZk> list);
	
	public int update(EngTestZk engTestZk);
	
	/**
	 * 如果pojo中的成员变量不为null，则更新
	 * @param EngTestZk，其中主键值不能为空
	 * @return
	 */	
	public int updateField(EngTestZk engTestZk);
	
	/**
	 * 注意map.src如果没有成员变量，则条件为1!=1，详情查看mybatis xml
	 * @param map.src(作为条件) map.des(作为结果) 
	 * @return
	 */	
	public int updateFieldByField(Map<String,EngTestZk> map);
	
	public int delete(EngTestZk engTestZk);
	
	public int deleteById(Long id);
	
	public EngTestZk get(Long id);	
	
	public EngTestZk queryLastOneByField(EngTestZk engTestZk);
	
	public List<EngTestZk> queryByField(EngTestZk engTestZk);
	
	public List<EngTestZk> queryInField(Map<String,Object> map);
		
	/****************************************************************/
	
	
}
