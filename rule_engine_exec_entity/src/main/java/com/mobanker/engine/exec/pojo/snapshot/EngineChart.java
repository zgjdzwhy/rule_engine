package com.mobanker.engine.exec.pojo.snapshot;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.alibaba.fastjson.JSONObject;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineChart implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type;
	//分析结果 1-成功 0-失败
	private int status;
	private int size;
	//消息
	private String msg;
	private JSONObject content;
	
	public EngineChart(String type,int status,String msg){
		this.type = type;
		this.status = status;
		this.msg = msg;
	}
}
