package com.mobanker.engine.framkwork.data.hbase;

import java.util.List;

import com.mobanker.engine.framkwork.data.entity.StepLoginInEntity;
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
public interface StepLoginInHbaseDao extends Dao<StepLoginInEntity> {

    public void save(StepLoginInEntity entity) throws HBaseORMException;

    public void saveList(List<StepLoginInEntity> entities) throws HBaseORMException;

    public StepLoginInEntity get(String id) throws HBaseORMException;

}
