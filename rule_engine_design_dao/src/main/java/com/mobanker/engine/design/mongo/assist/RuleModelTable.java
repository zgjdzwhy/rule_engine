package com.mobanker.engine.design.mongo.assist;

public enum RuleModelTable {
	RELEASE("rule_model_release"), MERGE("rule_model_merge"), SELF("rule_model_self");
	
	private String name;
	private RuleModelTable(String name) {  
        this.name = name;  
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}  
	
}
