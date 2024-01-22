package com.mobanker.engine.common.interceptor;

import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * 记录日志
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年6月7日
 * @version 1.0
 */
public class LoggerAop {
	private Logger logger = LoggerFactory.getLogger(this.getClass());	

	public Object deal(ProceedingJoinPoint pjp) throws Throwable {	
		
		List<Object> params = Arrays.asList(pjp.getArgs());
		String paramMessage = null;
		if(!CollectionUtils.isEmpty(params)){
			paramMessage = params.toString();
			if(paramMessage.length() > 1000){
				paramMessage = paramMessage.substring(0, 1000)+"...";
			}
		}
		
		logger.info("[logger aop begin] class:{},function:{},args:{}", pjp.getSignature().getDeclaringType().getSimpleName(),pjp.getSignature().getName(),paramMessage);
		Object returnMessage = pjp.proceed();
		String returnMsgStr = null;
		if(returnMessage!=null){
			returnMsgStr = returnMessage.toString();
			if(returnMsgStr.length() > 1000){
				returnMsgStr = returnMsgStr.substring(0, 1000)+"...";
			}
		}
		logger.info("[logger aop end] class:{},function:{},return:{}", pjp.getSignature().getDeclaringType().getSimpleName(),pjp.getSignature().getName(),returnMsgStr);
		return returnMessage;
		
	}
}
