package com.mobanker.engine.framkwork.cpnt.judge.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpntBaseInfo;
import com.mobanker.engine.framkwork.cpnt.judge.EngineJudge;
import com.mobanker.engine.framkwork.cpnt.judge.assist.EngineConditionCollection;
import com.mobanker.engine.framkwork.relation.EngineDependField;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;

public class EngineConditionJudge extends EngineCpntBaseInfo implements
	EngineJudge,EngineRelaCpnt,EngineDependField {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(EngineConditionJudge.class);	
	private static final String BUSI_DESC = "条件规则";	
	
	private EngineConditionCollection root;
	
	public EngineConditionCollection getRoot() {
		return root;
	}

	public void setRoot(EngineConditionCollection root) {
		this.root = root;
	}

	@Override
	public Map<String,Boolean> relaCpntIds() {		
		return Collections.emptyMap();
	}	
	
	@Override
	public boolean judge(EngineTransferData etd) {		
		boolean result=true;
        result=root.judge(etd);
		return result;
	}		
	
	@Override
	public Set<String> inputRela() {		
		Set<String> result = new HashSet<String>();
		result=root.inputRela();
		return result;
	}

	@Override
	public Set<String> outputRela() {
		Set<String> result = new HashSet<String>();
		result=root.outputRela();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof EngineConditionJudge)) return false;
		EngineConditionJudge another = (EngineConditionJudge) obj;	
		if(!Objects.equals(this.root, another.root)) return false;		
					
		return true;
	}

	@Override
	public Map<String,String> dependFields() {
		Map<String,String> result = new HashMap<String,String>();
//		for(String key : inputField){
//			result.put(key,"input");
//		}
//		for(String key : outputField){
//			result.put(key,"output");
//		}
		return result;
	}

	@Override
	public String type() {
		return BUSI_DESC;
	}		


}
