package com.mobanker.engine.framkwork.cpnt.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.context.EngineTransferContext;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineBridgeStep;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 
 * <p>Title: EngineAbstractStep.java<／p>
 * <p>Description: <／p>
 * <p>Company: mobanker.com<／p>
 * @author taojinn
 * @date 2016年2月14日
 * @version 1.0
 */
@Deprecated
public abstract class EngineLinkStep extends EngineAbstractStep {

	private static final String BUSI_DESC = "连接型组件执行";
	private static final Logger logger = LoggerFactory.getLogger(EngineLinkStep.class);		
	/**
	 * 用于wrap
	 * @param etc
	 */
	protected abstract void link(EngineTransferContext etc); 
	
	
	@Override
	@Deprecated
	public void run(EngineTransferData etd){		
		return;
		
//		EngineTransferData etd = etc.getEtd();
//		//如果是模块测试模式则，则不需要连接下一个stage
//		if(etd.modeIsModuleTest()){
//			logger.info(EngineUtil.logPrefix(etd,BUSI_DESC)
//					+"该运行模式是模块测试，则不需要连接下一个模块");		
//			return;
//		}				
//		link(etc);
	}
	


	

}
