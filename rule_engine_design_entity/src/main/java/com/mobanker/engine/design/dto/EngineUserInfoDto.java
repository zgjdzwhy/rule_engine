package com.mobanker.engine.design.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineUserInfoDto {
	
	private String username;
	private String realname;
	/**
	 * 0-普通业务人员,1-产品线管理员,2-部门主管
	 */
	private String role; 
	private String productIds;
	private String groupId;
	
}
