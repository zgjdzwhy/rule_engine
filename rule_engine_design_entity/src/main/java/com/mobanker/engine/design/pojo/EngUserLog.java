package com.mobanker.engine.design.pojo;

import java.sql.Timestamp;

import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.mobanker.framework.contract.dto.ResponseEntityDto;



@Table(name = "eng_user_log")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngUserLog{
	@Id
	private Long id;
	private String username;
	private String productType;	
	private String operateType;
	private String status;
	private Timestamp addtime;	
	
	public EngUserLog(String username,String productType,String operateType,ResponseEntityDto<?> ret){
		this.username = username;
		this.productType = productType;
		this.operateType = operateType;		
		this.status = ret.getStatus();
	}
}
