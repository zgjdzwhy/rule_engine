package com.mobanker.engine.framework.client.filemonitor;

import java.io.File;
import java.io.FileFilter;

/**
 * 
 * @author taojinn
 *
 */
public class EngineRuleFileFilterImpl implements FileFilter {

	//文件以.qr结尾
	@Override
	public boolean accept(File pathname) {		
		
		return pathname.getName().endsWith(".qr");
	}

}
