package com.mobanker.engine.design.pojo.hbase;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.apache.commons.lang3.StringUtils;

import com.mobanker.framework.hbase.dao.annonation.DatabaseField;
import com.mobanker.framework.hbase.dao.annonation.DatabaseTable;

/**
* <p>Title: StepLoginInEntity</p>
* <p>Description: </p>
* <p>Company: mobanker</p> 
* @author zhujj
* @date 2017年6月16日
* @version 1.0 
*/
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@DatabaseTable(tableName = "RULE_ENGINE:script_function")
public class ScriptFunctionEntity {
    /**
    *
    * rowKey:userId+orderId
    */
	
    @DatabaseField(id = true)
    private String id;
    
    @DatabaseField(familyName = "a")
	private String label;
    
    @DatabaseField(familyName = "a")
	private String name;
    
    @DatabaseField(familyName = "a")
	private String template;	
    
    @DatabaseField(familyName = "a")
    private String function;
    
    @DatabaseField(familyName = "a")
	private String productType;	
    
    @DatabaseField(familyName = "a")
	private String username;
    
    @DatabaseField(familyName = "a")
	private String sample;
		
    @DatabaseField(familyName = "a")
    private String createTime;//	创建时间

    @DatabaseField(familyName = "a")
    private String updateTime;//	更新时间
    
    /**  单个生成rowkey rowkey:Label - ProductType */
    public void setRowKey() {
    	if(StringUtils.isBlank(this.getLabel())) throw new RuntimeException("label为空");
    	if(StringUtils.isBlank(this.getProductType())) throw new RuntimeException("productType为空");
        this.id = this.getLabel() + "-" + this.getProductType();
    }

}
