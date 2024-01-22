package com.mobanker.engine.exec.business.snapshot.chartanaly;

import java.util.List;

import com.mobanker.engine.exec.pojo.snapshot.EngineChart;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年1月24日
 * @version 1.0
 */
public interface EngineChartAnaly {	
	public EngineChart generate(List<EngineTransferData> data,String fieldKey);	
}
