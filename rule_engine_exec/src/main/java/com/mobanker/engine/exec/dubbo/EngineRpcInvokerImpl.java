package com.mobanker.engine.exec.dubbo;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobanker.engine.common.EngineConst;
import com.mobanker.engine.exec.business.invoke.EngineSynInvokeEntry;
import com.mobanker.engine.exec.business.invoke.EngineSynInvokeType;
import com.mobanker.engine.rpc.api.EngineRpcInvoker;
import com.mobanker.engine.rpc.dto.EngineCallDto;
import com.mobanker.engine.rpc.dto.EngineInvokeDto;
import com.mobanker.engine.rpc.dto.EngineOutput;
import com.mobanker.engine.rpc.dto.EngineResponse;


@Service
public class EngineRpcInvokerImpl implements EngineRpcInvoker {

	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	@Autowired
	private EngineSynInvokeEntry engineSynInvokeEntry;
	
	
	@Override
	public EngineResponse<Map<String,Object>> synInvoke(EngineInvokeDto dto) {
		EngineResponse<Map<String,Object>> ret = new EngineResponse<Map<String,Object>>();
		try{
			EngineCallDto callDto = new EngineCallDto(dto.getProductType(),dto.getAppName(),null);
			callDto.setExecMode(dto.getExecMode());
			callDto.setAppParams(dto.getAppParams());
			
			Map<String,Object> result = engineSynInvokeEntry.invoke(EngineSynInvokeType.DUBBO, callDto);								
			ret = new EngineResponse<Map<String,Object>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,
					result);
		}catch(Exception e){
			logger.error("查询输出字段异常",e);
			ret = new EngineResponse<Map<String,Object>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}		
		return ret;
	}


	@Override
	public EngineResponse<Map<String, Object>> synInvoke(EngineCallDto dto) {
		EngineResponse<Map<String,Object>> ret = new EngineResponse<Map<String,Object>>();
		try{
			Map<String,Object> result = engineSynInvokeEntry.invoke(EngineSynInvokeType.DUBBO, dto);								
			ret = new EngineResponse<Map<String,Object>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,
					result);
		}catch(Exception e){
			logger.error("查询输出字段异常",e);
			ret = new EngineResponse<Map<String,Object>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}		
		return ret;
	}
	
	@Override
	public EngineResponse<EngineOutput> engineSynInvoke(EngineCallDto dto) {
		EngineResponse<EngineOutput> ret = new EngineResponse<EngineOutput>();
		try{
			EngineCallDto callDto = new EngineCallDto(dto.getProductType(),dto.getAppName(),null);
			callDto.setExecMode(dto.getExecMode());
			callDto.setAppParams(dto.getAppParams());
			
			Map<String,Object> result = engineSynInvokeEntry.invoke(EngineSynInvokeType.DUBBO, callDto);	
			
			EngineOutput callBackParam=new EngineOutput(result);
			ret = new EngineResponse<EngineOutput>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,
					callBackParam);
		}catch(Exception e){
			logger.error("查询输出字段异常",e);
			ret = new EngineResponse<EngineOutput>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}		
		return ret;
	}

}
