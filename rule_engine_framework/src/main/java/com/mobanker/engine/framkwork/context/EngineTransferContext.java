package com.mobanker.engine.framkwork.context;

import java.math.BigDecimal;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.manager.EngineStepProcessor;

/**
 * 框架步骤交互的上下文
 * <p>Title: EngineTransferContext.java<／p>
 * <p>Description: <／p>
 * <p>Company: mobanker.com<／p>
 * @author taojinn
 * @date 2016年1月29日
 * @version 1.0
 */

@Deprecated
public class EngineTransferContext {
	private EngineTransferData etd;	
	
	/**
	 * 为了能控制
	 */
	private EngineStepProcessor esp;
	
	public EngineTransferContext(EngineTransferData etd){
		this.etd = etd;
	}
	
	public EngineTransferData getEtd() {
		return etd;
	}
	public void setEtd(EngineTransferData etd) {
		this.etd = etd;
	}
	
	public EngineStepProcessor getEsp() {
		return esp;
	}

	public void setEsp(EngineStepProcessor esp) {
		this.esp = esp;
	}

	
	public void setOutput(String key,Object value){
		this.etd.getOutputParam().put(key, value);		
	}
	
	public void setInput(String key,Object value){
		this.etd.getInputParam().put(key, value);		
	}
	
	public Object getOutValue(String key){
		return this.etd.getOutputParam().get(key);
	}
	
	public String getOutString(String key){
		return this.etd.getOutputParam().getString(key);
	}
	
	public Integer getOutInteger(String key){
		return this.etd.getOutputParam().getInteger(key);
	}
	
	public BigDecimal getOutBigDecimal(String key){
		return this.etd.getOutputParam().getBigDecimal(key);
	}
	
	public Long getOutLong(String key){
		return this.etd.getOutputParam().getLong(key);
	}



}
