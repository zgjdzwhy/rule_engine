/**
* <p>Title: EngineCndtExecuter</p>
* <p>Description: </p>
* <p>Company: </p> 
* @author zhujunjie
* @date 下午2:04:48
*/
package com.mobanker.engine.framkwork.cpnt.judge.condition;

import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpntBaseInfo;
import com.mobanker.engine.framkwork.cpnt.judge.EngineJudge;

public abstract class EngineConditionBaseInfo extends EngineCpntBaseInfo implements EngineJudge{
	/** 
	* @Fields serialVersionUID : TODO(序列化) 
	*/
	private static final long serialVersionUID = 1L;

	/**
	 * 连接符号
	 */
	private String linker;

	public EngineConditionBaseInfo(){}
	
	public EngineConditionBaseInfo(String linker){

		this.linker = linker;
	}

	public String getLinker() {
		return linker;
	}

	public void setLinker(String linker) {
		this.linker = linker;
	}


	
}
