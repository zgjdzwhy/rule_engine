package com.mobanker.engine.exec.taskget.netty;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.common.EngineConst;
import com.mobanker.framework.server.MobankerApplication;
import com.mobanker.framework.server.netty.annotation.NettyBootstrap;

@NettyBootstrap(springApplicationContext = "applicationContext.xml",springServletContext = "mobanker-servlet.xml")
public class EngineTaskServer {
	
	private static Logger logger = LoggerFactory.getLogger(EngineTaskServer.class);

	public static void main(String[] args) throws Exception {
		//NettyServerBootstrap.createServer().setSpringApplicationContext("classpath:/applicationContext.xml").start();
		logger.info(EngineConst.CURRENT_APP_DESC+EngineConst.CURRENT_APP_VERSION);
		MobankerApplication.run(EngineTaskServer.class, args);
	}

	
}
