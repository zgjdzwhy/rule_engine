package com.mobanker.engine.exec.business.snapshot.dto;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;

public class EngineParamSnapShot {
	private String table;
	private String productType;
	private String etdContent;
	
	public EngineParamSnapShot(String table,EngineTransferData etd){
		this.table = table;
		this.productType = etd.getProductType();
		this.etdContent = JSONObject.toJSONString(etd);
	}
	
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}

	public String getEtdContent() {
		return etdContent;
	}

	public void setEtdContent(String etdContent) {
		this.etdContent = etdContent;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	
	
}
