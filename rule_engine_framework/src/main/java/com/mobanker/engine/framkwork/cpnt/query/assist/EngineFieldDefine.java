package com.mobanker.engine.framkwork.cpnt.query.assist;

import com.alibaba.fastjson.JSONObject;



public class EngineFieldDefine {
	/**
	 * 指标字段
	 */
	private String fieldKey;
	private String refValue;
	private String defaultValue;
	
	/**
	 * 指标类型String Long BigDecimal
	 */
	private String fieldType;

	public String getFieldKey() {
		return fieldKey;
	}

	public void setFieldKey(String fieldKey) {
		this.fieldKey = fieldKey;
	}

	public String getRefValue() {
		return refValue;
	}

	public void setRefValue(String refValue) {
		this.refValue = refValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	
	@Override
	public String toString(){
		return JSONObject.toJSONString(this);
	}
}
