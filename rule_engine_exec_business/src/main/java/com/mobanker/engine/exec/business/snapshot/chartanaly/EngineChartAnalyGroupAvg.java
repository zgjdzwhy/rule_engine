package com.mobanker.engine.exec.business.snapshot.chartanaly;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.exec.business.snapshot.chartanaly.EngineOneFieldAnaly;
import com.mobanker.engine.exec.pojo.snapshot.EngineChart;


/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年1月24日
 * @version 1.0
 */
public abstract class EngineChartAnalyGroupAvg extends EngineOneFieldAnaly{

	protected abstract EngineChart display(List<BigDecimal> total);
	
	protected EngineChart deal(List<Object> data) {	
		try{
			List<BigDecimal> total = sum(data);
			return display(total);		
		}catch(NumberFormatException e){
			return new EngineChart(this.getClass().getSimpleName(),0,"快照参数的值不是数值型，无法用这种图形来展示");			
		}
	}
	
	@SuppressWarnings("unchecked")
	protected List<BigDecimal> sum(List<Object> data) throws NumberFormatException{
		List<List<BigDecimal>> temp = new LinkedList<List<BigDecimal>>();

		for(Object one : data){
			List<BigDecimal> oneReturn;
			//判断是不是jsonArray,
			if(one instanceof List){
				oneReturn = take((List<Object>)one);
			}else{
				oneReturn = take(one);
			}							
			temp.add(oneReturn);			
		}		
		List<BigDecimal> total = aggregate(temp);
		return total;
	}

	private List<BigDecimal> take(List<Object> list){
		if(CollectionUtils.isEmpty(list)) return Collections.emptyList();
		List<BigDecimal> result = new LinkedList<BigDecimal>();
		for(Object one : list){
			BigDecimal temp = new BigDecimal(one.toString());
			result.add(temp);
		}
		return result;
	}
	
	private List<BigDecimal> take(Object one){
		List<BigDecimal> result = new LinkedList<BigDecimal>();
		BigDecimal temp = new BigDecimal(one.toString());
		result.add(temp);
		return result;
	}
	
	private List<BigDecimal> aggregate(List<List<BigDecimal>> data){
		List<BigDecimal> total = new LinkedList<BigDecimal>();
		
		Map<Integer,List<BigDecimal>> columFormat = new HashMap<Integer,List<BigDecimal>>();
		
		//先行转列
		for(List<BigDecimal> oneRow : data){
			for(int i=0;i<oneRow.size();i++){
				BigDecimal one = oneRow.get(i);				
				List<BigDecimal> colum = columFormat.get(i);
				if(colum != null){
					colum.add(one);
				}else{
					colum = new LinkedList<BigDecimal>();
					colum.add(one);
					columFormat.put(i, colum);
				}				
			}
		}
		
		for(Integer columKey : columFormat.keySet()){
			List<BigDecimal> columNumbers = columFormat.get(columKey);
			total.add(avg(columNumbers));
		}
		
		
		return total;
	}
	//算平均值
	private BigDecimal avg(List<BigDecimal> numbers){
		if(CollectionUtils.isEmpty(numbers)) return BigDecimal.ZERO;
		
		BigDecimal sum = BigDecimal.ZERO;
		for(BigDecimal number : numbers){
			sum = sum.add(number);
		}		
		return sum.divide(new BigDecimal(numbers.size()),2,RoundingMode.HALF_UP);
	}
	
}
