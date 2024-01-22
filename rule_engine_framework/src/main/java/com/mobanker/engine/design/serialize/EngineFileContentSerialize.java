package com.mobanker.engine.design.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.mobanker.engine.design.hotdeploy.file.EngineFileContent;

public class EngineFileContentSerialize {
	private Kryo kryo = new Kryo();
	//private Registration registration = kryo.register(EngineFlow.class);

	public byte[] serialize(EngineFileContent data) {
		// TODO Auto-generated method stub
		kryo = new Kryo();
		kryo.setReferences(true);
		// kryo.setRegistrationRequired(true);
		// kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
		// Registration registration = kryo.register(EngineTransferData.class);
		// 序列化
		Output output = new Output(10240, 10485760);
		kryo.writeObject(output, data);
		byte[] bb = output.toBytes();
		output.flush();

		return bb;
	}

	public EngineFileContent unserialize(byte[] bytes) {
		Input input =  new Input(bytes);
		EngineFileContent data = kryo.readObject(input, EngineFileContent.class);
		return data;
	}
}
