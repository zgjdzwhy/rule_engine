package com.mobanker.engine.exec.taskget.netty;


import com.mobanker.framework.server.netty.NettyServerBootstrap;

public class EngineTaskServer2 {

	public static void main(String[] args) throws Exception {		
		NettyServerBootstrap.createServer().setSpringApplicationContext("classpath:/applicationContext.xml").parseCommondArguments(new String[]{"-port","8092"}).start();
	}

	
}
