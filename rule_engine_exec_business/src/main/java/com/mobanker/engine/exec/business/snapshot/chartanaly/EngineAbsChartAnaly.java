package com.mobanker.engine.exec.business.snapshot.chartanaly;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.mobanker.engine.exec.pojo.snapshot.EngineChart;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年1月24日
 * @version 1.0
 */
public abstract class EngineAbsChartAnaly implements EngineChartAnaly{
		
	public EngineChart generate(List<EngineTransferData> data,String fieldKey){
		if(CollectionUtils.isEmpty(data)){
			return new EngineChart(this.getClass().getSimpleName(),0,"数据不存在");
		}			
		return this.analyze(data,fieldKey);
	}
	
	public abstract EngineChart analyze(List<EngineTransferData> data,String fieldKey);
}
