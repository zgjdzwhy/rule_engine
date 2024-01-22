package com.mobanker.engine.design.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineDefineFieldDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String fieldType;
	private String fieldKey;
	private String fieldName;	
	private String isArr;
	private String fieldUse;	
	private String fieldRefValue;
	private String username;
	private String realname;
}
