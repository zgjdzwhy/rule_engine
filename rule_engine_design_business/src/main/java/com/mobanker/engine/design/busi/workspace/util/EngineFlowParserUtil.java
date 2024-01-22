package com.mobanker.engine.design.busi.workspace.util;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.mobanker.engine.design.dto.EngineStageDto;
import com.mobanker.engine.framkwork.cpnt.stage.EngineStage;
import com.mobanker.engine.framkwork.cpnt.step.EngineStep;
import com.mobanker.engine.framkwork.cpnt.step.assist.EngineBranch;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineBridgeStep;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineRouteStep;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月29日
 * @version 1.0
 */
@Deprecated
public class EngineFlowParserUtil {
				
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	private final static String BUSI_DESC = "决策树显示";
	
	private static EngineStep getLastStep(EngineStage stage){
		List<EngineStep> stepList = stage.getList();
		if(CollectionUtils.isEmpty(stepList)) return null;		
		return stepList.get(stepList.size()-1);
	}
	
	private static EngineStage nextStage(EngineStage stage){		
		EngineStep lastStep = getLastStep(stage);
		if(lastStep == null) return null;
		if(lastStep.getClass() != EngineBridgeStep.class) return null; 
		
		EngineBridgeStep bridge = (EngineBridgeStep) lastStep;
		return bridge.getNextStage();
	}
	
	
	private static List<EngineStage> getChildren(EngineStage stage){		
		EngineStep lastStep = getLastStep(stage);
		
		if(lastStep == null) return null;
		if(lastStep.getClass() != EngineRouteStep.class) return null; 
		
		EngineRouteStep route = (EngineRouteStep) lastStep;
		if(CollectionUtils.isEmpty(route.getRouteStage())) return null;
		
		List<EngineStage> result = new LinkedList<EngineStage>();
		for(EngineBranch<EngineStage> oneRoute : route.getRouteStage()){
			result.add(oneRoute.getContent());
		}				
		if(route.getDefaultStage() != null)
			result.add(route.getDefaultStage());
		
		return result;
	}
	
	public static EngineStageDto transfer(EngineStage stage){
		EngineStageDto one = new EngineStageDto();
		one.setId(stage.id());
		one.setName(stage.name());
		one.setRight(stage.right());
		EngineStage nextStage = nextStage(stage);
		List<EngineStage> children = getChildren(stage);
		
		
		if(nextStage != null){
			EngineStageDto nextNode = transfer(nextStage);
			one.setNextNode(nextNode);
			return one;
		}
							
		
		if(!CollectionUtils.isEmpty(children)){
			List<EngineStageDto> dtoList = new LinkedList<EngineStageDto>();						
			for(EngineStage child : children){				
				EngineStageDto childDto = transfer(child);
				dtoList.add(childDto);
			}			
			one.setChildren(dtoList);
		}					
		return one;
	}
	
}
