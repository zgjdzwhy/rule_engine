/**
* <p>Title: EngineConditionExt</p>
* <p>Description: </p>
* <p>Company: </p> 
* @author zhujunjie
* @date 下午8:16:23
*/
package com.mobanker.engine.framkwork.cpnt.judge.condition;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mobanker.engine.framkwork.api.params.EngineParam;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineSelfCheck;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.relation.EngineDependField;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;
import com.mobanker.engine.framkwork.util.EngineUtil;

public abstract class EngineConditionExt extends EngineConditionBaseInfo implements EngineRelaCpnt,EngineSelfCheck,EngineDependField{

	/** 
	* @Fields serialVersionUID : TODO(序列化) 
	*/
	private static final long serialVersionUID = 1L;
	/**
	 * 字段
	 */
	private String field;
	/**
	 * 比较类型
	 */
	private String cmpType;
	/**
	 * 模式
	 */
	private boolean mode;
	
	/**
	 * 相关入参字段
	 */
	protected Set<String> inputField = new HashSet<String>();
	/**
	 *  相关出参字段
	 */
	protected Set<String> outputField = new HashSet<String>();
	
	
	public EngineConditionExt(){}
	
	public EngineConditionExt(String field,String cmpType,String value){

		this.field = field;
		this.cmpType = cmpType;
	}
	
	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getCmpType() {
		return cmpType;
	}

	public void setCmpType(String cmpType) {
		this.cmpType = cmpType;
	}

	public boolean isMode() {
		return mode;
	}

	public void setMode(boolean mode) {
		this.mode = mode;
	}

	public Set<String> getInputField() {
		return inputField;
	}

	public void setInputField(Set<String> inputField) {
		this.inputField = inputField;
	}

	public Set<String> getOutputField() {
		return outputField;
	}

	public void setOutputField(Set<String> outputField) {
		this.outputField = outputField;
	}
	
	
	@Override
	public Set<String> inputRela() {
		return inputField;
	}

	@Override
	public Set<String> outputRela() {
		return outputField;
	}

	@Override
	public Map<String, Boolean> relaCpntIds() {
		return Collections.emptyMap();
	}
	
	@Override
	public Map<String, String> dependFields() {
		Map<String,String> result = new HashMap<String,String>();
		for(String key : inputField){
			result.put(key,"input");
		}
		for(String key : outputField){
			result.put(key,"output");
		}
		return result;
	}

	public Object getParamField(EngineTransferData etd){
		String field=null;
		EngineParam param = new EngineParam(100);
		if(inputField.size()>0){
			//目前只会有一个
			for(String key:inputField){	
				field=key;
			}
			param=etd.getInputParam();
		}else{
			//目前只会有一个
			for(String key:outputField){
				field=key;
			}
			param=etd.getOutputParam();
		}
		EngineAssert.checkNotBlank(field, EngineUtil.logPrefix(cmpType)+"组件字段缺失,字段名称:"+this.field);
		
		Object paramObj= param.get(field);
		EngineAssert.checkNotNull(paramObj, EngineUtil.logPrefix(etd, "获取变量")+"必要参数缺失,字段名:"+getField());
		
		return paramObj;
	}

}

