package com.mobanker.engine.framkwork.parse.judge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.judge.impl.EngineScriptJudge;
import com.mobanker.engine.framkwork.parse.EngineCpntParser;
import com.mobanker.engine.framkwork.parse.one.EngineOneAbsParser;

public class EngineScriptJudgeParser extends EngineOneAbsParser {

	private static final String BUSI_DESC = "EngineScriptJudgeParser解析器";
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public EngineCpnt parse(JSONObject json, EngineCpntParser engineCpntParser) {
		super.startToDoLog(json);
			
		String script = json.getString("script");
		//EngineAssert.checkNotBlank(script, EngineUtil.logPrefix(BUSI_DESC)+"script元素不存在");
		script = script==null?"":script;
		
		EngineScriptJudge result = new EngineScriptJudge();
		super.fillBaseInfo(result, json);
							
		result.setScript(script);		
		return result;
	}						
}
