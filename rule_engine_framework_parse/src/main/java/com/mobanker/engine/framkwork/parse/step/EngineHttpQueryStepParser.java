package com.mobanker.engine.framkwork.parse.step;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineHttpQueryStep;
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
public class EngineHttpQueryStepParser extends EngineOneAbsParser{

	private static final String BUSI_DESC = "数据源解析器";
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	@Override
	public EngineCpnt parse(JSONObject json, EngineCpntParser engineCpntParser) {
		super.startToDoLog(json);
		
		JSONArray queryListEle = json.getJSONArray("queryList");
//		if(CollectionUtils.isEmpty(queryListEle))
//			throw new EngineCpntParseException(EngineUtil.logPrefix(BUSI_DESC)+"queryList元素不存在");
		

		EngineHttpQueryStep result = new EngineHttpQueryStep();
		super.fillBaseInfo(result, json);
		if(!CollectionUtils.isEmpty(queryListEle)){
			for(Object queryEle : queryListEle){
				String queryId = queryEle.toString();
				result.configQuery(queryId);	
				logger.info(EngineUtil.logPrefix(BUSI_DESC)+"准备寻找query:{}",queryId);
			}				
		}
				
		return result;
	}

}
