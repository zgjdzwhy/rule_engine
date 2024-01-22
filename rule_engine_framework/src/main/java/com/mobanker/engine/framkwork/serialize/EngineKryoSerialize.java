package com.mobanker.engine.framkwork.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.mobanker.engine.framkwork.cpnt.container.EngineCpntContainer;

/**
 * 通过kryo序列化参数
 * <p>
 * Title: EngineKryoSerialize.java<／p>
 * <p>
 * Description: <／p>
 * <p>
 * Company: mobanker.com<／p>
 * 
 * @author taojinn
 * @date 2016年1月29日
 * @version 1.0
 */

public class EngineKryoSerialize implements EnginePolicyFlowSerialize {

	private Kryo kryo = new Kryo();
	//private Registration registration = kryo.register(EngineFlow.class);

	@Override
	public byte[] serialize(EngineCpntContainer flow) {
		// TODO Auto-generated method stub
		kryo = new Kryo();
		kryo.setReferences(true);
		// kryo.setRegistrationRequired(true);
		// kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
		// Registration registration = kryo.register(EngineTransferData.class);
		// 序列化
		Output output = new Output(10240, 10485760);
		kryo.writeObject(output, flow);
		byte[] bb = output.toBytes();
		output.flush();

		return bb;
	}

	@Override
	public EngineCpntContainer unserialize(byte[] bytes) {
		Input input =  new Input(bytes);
		EngineCpntContainer flow = kryo.readObject(input, EngineCpntContainer.class);
		return flow;
	}

}
