package com.mobanker.engine.design.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Table(name = "eng_group")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngGroup implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	@Column(nullable = false)
	private String groupType;	
	private String groupName;
	private String creatorUser;
	
	private Timestamp addtime;	
}
