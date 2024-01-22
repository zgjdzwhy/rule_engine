package com.mobanker.engine.framkwork.cpnt.step;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpntBaseInfo;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.exception.EngineParamMissException;
import com.mobanker.engine.framkwork.exception.monitor.EngineMonitorTransaction;
import com.mobanker.engine.framkwork.exception.monitor.EngineStaticMonitorEnvironment;
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
public abstract class EngineAbstractStep extends EngineCpntBaseInfo implements EngineStep {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 用于wrap
	 * @param etd
	 */
	protected abstract void run(EngineTransferData etd); 
	

	
	@Override
	public void execute(EngineTransferData etd){	
		String productType = etd.getProductType();
		
		EngineMonitorTransaction trans = EngineStaticMonitorEnvironment.create("StepRun"+"-->"+productType,this.name());
		try{
			trans.setStatus(EngineMonitorTransaction.SUCCESS);
			//EE.logEvent("StepRun"+"-->"+productType, this.name());
			run(etd);
		}catch(Exception e){			
			trans.setStatus(e, null);
			//EE.logError(EngineUtil.logPrefix(etd,"执行步骤")+this.name()+":步骤发生严重错误，流程即将被中断",e);
			throw new EngineException(EngineUtil.logPrefix(etd,"执行步骤")+"步骤发生严重错误，流程即将被中断",e);			
		}finally{
			trans.complete();
		}
		
		
	}
	
	protected void checkEssentialParam(String checkParamConfig, Map<String, Object> param) throws EngineParamMissException {
		if (StringUtils.isBlank(checkParamConfig))
			throw new EngineParamMissException("检查表达式为空");
		if (param == null)
			throw new EngineParamMissException("需要检查的参数集为空");
		String[] checkParams = StringUtils.split(checkParamConfig, ',');
		for (String checkParam : checkParams) {
			if (param.get(checkParam) == null)
				throw new EngineParamMissException("必要参数缺失[" + checkParam + "]");
		}
	}

//	@Override
//	public void afterPropertiesSet() throws Exception{
//		if(StringUtils.isBlank(name)) name = "未定义";
//	}
	
	@Override
	public String toString(){
		return "<"+StringUtils.rightPad(this.getClass().getSimpleName(), 30) + "-" + name()+">";
	}
	
	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String id(){
		return this.id;
	}




	

}
