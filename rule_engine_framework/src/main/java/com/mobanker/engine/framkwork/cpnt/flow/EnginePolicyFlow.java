package com.mobanker.engine.framkwork.cpnt.flow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpntBaseInfo;
import com.mobanker.engine.framkwork.cpnt.judge.EngineJudge;
import com.mobanker.engine.framkwork.cpnt.stage.EngineStage;
import com.mobanker.engine.framkwork.cpnt.step.assist.EngineBranch;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.manager.EngineStepProcessor;
import com.mobanker.engine.framkwork.relation.EngineRelaCpnt;
import com.mobanker.engine.framkwork.relation.EngineRelaField;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 决策流组件
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年11月3日
 * @version 1.0
 */
public class EnginePolicyFlow extends EngineCpntBaseInfo implements EngineRelaCpnt,EngineCpnt,EngineRelaField{

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(EnginePolicyFlow.class);	
	private static final String BUSI_DESC = "决策流";	
	
	private EngineFlowNode root;
	
	public void run(EngineStepProcessor processor,EngineTransferData etd) {
		if(MapUtils.isEmpty(etd.getInputParam()))
			throw new EngineException(EngineUtil.logPrefix(etd, BUSI_DESC)+"inputParam参数为空");
		root.run(processor,etd);		
	}
	
	@Override
	public Map<String,Boolean> relaCpntIds() {
		return root.relaCpntIds();
	}
	@Override
	public String type() {
		return BUSI_DESC;
	}		
	
	public static class EngineFlowNode implements EngineRelaCpnt,EngineRelaField,Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * 当前执行的节点步骤，如果是分支节点则为null
		 */
		private EngineStage stage;
		/**
		 * 下个节点，如果是分支节点则为null
		 */
		private EngineFlowNode defaultNode;
		/**
		 * 分支路线,如果是执行节点，则为null
		 */
		private List<EngineBranch<EngineFlowNode>> route;
		
		
		public void run(EngineStepProcessor processor,EngineTransferData etd){
			
			EngineFlowNode nextNode = null;
			//如果有执行节点则执行,由于没有分支，则必定会走到默认分支上 
			if(stage != null){
				logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"该节点为执行节点，准备执行模块:{}",stage.name());	
				processor.addStepList(stage.getList());
				processor.launch();				
			}else{//否则查看是否是分支节点
				if(!CollectionUtils.isEmpty(route)){
					logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"该节点为分支节点，判断数量:{}",route.size());
					for(EngineBranch<EngineFlowNode> judgeRoute : route){
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
				nextNode.run(processor,etd);
			}else
				logger.info(EngineUtil.logPrefix(etd, BUSI_DESC)+"没有后续分支:{}",this);
		}
		
		@Override
		public Map<String,Boolean> relaCpntIds() {
			Map<String,Boolean> result = new HashMap<String,Boolean>();			
			
			if(stage instanceof EngineRelaCpnt){
				EngineRelaCpnt rela = (EngineRelaCpnt) stage;
				result.putAll(EngineUtil.relaNonlineal(rela));	
				result.put(stage.id(), true);
			}
			if(defaultNode != null) result.putAll(defaultNode.relaCpntIds());
			if(route != null){
				for(EngineBranch<EngineFlowNode> one : route){
					result.putAll(one.relaTreeCpntIds());										
				}	
			}					
			return result;
		}		
		
		@Override
		public Set<String> inputRela() {
			Set<String> result = new HashSet<String>();
			if(stage != null) result.addAll(stage.inputRela());
			if(defaultNode != null) result.addAll(defaultNode.inputRela());
			if(route != null){
				for(EngineBranch<EngineFlowNode> one : route){
					result.addAll(one.inputRela());										
				}	
			}				
			return result;
		}

		@Override
		public Set<String> outputRela() {
			Set<String> result = new HashSet<String>();
			if(stage != null) result.addAll(stage.outputRela());
			if(defaultNode != null) result.addAll(defaultNode.outputRela());
			if(route != null){
				for(EngineBranch<EngineFlowNode> one : route){
					result.addAll(one.outputRela());										
				}	
			}				
			return result;
		}
				
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof EngineFlowNode)) return false;
			EngineFlowNode another = (EngineFlowNode) obj;			
									
			if(this.stage == another.stage){
				//
			}else{
				if(this.stage == null) return false;
				if(another.stage == null) return false;	
				if(!this.stage.id().equals(another.stage.id())) return false;
			}
				
			if(!Objects.equals(this.defaultNode, another.defaultNode)) return false;
			if(!Objects.equals(this.route, another.route)) return false;			
			
			return true;
		}
		
		public void addBranch(EngineJudge judge,boolean mode,EngineFlowNode node){
			if(route == null) route = new LinkedList<EngineBranch<EngineFlowNode>>();
			route.add(new EngineBranch<EngineFlowNode>(judge, mode, node));
		}		
		
		

		public EngineFlowNode getDefaultNode() {
			return defaultNode;
		}

		public void setDefaultNode(EngineFlowNode defaultNode) {
			this.defaultNode = defaultNode;
		}

		public List<EngineBranch<EngineFlowNode>> getRoute() {
			return route;
		}




		public void setRoute(List<EngineBranch<EngineFlowNode>> route) {
			this.route = route;
		}




		public EngineStage getStage() {
			return stage;
		}

		public void setStage(EngineStage stage) {
			this.stage = stage;
		}

		public String toString(){
			return stage==null?"分支节点":stage.toString();
		}

	}

	public EngineFlowNode getRoot() {
		return root;
	}

	public void setRoot(EngineFlowNode root) {
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
		if(!(obj instanceof EnginePolicyFlow)) return false;
		EnginePolicyFlow another = (EnginePolicyFlow) obj;			
		return root.equals(another.root);
	}

}
