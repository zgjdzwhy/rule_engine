package com.mobanker.engine.inner.call.api;

import com.mobanker.engine.framkwork.cpnt.container.EngineCpntContainer;
import com.mobanker.engine.inner.call.dto.SnapshotBatchRunDto;
import com.mobanker.framework.contract.dto.ResponseEntityDto;

public interface EngineSnapshotBatchRun {
	public ResponseEntityDto<String> batchRun(EngineCpntContainer ruleModel,SnapshotBatchRunDto dto);
}
