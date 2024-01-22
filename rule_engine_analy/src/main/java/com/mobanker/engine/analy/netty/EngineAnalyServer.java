package com.mobanker.engine.analy.netty;


import com.mobanker.engine.common.EngineConst;
import com.mobanker.engine.framkwork.exception.monitor.EngineStaticMonitorEnvironment;
import com.mobanker.engine.framkwork.exception.monitor.ee.EngineEETransactionFactory;
import com.mobanker.framework.server.MobankerApplication;
import com.mobanker.framework.server.netty.annotation.NettyBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Env(profile = "classpath:/springs/mobanker-engine-local-properties.xml")
@NettyBootstrap(springApplicationContext = "applicationContext.xml",springServletContext = "mobanker-servlet.xml")
public class EngineAnalyServer {
 
	private static Logger logger = LoggerFactory.getLogger(EngineAnalyServer.class);
	
	public static void main(String[] args) throws Exception {		
		logger.info(EngineConst.CURRENT_APP_DESC+EngineConst.CURRENT_APP_VERSION);
		EngineStaticMonitorEnvironment.init(new EngineEETransactionFactory());
		MobankerApplication.run(EngineAnalyServer.class, args);
	}

	
}
