package com.mobanker.engine.framkwork.data.hbase;

import java.util.List;

import com.mobanker.engine.framkwork.data.entity.StepLoginInEntity;
import com.mobanker.engine.framkwork.data.entity.StepLoginOutEntity;
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
public interface StepLoginOutHbaseDao extends Dao<StepLoginOutEntity> {

    public void save(StepLoginOutEntity entity) throws HBaseORMException;

    public void saveList(List<StepLoginOutEntity> entities) throws HBaseORMException;

    public StepLoginOutEntity get(String id) throws HBaseORMException;

}
