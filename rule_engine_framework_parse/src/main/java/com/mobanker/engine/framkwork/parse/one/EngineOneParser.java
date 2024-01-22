package com.mobanker.engine.framkwork.parse.one;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.parse.EngineCpntParser;

/**
 * 单个组件解析接口
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年10月9日
 * @version 1.0
 */
public interface EngineOneParser {
	public EngineCpnt parse(JSONObject json,EngineCpntParser engineCpntParser) throws StackOverflowError;
}
