package com.mobanker.engine.framkwork.parse.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpntBaseInfo;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.util.EngineUtil;

public abstract class EngineOneAbsParser implements EngineOneParser {
	
	private static final String BUSI_DESC = "组件解析";
	private Logger logger = LoggerFactory.getLogger(this.getClass());	

	public void startToDoLog(JSONObject json){
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"{}开始解析元素:{}",this.getClass().getSimpleName(),json.toJSONString());
	}
	
	public EngineCpntBaseInfo fillBaseInfo(EngineCpntBaseInfo baseInfo,JSONObject json){
		String id = json.getString("id");
		String name = json.getString("name");
		String right = json.getString("right");
		String updatetime = json.getString("updatetime");
		EngineAssert.checkNotBlank(id, "组件没有属性id:"+json.toJSONString());

	
		baseInfo.setId(id);
		baseInfo.setName(name);
		baseInfo.setRight(right);
		baseInfo.setUpdatetime(updatetime);
		return baseInfo;
	}
	

}
