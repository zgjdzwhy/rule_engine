package com.mobanker.engine.framkwork.cpnt.step.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.judge.EngineJudge;
import com.mobanker.engine.framkwork.cpnt.step.EngineAbstractStep;
import com.mobanker.engine.framkwork.cpnt.step.EngineStep;
import com.mobanker.engine.framkwork.cpnt.step.assist.EngineBranch;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;
import com.mobanker.engine.framkwork.relation.EngineRelaField;
import com.mobanker.engine.framkwork.util.EngineUtil;

public class EnginePolicyTreeStep extends EngineAbstractStep implements EngineStep,EngineRelaCpnt{

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(EnginePolicyTreeStep.class);	
	private static final String BUSI_DESC = "决策树";	
	
	private PolicyTreeNode root;
			
	@Override
	protected void run(EngineTransferData etd) {
		root.run(etd);		
	}
	
	@Override
	public Map<String,Boolean> relaCpntIds() {
		return root.relaCpntIds();
	}
	
	@Override
	public String type() {
		return BUSI_DESC;
	}			
	
	public static class PolicyTreeNode implements EngineRelaCpnt,EngineRelaField,Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * 当前执行的节点步骤，如果是分支节点则为null
		 */
		private EngineStep step;
		/**
		 * 下个节点，如果是分支节点则为null
		 */
		private PolicyTreeNode defaultNode;
		/**
		 * 分支路线,如果是执行节点，则为null
		 */
		private List<EngineBranch<PolicyTreeNode>> route;
		

		
		public void run(EngineTransferData etd){
			
			PolicyTreeNode nextNode = null;
			//如果有执行节点则执行,由于没有分支，则必定会走到默认分支上 
			if(step != null){
				logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"该节点为执行节点，准备执行步骤:{}",step.name());
				step.execute(etd);
			}else{//否则查看是否是分支节点
				if(!CollectionUtils.isEmpty(route)){
					logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"该节点为分支节点，判断数量:{}",route.size());
					for(EngineBranch<PolicyTreeNode> judgeRoute : route){
						logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"该节点为分支节点，准备判断分支:{}",judgeRoute);
						if(judgeRoute.judge(etd)){//如果满足条件则走这条分支
							logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"满足该分支选择节点:{}",judgeRoute);
							nextNode = judgeRoute.getContent();
							if(nextNode == null){//如果分支内容为空
								logger.warn(EngineUtil.logPrefix(etd, BUSI_DESC)+"满足分支条件，但是分支内容为空，则结束");
								return;
							}							
							break;
						}	
					}
				}
			}			
			if(nextNode == null){//如果没有确切的分支路线，走默认分支
				logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"准备走默认分支:{}",defaultNode);
				nextNode = defaultNode;
			}
			
			//如果默认分支还是null，则说明这个分支是枝叶节点，无需下一步
			if(nextNode != null){
				//继续递归
				logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"节点执行开始:{}",defaultNode);
				nextNode.run(etd);
			}else
				logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"没有后续分支:{}",this);
		}
		
		
		
		
		public EngineStep getStep() {
			return step;
		}




		public void setStep(EngineStep step) {
			this.step = step;
		}




		public PolicyTreeNode getDefaultNode() {
			return defaultNode;
		}




		public void setDefaultNode(PolicyTreeNode defaultNode) {
			this.defaultNode = defaultNode;
		}




		public List<EngineBranch<PolicyTreeNode>> getRoute() {
			return route;
		}




		public void setRoute(List<EngineBranch<PolicyTreeNode>> route) {
			this.route = route;
		}




		public String toString(){
			return step==null?"分支节点":step.toString();
		}

		@Override
		public Map<String,Boolean> relaCpntIds() {
			Map<String,Boolean> result = new HashMap<String,Boolean>();			
			
			if(step instanceof EngineRelaCpnt){
				EngineRelaCpnt rela = (EngineRelaCpnt) step;
				result.putAll(EngineUtil.relaNonlineal(rela));	
				result.put(step.id(), true);
			}
			if(defaultNode != null) result.putAll(defaultNode.relaCpntIds());
			if(route != null){
				for(EngineBranch<PolicyTreeNode> one : route){
					//result.putAll(one.relaCpntIds());										
					result.putAll(one.relaTreeCpntIds());
				}	
			}					
			return result;
		}		
		
		public void addBranch(EngineJudge judge,boolean mode,PolicyTreeNode node){
			if(route == null) route = new LinkedList<EngineBranch<PolicyTreeNode>>();
			route.add(new EngineBranch<PolicyTreeNode>(judge, mode, node));
		}		
		
		@Override
		public Set<String> inputRela() {
			Set<String> result = new HashSet<String>();
			if(step != null) result.addAll(step.inputRela());
			if(defaultNode != null) result.addAll(defaultNode.inputRela());
			if(route != null){
				for(EngineBranch<PolicyTreeNode> one : route){
					result.addAll(one.inputRela());										
				}	
			}				
			return result;
		}

		@Override
		public Set<String> outputRela() {
			Set<String> result = new HashSet<String>();
			if(step != null) result.addAll(step.outputRela());
			if(defaultNode != null) result.addAll(defaultNode.outputRela());
			if(route != null){
				for(EngineBranch<PolicyTreeNode> one : route){
					result.addAll(one.outputRela());										
				}	
			}				
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof PolicyTreeNode)) return false;
			PolicyTreeNode another = (PolicyTreeNode) obj;			
						
			if(this.step == another.step){
				//
			}else{
				if(this.step == null) return false;
				if(another.step == null) return false;				
				if(!this.step.id().equals(another.step.id())) return false;
			}
				
			if(!Objects.equals(this.defaultNode, another.defaultNode)) return false;
			if(!Objects.equals(this.route, another.route)) return false;			
			
			return true;
		}
		
	}
	

	public PolicyTreeNode getRoot() {
		return root;
	}

	public void setRoot(PolicyTreeNode root) {
		this.root = root;
	}

	@Override
	public Set<String> inputRela() { 
		return root.inputRela();
	}

	@Override
	public Set<String> outputRela() {
		return root.outputRela();
	}


	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof EnginePolicyTreeStep)) return false;
		EnginePolicyTreeStep another = (EnginePolicyTreeStep) obj;			
		return root.equals(another.root);
	}

}
