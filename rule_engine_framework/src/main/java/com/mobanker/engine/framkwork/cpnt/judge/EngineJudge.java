package com.mobanker.engine.framkwork.cpnt.judge;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.relation.EngineRelaField;

/**
 * 规则组件接口
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年11月3日
 * @version 1.0
 */
public interface EngineJudge extends EngineCpnt,EngineRelaField{
	public boolean judge(EngineTransferData etd);	
}
