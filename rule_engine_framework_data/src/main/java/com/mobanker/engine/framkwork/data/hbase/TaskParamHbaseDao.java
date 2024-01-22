package com.mobanker.engine.framkwork.data.hbase;

import java.util.List;

import com.mobanker.engine.framkwork.data.entity.TaskParamEntity;
import com.mobanker.framework.exception.HBaseORMException;
import com.mobanker.framework.hbase.dao.Dao;

/**
* <p>Title: StepLoginInHbaseDao</p>
* <p>Description: </p>
* <p>Company: mobanker</p> 
* @author zhujj
* @date 2017年6月16日
* @version 1.0 
*/
public interface TaskParamHbaseDao extends Dao<TaskParamEntity> {

    public void save(TaskParamEntity entity) throws HBaseORMException;

    public void saveList(List<TaskParamEntity> entities) throws HBaseORMException;

    public TaskParamEntity get(String id) throws HBaseORMException;

}
