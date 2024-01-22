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
import com.mobanker.engine.framkwork.cpnt.judge.condition.impl.ConditionCharacterByLike;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.parse.EngineCpntParser;
import com.mobanker.engine.framkwork.parse.judge.one.EngineConditionOneAbsParser;

public class ConditionCharacterByLikeParser extends EngineConditionOneAbsParser{
	private static final String BUSI_DESC = "ConditionCharacterByLikeParser解析器";
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public EngineConditionExt parse(JSONObject json, EngineCpntParser engineCpntParser) {
		logger.info(BUSI_DESC+"开始解析");
		super.startToDoLog(json);
		ConditionCharacterByLike condition=new ConditionCharacterByLike();
		super.fillBaseInfo(condition, json);

		String value = json.getString("value");
		EngineAssert.checkNotBlank(value, "条件没有输入值:"+json.toJSONString());
		
		condition.setValue(value);
		condition.setCmpType(ConditionCharacterByLike.class.getSimpleName());
		logger.info(BUSI_DESC+"解析完成");
		return condition;
		
	}

}
