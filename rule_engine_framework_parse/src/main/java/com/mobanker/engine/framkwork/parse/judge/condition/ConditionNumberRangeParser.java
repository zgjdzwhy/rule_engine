/**
* <p>Title: ConditionCharacterEqualsParser</p>
* <p>Description: </p>
* <p>Company: </p> 
* @author zhujunjie
* @date 下午2:48:21
*/
package com.mobanker.engine.framkwork.parse.judge.condition;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.judge.condition.EngineConditionExt;
import com.mobanker.engine.framkwork.cpnt.judge.condition.impl.ConditionNumberRange;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineCpntParseException;
import com.mobanker.engine.framkwork.parse.EngineCpntParser;
import com.mobanker.engine.framkwork.parse.judge.one.EngineConditionOneAbsParser;
import com.mobanker.engine.framkwork.util.EngineUtil;

public class ConditionNumberRangeParser extends EngineConditionOneAbsParser{
	private static final String BUSI_DESC = "ConditionNumberRangeParser解析器";
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public EngineConditionExt parse(JSONObject json, EngineCpntParser engineCpntParser) {
		logger.info(BUSI_DESC+"开始解析");
		super.startToDoLog(json);
		ConditionNumberRange condition=new ConditionNumberRange();
		super.fillBaseInfo(condition, json);

		String value = json.getString("value");
		EngineAssert.checkNotBlank(value, "条件没有输入值:"+json.toJSONString());
		
	    Pattern p = Pattern.compile(super.regular);  
	    Matcher m = p.matcher(value); 
	    
	    if(!m.matches())throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"输入值格式不正确！ "+value);
		
	    String[] array =StringUtils.split(value, ",");
	    
	    String firstOperator=StringUtils.substring(array[0], 0,1);
	    if("[".equals(firstOperator)){
	    	condition.setMinOperator(0);
	    }else if("(".equals(firstOperator)){
	    	condition.setMinOperator(1);
	    }else{
	    	throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"输入值格式不正确！ "+value);
	    }
	    String minValueStr=StringUtils.substring(array[0],1);
	    if("*".equals(minValueStr)){
	    	condition.setMinOperator(-1);
	    }else{
	    	BigDecimal minValue;
			try {
				minValue = new BigDecimal(minValueStr);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"BigDecimal类型转换异常！ "+minValueStr);
			}
	    	condition.setMinValue(minValue);
	    }
	    
	    String secondOperator=StringUtils.substring(array[1], array[1].length()-1,array[1].length());
	    if("]".equals(secondOperator)){
	    	condition.setMaxOperator(0);
	    }else if(")".equals(secondOperator)){
	    	condition.setMaxOperator(1);
	    }else{
	    	throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"输入值格式不正确！ "+value);
	    }
	    String maxValueStr=StringUtils.substring(array[1],0,array[1].length()-1);
	    if("*".equals(maxValueStr)){
	    	condition.setMinOperator(-1);
	    }else{
	    	BigDecimal maxValue;
			try {
				maxValue = new BigDecimal(maxValueStr);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"BigDecimal类型转换异常！ "+maxValueStr);
			}
	    	condition.setMaxValue(maxValue);
	    	if(condition.getMinValue()!=null){
	    		if(condition.getMaxValue().compareTo(condition.getMinValue())<0){
	    			throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"最小值大于最大值！ "+value);
	    		}
	    	}
	    	
	    }
	    
		condition.setCmpType(ConditionNumberRange.class.getSimpleName());
		logger.info(BUSI_DESC+"解析完成");
		return condition;
		
	}

}
