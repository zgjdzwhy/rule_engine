package com.mobanker.engine.design.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.design.pojo.EngDefineField;
import com.mobanker.engine.design.pojo.EngProduct;
import com.mobanker.engine.design.pojo.EngQueryHost;
import com.mobanker.engine.design.pojo.EngScriptFunction;
import com.mobanker.engine.design.pojo.EngScriptTemplate;

/**
 * 包含了一个产品线所有的内容
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年10月28日
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineRuleAllConfigDto {
	
	/**
	 * eng_product 其中所属组会被替换
	 */
	private EngProduct engProduct;	
	/**
	 * eng_query_host
	 */
	private EngQueryHost engQueryHost;
	/**
	 * eng_query_dictionary和eng_query_field
	 */
	private List<EngineQueryModelDto> engineQueryModelList;
	
	/**
	 * eng_define_field
	 */
	private List<EngDefineField> engDefineFieldList;
	
	/**
	 * rule_model_self
	 */
	private List<JSONObject> ruleModelSelf;

	/**
	 * rule_model_merge
	 */
	private List<JSONObject> ruleModelMerge;
	
	/**
	 * rule_model_merge
	 */
	private List<EngScriptFunction> ruleScriptFunction;
	
	/**
	 * rule_model_merge
	 */
	private List<EngScriptTemplate> ruleScriptTemplate;
	
	
}
