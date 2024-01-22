package com.mobanker.engine.design.pojo;

import java.io.Serializable;

import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Table(name = "rule_script_template")
public class EngScriptTemplate implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String label;
	private String name;
	private String template;	
	private String productType;	
	private String username;
	private String sample;
}
