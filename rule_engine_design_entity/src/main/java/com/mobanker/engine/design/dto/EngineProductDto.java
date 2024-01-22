package com.mobanker.engine.design.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineProductDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String productType;
	private String name;
	private String groupId;
	
	public EngineProductDto(Long id,String name,String productType,String groupId){
		this.id = id;
		this.name = name;
		this.productType = productType;
		this.groupId = groupId;
	}
}
