package com.mobanker.engine.design.mongo;

import java.util.List;

import com.mobanker.engine.design.pojo.EngSerInfo;

@Deprecated
public interface EngSerDao {
	
	/**
	 * 
	 * @param productType
	 * @param cpntId
	 * @param jsonObject
	 */
	public void saveSer(EngSerInfo engSerInfo);
	
	/**
	 * 
	 * @param productType
	 * @param version
	 * @return
	 */
	public List<EngSerInfo> findSer(String productType,String version);
	
}
