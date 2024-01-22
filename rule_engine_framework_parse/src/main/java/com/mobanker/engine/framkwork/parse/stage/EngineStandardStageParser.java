package com.mobanker.engine.framkwork.parse.stage;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.stage.EngineStandardStage;
import com.mobanker.engine.framkwork.cpnt.step.EngineStep;
import com.mobanker.engine.framkwork.exception.EngineCpntParseException;
import com.mobanker.engine.framkwork.parse.EngineCpntParser;
import com.mobanker.engine.framkwork.parse.one.EngineOneAbsParser;
import com.mobanker.engine.framkwork.util.EngineUtil;

public class EngineStandardStageParser extends EngineOneAbsParser {

	private static final String BUSI_DESC = "EngineStandardStage解析器";
	private Logger logger = LoggerFactory.getLogger(this.getClass());						
	
	@Override
	public EngineCpnt parse(JSONObject json,EngineCpntParser engineCpntParser) {

		super.startToDoLog(json);
		
		JSONArray stepList = json.getJSONArray("stepList");
//		if(stepList == null)
//			throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"stepList元素不存在");
				
		EngineStandardStage result = new EngineStandardStage();
		super.fillBaseInfo(result, json);
		
//		String idRoot = json.getString("isRoot");
//		if(StringUtils.equals(idRoot, "true")){
//			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"stage is root:{}",json.getString("id"));
//			result.setRoot(true);
//		}		
		List<EngineStep> list = new LinkedList<EngineStep>();
		
		if(!CollectionUtils.isEmpty(stepList)){
			for(Object stepEle : stepList){
				String stepId = stepEle.toString();									
				logger.info(EngineUtil.logPrefix(BUSI_DESC)+"准备解析step:{}",stepId);
				EngineStep step = engineCpntParser.parse(stepId, EngineStep.class);
				if(step == null) throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"解析组件失败");
				list.add(step);
			}				
		}	
		result.setStepList(list);
		
		return result;
	}

}
