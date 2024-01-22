package com.mobanker.engine.framkwork.cpnt.step.assist;


import java.io.Serializable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import com.mobanker.engine.framkwork.cpnt.judge.EngineJudge;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;
import com.mobanker.engine.framkwork.relation.EngineRelaField;

/**
 * 组分支
 * <p>Company: mobanker.com</p>
 * @author zhujj
 * @date 2016年12月16日
 * @version 1.0
 * @param <T>
 */
public class EngineGroupBranch<T>  implements EngineRelaCpnt,EngineRelaField,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 组名
	 */
	private String name;
	/**
	 * 规则集
	 */
	private List<EngineBranch<T>> judgeList = new LinkedList<EngineBranch<T>>();
	
	public String toString(){
		return "组名:"+this.name;
	}
	
	public EngineGroupBranch(){}
	
	public EngineGroupBranch(String name,List<EngineBranch<T>> judgeList){
		this.name = name;
		this.judgeList = judgeList;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EngineBranch<T>> getJudgeList() {
		return judgeList;
	}

	public void setJudgeList(List<EngineBranch<T>> judgeList) {
		this.judgeList = judgeList;
	}

	public void addJudge(EngineJudge judge,boolean mode,T contant){
		EngineBranch<T> engineRuleJudge = new EngineBranch<T>(judge,mode,contant);
		judgeList.add(engineRuleJudge);
	}
	
	@Override
	public Map<String,Boolean> relaCpntIds() {
		Map<String,Boolean> result = new HashMap<String,Boolean>();		
		for(EngineBranch<T> branch:judgeList){
			result.putAll(branch.relaCpntIds());					
		}
		return result;
	}	
	
	@Override
	public Set<String> inputRela() {
		Set<String> result = new HashSet<String>();
		for(EngineBranch<T> branch:judgeList){
			result.addAll(branch.inputRela());
		}				
		return result;
	}

	@Override
	public Set<String> outputRela() {
		Set<String> result = new HashSet<String>();
		
		for(EngineBranch<T> branch:judgeList){
			result.addAll(branch.outputRela());
		}					
		return result;
	}

	/* (非 Javadoc) 
	* <p>Title: equals</p> 
	* <p>Description: </p> 
	* @param obj
	* @return 
	* @see java.lang.Object#equals(java.lang.Object) 
	*/
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof EngineGroupBranch)) return false;
		EngineGroupBranch<?> another = (EngineGroupBranch<?>) obj;		
		if(!StringUtils.equals(this.name, another.name)) return false;		
		if(!this.judgeList.equals(another.judgeList)) return false;		
				
		return true;
	}
	
	
}
