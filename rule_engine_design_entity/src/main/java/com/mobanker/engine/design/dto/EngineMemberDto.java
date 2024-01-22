package com.mobanker.engine.design.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineMemberDto {
	
	private String username;
	private String realname;
	private String role;
	private String roleDesc;
	
}
