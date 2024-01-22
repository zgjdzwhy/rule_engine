package com.mobanker.engine.framkwork.cpnt.judge.assist;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;
import com.mobanker.engine.framkwork.relation.EngineRelaField;

/**
 * 条件节点
 * <p>Company: mobanker.com</p>
 * @author zhujj
 * @date 2017年1月07日
 * @version 1.0
 * @param <T>
 */
@Deprecated
public class EngineConditionNode implements EngineRelaCpnt,EngineRelaField{

	/**
	 * 集合  以（或者||）并列
	 */
	private List<EngineConditionCollection> collectionList;
	
	public EngineConditionNode(){}
	
	public EngineConditionNode(List<EngineConditionCollection> collectionList){
		this.collectionList = collectionList;
	}

	public List<EngineConditionCollection> getCollectionList() {
		return collectionList;
	}

	public void setCollectionList(List<EngineConditionCollection> collectionList) {
		this.collectionList = collectionList;
	}

	public boolean judge(EngineTransferData etd){
		boolean result=true;
//		if(collectionList!=null&&collectionList.size()>0){
//			for(EngineConditionCollection collection:collectionList){
//				result=collection.judge(etd);
//				//lis 以（或者||）并列 一个为真即为真
//				if(result){
//					break;
//				}
//			}
//		}
		return result;
	}
	
	@Override
	public Set<String> inputRela() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> outputRela() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Boolean> relaCpntIds() {
		// TODO Auto-generated method stub
		return null;
	}

	

	
}
