package com.mobanker.engine.design.dto;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Deprecated
public class EngineStageDto {	
	private String id;
	private String name;
	private String right;
	private EngineStageDto nextNode;
	private List<EngineStageDto> children;
		
	public void addChild(EngineStageDto dto){
		if(children == null)
			children = new LinkedList<EngineStageDto>();
		children.add(dto);		
	}
}
