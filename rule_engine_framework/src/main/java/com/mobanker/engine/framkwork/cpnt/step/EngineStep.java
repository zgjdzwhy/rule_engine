package com.mobanker.engine.framkwork.cpnt.step;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.relation.EngineRelaField;

/**
 * 步骤组件
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年10月28日
 * @version 1.0
 */
public interface EngineStep extends EngineCpnt,EngineRelaField{
	public void execute(EngineTransferData etd);
}
