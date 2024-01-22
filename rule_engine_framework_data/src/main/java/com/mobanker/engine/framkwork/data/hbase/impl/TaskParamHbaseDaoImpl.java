package com.mobanker.engine.framkwork.data.hbase.impl;

import com.mobanker.engine.framkwork.data.entity.TaskParamEntity;
import com.mobanker.engine.framkwork.data.hbase.TaskParamHbaseDao;
import com.mobanker.framework.exception.HBaseORMException;
import com.mobanker.framework.hbase.dao.impl.BaseDaoImpl;
import com.mobanker.framework.hbase.dao.impl.value.ValueFactory;
import com.mobanker.framework.tracking.EETransaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskParamHbaseDaoImpl extends BaseDaoImpl<TaskParamEntity> implements TaskParamHbaseDao {

    @EETransaction(type = "SQL", name = "TaskParamHbaseDaoImpl.save")
    @Override
    public void save(TaskParamEntity entity) throws HBaseORMException {
     //   this.bulkUpdate(entity);
    }

    @EETransaction(type = "SQL", name = "TaskParamHbaseDaoImpl.saveList")
    @Override
    public void saveList(List<TaskParamEntity> entities) throws HBaseORMException {
       // this.bulkUpdate(entities);
    }

    @EETransaction(type = "SQL", name = "TaskParamHbaseDaoImpl.get")
    @Override
    public TaskParamEntity get(String id) throws HBaseORMException {
        return this.queryById(ValueFactory.TypeCreate(id));
    }

}
