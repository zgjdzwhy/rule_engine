package com.mobanker.engine.framkwork.cpnt.step.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineSelfCheck;
import com.mobanker.engine.framkwork.cpnt.judge.EngineJudge;
import com.mobanker.engine.framkwork.cpnt.step.EngineAbstractStep;
import com.mobanker.engine.framkwork.cpnt.step.EngineStep;
import com.mobanker.engine.framkwork.cpnt.step.assist.EngineBranch;
import com.mobanker.engine.framkwork.exception.EngineCpntSelfCheckException;
import com.mobanker.engine.framkwork.relation.EngineDependField;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;

public class EngineRuleCollectionStep extends EngineAbstractStep implements EngineStep,EngineRelaCpnt,EngineSelfCheck,EngineDependField{

	private static final long serialVersionUID = 1L;
	private List<EngineBranch<String>> judgeList = new LinkedList<EngineBranch<String>>();
	private String hitOutput;
	private String passOutput;		
		
	@Override
	public Map<String,Boolean> relaCpntIds() {
		Map<String,Boolean> result = new HashMap<String,Boolean>();		
		for(EngineBranch<String> one : judgeList){
			result.putAll(one.relaCpntIds());					
		}			
		return result;
	}
	
	@Override
	public String type() {
		return "规则集";
	}			
	
	@Override
	protected void run(EngineTransferData etd) {
		List<String> hitResult = new LinkedList<String>();				
		List<String> passResult = new LinkedList<String>();
		for(EngineBranch<String> ruleJudge : judgeList){
			if(ruleJudge.judge(etd)){//如果符合条件
				hitResult.add(ruleJudge.getContent());
			}else
				passResult.add(ruleJudge.getContent());
		}			
		etd.getOutputParam().put(hitOutput, hitResult);
		
		if(StringUtils.isNotBlank(passOutput))//如果有设置passOutput则输出此项
			etd.getOutputParam().put(passOutput, passResult);
		
	}
	
	public void addJudge(EngineJudge judge,boolean mode,String reasonCode){
		EngineBranch<String> engineRuleJudge = new EngineBranch<String>(judge,mode,reasonCode);
		judgeList.add(engineRuleJudge);
	}
	



	public String getHitOutput() {
		return hitOutput;
	}

	public void setHitOutput(String hitOutput) {
		this.hitOutput = hitOutput;
	}

	public String getPassOutput() {
		return passOutput;
	}

	public void setPassOutput(String passOutput) {
		this.passOutput = passOutput;
	}


	@Override
	public Set<String> inputRela() {
		Set<String> result = new HashSet<String>();
		for(EngineBranch<String> branch : judgeList){
			result.addAll(branch.inputRela());
		}
		return result;
	}


	@Override
	public Set<String> outputRela() {
		Set<String> result = new HashSet<String>();
		for(EngineBranch<String> branch : judgeList){
			result.addAll(branch.outputRela());
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof EngineRuleCollectionStep)) return false;
		EngineRuleCollectionStep another = (EngineRuleCollectionStep) obj;	
		if(!StringUtils.equals(this.hitOutput, another.hitOutput)) return false;
		if(!StringUtils.equals(this.passOutput, another.passOutput)) return false;
		
		if(!this.judgeList.equals(another.judgeList)) return false;		
		return true;
	}

	@Override
	public void checkSelf() throws EngineCpntSelfCheckException {
		//脚本中需要需要包含_result字符			
		if(StringUtils.isBlank(hitOutput)) 
			throw new EngineCpntSelfCheckException("类型[规则集]名称["+name()+"]创建者["+right()+"]输出字段为空");		
	}


	@Override
	public Map<String,String> dependFields() {
		Map<String,String> result = new HashMap<String,String>();
		if(StringUtils.isNoneBlank(hitOutput)) result.put(hitOutput,"output");
		if(StringUtils.isNoneBlank(passOutput)) result.put(passOutput,"output");		
		return result;
	}

	
}
