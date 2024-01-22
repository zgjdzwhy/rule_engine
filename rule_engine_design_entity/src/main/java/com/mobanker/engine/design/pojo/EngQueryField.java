package com.mobanker.engine.design.pojo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Table(name = "eng_query_field")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngQueryField {
	@Id
	private Long id;
	
	@Column(nullable = false)
	private String queryId;
	private String fieldType;
	
	@Column(nullable = false)
	private String fieldKey;
	@Column(nullable = false)
	private String fieldName;
	
	private String fieldRefValue;
	private String fieldDefaultValue;
	private String isArr;
	private String productType;
	private String memo;	
}
