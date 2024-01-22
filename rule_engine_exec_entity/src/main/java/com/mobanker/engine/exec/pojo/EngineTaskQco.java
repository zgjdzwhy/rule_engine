package com.mobanker.engine.exec.pojo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.mobanker.engine.common.page.PageQco;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineTaskQco extends PageQco implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 产品类型
	 */
	private String productType;	
	/**
	 * 请求流水号
	 */
	private String appRequestId;
	/**
	 * 参数（模糊匹配）
	 */
	private String appParams;
	/**
	 * 任务状态
	 */
	private String status;
	
	/**
	 * 任务分配情况
	 */
	private Integer assignStep;
	
	/**
	 * 起始时间
	 */
	private String beginTime;
	
	/**
	 * 结束时间
	 */
	private String endTime;
	
	/**
	 * 规则版本号
	 */
	private String ruleVersion;
	
}
