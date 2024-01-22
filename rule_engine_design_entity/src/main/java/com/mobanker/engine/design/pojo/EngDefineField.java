package com.mobanker.engine.design.pojo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Table(name = "eng_define_field")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngDefineField {
	@Id
	private Long id;
		
	private String fieldType;
	
	@Column(nullable = false)
	private String fieldKey;
	
	@Column(nullable = false)
	private String fieldName;	
	private String isArr;
	private String fieldRefValue;
	
	@Column(nullable = false)
	private String fieldUse;
	
	@Column(nullable = false)
	private String username;
	
	@Column(nullable = false)
	private String productType;
	
	private String memo;	
	private Timestamp addtime;
}
