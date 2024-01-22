package com.mobanker.engine.design.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineCpntListDisplay {
	public String id;
	public String name;
	public String type;
	public String right;
	public String updatetime;	
	
	public EngineCpntListDisplay(String id,String name,String type,String right,String updatetime){
		this.id = id;
		this.name = name;
		this.type = type;
		this.right = right;
		this.updatetime = updatetime;
	}
}
