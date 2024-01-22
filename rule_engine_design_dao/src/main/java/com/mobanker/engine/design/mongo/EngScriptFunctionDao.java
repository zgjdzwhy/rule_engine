package com.mobanker.engine.design.mongo;

import java.util.List;

import com.mobanker.engine.design.pojo.EngScriptFunction;

public interface EngScriptFunctionDao {
	
	
	/**
	 * 
	 * @param productType
	 * @param cpntId
	 * @param jsonObject
	 */
	public EngScriptFunction findOne(String productType,String label);
	
	
	/**
	 * 
	 * @param productType
	 * @param cpntId
	 * @param jsonObject
	 */
	public void insert(EngScriptFunction engScriptFunction);
	
	/**
	 * 
	 * @param productType
	 * @param cpntId
	 * @param jsonObject
	 */
	public void update(EngScriptFunction engScriptFunction);	
	
	
	public void delete(String productType, String label);
	/**
	 * 
	 * @param productType
	 * @param version
	 * @return
	 */
	public List<EngScriptFunction> findByProduct(String productType);
	
	
	/**
	 * 
	 * @param productType
	 * @param cpntId
	 * @param jsonObject
	 */
	@Deprecated
	public void deleteByProductType(String productType);		
}
