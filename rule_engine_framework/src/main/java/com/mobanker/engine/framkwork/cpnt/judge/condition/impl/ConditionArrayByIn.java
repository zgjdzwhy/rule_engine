/**
* <p>Title: ConditionCharacterEquals</p>
* <p>Description: </p>
* <p>Company: </p> 
* @author zhujunjie
* @date 下午6:06:18
*/
package com.mobanker.engine.framkwork.cpnt.judge.condition.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.judge.assist.EngineConditionCollection;
import com.mobanker.engine.framkwork.cpnt.judge.condition.EngineConditionExt;
import com.mobanker.engine.framkwork.exception.EngineConditionException;
import com.mobanker.engine.framkwork.exception.EngineCpntSelfCheckException;
import com.mobanker.engine.framkwork.util.EngineUtil;


/**
* <p>Title: ConditionArrayEquals</p>
* <p>Description: </p>
* <p>Company: mobanker</p> 
* @author zhujj
* @date 2017年1月8日
* @version 1.0 
*/
public class ConditionArrayByIn extends EngineConditionExt {

	/** 
	* @Fields serialVersionUID :序列化
	*/
	private static final long serialVersionUID = 1L;
	private static final String BUSI_DESC = "数组被包含于数组";	
	
	/**
	 * 输入数值
	 */
	private List<String> valueList;
	
	
	public List<String> getValueList() {
		return valueList;
	}

	public void setValueList(List<String> valueList) {
		this.valueList = valueList;
	}

	@Override
	public boolean judge(EngineTransferData etd) {
		
		boolean result=false;
		Object paramObj=  getParamField(etd);	
		//无论字符数字都转换成字符匹配
		JSONArray paramArray=null;
		if(paramObj instanceof JSONArray){
			paramArray=(JSONArray) paramObj;
		}else{
			throw new EngineConditionException(EngineUtil.logPrefix(etd, BUSI_DESC)+"参数类型错误非数组类型,字段名:"+getField()+",参数:"+paramObj.toString());
		}
		result=true;
		for(Object objValue:paramArray){
			boolean flag=true;
			for(String value:valueList){
				if(value.trim().equals(objValue.toString())){
					flag=false;
				}
			}
			//param包含于value
			if(flag){
				result=false;
				break;
			}
		}
		
		if(valueList.size()==0&&paramArray.size()==0){
			result = true;	
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
		if(!(obj instanceof ConditionArrayByIn)) return false;
		ConditionArrayByIn another = (ConditionArrayByIn) obj;	
		
		if(StringUtils.equals(super.getField(), another.getField()))return false;
		if(StringUtils.equals(super.getCmpType(), another.getCmpType()))return false;
		if(super.isMode()==another.isMode())return false;
		if(StringUtils.equals(super.getLinker(), another.getLinker()))return false;
		
		if(this.valueList.equals(another.valueList))return false;		

		return true;
	}
}
