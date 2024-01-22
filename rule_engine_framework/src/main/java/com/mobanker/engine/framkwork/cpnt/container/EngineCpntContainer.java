package com.mobanker.engine.framkwork.cpnt.container;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.flow.EnginePolicyFlow;

public class EngineCpntContainer implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String,EngineCpnt> allCpnt;
	private EnginePolicyFlow root;
	private String ruleVersion;
	
	public Map<String, EngineCpnt> getAllCpnt() {
		return allCpnt;
	}
	public void setAllCpnt(Map<String, EngineCpnt> allCpnt) {
		this.allCpnt = allCpnt;
	}
	public EnginePolicyFlow getRoot() {
		return root;
	}
	public void setRoot(EnginePolicyFlow root) {
		this.root = root;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getOneCpnt(String id,Class<T> clazz){
		if(allCpnt.get(id)==null) return null;		
		return (T) allCpnt.get(id);
	}
	
	public EngineCpnt getOneCpnt(String cpntId){
		if(allCpnt.get(cpntId)==null) return null;		
		return allCpnt.get(cpntId);
	}
	
	public <T> List<T> getCpntsByType(Class<T> clazz){
		List<T> cpntList = new LinkedList<T>();
		for(EngineCpnt cpnt : allCpnt.values()){
			if(clazz.isInstance(cpnt)){
				@SuppressWarnings("unchecked")
				T t = (T) cpnt;
				cpntList.add(t);
			}				
		}
		
		return cpntList;
	}
	public String getRuleVersion() {
		return ruleVersion;
	}
	public void setRuleVersion(String ruleVersion) {
		this.ruleVersion = ruleVersion;
	}
	
	
	
}
