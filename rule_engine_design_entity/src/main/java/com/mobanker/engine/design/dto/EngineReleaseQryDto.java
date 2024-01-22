package com.mobanker.engine.design.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineReleaseQryDto {
	private String username;
	private String productType;
	private String status;
	private String version;
	private String applyUser;
	private Boolean isPrd;
}
