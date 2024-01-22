package com.mobanker.engine.exec.business.snapshot.chartanaly.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.exec.business.snapshot.chartanaly.EngineChartAnalyGroupSum;
import com.mobanker.engine.exec.pojo.snapshot.EngineChart;


/**
 * 汇总比例条形图,返回形式
 * {
 * 	field1 : 0.95,
 * 	field2 : 0.94,
 * 	field3 : 0.91
 * }
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年1月24日
 * @version 1.0
 */
public class EngineChartAnalyRowBarRatiosum extends EngineChartAnalyGroupSum{
 
	@Override
	protected EngineChart deal(Map<String, BigDecimal> total,int size) {
		EngineChart result = new EngineChart(this.getClass().getSimpleName(),1,"分析成功");
		Map<String, BigDecimal> temp = new HashMap<String, BigDecimal>();
		
		for(String key : total.keySet()){
			BigDecimal value = total.get(key);
			BigDecimal ratio = value.divide(new BigDecimal(size), 4, RoundingMode.FLOOR);
			temp.put(key, ratio);
		}
		JSONObject content = new JSONObject();
		for(String key : temp.keySet()){
			content.put(key, temp.get(key));			
		}
		result.setContent(content);
		return result;
	}	

}
