package com.mobanker.engine.framkwork.data.dao;

import java.util.List;
import java.util.Map;

import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
/**
 * 
 * <p>Company: mobanker.com</p>
 * @author machine_tao
 * @version 1.0
 */
public interface EngineTaskInfoDao{

	public int insert(EngineTaskInfo engineTaskInfo);
	
	public int batchInsert(List<EngineTaskInfo> list);
	
	public int update(EngineTaskInfo engineTaskInfo);
	
	/**
	 * 如果pojo中的成员变量不为null，则更新
	 * @param EngineTaskInfo，其中主键值不能为空
	 * @return
	 */	
	public int updateField(EngineTaskInfo engineTaskInfo);
	
	/**
	 * 注意map.src如果没有成员变量，则条件为1!=1，详情查看mybatis xml
	 * @param map.src(作为条件) map.des(作为结果) 
	 * @return
	 */	
	public int updateFieldByField(Map<String,EngineTaskInfo> map);
	
	public int delete(EngineTaskInfo engineTaskInfo);
	
	public int deleteById(Long id);
	
	public EngineTaskInfo get(Long id);	
	
	public EngineTaskInfo queryLastOneByField(EngineTaskInfo engineTaskInfo);
	
	public List<EngineTaskInfo> queryByField(EngineTaskInfo engineTaskInfo);
	
	public List<EngineTaskInfo> queryInField(Map<String,Object> map);
		
	public List<EngineTaskInfo> queryByAssignStep(Integer assignStep);
	
	public int queryCountByAssignStep(Integer assignStep);
	
	public EngineTaskInfo queryLastOneByAssignStep(Integer assignStep);
	
	public List<EngineTaskInfo> queryBySomeAssignStep(List<Integer> list);
	
	public int queryCountBySomeAssignStep(List<Integer> list);

	//----------------------------------------------------------------------------------------------------------------
	public List<EngineTaskInfo> getNeedAssignTaskList(int assignNum);
	
	public int updateTaskAssigningFlag(List<EngineTaskInfo> item);
	
	public int updateFailTaskAssignFlag();
	
	public int updateInterruptTaskAssignFlag();
	
	public List<EngineTaskInfo> queryTaskCondition(EngineTaskInfo task);
}
