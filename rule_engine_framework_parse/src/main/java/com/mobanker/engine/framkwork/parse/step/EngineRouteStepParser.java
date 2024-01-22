package com.mobanker.engine.framkwork.parse.step;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.judge.EngineJudge;
import com.mobanker.engine.framkwork.cpnt.stage.EngineStage;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineRouteStep;
import com.mobanker.engine.framkwork.exception.EngineAssert;
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
public class EngineRouteStepParser extends EngineOneAbsParser{

	private static final String BUSI_DESC = "路由器解析器";
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	@Override
	public EngineCpnt parse(JSONObject json, EngineCpntParser engineCpntParser) {
		super.startToDoLog(json);
		
		JSONArray routeList = json.getJSONArray("routeList");
		if(CollectionUtils.isEmpty(routeList))
			throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"routeList元素不存在");
		
	
		EngineRouteStep result = new EngineRouteStep();
		super.fillBaseInfo(result, json);
		
		for(Object route : routeList){
			EngineAssert.checkArgument(route instanceof JSONObject, "routeList中存放的是不对象:%s",routeList.toJSONString());
			
			JSONObject routeJson = (JSONObject) route;
			String judgeId = routeJson.getString("judge");
			String modeStr = routeJson.getString("mode");
			String stageId = routeJson.getString("stage");
						
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"准备解析judge:{}",judgeId);
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"准备解析stage:{}",stageId);
			
			boolean mode = true;
			if(StringUtils.isNoneBlank(modeStr))
				mode = BooleanUtils.toBoolean(modeStr);
			
			EngineJudge judge = engineCpntParser.parse(judgeId, EngineJudge.class);
			EngineStage stage = engineCpntParser.parse(stageId, EngineStage.class);
		
			if(judge == null) throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"解析judge组件失败");
			if(stage == null) throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"解析stage组件失败");			

			result.addRoute(judge, mode, stage);
		}		
	
		
		//默认stage
		String defaultStage = json.getString("defaultStage");
		if(StringUtils.isNoneBlank(defaultStage)){			
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"准备解析defaultStage:{}",defaultStage);
			EngineStage stage = engineCpntParser.parse(defaultStage, EngineStage.class);
			if(stage == null) throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"解析defaultStage组件失败");	
			result.setDefaultStage(stage);
		}		
		
		return result;
	}

}
