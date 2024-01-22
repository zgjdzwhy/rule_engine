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
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineScoreCardStep;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineCpntParseException;
import com.mobanker.engine.framkwork.parse.EngineCpntParser;
import com.mobanker.engine.framkwork.parse.one.EngineOneAbsParser;
import com.mobanker.engine.framkwork.util.EngineUtil;


/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月30日
 * @version 1.0
 */
@Deprecated
public class EngineScoreCardStepParser extends EngineOneAbsParser{

	private static final String BUSI_DESC = "评分卡解析器";
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	@Override
	public EngineCpnt parse(JSONObject json, EngineCpntParser engineCpntParser) {
		super.startToDoLog(json);
		
		JSONArray judgeList = json.getJSONArray("judgeList");
//		if(CollectionUtils.isEmpty(judgeList))
//			throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"judgeList元素不存在");
		
		String output = json.getString("output");
		//if(StringUtils.isBlank(output)) throw 
		//	new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"output是空");
		
		
		EngineScoreCardStep result = new EngineScoreCardStep();
		super.fillBaseInfo(result, json);		
		result.setOutput(output);
		
		if(!CollectionUtils.isEmpty(judgeList)){
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
						
				result.addJudge(judge, mode, score);
			}					
		}		
		
		return result;
	}

}
