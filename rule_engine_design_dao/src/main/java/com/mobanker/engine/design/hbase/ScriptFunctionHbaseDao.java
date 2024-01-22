package com.mobanker.engine.design.hbase;

import com.mobanker.engine.design.pojo.hbase.ScriptFunctionEntity;
import com.mobanker.framework.exception.HBaseORMException;
import com.mobanker.framework.hbase.dao.Dao;

import java.util.List;

/**
* <p>Title: StepLoginInHbaseDao</p>
* <p>Description: </p>
* <p>Company: mobanker</p> 
* @author zhujj
* @date 2017年6月16日
* @version 1.0 
*/
public interface ScriptFunctionHbaseDao extends Dao<ScriptFunctionEntity> {

    public void save(ScriptFunctionEntity entity) throws HBaseORMException;

    public void saveList(List<ScriptFunctionEntity> entities) throws HBaseORMException;

    public ScriptFunctionEntity get(String id) throws HBaseORMException;

	List<ScriptFunctionEntity> queryFunctionByType(String type)
			throws HBaseORMException;

	void delete(String id) throws HBaseORMException;

    void bulkUpdate(ScriptFunctionEntity entity);

    void bulkUpdate(List<ScriptFunctionEntity> entities);
}
