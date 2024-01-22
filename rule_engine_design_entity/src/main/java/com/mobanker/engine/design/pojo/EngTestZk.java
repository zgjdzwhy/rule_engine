package com.mobanker.engine.design.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Table(name = "eng_test_zk")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngTestZk implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private Long id;

	private String zkName;
	private String zkUrl;
	private String memo;	
	private String status;
	private String username;
	private Timestamp addtime;	
	
	
}
