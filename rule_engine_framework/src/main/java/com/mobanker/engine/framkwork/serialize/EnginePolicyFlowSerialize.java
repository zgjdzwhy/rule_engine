package com.mobanker.engine.framkwork.serialize;

import com.mobanker.engine.framkwork.cpnt.container.EngineCpntContainer;

/**
 * 序列化参数接口 被废弃了 改为将参数数据直接存储到mongo上
 * <p>Title: EngineTransferDataSerialize.java<／p>
 * <p>Description: <／p>
 * <p>Company: mobanker.com<／p>
 * @author taojinn
 * @date 2016年1月29日
 * @version 1.0
 */


public interface EnginePolicyFlowSerialize {
	public byte[] serialize(EngineCpntContainer flow);
	
	public EngineCpntContainer unserialize(byte[] bytes);
}
