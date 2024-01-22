package com.mobanker.engine.design.mongo;

import java.util.List;

import com.mobanker.engine.design.pojo.EngScriptTemplate;

public interface EngScriptTemplateDao{
	

	/**
	 * 
	 * @param productType
	 * @param cpntId
	 * @param jsonObject
	 */
	public EngScriptTemplate findOne(String label);
	
	
	/**
	 * 
	 * @param productType
	 * @param cpntId
	 * @param jsonObject
	 */
	public void insert(EngScriptTemplate one);
	
	/**
	 * 
	 * @param productType
	 * @param cpntId
	 * @param jsonObject
	 */
	public void update(EngScriptTemplate one);	
	
	
	/**
	 * 
	 * @param productType
	 * @param cpntId
	 * @param jsonObject
	 */
	public void delete(String label);	
	
	
	/**
	 * 
	 * @param productType
	 * @param version
	 * @return
	 */
	public List<EngScriptTemplate> getAll();
	
			
}
