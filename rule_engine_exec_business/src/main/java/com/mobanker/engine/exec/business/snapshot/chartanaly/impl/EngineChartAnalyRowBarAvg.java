package com.mobanker.engine.exec.business.snapshot.chartanaly.impl;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.exec.business.snapshot.chartanaly.EngineChartAnalyGroupAvg;
import com.mobanker.engine.exec.pojo.snapshot.EngineChart;


/**
 * 平均值条形图
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年1月24日
 * @version 1.0
 */
public class EngineChartAnalyRowBarAvg extends EngineChartAnalyGroupAvg{	
	@Override
	protected EngineChart display(List<BigDecimal> total) {
		EngineChart result = new EngineChart(this.getClass().getSimpleName(),1,"分析成功");	
		JSONObject content = new JSONObject();
		content.put("avg", total);
		result.setContent(content);
		return result;		
	}

	
}
