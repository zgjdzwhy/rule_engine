package com.mobanker.engine.exec.business.snapshot.chartanaly;

import java.util.LinkedList;
import java.util.List;

import com.mobanker.engine.exec.pojo.snapshot.EngineChart;
import com.mobanker.engine.framkwork.api.params.EngineParam;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;

public abstract class EngineOneFieldAnaly extends EngineAbsChartAnaly {

	@Override
	public EngineChart analyze(List<EngineTransferData> data, String fieldKey) {
		List<Object> oneFieldDataList = new LinkedList<Object>();
		for(EngineTransferData etd : data){
			EngineParam ep = etd.getOutputParam();
			Object value = ep.get(fieldKey);
			if(value == null) continue;
			oneFieldDataList.add(value);
		}
		if(oneFieldDataList.size() == 0){
			return new EngineChart(this.getClass().getSimpleName(),0,"没有该字段相关数据");
		}		
		return deal(oneFieldDataList);
	}

	
	protected abstract EngineChart deal(List<Object> data);
}
