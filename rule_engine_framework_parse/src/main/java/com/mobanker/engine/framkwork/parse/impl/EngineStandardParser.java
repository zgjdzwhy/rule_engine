package com.mobanker.engine.framkwork.parse.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.flow.EnginePolicyFlow;
import com.mobanker.engine.framkwork.cpnt.judge.impl.EngineConditionJudge;
import com.mobanker.engine.framkwork.cpnt.judge.impl.EngineScriptJudge;
import com.mobanker.engine.framkwork.cpnt.stage.EngineStandardStage;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineBridgeStep;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineHttpQueryStep;
import com.mobanker.engine.framkwork.cpnt.step.impl.EnginePolicyTreeStep;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineRouteStep;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineRuleCollectionStep;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineScoreCardStep;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineScoreCardV2Step;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineScriptStep;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineCpntParseException;
import com.mobanker.engine.framkwork.parse.EngineCpntParser;
import com.mobanker.engine.framkwork.parse.factory.EngineCpntRegisterFactory;
import com.mobanker.engine.framkwork.parse.flow.EnginePolicyFlowParser;
import com.mobanker.engine.framkwork.parse.judge.EngineConditionJudgeParser;
import com.mobanker.engine.framkwork.parse.judge.EngineScriptJudgeParser;
import com.mobanker.engine.framkwork.parse.one.EngineOneAbsParser;
import com.mobanker.engine.framkwork.parse.stage.EngineStandardStageParser;
import com.mobanker.engine.framkwork.parse.step.EngineBridgeStepParser;
import com.mobanker.engine.framkwork.parse.step.EngineHttpQueryStepParser;
import com.mobanker.engine.framkwork.parse.step.EnginePolicyTreeStepParser;
import com.mobanker.engine.framkwork.parse.step.EngineRouteStepParser;
import com.mobanker.engine.framkwork.parse.step.EngineRuleCollectionStepParser;
import com.mobanker.engine.framkwork.parse.step.EngineScoreCardStepParser;
import com.mobanker.engine.framkwork.parse.step.EngineScoreCardV2StepParser;
import com.mobanker.engine.framkwork.parse.step.EngineScriptStepParser;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月10日
 * @version 1.0
 */
public class EngineStandardParser implements EngineCpntParser{

	private static final String BUSI_DESC = "组件解析";
	private Logger logger = LoggerFactory.getLogger(this.getClass());			
	
	private EngineCpntRegisterFactory registerFactory = new EngineCpntRegisterFactory();
	private Map<String,JSONObject> jsonAll = new HashMap<String,JSONObject>();
	private Map<String,EngineOneAbsParser> parserSelector = new HashMap<String, EngineOneAbsParser>();
	
	public EngineStandardParser(List<JSONObject> jsonList){		
		for(JSONObject one : jsonList){
			String id = one.getString("id");
			EngineAssert.checkNotBlank(id, EngineUtil.logPrefix(BUSI_DESC)+"对象规则模型中有存在缺少id属性的组件,json:%s",one);
			jsonAll.put(id, one);
		}
		
		parserSelector.put(EngineStandardStage.class.getSimpleName(), new EngineStandardStageParser());
		parserSelector.put(EngineScriptStep.class.getSimpleName(), new EngineScriptStepParser());
		parserSelector.put(EngineRuleCollectionStep.class.getSimpleName(), new EngineRuleCollectionStepParser());
		parserSelector.put(EngineScoreCardStep.class.getSimpleName(), new EngineScoreCardStepParser());
		parserSelector.put(EngineBridgeStep.class.getSimpleName(), new EngineBridgeStepParser());
		parserSelector.put(EngineRouteStep.class.getSimpleName(), new EngineRouteStepParser());
		parserSelector.put(EngineScriptJudge.class.getSimpleName(), new EngineScriptJudgeParser());
		parserSelector.put(EngineHttpQueryStep.class.getSimpleName(), new EngineHttpQueryStepParser());
		parserSelector.put(EnginePolicyTreeStep.class.getSimpleName(), new EnginePolicyTreeStepParser());
		parserSelector.put(EnginePolicyFlow.class.getSimpleName(), new EnginePolicyFlowParser());		
		parserSelector.put(EngineScoreCardV2Step.class.getSimpleName(), new EngineScoreCardV2StepParser());
		parserSelector.put(EngineConditionJudge.class.getSimpleName(), new EngineConditionJudgeParser());
	}
	
	
	@Override
	public void addExternalCpnt(Collection<EngineCpnt> cpntList) {
		for(EngineCpnt cpnt : cpntList){
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"添加外部组件:{}",cpnt.id());
			registerFactory.register(cpnt);
		}
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends EngineCpnt> T parse(String cpntId, Class<T> clazz){
		
		if(StringUtils.isBlank(cpntId))
			throw new EngineCpntParseException("入参ID为空");		
		
		T t = registerFactory.getExistCpnt(cpntId, clazz);
		if(t != null) return t;
		
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"开始从json中查找组件id:{}",cpntId);
		
		JSONObject oneJson = jsonAll.get(cpntId);
		EngineAssert.checkNotNull(oneJson, "模型中不存在该id的组件,id:%s",cpntId);
		String cpntType = oneJson.getString("type");
		String cpntName = oneJson.getString("name");
		cpntType = cpntType.trim();
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"组件名称:{},类型为:{}",cpntName,cpntType);
		EngineOneAbsParser oneParser = parserSelector.get(cpntType);
		
		if(oneParser == null) throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"没有这种类型的解析器:"+cpntType);

		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"发现解析器:{}",oneParser.getClass().getSimpleName());
		try{
			EngineCpnt object = oneParser.parse(oneJson,this);
			if(object == null) throw new EngineCpntParseException("没有解析出对象，请检查配置 cpntId:"+cpntId+",clazz:"+clazz.getSimpleName());
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"解析结果类型:{},转换类型:{}",object.getClass().getSimpleName(),clazz.getSimpleName());			
			//注册
			registerFactory.register(object);				
			return (T) object;
		}catch(StackOverflowError e){
			throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"解析组件失败,组件中存在无限死循环，请查看相关组件,cpntName:"+cpntName+",cpntType:"+cpntType,e);
		}

	}

	@Override
	public Map<String, EngineCpnt> getParsedCpnt() { 
		return registerFactory.getRegisterCpnt();
	}




	
}
