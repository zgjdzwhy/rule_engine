package com.mobanker.engine.design.pojo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Table(name = "eng_fat_test")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngFatTest {
	@Id
	private Long id;
	private String url;
	@Column(nullable = false)
	private String productType;
	private String memo;
}
