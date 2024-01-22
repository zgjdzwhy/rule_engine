package com.mobanker.engine.design.pojo;

import javax.persistence.Id;
import javax.persistence.Table;

import com.mobanker.framework.crud.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Table(name = "eng_rule_model")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngRuleModel extends BaseEntity{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	private String repositoryType;
	private String productType;
	private String ruleId;	
	private String ruleRight;
	private String ruleName;
	private String cpntType;
	private String freshPoint;
	private String version;
	private String status;
	private String encryptKey;
	private String content;
	private String serverIp;
	
}
