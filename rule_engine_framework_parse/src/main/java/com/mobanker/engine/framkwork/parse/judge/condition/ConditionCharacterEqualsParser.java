/**
* <p>Title: ConditionCharacterEqualsParser</p>
* <p>Description: </p>
* <p>Company: </p> 
* @author zhujunjie
* @date 下午2:48:21
*/
package com.mobanker.engine.framkwork.parse.judge.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.judge.condition.EngineConditionExt;
import com.mobanker.engine.framkwork.cpnt.judge.condition.impl.ConditionCharacterEquals;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.parse.EngineCpntParser;
import com.mobanker.engine.framkwork.parse.judge.one.EngineConditionOneAbsParser;

public class ConditionCharacterEqualsParser extends EngineConditionOneAbsParser{
	private static final String BUSI_DESC = "ConditionCharacterEqualsParser解析器";
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public EngineConditionExt parse(JSONObject json, EngineCpntParser engineCpntParser) {
		logger.info(BUSI_DESC+"开始解析");
		super.startToDoLog(json);
		ConditionCharacterEquals condition=new ConditionCharacterEquals();
		super.fillBaseInfo(condition, json);

		String value = json.getString("value");
		EngineAssert.checkNotBlank(value, "条件没有输入值:"+json.toJSONString());
		
		condition.setValue(value);
		condition.setCmpType(ConditionCharacterEquals.class.getSimpleName());
		logger.info(BUSI_DESC+"解析完成");
		return condition;
		
	}

}
