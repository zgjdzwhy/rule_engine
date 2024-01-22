package com.mobanker.engine.framkwork.cpnt.step.assist;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.judge.EngineJudge;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;
import com.mobanker.engine.framkwork.relation.EngineRelaField;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 分支工具
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月30日
 * @version 1.0
 * @param <T>
 */
public class EngineBranch<T> implements EngineRelaCpnt,EngineRelaField,Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 判断
	 */
	private EngineJudge judge;
	/**
	 * 模式
	 */
	private boolean mode;
	
	/**
	 * 内容
	 */
	private T content;
	
	public String toString(){
		return judge.name()+"->模式:"+mode+"->"+content;
	}
	
	public EngineBranch(){}
	
	public EngineBranch(EngineJudge judge,boolean mode,T content){
		this.judge = judge;
		this.mode = mode;
		this.content = content;
	}
	
	public boolean judge(EngineTransferData etd){
		return judge.judge(etd) == mode;
	}

	public EngineJudge getJudge() {
		return judge;
	}

	public void setJudge(EngineJudge judge) {
		this.judge = judge;
	}

	public boolean isMode() {
		return mode;
	}

	public void setMode(boolean mode) {
		this.mode = mode;
	}

	public T getContent() {
		return content;
	}

	public void setContent(T content) {
		this.content = content;
	}

	@Override
	public Map<String,Boolean> relaCpntIds() {
		Map<String,Boolean> result = new HashMap<String,Boolean>();
		
		EngineJudge judge = this.getJudge();
		T content = this.getContent();
		
		if(judge != null && judge instanceof EngineRelaCpnt){//暂时judge没有符合条件
			EngineRelaCpnt rela = (EngineRelaCpnt) judge;			
			result.putAll(EngineUtil.relaNonlineal(rela));				
			result.put(judge.id(),true);
		}
		if(content != null && content instanceof EngineCpnt){
			EngineCpnt cpnt = (EngineCpnt) content;
			if(cpnt instanceof EngineRelaCpnt){
				EngineRelaCpnt rela = (EngineRelaCpnt) cpnt;
				result.putAll(EngineUtil.relaNonlineal(rela));				
			}		
			result.put(cpnt.id(),true);
		} 						
		return result;
	}


	public Map<String,Boolean> relaTreeCpntIds() {
		Map<String,Boolean> result = new HashMap<String,Boolean>();
		
		EngineJudge judge = this.getJudge();
		T content = this.getContent();
		
		if(judge != null && judge instanceof EngineRelaCpnt){//暂时judge没有符合条件
			EngineRelaCpnt rela = (EngineRelaCpnt) judge;			
			result.putAll(EngineUtil.relaNonlineal(rela));				
			result.put(judge.id(),true);
		}
		if(content instanceof EngineRelaCpnt){
			EngineRelaCpnt rela = (EngineRelaCpnt) content;
			result.putAll(rela.relaCpntIds());				
		}						
		return result;
	}	
	
	@Override
	public Set<String> inputRela() {
		Set<String> result = new HashSet<String>();
		T content = this.getContent();
		
		if(judge != null){//暂时judge没有符合条件
			result.addAll(judge.inputRela());
		}
		if(content != null && content instanceof EngineRelaField){
			EngineRelaField rela = (EngineRelaField) content;
			result.addAll(rela.inputRela());
		} 					
		return result;
	}

	@Override
	public Set<String> outputRela() {
		Set<String> result = new HashSet<String>();
		T content = this.getContent();
		
		if(judge != null){//暂时judge没有符合条件
			result.addAll(judge.outputRela());
		}
		if(content != null && content instanceof EngineRelaField){
			EngineRelaField rela = (EngineRelaField) content;
			result.addAll(rela.outputRela());
		} 					
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof EngineBranch)) return false;
		EngineBranch<?> another = (EngineBranch<?>) obj;			
		
		if(!this.judge.id().equals(another.judge.id())) return false;			
		if(this.mode != another.mode) return false;
		
		if((this.content == null)&&(another.content == null)) return true;
		if((this.content == null)&&(another.content != null)) return false;
		if((this.content != null)&&(another.content == null)) return false;
		
		if(this.content.getClass() != another.content.getClass()) return false; 
		//如果是组件,id相等则表明相等，否则需要2个对象的equal相等才算相等
		if(this.content instanceof EngineCpnt){
			EngineCpnt thisCpnt = (EngineCpnt) this.content;
			EngineCpnt anotherCpnt = (EngineCpnt) another.content;			
			if(!thisCpnt.id().equals(anotherCpnt.id())) return false;
		}else{
			if(!this.content.equals(another.content)) return false;			
		}
				
		return true;
	}
	
	
}
