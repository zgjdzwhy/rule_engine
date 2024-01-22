package com.mobanker.engine.framkwork.parse.step;

import java.math.BigDecimal;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.judge.EngineJudge;
import com.mobanker.engine.framkwork.cpnt.step.assist.EngineGroupBranch;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineScoreCardV2Step;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineCpntParseException;
import com.mobanker.engine.framkwork.parse.EngineCpntParser;
import com.mobanker.engine.framkwork.parse.one.EngineOneAbsParser;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author zhujunjie
 * @date 2016年12月30日
 * @version 1.0
 */
public class EngineScoreCardV2StepParser extends EngineOneAbsParser{

	private static final String BUSI_DESC = "评分卡V2解析器";
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	@Override
	public EngineCpnt parse(JSONObject json, EngineCpntParser engineCpntParser) {
		super.startToDoLog(json);
		
		JSONArray groupList = json.getJSONArray("groupList");
//		if(CollectionUtils.isEmpty(judgeList))
//			throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"judgeList元素不存在");
		
		String outputTotal = json.getString("outputTotal");
		String outputGroup = json.getString("outputGroup");
		//if(StringUtils.isBlank(output)) throw 
		//	new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"output是空");
		
		
		EngineScoreCardV2Step result = new EngineScoreCardV2Step();
		super.fillBaseInfo(result, json);		
		
		if(StringUtils.isNoneBlank(outputTotal)){
			result.setOutputTotal(outputTotal);
		}
		if(StringUtils.isNoneBlank(outputGroup)){
			result.setOutputGroup(outputGroup);
		}
		
		if(!CollectionUtils.isEmpty(groupList)){
			for(Object groupEle : groupList){
				EngineAssert.checkArgument(groupEle instanceof JSONObject, "groupList中存放的是不对象:%s",groupList.toJSONString());
				
				JSONObject groupJson = (JSONObject) groupEle;
				String groupName = groupJson.getString("name");
				JSONArray judgeList = groupJson.getJSONArray("judgeList");
				
				logger.info(EngineUtil.logPrefix(BUSI_DESC)+"准备解析group:{}",groupName);
				
				if(!CollectionUtils.isEmpty(judgeList)){
					EngineGroupBranch<BigDecimal> groupBranch=new EngineGroupBranch<BigDecimal>();
					groupBranch.setName(groupName);
					for(Object judgeEle : judgeList){
						EngineAssert.checkArgument(judgeEle instanceof JSONObject, "judgeList中存放的是不对象:%s",judgeList.toJSONString());
						
						JSONObject judgeJson = (JSONObject) judgeEle;
						String judgeId = judgeJson.getString("judge");
						String scoreStr = judgeJson.getString("score");			
						String modeStr = judgeJson.getString("mode");		
						
						boolean mode = true;
						if(StringUtils.isNoneBlank(modeStr))
							mode = BooleanUtils.toBoolean(modeStr);	
						
						
						BigDecimal score = new BigDecimal(scoreStr);
						logger.info(EngineUtil.logPrefix(BUSI_DESC)+"准备解析judge:{}",judgeId);
						EngineJudge judge = engineCpntParser.parse(judgeId, EngineJudge.class);
						if(judge == null) throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"解析judge组件失败");
								
						groupBranch.addJudge(judge, mode, score);
					}		
					result.addGroup(groupBranch);
				}
			}					
		}		
	
		return result;
	}

}
