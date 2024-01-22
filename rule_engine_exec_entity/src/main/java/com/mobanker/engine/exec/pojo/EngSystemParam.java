package com.mobanker.engine.exec.pojo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Table(name = "eng_system_param")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngSystemParam {
	@Id
	private Long id;
	private String name;
	
	@Column(nullable = false)
	private String nid;
	private String value;	
	
}
