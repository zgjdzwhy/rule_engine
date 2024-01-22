package com.mobanker.engine.exec.business.snapshot.dto;

import java.io.Serializable;

public class BatchStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int status;
	private String msg;	
	
	public BatchStatus(int status,String msg){
		this.status = status;
		this.msg = msg;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
