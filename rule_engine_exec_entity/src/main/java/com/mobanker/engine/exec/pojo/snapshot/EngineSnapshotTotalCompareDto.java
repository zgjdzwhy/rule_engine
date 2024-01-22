package com.mobanker.engine.exec.pojo.snapshot;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 总体对比
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年1月24日
 * @version 1.0
 */

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineSnapshotTotalCompareDto implements Serializable{
	 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EngineChart oldOne;
	private EngineChart newOne;
	
	public EngineSnapshotTotalCompareDto(EngineChart oldOne,EngineChart newOne){
		this.oldOne = oldOne;
		this.newOne = newOne;
	}
}
