package com.mobanker.engine.framkwork.data.hbase.impl;

import com.mobanker.engine.framkwork.data.entity.StepLoginOutEntity;
import com.mobanker.engine.framkwork.data.hbase.StepLoginOutHbaseDao;
import com.mobanker.framework.exception.HBaseORMException;
import com.mobanker.framework.hbase.dao.impl.BaseDaoImpl;
import com.mobanker.framework.hbase.dao.impl.value.ValueFactory;
import com.mobanker.framework.tracking.EETransaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StepLoginOutHbaseDaoImpl extends BaseDaoImpl<StepLoginOutEntity> implements StepLoginOutHbaseDao {

    @EETransaction(type = "SQL", name = "StepLoginOutHbaseDaoImpl.save")
    @Override
    public void save(StepLoginOutEntity entity) throws HBaseORMException {
       // this.bulkUpdate(entity);
    }

    @EETransaction(type = "SQL", name = "StepLoginOutHbaseDaoImpl.saveList")
    @Override
    public void saveList(List<StepLoginOutEntity> entities) throws HBaseORMException {
        //this.bulkUpdate(entities);
    }

    @EETransaction(type = "SQL", name = "StepLoginOutHbaseDaoImpl.get")
    @Override
    public StepLoginOutEntity get(String id) throws HBaseORMException {
        return this.queryById(ValueFactory.TypeCreate(id));
    }

}
