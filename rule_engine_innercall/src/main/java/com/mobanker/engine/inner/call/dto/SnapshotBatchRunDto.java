package com.mobanker.engine.inner.call.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class SnapshotBatchRunDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long releaseId;
	private String username;
	private String productType;	
	private String beginTime;
	private String endTime;
		
}
