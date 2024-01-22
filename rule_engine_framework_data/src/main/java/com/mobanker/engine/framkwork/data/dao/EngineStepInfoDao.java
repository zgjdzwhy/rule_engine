package com.mobanker.engine.framkwork.data.dao;

import java.util.List;
import java.util.Map;

import com.mobanker.engine.framkwork.data.entity.EngineStepInfo;
/**
 * 
 * <p>Company: mobanker.com</p>
 * @author machine_tao
 * @version 1.0
 */
public interface EngineStepInfoDao{

public int insert(EngineStepInfo engineStepInfo);
	
	public int batchInsert(List<EngineStepInfo> list);
	
	public int update(EngineStepInfo engineStepInfo);
	
	/**
	 * 如果pojo中的成员变量不为null，则更新
	 * @param EngineStepInfo，其中主键值不能为空
	 * @return
	 */	
	public int updateField(EngineStepInfo engineStepInfo);
	
	/**
	 * 注意map.src如果没有成员变量，则条件为1!=1，详情查看mybatis xml
	 * @param map.src(作为条件) map.des(作为结果) 
	 * @return
	 */	
	public int updateFieldByField(Map<String,EngineStepInfo> map);
	
	public int delete(EngineStepInfo engineStepInfo);
	
	public int deleteById(Long id);
	
	public EngineStepInfo get(Long id);	
	
	public EngineStepInfo queryLastOneByField(EngineStepInfo engineStepInfo);
	
	public List<EngineStepInfo> queryByField(EngineStepInfo engineStepInfo);
	
	public List<EngineStepInfo> queryInField(Map<String,Object> map);
		
	public List<EngineStepInfo> queryByTaskId(Long taskId);
	
	public int queryCountByTaskId(Long taskId);
	
	public EngineStepInfo queryLastOneByTaskId(Long taskId);
	
	public List<EngineStepInfo> queryBySomeTaskId(List<Long> list);
	
	public int queryCountBySomeTaskId(List<Long> list);

	public List<EngineStepInfo> queryByAppRequestId(String appRequestId);
	
	public int queryCountByAppRequestId(String appRequestId);
	
	public EngineStepInfo queryLastOneByAppRequestId(String appRequestId);
	
	public List<EngineStepInfo> queryBySomeAppRequestId(List<String> list);
	
	public int queryCountBySomeAppRequestId(List<String> list);
		
	
	/**********************************************************************************************/ 
	
	public List<EngineStepInfo> queryLastFailTaskSteps(Long taskId);
}
