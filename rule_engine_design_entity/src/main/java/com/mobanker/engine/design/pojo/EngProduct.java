package com.mobanker.engine.design.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Table(name = "eng_product")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngProduct implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	@Column(nullable = false)
	private String productType;	
	private String name;
	private String groupId;
	private String creatorUser;
	
	private Timestamp addtime;	
}
