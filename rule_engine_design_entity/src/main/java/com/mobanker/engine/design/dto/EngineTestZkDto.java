package com.mobanker.engine.design.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.mobanker.engine.design.pojo.EngTestZk;


@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineTestZkDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String zkName;
	private String zkUrl;
	private String memo;	
	private String status;
	private String username;
	
	public EngineTestZkDto(EngTestZk zk){
		this.id = zk.getId();
		this.zkName = zk.getZkName();
		this.zkUrl = zk.getZkUrl();
		this.memo = zk.getMemo();
		this.status = zk.getStatus();
		this.username = zk.getUsername();
	}
}
