package com.mobanker.engine.framkwork.parse.judge;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.judge.assist.EngineConditionCollection;
import com.mobanker.engine.framkwork.cpnt.judge.condition.EngineConditionBaseInfo;
import com.mobanker.engine.framkwork.cpnt.judge.condition.EngineConditionExt;
import com.mobanker.engine.framkwork.cpnt.judge.condition.impl.ConditionArrayByIn;
import com.mobanker.engine.framkwork.cpnt.judge.condition.impl.ConditionArrayEquals;
import com.mobanker.engine.framkwork.cpnt.judge.condition.impl.ConditionArrayIn;
import com.mobanker.engine.framkwork.cpnt.judge.condition.impl.ConditionArrayIntersection;
import com.mobanker.engine.framkwork.cpnt.judge.condition.impl.ConditionCharacterByLike;
import com.mobanker.engine.framkwork.cpnt.judge.condition.impl.ConditionCharacterEquals;
import com.mobanker.engine.framkwork.cpnt.judge.condition.impl.ConditionCharacterIn;
import com.mobanker.engine.framkwork.cpnt.judge.condition.impl.ConditionCharacterLike;
import com.mobanker.engine.framkwork.cpnt.judge.condition.impl.ConditionLengthRange;
import com.mobanker.engine.framkwork.cpnt.judge.condition.impl.ConditionNumberRange;
import com.mobanker.engine.framkwork.cpnt.judge.impl.EngineConditionJudge;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineCpntParseException;
import com.mobanker.engine.framkwork.parse.EngineCpntParser;
import com.mobanker.engine.framkwork.parse.judge.condition.ConditionArrayByInParser;
import com.mobanker.engine.framkwork.parse.judge.condition.ConditionArrayEqualsParser;
import com.mobanker.engine.framkwork.parse.judge.condition.ConditionArrayInParser;
import com.mobanker.engine.framkwork.parse.judge.condition.ConditionArrayIntersectionParser;
import com.mobanker.engine.framkwork.parse.judge.condition.ConditionCharacterByLikeParser;
import com.mobanker.engine.framkwork.parse.judge.condition.ConditionCharacterEqualsParser;
import com.mobanker.engine.framkwork.parse.judge.condition.ConditionCharacterInParser;
import com.mobanker.engine.framkwork.parse.judge.condition.ConditionCharacterLikeParser;
import com.mobanker.engine.framkwork.parse.judge.condition.ConditionLengthRangeParser;
import com.mobanker.engine.framkwork.parse.judge.condition.ConditionNumberRangeParser;
import com.mobanker.engine.framkwork.parse.judge.one.EngineConditionOneAbsParser;
import com.mobanker.engine.framkwork.parse.one.EngineOneAbsParser;
import com.mobanker.engine.framkwork.util.EngineUtil;

public class EngineConditionJudgeParser extends EngineOneAbsParser {

	private static final String BUSI_DESC = "EngineConditionJudgeParser解析器";
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	public final static Map<String,EngineConditionOneAbsParser> conditionMap = new HashMap<String,EngineConditionOneAbsParser>() {
	{    

		put(ConditionCharacterEquals.class.getSimpleName(), new ConditionCharacterEqualsParser());
		put(ConditionCharacterIn.class.getSimpleName(), new ConditionCharacterInParser());
		put(ConditionCharacterByLike.class.getSimpleName(), new ConditionCharacterByLikeParser());
		put(ConditionCharacterLike.class.getSimpleName(), new ConditionCharacterLikeParser());
		put(ConditionLengthRange.class.getSimpleName(), new ConditionLengthRangeParser());
		put(ConditionNumberRange.class.getSimpleName(), new ConditionNumberRangeParser());
		put(ConditionArrayByIn.class.getSimpleName(), new ConditionArrayByInParser());
		put(ConditionArrayEquals.class.getSimpleName(), new ConditionArrayEqualsParser());
		put(ConditionArrayIn.class.getSimpleName(), new ConditionArrayInParser());
		put(ConditionArrayIntersection.class.getSimpleName(), new ConditionArrayIntersectionParser());
		
	}};
	
	
	@Override
	public EngineCpnt parse(JSONObject json, EngineCpntParser engineCpntParser) {
		super.startToDoLog(json);		
		EngineConditionJudge result = new EngineConditionJudge();
		super.fillBaseInfo(result, json);
		
		JSONArray nodeTree = json.getJSONArray("nodeTree");
		if(CollectionUtils.isEmpty(nodeTree))
			throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"解析nodeTree组件失败，不存在nodeTree节点");
		
		Long num=1L;
		
		EngineConditionCollection tree = parseNode(nodeTree,engineCpntParser,num);
		
		result.setRoot(tree);
		
		return result;
	}

	private EngineConditionCollection parseNode(JSONArray nodeTree,EngineCpntParser engineCpntParser,Long num){		
		
		EngineConditionCollection tree = new EngineConditionCollection();
		Map<String, EngineConditionBaseInfo> collectionMap=new HashMap<String, EngineConditionBaseInfo>();
		if(!CollectionUtils.isEmpty(nodeTree)){
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"存在nodeTree节点，开始解析nodeTree组件");
			for(Object nodeEle : nodeTree){
				EngineAssert.checkArgument(nodeEle instanceof JSONObject, "nodeTree中存放的是不对象:%s",nodeTree.toJSONString());		
				JSONObject nodeJson = (JSONObject) nodeEle;
				JSONArray collectionJson = nodeJson.getJSONArray("collections");
				
				if(!CollectionUtils.isEmpty(collectionJson)){
					String linker= nodeJson.getString("linker");
					EngineConditionCollection collection=null;
					collection=parseNode(collectionJson,engineCpntParser,num);
					collection.setLinker(linker);
					collectionMap.put(num.toString(), collection);
					num++;
				}
				
				JSONObject conditionJson = nodeJson.getJSONObject("condition");
		
				if(conditionJson!=null){
					
					String cmpType =conditionJson.getString("cmpType");
					EngineAssert.checkNotBlank(cmpType,"condition中没有cmpType类型",conditionJson.toString());
					EngineConditionOneAbsParser parser= conditionMap.get(cmpType);
					EngineConditionExt condition= parser.parse(conditionJson, engineCpntParser);
					
					collectionMap.put(num.toString(), condition);
					num++;
				}
				
				
			}
		}
		tree.setCollections(collectionMap);
		
		return tree;
	}					
}
