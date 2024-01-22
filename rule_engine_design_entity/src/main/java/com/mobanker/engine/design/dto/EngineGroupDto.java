package com.mobanker.engine.design.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineGroupDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String groupType;
	private String groupName;
	
	public EngineGroupDto(Long id,String groupName,String groupType){
		this.id = id;
		this.groupName = groupName;
		this.groupType = groupType;
	}
}
