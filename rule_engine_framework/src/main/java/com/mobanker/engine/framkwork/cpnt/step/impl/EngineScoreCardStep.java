package com.mobanker.engine.framkwork.cpnt.step.impl;

import java.math.BigDecimal;
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

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月18日
 * @version 1.0
 */
@Deprecated
public class EngineScoreCardStep extends EngineAbstractStep implements EngineStep,EngineRelaCpnt,EngineSelfCheck,EngineDependField{

	private static final long serialVersionUID = 1L;
	private List<EngineBranch<BigDecimal>> judgeList = new LinkedList<EngineBranch<BigDecimal>>();
	private String output;		
	
	@Override
	public Map<String,Boolean> relaCpntIds() {
		Map<String,Boolean> result = new HashMap<String,Boolean>();		
		for(EngineBranch<BigDecimal> one : judgeList){
			result.putAll(one.relaCpntIds());					
		}			
		return result;
	}
	
	@Override
	protected void run(EngineTransferData etd) {	
		BigDecimal all = BigDecimal.ZERO;
		for(EngineBranch<BigDecimal> ruleJudge : judgeList){
			if(ruleJudge.judge(etd)){//如果符合条件
				all = all.add(ruleJudge.getContent());
			}
		}		
				
		etd.getOutputParam().put(output, all);		
	}


	public void addJudge(EngineJudge judge,boolean mode,BigDecimal score){
		EngineBranch<BigDecimal> engineRuleJudge = new EngineBranch<BigDecimal>(judge,mode,score);
		judgeList.add(engineRuleJudge);
	}
	
	public List<EngineBranch<BigDecimal>> getJudgeList() {
		return judgeList;
	}



	public void setJudgeList(List<EngineBranch<BigDecimal>> judgeList) {
		this.judgeList = judgeList;
	}



	public String getOutput() {
		return output;
	}



	public void setOutput(String output) {
		this.output = output;
	}

	@Override
	public Set<String> inputRela() {
		Set<String> result = new HashSet<String>();
		for(EngineBranch<BigDecimal> branch : judgeList){
			result.addAll(branch.inputRela());
		}
		return result;
	}


	@Override
	public Set<String> outputRela() {
		Set<String> result = new HashSet<String>();
		for(EngineBranch<BigDecimal> branch : judgeList){
			result.addAll(branch.outputRela());
		}
		if(StringUtils.isNoneBlank(output)) result.add(output);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof EngineScoreCardStep)) return false;
		EngineScoreCardStep another = (EngineScoreCardStep) obj;	
		if(!StringUtils.equals(this.output, another.output)) return false;		
		
		if(!this.judgeList.equals(another.judgeList)) return false;		
		return true;
	}

	@Override
	public void checkSelf() throws EngineCpntSelfCheckException {
		//脚本中需要需要包含_result字符			
		if(StringUtils.isBlank(output)) 
			throw new EngineCpntSelfCheckException("类型[规则集]名称["+name()+"]创建者["+right()+"]输出字段为空");		
	}
	

	
	@Override
	public String type() {
		return "评分卡";
	}

	@Override
	public Map<String, String> dependFields() {
		Map<String,String> result = new HashMap<String,String>();
		if(StringUtils.isNoneBlank(output)) result.put(output,"output");		
		return result;
	}		
}
