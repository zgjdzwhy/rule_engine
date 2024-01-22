package com.mobanker.engine.framkwork.cpnt.judge.assist;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.judge.EngineJudge;
import com.mobanker.engine.framkwork.cpnt.judge.condition.EngineConditionBaseInfo;
import com.mobanker.engine.framkwork.exception.EngineConditionException;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;
import com.mobanker.engine.framkwork.relation.EngineRelaField;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 条件集合
 * <p>Company: mobanker.com</p>
 * @author zhujj
 * @date 2017年1月08日
 * @version 1.0
 * @param <T>
 */
public class EngineConditionCollection extends EngineConditionBaseInfo  implements EngineJudge,EngineRelaCpnt,EngineRelaField{

	/** 
	* @Fields serialVersionUID : TODO(序列化) 
	*/
	private static final long serialVersionUID = 1L;
	private static final String BUSI_DESC = "条件规则集合";	

	/**
	 * 条件
	 */
	private Map<String,EngineConditionBaseInfo> collections;
	/**
	 * 组名称
	 */
	private String name;
	
	
	public String toString(){
		//return judge.name()+"->模式:"+mode+"->"+content;
		return null;
	}
	
	public EngineConditionCollection(){}
	
	public EngineConditionCollection(Map<String,EngineConditionBaseInfo> collections){
		this.collections = collections;
	}
	
	public Map<String, EngineConditionBaseInfo> getCollections() {
		return collections;
	}

	public void setCollections(Map<String, EngineConditionBaseInfo> collections) {
		this.collections = collections;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean judge(EngineTransferData etd){
		
		boolean result=true;
		if(!MapUtils.isEmpty(collections)){
			//先主键排序一遍，顺序执行
			Map<String,EngineConditionBaseInfo> map= EngineUtil.sortMapByKey(collections);
			int i=1;
			for(String key:map.keySet()){
				EngineConditionBaseInfo conditionBase=map.get(key);
				//集合中第一个条件，没有连接符
				if(i==1){	
					result=conditionBase.judge(etd);
					i=2;
				}else{
					boolean retRslt=conditionBase.judge(etd);
					//并且时两者必须都为真
					if(ConditionConstants.CONDITION_LINKER_AND.equals(conditionBase.getLinker())){
						if(result) result=retRslt;
					//或者时有一个为真即可
					}else if(ConditionConstants.CONDITION_LINKER_OR.equals(conditionBase.getLinker())){
						if(retRslt||result){
							result=true;
						}else{
							result=false;
						}
					}else{
						throw new EngineConditionException(EngineUtil.logPrefix(etd, BUSI_DESC)+"连接符类型有误,连接符:"+conditionBase.getLinker());
					}
				}
				
			}
		}
		
		return result;
	}

	@Override
	public Set<String> inputRela() {
		Set<String> result = new HashSet<String>();
		if(!MapUtils.isEmpty(collections)){
			for(String key:collections.keySet()){
				EngineConditionBaseInfo conditionBase=collections.get(key);
				result.addAll(conditionBase.inputRela());
			}
		}

		return result;
	}

	@Override
	public Set<String> outputRela() {
		Set<String> result = new HashSet<String>();
		if(!MapUtils.isEmpty(collections)){
			for(String key:collections.keySet()){
				EngineConditionBaseInfo conditionBase=collections.get(key);
				result.addAll(conditionBase.outputRela());
			}
		}

		return result;
	}

	@Override
	public Map<String, Boolean> relaCpntIds() {
		return Collections.emptyMap();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof EngineConditionCollection)) return false;
		EngineConditionCollection another = (EngineConditionCollection) obj;	
		if(!StringUtils.equals(super.getLinker(), another.getLinker()))return false;
		if(!StringUtils.equals(this.name(), another.name()))return false;
		if(!this.collections.equals(another.collections))return false;		
		
		return true;
	}

	@Override
	public String type() {
		// TODO Auto-generated method stub
		return BUSI_DESC;
	}

}
