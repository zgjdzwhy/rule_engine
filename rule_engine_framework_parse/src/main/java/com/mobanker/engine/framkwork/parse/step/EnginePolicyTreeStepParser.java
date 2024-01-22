package com.mobanker.engine.framkwork.parse.step;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.judge.EngineJudge;
import com.mobanker.engine.framkwork.cpnt.step.EngineStep;
import com.mobanker.engine.framkwork.cpnt.step.impl.EnginePolicyTreeStep;
import com.mobanker.engine.framkwork.cpnt.step.impl.EnginePolicyTreeStep.PolicyTreeNode;
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
public class EnginePolicyTreeStepParser extends EngineOneAbsParser{

	private static final String BUSI_DESC = "决策树解析器";
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	@Override
	public EngineCpnt parse(JSONObject json, EngineCpntParser engineCpntParser) {
		super.startToDoLog(json);		
		EnginePolicyTreeStep result = new EnginePolicyTreeStep();
		super.fillBaseInfo(result, json);
		
		JSONObject tree = json.getJSONObject("tree");
		if(tree == null)
			throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"解析tree组件失败，不存在tree节点");
		
		PolicyTreeNode rootTree = parseNode(tree,engineCpntParser);
		
		result.setRoot(rootTree);
		
		return result;
	}

	private PolicyTreeNode parseNode(JSONObject treeNode,EngineCpntParser engineCpntParser){		
		
		PolicyTreeNode node = new PolicyTreeNode();

		String step = treeNode.getString("step");
		JSONObject defaultNode = treeNode.getJSONObject("defaultNode");
		JSONArray route = treeNode.getJSONArray("route");
		
		if(StringUtils.isBlank(step)&&route==null)
			throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"解析node组件失败，该节点既不存在step属性也不存在route属性:"+treeNode);
		
		if(StringUtils.isNotBlank(step)){
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"存在step节点，开始解析step组件");
			EngineStep engineStep = engineCpntParser.parse(step, EngineStep.class);
			if(engineStep == null) throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"解析step组件失败:"+treeNode);	
			node.setStep(engineStep);			
		}
		
		if(defaultNode != null){
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"存在defaultNode节点，开始解析defaultNode组件");
			PolicyTreeNode parseNode = parseNode(defaultNode,engineCpntParser);
			node.setDefaultNode(parseNode);
		}else{
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"该节点defaultNode属性为空，返回上个部件的解析");
		}
		
		if(route != null){
			logger.info(EngineUtil.logPrefix(BUSI_DESC)+"存在route节点，开始解析route组件");
			for(Object routeEle : route){
				EngineAssert.checkArgument(routeEle instanceof JSONObject, "route中存放的是不对象:%s",route.toJSONString());				
				JSONObject routeJson = (JSONObject) routeEle;
				String judge = routeJson.getString("judge");
				String modeStr = routeJson.getString("mode");
				JSONObject routeNodeJson = routeJson.getJSONObject("node");
				boolean mode = true;
				if(StringUtils.isNoneBlank(modeStr))
					mode = BooleanUtils.toBoolean(modeStr);
				
				EngineJudge engineJudge = engineCpntParser.parse(judge, EngineJudge.class);
				if(engineJudge == null) throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"解析judge组件失败:"+routeJson);
				
				PolicyTreeNode parseNode = null;
				if(routeNodeJson != null){					
					parseNode = parseNode(routeNodeJson,engineCpntParser);					
				}else{//存在分支，但么有设置分支的内容，默认内容为空
					logger.warn(EngineUtil.logPrefix(BUSI_DESC)+"存在route节点，但是没有设置分支内容，默认内容为空");
				}				
				node.addBranch(engineJudge, mode, parseNode);
			}
		}
		
		
		return node;
	}
	
}
