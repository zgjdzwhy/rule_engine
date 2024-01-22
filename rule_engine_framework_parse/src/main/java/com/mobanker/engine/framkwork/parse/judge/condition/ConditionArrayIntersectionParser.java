/**
* <p>Title: ConditionCharacterEqualsParser</p>
* <p>Description: </p>
* <p>Company: </p> 
* @author zhujunjie
* @date 下午2:48:21
*/
package com.mobanker.engine.framkwork.parse.judge.condition;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.judge.condition.EngineConditionExt;
import com.mobanker.engine.framkwork.cpnt.judge.condition.impl.ConditionArrayEquals;
import com.mobanker.engine.framkwork.cpnt.judge.condition.impl.ConditionArrayIntersection;
import com.mobanker.engine.framkwork.cpnt.judge.condition.impl.ConditionCharacterEquals;
import com.mobanker.engine.framkwork.cpnt.judge.condition.impl.ConditionCharacterIn;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.parse.EngineCpntParser;
import com.mobanker.engine.framkwork.parse.judge.one.EngineConditionOneAbsParser;

public class ConditionArrayIntersectionParser extends EngineConditionOneAbsParser{
	private static final String BUSI_DESC = "ConditionArrayIntersectionParser解析器";
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public EngineConditionExt parse(JSONObject json, EngineCpntParser engineCpntParser) {
		logger.info(BUSI_DESC+"开始解析");
		super.startToDoLog(json);
		ConditionArrayIntersection condition=new ConditionArrayIntersection();
		super.fillBaseInfo(condition, json);

		String value = json.getString("value");
		EngineAssert.checkNotBlank(value, "条件没有输入值:"+json.toJSONString());
		String[] array=StringUtils.split(value, ",");
		List<String> valueList=new ArrayList<String>();
		for(String paramter:array){
			valueList.add(paramter);
		}
		condition.setValueList(valueList);
		condition.setCmpType(ConditionCharacterIn.class.getSimpleName());
		logger.info(BUSI_DESC+"解析完成");
		return condition;
		
	}

}
