package com.mobanker.engine.design.pojo.transfer;

import com.google.common.base.Function;
import com.mobanker.engine.design.dto.EngineUserInfoDto;
import com.mobanker.engine.design.pojo.EngBusiOperator;


public class EngineUserInfoDtoFunction implements Function<EngBusiOperator,EngineUserInfoDto>{

	@Override
	public EngineUserInfoDto apply(EngBusiOperator input) {
		EngineUserInfoDto result = new EngineUserInfoDto();
		result.setProductIds(input.getProductIds());
		result.setRealname(input.getName());
		result.setRole(input.getRole());
		result.setUsername(input.getUsername());	
		result.setGroupId(input.getGroupId());
		return result;
	}	

}
