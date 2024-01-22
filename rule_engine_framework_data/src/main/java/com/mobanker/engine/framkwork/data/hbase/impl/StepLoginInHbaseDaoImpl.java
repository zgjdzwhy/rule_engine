package com.mobanker.engine.framkwork.data.hbase.impl;

import com.mobanker.engine.framkwork.data.entity.StepLoginInEntity;
import com.mobanker.engine.framkwork.data.hbase.StepLoginInHbaseDao;
import com.mobanker.framework.exception.HBaseORMException;
import com.mobanker.framework.hbase.dao.impl.BaseDaoImpl;
import com.mobanker.framework.hbase.dao.impl.value.ValueFactory;
import com.mobanker.framework.tracking.EETransaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StepLoginInHbaseDaoImpl extends BaseDaoImpl<StepLoginInEntity> implements StepLoginInHbaseDao {

    @EETransaction(type = "SQL", name = "StepLoginInHbaseDaoImpl.save")
    @Override
    public void save(StepLoginInEntity entity) throws HBaseORMException {
       // this.bulkUpdate(entity);
    }

    @EETransaction(type = "SQL", name = "StepLoginOutHbaseDaoImpl.saveList")
    @Override
    public void saveList(List<StepLoginInEntity> entities) throws HBaseORMException {
        //this.bulkUpdate(entities);
    }

    @EETransaction(type = "SQL", name = "StepLoginOutHbaseDaoImpl.get")
    @Override
    public StepLoginInEntity get(String id) throws HBaseORMException {
        return this.queryById(ValueFactory.TypeCreate(id));
    }

}
