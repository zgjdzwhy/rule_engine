package com.mobanker.engine.framework.client.filemonitor;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.design.hotdeploy.file.EngineFileContent;
import com.mobanker.engine.design.serialize.EngineFileContentSerialize;
import com.mobanker.engine.framework.client.api.EngineClientEnviroment;



public class EngineRuleFileListenerAdaptor extends FileAlterationListenerAdaptor{

	private Logger logger = LoggerFactory.getLogger(this.getClass());						
	
	@Override  
    public void onStart(FileAlterationObserver arg0) {  
        //System.out.println("begin listening!");  
        super.onStart(arg0);  
    }  
      
    @Override  
    public void onStop(FileAlterationObserver arg0) {  
        //System.out.println("end listening!");  
        super.onStop(arg0);  
    }  
      
    @Override  
    public void onDirectoryCreate(File fold) {  
    	logger.info("fold: "+fold.getAbsolutePath()+" is created.");  
        super.onDirectoryCreate(fold);  
    }  
      
    @Override  
    public void onDirectoryChange(File fold) {  
    	logger.info("fold: "+fold.getAbsolutePath()+" is changed.");  
        super.onDirectoryChange(fold);  
    }  
      
    @Override  
    public void onDirectoryDelete(File fold) {  
    	logger.info("fold: "+fold.getAbsolutePath()+" is deleted.");  
        super.onDirectoryDelete(fold);  
    }  
      
    @Override  
    public void onFileCreate(File file) {  
    	logger.info("qr : "+file.getAbsolutePath()+" is created.");          
        try {
			byte[] bytes = FileUtils.readFileToByteArray(file);
			String fileName = file.getName();			
			logger.info("unserialize file:{},length:{}",fileName,bytes.length);      
			EngineFileContentSerialize fileSerializer = new EngineFileContentSerialize();
			EngineFileContent fileContent = fileSerializer.unserialize(bytes);
			String product = fileName.substring(0,fileName.lastIndexOf("."));			
			logger.info("unserialize file success!");
			EngineClientEnviroment.getProductManager().putFlow(product, fileContent.getBytes());
		} catch (IOException e) {
			logger.error("qr update error!",e);
		}        
        super.onFileCreate(file);  
    }  
      
    @Override  
    public void onFileChange(File file) {  
    	logger.info("qr: "+file.getAbsolutePath()+" is changed.");  
        try {
			byte[] bytes = FileUtils.readFileToByteArray(file);
			String fileName = file.getName();
			EngineClientEnviroment.getProductManager().putFlow(fileName, bytes);
		} catch (IOException e) {
			logger.error("qr update error!",e);
		}   
        super.onFileChange(file);  
    }  
      
    @Override  
    public void onFileDelete(File file) {  
    	logger.warn("file: "+file.getAbsolutePath()+" is deleted");         
        super.onFileDelete(file);  
    }  
	
}
