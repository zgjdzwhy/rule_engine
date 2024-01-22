package com.mobanker.engine.framkwork.parse.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineScriptStep;
import com.mobanker.engine.framkwork.parse.EngineCpntParser;
import com.mobanker.engine.framkwork.parse.one.EngineOneAbsParser;

public class EngineScriptStepParser extends EngineOneAbsParser {

	private static final String BUSI_DESC = "脚本解析器";
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
		
	
	@Override
	public EngineCpnt parse(JSONObject json,EngineCpntParser engineCpntParser) {
		EngineScriptStep result = new EngineScriptStep();
		super.fillBaseInfo(result, json);
		
		String script = json.getString("script");		
		script = script==null?"":script;
		
		result.setScript(script);
		
		return result;
	}

}
