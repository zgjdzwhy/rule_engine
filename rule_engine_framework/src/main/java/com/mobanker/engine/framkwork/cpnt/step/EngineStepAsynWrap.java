package com.mobanker.engine.framkwork.cpnt.step;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.context.EngineTransferContext;

/**
 * 步骤的异步包装执行器
 * <p>Title: EngineAbstractStep.java<／p>
 * <p>Description: <／p>
 * <p>Company: mobanker.com<／p>
 * @author taojinn
 * @date 2016年1月27日
 * @version 1.0
 */
@Deprecated
public class EngineStepAsynWrap implements Runnable {	
	private EngineStep step;	
	private EngineTransferContext etc;
	
	public EngineStepAsynWrap(EngineStep step,EngineTransferContext etc){
		this.step = step;		
		this.etc = etc;
	}
	
	@Override
	public void run(){
		//因为EngineTransferData线程不安全，此处进行了深克隆
		EngineTransferData etd = etc.getEtd().clone();		
		step.execute(etd);
	}

}
