package com.mobanker.engine.design.controller;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mobanker.engine.common.EngineConst;
import com.mobanker.engine.common.servlet.ServletUtils;
import com.mobanker.engine.design.dao.EngQueryDictionaryDao;
import com.mobanker.engine.design.dao.EngQueryFieldDao;
import com.mobanker.engine.design.pojo.EngQueryDictionary;
import com.mobanker.engine.design.pojo.EngQueryField;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.framework.contract.dto.ResponseEntityDto;

/**
 * 模拟测试指标库
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月4日
 * @version 1.0
 */
@Controller
@RequestMapping("dataGetTest")
public class EngineTestQueryController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());	

	@Autowired
	private EngQueryFieldDao engQueryFieldDao;
	
	@Autowired
	private EngQueryDictionaryDao engQueryDictionaryDao;
	
	
	/**
	 * http://localhost:8080/ruleModelDesign/loginIn
	 * 登录
	 * @param operatorId
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/getData", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Map<String,Object>> getData(HttpServletRequest request) {
		
		Map<String, Object> params = ServletUtils.getParametersStartingWith(request, null);
		ResponseEntityDto<Map<String,Object>> ret;
		try{
			EngineAssert.checkNotNull(params.get("queryIds"));			
			String queryIds = params.remove("queryIds").toString();
			String[] queryIdsArr = StringUtils.split(queryIds,",");
			
			
			Map<String,Object> result = new HashMap<String,Object>();
			
			for(String queryId : queryIdsArr){
				EngQueryDictionary engQueryDictionary = engQueryDictionaryDao.queryLastOneByQueryId(queryId);	
				EngineAssert.checkNotNull(engQueryDictionary,"查询项没找到,queryid:%s",queryId);
				
				List<EngQueryField> fieldList = engQueryFieldDao.queryByQueryIdAndProductType(engQueryDictionary.getQueryId(), engQueryDictionary.getProductType());								
					for(EngQueryField field : fieldList){						
						try{
							String isArrStr = field.getIsArr();
							String type = field.getFieldType();
							String srcObjstr = field.getFieldRefValue();
							Object transObj = null;
							if(StringUtils.equals(isArrStr, "1")){
								transObj = transfer2List(srcObjstr,type);					
							}else{										
								transObj = transfer(srcObjstr,type);	
							}		
							EngineAssert.checkNotBlank(field.getFieldKey());
							EngineAssert.checkNotNull(transObj);				
							result.put(field.getFieldKey(), transObj);
						}catch(Exception e){
							throw new EngineException("获取数据发生错误,queryId:"+field.getFieldKey(),e);
						}
					}				
			}

									
			ret = new ResponseEntityDto<Map<String,Object>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,result);		
		}catch(Exception e){
			logger.error("登录异常",e);
			ret = new ResponseEntityDto<Map<String,Object>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		
		return ret;
	}		
	private Object transfer2List(String objstr,String type){		
		List<Object> list = new LinkedList<>();
		list.add(transfer(objstr,type));
		list.add(transfer(objstr,type));
		list.add(transfer(objstr,type));
		list.add(transfer(objstr,type));
		list.add(transfer(objstr,type));
		return list;		
	}	
	
	private Object transfer(String objstr,String type){
	
		if(StringUtils.equals(type, "String")){
			if(objstr == null) return "";
			return objstr;
		}else if(StringUtils.equals(type, "BigDecimal")){
			if(objstr == null) return BigDecimal.ZERO;
			return new BigDecimal(objstr);
		}else if(StringUtils.equals(type, "Long")){
			if(objstr == null) return Long.valueOf(0L);
			return Long.valueOf(objstr);
		}else if(StringUtils.equals(type, "Boolean")){
			if(objstr == null) return false;
			return Boolean.valueOf(objstr);
		}		
		
		throw new EngineException("transfer error");
	}
	

}
