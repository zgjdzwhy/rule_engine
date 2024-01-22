package com.mobanker.engine.framkwork.data.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.apache.commons.lang3.StringUtils;

import com.mobanker.engine.framkwork.api.params.EngineParam;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.util.IdUtil;
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
@DatabaseTable(tableName = "RULE_ENGINE:step_login_in")
public class StepLoginInEntity {
    /**
    *
    * rowKey:userId+orderId
    */
    @DatabaseField(id = true)
    private String id;
    
    @DatabaseField(familyName = "a")
	private Long taskId;
    
    @DatabaseField(familyName = "a")
	private String taskTime;
    
    @DatabaseField(familyName = "a")
	private String curStepName;
    
    @DatabaseField(familyName = "a")
	private Long curStepId;
    
    @DatabaseField(familyName = "a")
	private String curStepBean;
    
    @DatabaseField(familyName = "a")
	private long curTime;
    
    @DatabaseField(familyName = "a")
	private String ipAddress;
	
	//产品线
    @DatabaseField(familyName = "a")
	private String productType;
    
    @DatabaseField(familyName = "a")
	private String appName;
    
    @DatabaseField(familyName = "a")
	private String appRequestId;
	
	//引擎三要素
    @DatabaseField(familyName = "a")
	private String inputParam;
    
    @DatabaseField(familyName = "a")
	private String outputParam;
	
    @DatabaseField(familyName = "a")
	private String ruleVersion;
	
    @DatabaseField(familyName = "a")
    private String createTime;//	创建时间

    @DatabaseField(familyName = "a")
    private String updateTime;//	更新时间
    
    
    /**  单个生成rowkey rowkey:StepId(反转补齐10位) - ProductType */
    public void setRowKey() {
        if(StringUtils.isBlank(this.getCurStepId().toString())){
            throw new EngineException("stepId为空！");
        }
        this.id = IdUtil.getRowkeyId(this.getCurStepId().toString(), this.getProductType());
    }

}
