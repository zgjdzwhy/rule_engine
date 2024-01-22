package com.mobanker.engine.framkwork.parse.judge.one;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framkwork.cpnt.judge.condition.EngineConditionExt;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineCpntSelfCheckException;
import com.mobanker.engine.framkwork.util.EngineUtil;

public abstract class EngineConditionOneAbsParser implements EngineConditionOneParser {
	
	private static final String BUSI_DESC = "条件类型解析";
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	/** 
	* @Fields regular : 格式为 [数字,数字)   []为大于等于、小于等于  ()大于小于  
	*/
	public static final String regular="^(\\[|\\()+((-?(([0-9]+\\.[0-9]*[1-9][0-9]*)"
			+ "|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*)))|0|\\*),((-?(([0-9]+\\.[0-9]*[1-9][0-9]*)"
			+ "|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*)))|0|\\*)(\\]|\\))$";

	public void startToDoLog(JSONObject json){
		logger.info(EngineUtil.logPrefix(BUSI_DESC)+"{}开始解析元素:{}",this.getClass().getSimpleName(),json.toJSONString());
	}
	
	public EngineConditionExt fillBaseInfo(EngineConditionExt baseInfo,JSONObject json){
		//条件类型暂不作为组件，所以没有id 名称
/*		String id = json.getString("id");
		String name = json.getString("name");
		String right = json.getString("right");
		String updatetime = json.getString("updatetime");
		EngineAssert.checkNotBlank(id, "组件没有属性id:"+json.toJSONString());

	
		baseInfo.setId(id);
		baseInfo.setName(name);
		baseInfo.setRight(right);
		baseInfo.setUpdatetime(updatetime);*/
		

		String linker = json.getString("linker");
		//EngineAssert.checkNotBlank(linker, "条件没有连接符:"+json.toJSONString());
		String field = json.getString("field");
		EngineAssert.checkNotBlank(field, "条件没有字段:"+json.toJSONString());
		String modeStr = json.getString("mode");
		EngineAssert.checkNotBlank(modeStr, "条件没有模式:"+json.toJSONString());

		boolean mode = true;
		mode = BooleanUtils.toBoolean(modeStr);
		
		baseInfo.setLinker(linker);
		baseInfo.setField(field);
		baseInfo.setMode(mode);
		
		//入参
		Set<String> inputField = new HashSet<String>();
		//出参
		Set<String> outputField = new HashSet<String>();
		String input="input.";
		String output="output.";
		if(field.indexOf(input) >= 0){
			input=StringUtils.replace(field,input,"");
			inputField.add(input);
		}else if(field.indexOf(output) >= 0){
			output=StringUtils.replace(field,output,"");
			outputField.add(output);
		}else
			throw new EngineCpntSelfCheckException("关联字段类型异常："+field);	
	
		baseInfo.setInputField(inputField);
		baseInfo.setOutputField(outputField);
		
		return baseInfo;
	}
	

}
