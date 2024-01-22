package com.mobanker.engine.framkwork.promanager;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.framkwork.cpnt.container.EngineCpntContainer;
import com.mobanker.engine.framkwork.serialize.EngineKryoSerialize;
import com.mobanker.engine.framkwork.serialize.EnginePolicyFlowSerialize;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月11日
 * @version 1.0
 */


public class EngineRuntimeProductManagerImpl implements EngineRuntimeProductManager{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());						
	
	
	private Map<String,EngineCpntContainer> productMap = new HashMap<String, EngineCpntContainer>();
	
	@Override
	public EngineCpntContainer getFlow(String product){
		return productMap.get(product);
	}

	@Override
	public void putFlow(String productType, EngineCpntContainer flow) {
		productMap.put(productType, flow);
	}
	
	@Override
	public void putFlow(String productType, byte[] bytes) {
		logger.info("start unserialize rule container,product:{},length:{}",productType,bytes.length);				
		EnginePolicyFlowSerialize serializer = new EngineKryoSerialize();
		EngineCpntContainer container = serializer.unserialize(bytes);
		logger.info("finish unserialize rule container,product:{},root:{}",productType,container.getRoot());
		//replaceQueryUrl(container,productType);		
		this.putFlow(productType, container);
	}
	
	
}
