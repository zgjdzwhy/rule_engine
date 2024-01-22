package com.mobanker.engine.exec.business.invoke;

import java.util.Map;

import com.mobanker.engine.rpc.dto.EngineCallDto;

public interface EngineSynInvokeEntry {
	/**
	 * 调用
	 * @param type
	 * @param dto
	 * @return
	 */
	public Map<String,Object> invoke(EngineSynInvokeType type,EngineCallDto dto);
}
