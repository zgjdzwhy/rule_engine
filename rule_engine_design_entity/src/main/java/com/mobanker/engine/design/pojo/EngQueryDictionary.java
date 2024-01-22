package com.mobanker.engine.design.pojo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Table(name = "eng_query_dictionary")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngQueryDictionary {
	@Id
	private Long id;
	private String queryDomain;
	
	@Column(nullable = false)
	private String queryId;
	private String queryName;
	
	@Column(nullable = false)
	private String productType;
	private String memo;	
}
