package com.mobanker.engine.framework.client.api;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.framework.client.pojo.EngineRuleRes;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.cpnt.container.EngineCpntContainer;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.util.EngineUtil;

/**
 * 
 * @author taojinn
 *
 */
public class EngineClientApi {
	
	private static Logger logger = LoggerFactory.getLogger(EngineClientApi.class);
	
	public static EngineRuleRes call(String product,Map<String,Object> param){			
		String uuid = EngineUtil.getUUID();
		logger.info("engine call start,product:{},param:{},uuid:{}",product,param,uuid);
		EngineRuleRes res = new EngineRuleRes();
		
		try{
			EngineCpntContainer engineCpntContainer = EngineClientEnviroment.getProductManager().getFlow(product);
			EngineAssert.checkNotNull(engineCpntContainer, "product[%s] is not exists!",product);						
			
			EngineTaskInfo taskInfo = new EngineTaskInfo();
			taskInfo.setId(0L);
			taskInfo.setProductType(product);
			taskInfo.setAppName("client");		
			taskInfo.setAppParams(JSONObject.toJSONString(param));
			taskInfo.setAppRequestId(uuid);			
			taskInfo.setExecTime("00000000000000"); 
			taskInfo.setPriority(99);
			taskInfo.setAssignStep(-1);
			taskInfo.setStatus("0");
			taskInfo.setChannel("jar");
			taskInfo.setFailTimes(0);
			taskInfo.setSourceDetail("");		
			taskInfo.setBeginTime(EngineUtil.get14CurrentDateTime());
			taskInfo.setEndTime("");
			taskInfo.setRuleVersion("");
			
			EngineTransferData etd = EngineClientEnviroment.getTaskLauncher().runTask(taskInfo, engineCpntContainer.getRoot());
			Map<String,Object> output = etd.getOutputParam();
			logger.info("engine call success,product:{},result:{},uuid:{}",product,output,uuid);
			res.setData(output);
		}catch(Exception e){
			res.setError("84001001");			
			res.setMsg(e.getMessage());
		}
			
		return res;
	}
}
