package com.mobanker.engine.rpc.api;

import java.util.Map;

import com.mobanker.engine.rpc.dto.EngineCallDto;
import com.mobanker.engine.rpc.dto.EngineInvokeDto;
import com.mobanker.engine.rpc.dto.EngineOutput;
import com.mobanker.engine.rpc.dto.EngineResponse;

/**
 * 决策引擎rpc入口
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年10月18日
 * @version 1.0
 */
public interface EngineRpcInvoker {
	
	/**
	 * 同步调用
	 * 已废弃,请使用engineSynInvoke(EngineCallDto dto)
	 * @param dto
	 * @return
	 */
	@Deprecated
	public EngineResponse<Map<String,Object>> synInvoke(EngineInvokeDto dto); 
	
	/**
	 * 同步调用 
	 * 已废弃,请使用engineSynInvoke(EngineCallDto dto)
	 * @param dto
	 * @return
	 * 
	 */
	@Deprecated
	public EngineResponse<Map<String,Object>> synInvoke(EngineCallDto dto);

	/** 
	* @Title: engineSynInvoke 
	* @Description: 最新同步调用接口，对返回类型统一处理，优先使用
	* 参数@param dto
	* 参数@return
	* @return EngineResponse<CallBackParam>    返回类型 
	* @throws 
	*/
	public EngineResponse<EngineOutput> engineSynInvoke(EngineCallDto dto);
}
