package com.mobanker.engine.framkwork.cpnt.query.assist;

import java.util.List;



public class EngineQueryDefine {
	private String queryId;
	private List<EngineFieldDefine> fieldDefineList;
	public String getQueryId() {
		return queryId;
	}
	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}
	public List<EngineFieldDefine> getFieldDefineList() {
		return fieldDefineList;
	}
	public void setFieldDefineList(List<EngineFieldDefine> fieldDefineList) {
		this.fieldDefineList = fieldDefineList;
	}	
	
	
}
