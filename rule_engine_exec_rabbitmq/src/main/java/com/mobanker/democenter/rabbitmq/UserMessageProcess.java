package com.mobanker.democenter.rabbitmq;

import com.mobanker.framework.rabbitmq.MQProcess;
import com.mobanker.framework.rabbitmq.MQResponse;

/**
 * 
 * @author lait.zhang@gmail.com
 * @Date 2016年8月19日-下午5:38:22
 * @Description:<br>
 * 					</br>
 *
 */
public class UserMessageProcess implements MQProcess<UserMessage> {
	@Override
	public MQResponse process(UserMessage userMessage) {
		System.out.println(userMessage);
		return new MQResponse(true, "");
	}
}
