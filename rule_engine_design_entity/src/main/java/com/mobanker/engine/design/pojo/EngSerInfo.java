package com.mobanker.engine.design.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngSerInfo {
	private String fileName;
	private byte[] bytes;
	private String version;
	private String productType;	
}
