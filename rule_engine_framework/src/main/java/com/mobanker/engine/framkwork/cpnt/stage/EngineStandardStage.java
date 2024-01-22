package com.mobanker.engine.framkwork.cpnt.stage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpntBaseInfo;
import com.mobanker.engine.framkwork.cpnt.step.EngineStep;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月10日
 * @version 1.0
 */
public class EngineStandardStage extends EngineCpntBaseInfo implements EngineStage {

	private static final long serialVersionUID = 1L;
	private static final String BUSI_DESC = "模块";	
	
	private List<EngineStep> stepList;
	
	@Override
	public List<EngineStep> getList() {
		return stepList;
	}

	public List<EngineStep> getStepList() {
		return stepList;
	}

	public void setStepList(List<EngineStep> stepList) {
		this.stepList = stepList;
	}
	
	@Override
	public String type() {
		return BUSI_DESC;
	}		
	
	@Override
	public Map<String,Boolean> relaCpntIds() {		
		Map<String,Boolean> result = new HashMap<String,Boolean>();
		for(EngineStep step : stepList){
			if(step instanceof EngineRelaCpnt){
				EngineRelaCpnt rela = (EngineRelaCpnt) step;								
				result.putAll(EngineUtil.relaNonlineal(rela));				
				result.put(step.id(),true);
			}
		}		
		return result;
	}

	@Override
	public Set<String> inputRela() {
		Set<String> result = new HashSet<String>();
		for(EngineStep step : stepList){
			result.addAll(step.inputRela());
		}
		return result;
	}

	@Override
	public Set<String> outputRela() {
		Set<String> result = new HashSet<String>();
		for(EngineStep step : stepList){
			result.addAll(step.outputRela());
		}
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof EngineStandardStage)) return false;
		EngineStandardStage another = (EngineStandardStage) obj;	
		
		ListIterator<EngineStep> e1 = this.stepList.listIterator();
        ListIterator<EngineStep> e2 = another.stepList.listIterator();
        while (e1.hasNext() && e2.hasNext()) {
        	EngineStep o1 = e1.next();
        	EngineStep o2 = e2.next();        	
        	if(o1 == o2) continue;
        	if(o1 == null) return false;
        	if(o2 == null) return false;        
        	if(!o1.id().equals(o2.id())) return false;        	
        }
        return !(e1.hasNext() || e2.hasNext());
	}
}
