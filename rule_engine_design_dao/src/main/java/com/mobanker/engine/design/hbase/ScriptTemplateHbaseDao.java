package com.mobanker.engine.design.hbase;

import com.mobanker.engine.design.pojo.hbase.ScriptTemplateEntity;
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
public interface ScriptTemplateHbaseDao extends Dao<ScriptTemplateEntity> {

    public void save(ScriptTemplateEntity entity) throws HBaseORMException;

    public void saveList(List<ScriptTemplateEntity> entities) throws HBaseORMException;

    public ScriptTemplateEntity get(String id) throws HBaseORMException;

	void delete(String id) throws HBaseORMException;

	List<ScriptTemplateEntity> queryAllTemplate(String type)
			throws HBaseORMException;

    void bulkUpdate(List<ScriptTemplateEntity> entities);
}
