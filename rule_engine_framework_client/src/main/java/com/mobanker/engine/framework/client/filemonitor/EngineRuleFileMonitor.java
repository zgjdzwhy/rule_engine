package com.mobanker.engine.framework.client.filemonitor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineRuleFileMonitor {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//监听目录 默认classpath下
	private String moniDir;
	
	public EngineRuleFileMonitor(){
		try {
			//查看目录下有没有qlrule.properties
			InputStream is=this.getClass().getResourceAsStream("/qlrule.properties");
			if(is != null){
				Properties properties = new Properties();
				properties.load(is);			
				moniDir = properties.getProperty("ruledir");				
			}else{
				logger.info("ruledir is not exist,so read default dir!");
				URL url = this.getClass().getResource("/");
				if(url != null){
					moniDir = url.getFile();
				}	
			}	
		} catch (IOException e) {
			logger.error("get monidir error",e);
		}
	}
	
	public void init(){
		File dir = getDir();
		File[] qrList = dir.listFiles(new EngineRuleFileFilterImpl());
		for(File qrFile : qrList){
			new EngineRuleFileListenerAdaptor().onFileCreate(qrFile);
		}		
	}
	
	private File getDir(){
		if(moniDir == null)
			throw new NullPointerException("monitor dir is not config");
		File dir = new File(moniDir);
		if(!dir.exists())
			throw new RuntimeException("monitor dir is not exist["+moniDir+"]");
		
		return dir;
	}
	
	public void start(){
		File dir = getDir();
		try {  
            //构造观察类主要提供要观察的文件或目录，当然还有详细信息的filter  
            FileAlterationObserver observer = new FileAlterationObserver(dir,new EngineRuleFileFilterImpl());  
            //构造收听类 没啥好说的  
            EngineRuleFileListenerAdaptor listener = new EngineRuleFileListenerAdaptor();  
            //为观察对象添加收听对象  
            observer.addListener(listener);  
            //配置Monitor，第一个参数单位是毫秒，是监听的间隔；第二个参数就是绑定我们之前的观察对象。  
            FileAlterationMonitor fileMonitor = new FileAlterationMonitor(5000,new FileAlterationObserver[]{observer});  
            //启动开始监听  
            fileMonitor.start();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}
	
}
