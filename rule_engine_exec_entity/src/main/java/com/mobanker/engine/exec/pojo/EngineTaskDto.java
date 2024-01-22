package com.mobanker.engine.exec.pojo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineTaskDto implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/********************************************************这4个属性用于必填**************************************************/
	/**
	 * 产品类型
	 */
	private String productType;
	/**
	 * 调用方应用名
	 */
	private String appName;		
	/**
	 * 调用方请求号 要求唯一
	 */
	private String appRequestId;
	/**
	 * 调用方提供的原始数据
	 */
	private String appParams;
	
	/**
	 * 对方callback地址 可以为空如果为空 则是将结果发送到mq
	 */
	private String appCallBack;
	
	/********************************************************以下属性用于异步调用**************************************************/
	/**
	 * 优先级
	 */
	private Integer priority;

	/**
	 * 可处理时间 可为空 格式yyyyMMddHHmmss
	 */
	private String execTime;
	
}
