package com.mobanker.engine.design.pojo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Table(name = "eng_busi_operator")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngBusiOperator implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private Long id;
	private String name;
	
	@Column(nullable = false)
	private String username;
	private String password;
		
	private String productIds;  //拥有项目 以逗号分隔
	private String role; //0-普通业务人员,1-产品线管理员,2-部门主管
	private String groupId; //组id
	private Timestamp addtime;	
	
	private static final Map<String,String> roleMap = new HashMap<String, String>();
	static{
		roleMap.put("0", "业务员");
		roleMap.put("1", "产品线管理员");
		roleMap.put("2", "经理");
		roleMap.put("3", "总监");
	}
	
	public String roleDesc(){
		return roleMap.get(role);
	}
}
