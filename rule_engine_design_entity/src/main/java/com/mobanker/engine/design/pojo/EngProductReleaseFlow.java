package com.mobanker.engine.design.pojo;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Table(name = "eng_product_release_flow")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngProductReleaseFlow{
	@Id
	private Long id;
	@Column(nullable = false)
	private String productType;
	
	private String productTypeId;
	
	private String version;
	private String versionName;
	
	private String status;
	
	@Column(nullable = false)
	private String applyUser;
	private String applyTime;
	
	private String auditUser1;
	private String auditTime1;
	private String auditUser2;
	private String auditTime2;
	
	private String finalAuditUser;
	private String finalAuditTime;
	
	private String releaseTime;

	private Timestamp addtime;	
	
	public void init(){
		productType = "";
		version = "";
		versionName = "";
		status = "";
		applyUser = "";
		applyTime = "";
		auditUser1 = "";
		auditTime1 = "";
		auditUser2 = "";
		auditTime2 = "";
		finalAuditUser = "";
		finalAuditTime = "";
		releaseTime = "";
	}	
	private final static Map<String,String> statusMap = new HashMap<String,String>();
	static{
		statusMap.put("0", "申请[0]");
		statusMap.put("1", "审核[1]");
		statusMap.put("2", "审核[2]");
		statusMap.put("f", "完成[f]");
		statusMap.put("c", "撤销[c]");		
	}
	
	public static String getStatusText(String status){		
		String result = statusMap.get(status);
		return result==null?"未知["+status+"]":result;
	}
}
