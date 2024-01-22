package com.mobanker.engine.exec.business.snapshot.chartanaly;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.mobanker.engine.exec.pojo.snapshot.EngineChart;


/**
 * 汇总分类数
 * {
 * 	field1 : 234, 
 * 	field2 : 237,
 * 	field3 : 238
 * }
 * 
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年1月24日
 * @version 1.0
 */
public abstract class EngineChartAnalyGroupSum extends EngineOneFieldAnaly{
 
	
	protected abstract EngineChart deal(Map<String,BigDecimal> total,int size);
	
	protected EngineChart deal(List<Object> data) {	
		Map<String,BigDecimal> total = sum(data);		
		return deal(total,data.size());		
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String,BigDecimal> sum(List<Object> data) {	
		Map<String,BigDecimal> total = new HashMap<String, BigDecimal>();
		for(Object one : data){
			Map<String,BigDecimal> oneReturn;
			//判断是不是jsonArray,
			if(one instanceof List){
				oneReturn = take((List<Object>)one);
			}else{
				oneReturn = take(one);
			}							
			total = aggregate(total,oneReturn);
		}			
		return total;
	}

	private Map<String,BigDecimal> take(List<Object> list){
		if(CollectionUtils.isEmpty(list)) return Collections.emptyMap();
		Map<String,BigDecimal> result = new HashMap<String,BigDecimal>();
		for(Object one : list){
			result = aggregate(result,take(one));			
		}
		return result;
	}
	
	private Map<String,BigDecimal> take(Object one){
		Map<String,BigDecimal> result = new HashMap<String,BigDecimal>();
		result.put(one.toString(), BigDecimal.ONE);
		return result;
	}
	
	private Map<String,BigDecimal> aggregate(Map<String,BigDecimal> total,Map<String,BigDecimal> one){
		for(String key : one.keySet()){
			BigDecimal value = one.get(key);			
			BigDecimal totalValue = total.get(key); 
			if(totalValue == null){
				total.put(key, value);
			}else{
				total.put(key, totalValue.add(value));
			}
		}
		return total;
	}
}
