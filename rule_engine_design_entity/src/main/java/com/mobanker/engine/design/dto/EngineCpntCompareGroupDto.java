package com.mobanker.engine.design.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineCpntCompareGroupDto {
	private EngineCpntCompareDto one;	
	private EngineCpntCompareDto another;	
		
	public EngineCpntCompareGroupDto(EngineCpntCompareDto one,EngineCpntCompareDto another){
		this.one = one;
		this.another = another;
	}	
}
