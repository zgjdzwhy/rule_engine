/**
* <p>Title: ConditionCharacterEquals</p>
* <p>Description: </p>
* <p>Company: </p> 
* @author zhujunjie
* @date 下午6:06:18
*/
package com.mobanker.engine.framkwork.cpnt.judge.condition.impl;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.judge.condition.EngineConditionExt;
import com.mobanker.engine.framkwork.exception.EngineConditionException;
import com.mobanker.engine.framkwork.exception.EngineCpntSelfCheckException;
import com.mobanker.engine.framkwork.util.EngineUtil;


/**
* <p>Title: ConditionCharacterEquals</p>
* <p>Description: </p>
* <p>Company: mobanker</p> 
* @author zhujj
* @date 2017年1月8日
* @version 1.0 
*/
public class ConditionNumberRange extends EngineConditionExt {

	/** 
	* @Fields serialVersionUID :序列化
	*/
	private static final long serialVersionUID = 1L;
	private static final String BUSI_DESC = "数字范围";	
	
	/**
	 * 最小值
	 */
	private BigDecimal minValue;
	/**
	 * 最小值运算符 -1 无限  0 大于等于 1 大于
	 */
	private int minOperator=-1;
	/**
	 * 最大值
	 */
	private BigDecimal maxValue;
	/**
	 * 最大值运算符 -1 无限  0 小于等于 1小于
	 */
	private int maxOperator=-1;

	
	public BigDecimal getMinValue() {
		return minValue;
	}

	public void setMinValue(BigDecimal minValue) {
		this.minValue = minValue;
	}

	public int getMinOperator() {
		return minOperator;
	}

	public void setMinOperator(int minOperator) {
		this.minOperator = minOperator;
	}

	public BigDecimal getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(BigDecimal maxValue) {
		this.maxValue = maxValue;
	}

	public int getMaxOperator() {
		return maxOperator;
	}

	public void setMaxOperator(int maxOperator) {
		this.maxOperator = maxOperator;
	}

	@Override
	public boolean judge(EngineTransferData etd) {
		
		boolean result=false;
		Object paramObj= getParamField(etd);
		String paramValueStr=paramObj.toString();
		
		BigDecimal paramValue;
		try {
			paramValue = new BigDecimal(paramValueStr);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			throw new EngineConditionException(EngineUtil.logPrefix(etd, BUSI_DESC)+"条件发生错误,BigDecimal字段类型转换异常,字段名:"+getField(),e);
		}
		
		if(minOperator>=0){
			if(paramValue.compareTo(minValue)>=minOperator){
				if(maxOperator>=0){
					if(maxValue.compareTo(paramValue)>=maxOperator){
						result=true;
					}
				}else{
					result=true;
				}
			}
		}else{
			if(maxOperator>=0){
				if(maxValue.compareTo(paramValue)>=maxOperator){
					result=true;
				}
			}else{
				result=true;
			}
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
		if(!(obj instanceof ConditionNumberRange)) return false;
		ConditionNumberRange another = (ConditionNumberRange) obj;	
		
		if(StringUtils.equals(super.getField(), another.getField()))return false;
		if(StringUtils.equals(super.getCmpType(), another.getCmpType()))return false;
		if(super.isMode()==another.isMode())return false;
		if(StringUtils.equals(super.getLinker(), another.getLinker()))return false;
		
		if(this.minValue.compareTo(another.minValue)==0)return false;		
		if(this.maxValue.compareTo(another.maxValue)==0)return false;
		if(this.minOperator==another.minOperator)return false;
		if(this.maxOperator==another.maxOperator)return false;		

		return true;
	}
}
