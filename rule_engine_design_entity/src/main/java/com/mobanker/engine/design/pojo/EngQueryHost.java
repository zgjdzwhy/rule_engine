package com.mobanker.engine.design.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Table(name = "eng_query_host")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngQueryHost implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	private String url;
	
	private String urlParamKeys;
	private String urlType;
	private Integer timeout;
	
	@Column(nullable = false)
	private String productType;
	private String fatalDeal;
	private String memo;	
}
