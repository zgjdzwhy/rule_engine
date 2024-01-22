package com.mobanker.engine.exec.business.snapshot.chartanaly.impl;

import java.math.BigDecimal;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.exec.business.snapshot.chartanaly.EngineChartAnalyGroupSum;
import com.mobanker.engine.exec.pojo.snapshot.EngineChart;


/**
 * 汇总数量条形图,返回形式
 * {
 * 	field1 : 235,
 * 	field2 : 235,
 * 	field3 : 323
 * }
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年1月24日
 * @version 1.0
 */
public class EngineChartAnalyRowBarNumsum extends EngineChartAnalyGroupSum{
 
	@Override
	protected EngineChart deal(Map<String, BigDecimal> total,int size) {
		EngineChart result = new EngineChart(this.getClass().getSimpleName(),1,"分析成功");
		JSONObject content = new JSONObject();
		for(String key : total.keySet()){
			content.put(key, total.get(key));			
		}
		result.setContent(content);
		return result;
	}	

}
