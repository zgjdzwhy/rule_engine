package com.mobanker.engine.design.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineCpntCompareDto {
	private String cpntId;
	private String cpntName;
	private String type;	
	private String version;
}
