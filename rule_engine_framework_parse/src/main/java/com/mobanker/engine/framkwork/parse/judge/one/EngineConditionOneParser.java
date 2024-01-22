package com.mobanker.engine.framkwork.parse.judge.one;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.judge.condition.EngineConditionExt;
import com.mobanker.engine.framkwork.parse.EngineCpntParser;

/**
 * 单个组件解析接口
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年10月9日
 * @version 1.0
 */
public interface EngineConditionOneParser {
	public EngineConditionExt parse(JSONObject json,EngineCpntParser engineCpntParser) throws StackOverflowError;
}
