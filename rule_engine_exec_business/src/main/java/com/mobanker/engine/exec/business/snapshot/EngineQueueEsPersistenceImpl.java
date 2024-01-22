package com.mobanker.engine.exec.business.snapshot;

import org.springframework.beans.factory.annotation.Autowired;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.data.EngineRuntimePersistence;
import com.mobanker.engine.framkwork.data.impl.EngineEsPersistenceImpl;
import com.mobanker.engine.framkwork.exception.EngineAssert;


/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月12日
 * @version 1.0
 */
@Deprecated
public class EngineQueueEsPersistenceImpl extends EngineEsPersistenceImpl implements EngineRuntimePersistence {
				
	@Autowired
	private EngineParamSnapshotService engineParamSnapshotService;	
		
	protected void logStepParam(String table,EngineTransferData etd){
		String productType = etd.getProductType();
		EngineAssert.checkNotBlank(productType);
		engineParamSnapshotService.snapshot(table, etd);							
	}
}
