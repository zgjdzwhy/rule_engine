package com.mobanker.engine.design.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineQueryFieldDto {	
	private String fieldType;
	private String fieldKey;
	private String fieldName;
	private String fieldRefValue;
	private String fieldDefaultValue;
	private String isArr;
}
