package com.mobanker.engine.exec.business.tasktrace;

import java.util.Set;

import com.mobanker.engine.exec.pojo.EngineTaskQco;
import com.mobanker.engine.exec.pojo.snapshot.EngineSnapshotTotalCompareDto;
import com.mobanker.engine.framkwork.cpnt.container.EngineCpntContainer;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年1月23日
 * @version 1.0
 */
public interface EngineTaskBatchRun {
	
	/**
	 * 开始批处理
	 * @param ruleModel
	 * @param qco
	 */
	public void batchRun(EngineCpntContainer ruleModel,EngineTaskQco qco);
	
	/**
	 * 获取结果（字段）
	 * @param productType
	 * @return
	 */
	public Set<String> showOutputFields(String productType);
	/**
	 * 用户习惯
	 * @param productType
	 * @param fieldKey
	 * @return
	 */
	public String getCustomAnalyType(String productType,String fieldKey);
	/**
	 * 展示图形
	 * @param productType
	 * @param fieldKey
	 * @param analyType
	 * @return
	 */
	public EngineSnapshotTotalCompareDto displayFieldChartResult(String productType,String fieldKey,String analyType);	
	
	/**
	 * 清除上次结果
	 * @param productType
	 */
	public void clearLastBatchRunInfo(String productType);
}
