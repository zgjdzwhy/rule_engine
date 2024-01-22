package com.mobanker.engine.framkwork.cpnt.step.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.mobanker.engine.framkwork.cpnt.step.assist.EngineGroupBranch;
import com.mobanker.engine.framkwork.exception.EngineCpntSelfCheckException;
import com.mobanker.engine.framkwork.relation.EngineDependField;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author zhujunjie
 * @date 2016年12月28日
 * @version 1.0
 */
public class EngineScoreCardV2Step extends EngineAbstractStep implements EngineStep,EngineRelaCpnt,EngineSelfCheck,EngineDependField{

	private static final long serialVersionUID = 1L;
	private List<EngineGroupBranch<BigDecimal>> groupList = new ArrayList<EngineGroupBranch<BigDecimal>>();
	private String outputTotal;		
	private String outputGroup;		
	
	@Override
	public Map<String,Boolean> relaCpntIds() {
		Map<String,Boolean> result = new HashMap<String,Boolean>();		
		for(EngineGroupBranch<BigDecimal> groupBranch:groupList){
			result.putAll(groupBranch.relaCpntIds());					
		}
		return result;
	}
	
	public void addGroup(EngineGroupBranch<BigDecimal> groupBranch){
		groupList.add(groupBranch);
	}
	
	@Override
	protected void run(EngineTransferData etd) {	
		BigDecimal all = BigDecimal.ZERO;
		List<BigDecimal> group=new LinkedList<BigDecimal>();
		for(EngineGroupBranch<BigDecimal> groupBranch:groupList){
			List<EngineBranch<BigDecimal>> judgeList =groupBranch.getJudgeList();
			BigDecimal groupScore = BigDecimal.ZERO;
			if(judgeList!=null&&judgeList.size()>0) {
				for(EngineBranch<BigDecimal> ruleJudge : judgeList){
					//如果符合条件 组中规则按顺序判断，有成功即返回，不累加
					if(ruleJudge.judge(etd)){
						groupScore=groupScore.add(ruleJudge.getContent());
						break;
					}
				}		
			}
			group.add(groupScore);
			all = all.add(groupScore);
		}
		if(StringUtils.isNoneBlank(outputTotal)){
			etd.getOutputParam().put(outputTotal, all);	
		}
		if(StringUtils.isNoneBlank(outputGroup)){
			etd.getOutputParam().put(outputGroup, group);		
		}
	}

	@Override
	public Set<String> inputRela() {
		Set<String> result = new HashSet<String>();
		for(EngineGroupBranch<BigDecimal> groupBranch:groupList){
			result.addAll(groupBranch.inputRela());
		}
		return result;
	}


	@Override
	public Set<String> outputRela() {
		Set<String> result = new HashSet<String>();
		for(EngineGroupBranch<BigDecimal> groupBranch:groupList){
			result.addAll(groupBranch.outputRela());
			if(StringUtils.isNoneBlank(outputTotal)) result.add(outputTotal);		
			if(StringUtils.isNoneBlank(outputGroup)) result.add(outputGroup);		
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof EngineScoreCardV2Step)) return false;
		EngineScoreCardV2Step another = (EngineScoreCardV2Step) obj;	
		if(!StringUtils.equals(this.outputTotal, another.outputTotal)) return false;		
		if(!StringUtils.equals(this.outputGroup, another.outputGroup)) return false;		
		
		if(!this.groupList.equals(another.groupList)) return false;		
		return true;
	}

	@Override
	public void checkSelf() throws EngineCpntSelfCheckException {
//		//需要包含输出总分字段			
//		if(StringUtils.isBlank(outputTotal)) 
//			throw new EngineCpntSelfCheckException("类型[评分卡]名称["+name()+"]创建者["+right()+"]输出总分字段为空");		
//		//需要包含输出组字段			
//		if(StringUtils.isBlank(outputGroup)) 
//			throw new EngineCpntSelfCheckException("类型[评分卡]名称["+name()+"]创建者["+right()+"]输出组字段为空");		
	}
	

	
	@Override
	public String type() {
		return "评分卡V2";
	}

	@Override
	public Map<String, String> dependFields() {
		Map<String,String> result = new HashMap<String,String>();
		if(StringUtils.isNoneBlank(outputTotal)) result.put(outputTotal,"output");
		if(StringUtils.isNoneBlank(outputGroup)) result.put(outputGroup,"output");
		return result;
	}

	public List<EngineGroupBranch<BigDecimal>> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<EngineGroupBranch<BigDecimal>> groupList) {
		this.groupList = groupList;
	}

	public String getOutputTotal() {
		return outputTotal;
	}

	public void setOutputTotal(String outputTotal) {
		this.outputTotal = outputTotal;
	}

	public String getOutputGroup() {
		return outputGroup;
	}

	public void setOutputGroup(String outputGroup) {
		this.outputGroup = outputGroup;
	}		
	
	
}
