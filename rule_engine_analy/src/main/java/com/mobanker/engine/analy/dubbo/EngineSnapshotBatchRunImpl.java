package com.mobanker.engine.analy.dubbo;

import org.springframework.beans.factory.annotation.Autowired;

import com.mobanker.engine.exec.business.tasktrace.EngineTaskBatchRun;
import com.mobanker.engine.exec.pojo.EngineTaskQco;
import com.mobanker.engine.framkwork.cpnt.container.EngineCpntContainer;
import com.mobanker.engine.inner.call.api.EngineSnapshotBatchRun;
import com.mobanker.engine.inner.call.dto.SnapshotBatchRunDto;
import com.mobanker.framework.contract.dto.ResponseEntityDto;

public class EngineSnapshotBatchRunImpl implements EngineSnapshotBatchRun {

	@Autowired
	private EngineTaskBatchRun engineTaskBatchRun;
	
	@Override
	public ResponseEntityDto<String> batchRun(EngineCpntContainer ruleModel, SnapshotBatchRunDto dto) {		
		ResponseEntityDto<String> result = new ResponseEntityDto<String>();
		result.setStatus("1");
		result.setMsg("调用成功");		
		try{
			EngineTaskQco qco = new EngineTaskQco();
			qco.setProductType(dto.getProductType());
			qco.setBeginTime(dto.getBeginTime());
			qco.setEndTime(dto.getEndTime());
			engineTaskBatchRun.batchRun(ruleModel, qco);
		}catch(Exception e){
			result.setStatus("0");
			result.setMsg("快照测试失败，原因:"+e.getMessage());		
		}
		return result;
	}

}
