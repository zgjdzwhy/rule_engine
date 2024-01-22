package com.mobanker.engine.design.mongo;

import com.mobanker.engine.design.hbase.ScriptTemplateHbaseDao;
import com.mobanker.engine.design.pojo.EngScriptFunction;
import com.mobanker.engine.design.pojo.EngScriptTemplate;
import com.mobanker.engine.design.pojo.hbase.ScriptTemplateEntity;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.engine.framkwork.util.IdUtil;
import com.mobanker.framework.exception.HBaseORMException;
import com.mobanker.framework.tracking.EETransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository("hbaseScriptTemplateDao")
public class HbaseScriptTemplateDaoImpl implements EngScriptTemplateDao{

	private Logger logger = LoggerFactory.getLogger(this.getClass());						
		
	
	private static final String TYPE = "defaultType";
	
	@Autowired
	private ScriptTemplateHbaseDao scriptTemplateHbaseDao;	

	@EETransaction(type = "Service", name = "HbaseScriptTemplateDaoImpl.findOne")
	@Override
	public EngScriptTemplate findOne(String label) {
		EngScriptTemplate template=null;
		ScriptTemplateEntity entity= null;
		try {
			entity = scriptTemplateHbaseDao.get(IdUtil.getFunctionId(label, TYPE));
		} catch (HBaseORMException e) {
			e.printStackTrace();
		}
		if(entity!=null){
			template=new EngScriptTemplate();
			BeanUtils.copyProperties(entity, template);
		}
		return template;
	}	
	
	@EETransaction(type = "Service", name = "HbaseScriptTemplateDaoImpl.insert")
	@Override
	public void insert(EngScriptTemplate one) {
		save(one,true);
	}	
	
	@EETransaction(type = "Service", name = "HbaseScriptTemplateDaoImpl.update")
	@Override
	public void update(EngScriptTemplate one) {
		save(one,false);	
	}

	@EETransaction(type = "Service", name = "HbaseScriptTemplateDaoImpl.delete")
	@Override
	public void delete(String label) {
		try {
			scriptTemplateHbaseDao.delete(IdUtil.getFunctionId(label, TYPE));
		} catch (HBaseORMException e) {
			e.printStackTrace();
		}
	}	
	
	@EETransaction(type = "Service", name = "HbaseScriptTemplateDaoImpl.getAll")
	@Override
	public List<EngScriptTemplate> getAll() {
		List<ScriptTemplateEntity> entityList= null;
		try {
			entityList = scriptTemplateHbaseDao.queryAllTemplate(TYPE);
		} catch (HBaseORMException e) {
			e.printStackTrace();
		}
		List<EngScriptTemplate> list = new LinkedList<EngScriptTemplate>();
		if(entityList!=null){
			for(ScriptTemplateEntity entity:entityList){
				EngScriptFunction function=new EngScriptFunction();
				BeanUtils.copyProperties(entity, function);
				list.add(function);
			}
		}
		
		return list;			
	}	

	private void save(EngScriptTemplate engScriptTemplate,boolean flag){
		String productType = TYPE;
		engScriptTemplate.setProductType(productType);
		ScriptTemplateEntity entity =new ScriptTemplateEntity();
		BeanUtils.copyProperties(engScriptTemplate, entity);
		String dateTmie=EngineUtil.get14CurrentDateTime();
		if(flag){
			entity.setCreateTime(dateTmie);
		}
		entity.setUpdateTime(dateTmie);
		entity.setRowKey();
		try {
			scriptTemplateHbaseDao.save(entity);
		} catch (HBaseORMException e) {
			e.printStackTrace();
		}
	}

}
