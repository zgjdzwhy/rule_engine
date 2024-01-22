package com.mobanker.engine.design.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineQueryModelDto {	
	private String queryDomain;
	private String queryId;
	private String queryName;
	private List<EngineQueryFieldDto> fieldList;
	private String memo;	
}
