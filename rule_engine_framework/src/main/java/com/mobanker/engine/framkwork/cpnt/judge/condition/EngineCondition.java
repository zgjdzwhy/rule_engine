package com.mobanker.engine.framkwork.cpnt.judge.condition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mobanker.engine.framkwork.api.params.EngineParam;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.judge.EngineJudge;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;
import com.mobanker.engine.framkwork.relation.EngineRelaField;
import com.mobanker.engine.framkwork.util.EngineUtil;


/**
* <p>Title: EngineCondition</p>
* <p>Description: </p>
* <p>Company: mobanker</p> 
* @author zhujj
* @date 2017年1月5日
* @version 1.0 
*/
public class EngineCondition implements EngineRelaCpnt,EngineRelaField{

	/**
	 * 字段
	 */
	private String field;
	/**
	 * 比较类型
	 */
	private String cmpType;
	
	/**
	 * 数值
	 */
	private String value;
	
	/**
	 * 连接符号
	 */
	private String linker;
	
	public String toString(){
		return field+"->比较:"+cmpType+"->"+value;
	}
	
	public EngineCondition(){}
	
	public EngineCondition(String field,String cmpType,String value,String linker){
		this.field = field;
		this.cmpType = cmpType;
		this.value = value;
		this.linker = linker;
	}
	
	public boolean judge(EngineTransferData etd,boolean preReslt){
		return true;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getLinker() {
		return linker;
	}

	public void setLinker(String linker) {
		this.linker = linker;
	}

	@Override
	public Map<String,Boolean> relaCpntIds() {
		Map<String,Boolean> result = new HashMap<String,Boolean>();
		
//		EngineJudge judge = this.getJudge();
//		T content = this.getContent();
//		
//		if(judge != null && judge instanceof EngineRelaCpnt){//暂时judge没有符合条件
//			EngineRelaCpnt rela = (EngineRelaCpnt) judge;			
//			result.putAll(EngineUtil.relaNonlineal(rela));				
//			result.put(judge.id(),true);
//		}
//		if(content != null && content instanceof EngineCpnt){`
//			EngineCpnt cpnt = (EngineCpnt) content;
//			if(cpnt instanceof EngineRelaCpnt){
//				EngineRelaCpnt rela = (EngineRelaCpnt) cpnt;
//				result.putAll(EngineUtil.relaNonlineal(rela));				
//			}		
//			result.put(cpnt.id(),true);
//		} 						
		return result;
	}


	public Map<String,Boolean> relaTreeCpntIds() {
		Map<String,Boolean> result = new HashMap<String,Boolean>();
		
//		EngineJudge judge = this.getJudge();
//		T content = this.getContent();
//		
//		if(judge != null && judge instanceof EngineRelaCpnt){//暂时judge没有符合条件
//			EngineRelaCpnt rela = (EngineRelaCpnt) judge;			
//			result.putAll(EngineUtil.relaNonlineal(rela));				
//			result.put(judge.id(),true);
//		}
//		if(content instanceof EngineRelaCpnt){
//			EngineRelaCpnt rela = (EngineRelaCpnt) content;
//			result.putAll(rela.relaCpntIds());				
//		}						
		return result;
	}	
	
	@Override
	public Set<String> inputRela() {
		Set<String> result = new HashSet<String>();
//		T content = this.getContent();
//		
//		if(judge != null){//暂时judge没有符合条件
//			result.addAll(judge.inputRela());
//		}
//		if(content != null && content instanceof EngineRelaField){
//			EngineRelaField rela = (EngineRelaField) content;
//			result.addAll(rela.inputRela());
//		} 					
		return result;
	}

	@Override
	public Set<String> outputRela() {
		Set<String> result = new HashSet<String>();
//		T content = this.getContent();
//		
//		if(judge != null){//暂时judge没有符合条件
//			result.addAll(judge.outputRela());
//		}
//		if(content != null && content instanceof EngineRelaField){
//			EngineRelaField rela = (EngineRelaField) content;
//			result.addAll(rela.outputRela());
//		} 					
		return result;
	}

	@Override
	public boolean equals(Object obj) {
//		if(!(obj instanceof EngineCondition)) return false;
//		EngineCondition<?> another = (EngineCondition<?>) obj;			
//		
//		if(!this.judge.id().equals(another.judge.id())) return false;			
//		if(this.mode != another.mode) return false;
//		
//		if((this.content == null)&&(another.content == null)) return true;
//		if((this.content == null)&&(another.content != null)) return false;
//		if((this.content != null)&&(another.content == null)) return false;
//		
//		if(this.content.getClass() != another.content.getClass()) return false; 
//		//如果是组件,id相等则表明相等，否则需要2个对象的equal相等才算相等
//		if(this.content instanceof EngineCpnt){
//			EngineCpnt thisCpnt = (EngineCpnt) this.content;
//			EngineCpnt anotherCpnt = (EngineCpnt) another.content;			
//			if(!thisCpnt.id().equals(anotherCpnt.id())) return false;
//		}else{
//			if(!this.content.equals(another.content)) return false;			
//		}
				
		return true;
	}
	
	
}
