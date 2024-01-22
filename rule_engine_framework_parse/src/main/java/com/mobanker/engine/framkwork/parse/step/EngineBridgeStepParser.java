package com.mobanker.engine.framkwork.parse.step;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.stage.EngineStage;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineBridgeStep;
import com.mobanker.engine.framkwork.exception.EngineCpntParseException;
import com.mobanker.engine.framkwork.parse.EngineCpntParser;
import com.mobanker.engine.framkwork.parse.one.EngineOneAbsParser;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月11日
 * @version 1.0
 */
@Deprecated
public class EngineBridgeStepParser extends EngineOneAbsParser{

	private static final String BUSI_DESC = "桥接器解析器";
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	@Override
	public EngineCpnt parse(JSONObject json, EngineCpntParser engineCpntParser) {
		super.startToDoLog(json);
		
		String nextStageId = json.getString("nextStage");
		if(StringUtils.isBlank(nextStageId))
			throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"nextStage元素不存在");
		
		EngineBridgeStep result = new EngineBridgeStep();
		super.fillBaseInfo(result, json);

		EngineStage stage = engineCpntParser.parse(nextStageId, EngineStage.class);	
		result.setNextStage(stage);
		
		return result;
	}

}
