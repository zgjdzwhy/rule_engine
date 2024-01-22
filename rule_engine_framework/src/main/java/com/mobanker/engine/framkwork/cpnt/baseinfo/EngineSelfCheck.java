package com.mobanker.engine.framkwork.cpnt.baseinfo;

import com.mobanker.engine.framkwork.exception.EngineCpntSelfCheckException;


/**
 * 组件自身检查
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月1日
 * @version 1.0
 */

public interface EngineSelfCheck{			
	public void checkSelf() throws EngineCpntSelfCheckException;	
}
