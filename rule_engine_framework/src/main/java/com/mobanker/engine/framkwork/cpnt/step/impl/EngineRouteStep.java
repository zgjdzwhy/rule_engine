package com.mobanker.engine.framkwork.cpnt.step.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.context.EngineTransferContext;
import com.mobanker.engine.framkwork.cpnt.judge.EngineJudge;
import com.mobanker.engine.framkwork.cpnt.stage.EngineStage;
import com.mobanker.engine.framkwork.cpnt.step.EngineLinkStep;
import com.mobanker.engine.framkwork.cpnt.step.EngineStep;
import com.mobanker.engine.framkwork.cpnt.step.assist.EngineBranch;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.manager.EngineStepProcessor;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 路由
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月4日
 * @version 1.0
 */
@Deprecated
public class EngineRouteStep extends EngineLinkStep implements EngineStep,EngineRelaCpnt{

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(EngineRouteStep.class);	
	private static final String BUSI_DESC = "路由组件执行";
	private List<EngineBranch<EngineStage>> routeStage = new LinkedList<EngineBranch<EngineStage>>();
	private EngineStage defaultStage;
	
	@Override
	public Map<String,Boolean> relaCpntIds() {
		Map<String,Boolean> result = new HashMap<String,Boolean>();			

		if(defaultStage!=null){			
			result.putAll(EngineUtil.relaNonlineal(defaultStage));
			result.put(defaultStage.id(), true);
		}		
		for(EngineBranch<EngineStage> one : routeStage){
			result.putAll(one.relaCpntIds());					
		}			
		return result;
	}	
		
	@Override
	public String type() {
		return BUSI_DESC;
	}			
	
	@Override
	protected void link(EngineTransferContext etc) {		
		EngineTransferData etd = etc.getEtd();
		EngineStepProcessor esp = etc.getEsp();
		EngineAssert.checkNotNull(esp);
		
		for(EngineBranch<EngineStage> routing : routeStage){
			if(routing.judge(etd)){
				logger.info(EngineUtil.logPrefix(etd,BUSI_DESC)
						+"满足路由:{}",routing);				
				EngineStage stage = routing.getContent();
				addStage(esp,stage);
				return;
			}	
		}
		
		if(defaultStage != null)
			addStage(esp,defaultStage);
		else			
			throw new EngineException(EngineUtil.logPrefix(etd,BUSI_DESC)+"没有匹配到路由规则,router:"+name());
	}


	public void addRoute(EngineJudge judge,boolean mode,EngineStage stage){
		EngineBranch<EngineStage> route = new EngineBranch<EngineStage>(judge,mode,stage);
		routeStage.add(route);
	}
	

	public EngineStage getDefaultStage() {
		return defaultStage;
	}



	public void setDefaultStage(EngineStage defaultStage) {
		this.defaultStage = defaultStage;
	}



	private void addStage(EngineStepProcessor esp,EngineStage stage){
		List<EngineStep> stepList = stage.getList();
		for(EngineStep step : stepList){
			esp.addStep(step);					
		}
	}


	public List<EngineBranch<EngineStage>> getRouteStage() {
		return routeStage;
	}


	public void setRouteStage(List<EngineBranch<EngineStage>> routeStage) {
		this.routeStage = routeStage;
	}

	@Override
	public Set<String> inputRela() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> outputRela() {
		// TODO Auto-generated method stub
		return null;
	}



	
	

}
