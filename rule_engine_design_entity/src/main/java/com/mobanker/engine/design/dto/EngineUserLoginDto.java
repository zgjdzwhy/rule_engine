package com.mobanker.engine.design.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineUserLoginDto implements Serializable{
	
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/
	private static final long serialVersionUID = 1L;

	private String loginId;
	
	private Boolean isPass;

	private String name;
	/**
	 * 0-普通业务人员,1-产品线管理员,2-部门主管
	 */
	private String role; 
	
	private String groupId;
}
