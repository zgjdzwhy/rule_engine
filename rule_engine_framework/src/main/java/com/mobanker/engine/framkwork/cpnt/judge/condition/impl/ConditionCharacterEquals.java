/**
* <p>Title: ConditionCharacterEquals</p>
* <p>Description: </p>
* <p>Company: </p> 
* @author zhujunjie
* @date 下午6:06:18
*/
package com.mobanker.engine.framkwork.cpnt.judge.condition.impl;

import org.apache.commons.lang3.StringUtils;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.judge.condition.EngineConditionExt;
import com.mobanker.engine.framkwork.exception.EngineCpntSelfCheckException;


/**
* <p>Title: ConditionCharacterEquals</p>
* <p>Description: </p>
* <p>Company: mobanker</p> 
* @author zhujj
* @date 2017年1月8日
* @version 1.0 
*/
public class ConditionCharacterEquals extends EngineConditionExt {

	/** 
	* @Fields serialVersionUID :序列化
	*/
	private static final long serialVersionUID = 1L;
	private static final String BUSI_DESC = "字符等于";	
	
	/**
	 * 输入数值
	 */
	private String value;
	
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean judge(EngineTransferData etd) {
		
		boolean result=false;
		Object paramObj=  getParamField(etd);	
		//无论字符数字都转换成字符匹配
		String paramValue=paramObj.toString();
		if(value.trim().equals(paramValue)){
			result=true;
		}
		
		return result == isMode();
	}

	@Override
	public String type() {
		// TODO Auto-generated method stub
		return BUSI_DESC;
	}

	@Override
	public void checkSelf() throws EngineCpntSelfCheckException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ConditionCharacterEquals)) return false;
		ConditionCharacterEquals another = (ConditionCharacterEquals) obj;	
		
		if(StringUtils.equals(super.getField(), another.getField()))return false;
		if(StringUtils.equals(super.getCmpType(), another.getCmpType()))return false;
		if(super.isMode()==another.isMode())return false;
		if(StringUtils.equals(super.getLinker(), another.getLinker()))return false;
		
		if(StringUtils.equals(this.value,another.value))return false;		

		return true;
	}
}
