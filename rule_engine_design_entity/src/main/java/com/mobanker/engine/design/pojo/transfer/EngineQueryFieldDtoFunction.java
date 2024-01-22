package com.mobanker.engine.design.pojo.transfer;

import com.google.common.base.Function;
import com.mobanker.engine.design.dto.EngineQueryFieldDto;
import com.mobanker.engine.design.pojo.EngQueryField;


public class EngineQueryFieldDtoFunction implements Function<EngQueryField,EngineQueryFieldDto>{

	@Override
	public EngineQueryFieldDto apply(EngQueryField input) {
		EngineQueryFieldDto result = new EngineQueryFieldDto();
		result.setFieldDefaultValue(input.getFieldDefaultValue());
		result.setFieldKey(input.getFieldKey());
		result.setFieldName(input.getFieldName());
		result.setFieldRefValue(input.getFieldRefValue());
		result.setFieldType(input.getFieldType());
		result.setIsArr(input.getIsArr());		
		return result;
	}	

}
