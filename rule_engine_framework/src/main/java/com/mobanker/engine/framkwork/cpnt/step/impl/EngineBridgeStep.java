package com.mobanker.engine.framkwork.cpnt.step.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.context.EngineTransferContext;
import com.mobanker.engine.framkwork.cpnt.stage.EngineStage;
import com.mobanker.engine.framkwork.cpnt.step.EngineLinkStep;
import com.mobanker.engine.framkwork.cpnt.step.EngineStep;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 桥接stage
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月4日
 * @version 1.0
 */
@Deprecated
public class EngineBridgeStep extends EngineLinkStep implements EngineStep,EngineRelaCpnt{

	private static final long serialVersionUID = 1L;
	private static final String BUSI_DESC = "桥接组件执行";
	private static final Logger logger = LoggerFactory.getLogger(EngineBridgeStep.class);	

	private EngineStage nextStage;

	public EngineStage getNextStage() {
		return nextStage;
	}


	public void setNextStage(EngineStage nextStage) {
		this.nextStage = nextStage;
	}


	@Override
	protected void link(EngineTransferContext etc) {		
		for(EngineStep step : nextStage.getList()){		
			etc.getEsp().addStep(step);
		}
	}


	@Override
	public Map<String,Boolean> relaCpntIds() {
		Map<String,Boolean> result = new HashMap<String,Boolean>();
		result.putAll(EngineUtil.relaNonlineal(nextStage));
		result.put(nextStage.id(), true);
		return result;
	}


	@Override
	public Set<String> inputRela() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String type() {
		return BUSI_DESC;
	}		

	@Override
	public Set<String> outputRela() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
		String aa = null;
		System.out.println(aa instanceof String);
		
	}

}
