package com.mobanker.engine.design.busi.user;

import java.util.Collection;

import com.mobanker.engine.design.dto.EngineUserInfoDto;
import com.mobanker.engine.design.dto.EngineUserLoginDto;
import com.mobanker.engine.design.pojo.EngBusiOperator;
import com.mobanker.framework.contract.dto.ResponseEntityDto;

/**
 * 用户服务
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月6日
 * @version 1.0
 */
public interface EngineUserService {
	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public EngineUserLoginDto loginIn(String username,String password,String sessionId);
	
	/**
	 * 记录操作人员行为
	 * @param username
	 * @param productType
	 * @param logType
	 */
	public void operatorLog(String username, String productType, String operateType, ResponseEntityDto<?> ret);
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @param realname
	 * @param role
	 */
	public void addUserInfo(String username,String realname,String role,String productIds,String groupId);
	
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @param realname
	 * @param role
	 */
	public Collection<EngineUserInfoDto> showUserList(String productType,String username,String realname,String groupId);
	
	/**
	 * 
	 * @param username
	 * @param realname
	 * @param role
	 */
	public void updateUserInfo(String username,String realname,String role,String productIds,String groupId);
	
	/**
	 * 
	 * @param username
	 * @param oldPassword
	 * @param newPassword
	 */
	public void updateUserPswd(String username,String oldPassword,String newPassword);
	
	/**
	 * 重置密码
	 * @param username
	 */
	public void resetUserPswd(String username);

	EngineUserLoginDto getUserInfoBySeesionId(String sessionId);

	void userLoginOut(String username, String sessionId);

	void updateUserInfo(EngBusiOperator engBusiOperator);
}
