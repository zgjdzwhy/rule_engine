package com.mobanker.engine.rpc.dto;

import java.io.Serializable;

public class EngineResponse<T> implements Serializable {
	private static final long serialVersionUID = -720807478055084231L;
	
	private String status;
	private String error;
	private String msg;
	private T data;

	public EngineResponse(){
		
	}
	
	public EngineResponse(String status){
		this.status = status;
	}
	
	public EngineResponse(String status, String error){
		this.status = status;
		this.error = error;
	}
	
	public EngineResponse(String status, T data){
		this.status = status;
		this.data = data;
	}
	

	public EngineResponse(String status, String error, String msg, T data){
		this.status = status;
		this.error = error;
		this.msg = msg;
		this.data = data;
	}
	
	public String getStatus() {
		return status;
	}

	public EngineResponse<T> setStatus(String status) {
		this.status = status;
		return this;
	}

	public String getError() {
		return error;
	}

	public EngineResponse<T> setError(String error) {
		this.error = error;
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public EngineResponse<T> setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public T getData() {
		return data;
	}

	public EngineResponse<T> setData(T data) {
		this.data = data;
		return this;
	}

}