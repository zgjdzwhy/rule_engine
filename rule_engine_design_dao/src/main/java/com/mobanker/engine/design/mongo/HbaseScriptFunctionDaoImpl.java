package com.mobanker.engine.design.mongo;

import com.mobanker.engine.design.hbase.ScriptFunctionHbaseDao;
import com.mobanker.engine.design.pojo.EngScriptFunction;
import com.mobanker.engine.design.pojo.hbase.ScriptFunctionEntity;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.engine.framkwork.util.IdUtil;
import com.mobanker.framework.exception.HBaseORMException;
import com.mobanker.framework.tracking.EETransaction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository("hbaseScriptFunctionDao")
public class HbaseScriptFunctionDaoImpl implements EngScriptFunctionDao{

	private Logger logger = LoggerFactory.getLogger(this.getClass());						
		
	@Autowired
	private ScriptFunctionHbaseDao scriptFunctionHbaseDao;	
	
	@EETransaction(type = "Service", name = "HbaseScriptFunctionDaoImpl.findOne")
	@Override
	public EngScriptFunction findOne(String productType,String label) {
		if(StringUtils.isBlank(label)) throw new RuntimeException("label为空");
		if(StringUtils.isBlank(productType)) throw new RuntimeException("productType为空");
		EngScriptFunction function=null;
		ScriptFunctionEntity entity= null;
		try {
			entity = scriptFunctionHbaseDao.get(IdUtil.getFunctionId(label, productType));
		} catch (HBaseORMException e) {
			e.printStackTrace();
		}
		if(entity!=null){
			function=new EngScriptFunction();
			BeanUtils.copyProperties(entity, function);
		}
		return function;
	}	
	
	@EETransaction(type = "Service", name = "HbaseScriptFunctionDaoImpl.insert")
	@Override
	public void insert(EngScriptFunction engScriptFunction) {
		save(engScriptFunction,true);
	}	
	
	@EETransaction(type = "Service", name = "HbaseScriptFunctionDaoImpl.update")
	@Override
	public void update(EngScriptFunction engScriptFunction) {
		save(engScriptFunction,false);
	}
	
	@EETransaction(type = "Service", name = "HbaseScriptFunctionDaoImpl.delete")
	@Override
	public void delete(String productType, String label) {
		try {
			scriptFunctionHbaseDao.delete(IdUtil.getFunctionId(label, productType));
		} catch (HBaseORMException e) {
			e.printStackTrace();
		}
	}		

	@EETransaction(type = "Service", name = "HbaseScriptFunctionDaoImpl.findByProduct")
	@Override
	public List<EngScriptFunction> findByProduct(String productType) {

		List<ScriptFunctionEntity> entityList= null;
		try {
			entityList = scriptFunctionHbaseDao.queryFunctionByType(productType);
		} catch (HBaseORMException e) {
			e.printStackTrace();
		}
		List<EngScriptFunction> list = new LinkedList<EngScriptFunction>();
		if(entityList!=null){
			for(ScriptFunctionEntity entity:entityList){
				EngScriptFunction function=new EngScriptFunction();
				BeanUtils.copyProperties(entity, function);
				list.add(function);
			}
		}
		
		return list;		
	}

	@EETransaction(type = "Service", name = "HbaseScriptFunctionDaoImpl.deleteByProductType")
	@Override
	public void deleteByProductType(String productType) {
		throw new RuntimeException("该方法不可用");
	}

	private void save(EngScriptFunction engScriptFunction,boolean flag){
		
		ScriptFunctionEntity entity = new ScriptFunctionEntity();
		BeanUtils.copyProperties(engScriptFunction , entity);
		entity.setRowKey();
		String dateTmie=EngineUtil.get14CurrentDateTime();
		if(flag){
			entity.setCreateTime(dateTmie);
		}
		entity.setUpdateTime(dateTmie);
		try {
			scriptFunctionHbaseDao.save(entity);
		} catch (HBaseORMException e) {
			e.printStackTrace();
		}
	}

}
