package com.mobanker.engine.design.busi.user;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mobanker.engine.design.dao.EngBusiOperatorDao;
import com.mobanker.engine.design.dao.EngProductDao;
import com.mobanker.engine.design.dao.EngUserLogDao;
import com.mobanker.engine.design.dto.EngineUserInfoDto;
import com.mobanker.engine.design.dto.EngineUserLoginDto;
import com.mobanker.engine.design.pojo.EngBusiOperator;
import com.mobanker.engine.design.pojo.EngProduct;
import com.mobanker.engine.design.pojo.EngUserLog;
import com.mobanker.engine.design.pojo.transfer.EngineUserInfoDtoFunction;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.framkwork.util.EngineUtil;
import com.mobanker.framework.contract.dto.ResponseEntityDto;
import com.mobanker.zkc.util.EncryptUtils;

@Service
public class EngineUserServiceImpl implements EngineUserService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	private static String RULE_USER_SESSION="model_user_session";
	private static String RULE_USER_SESSION_REL="model_user_session_rel";
	
	@Autowired
	private EngBusiOperatorDao engBusiOperatorDao;
	
	@Autowired
	private EngUserLogDao engUserLogDao;
	
	@Autowired
	private EngProductDao engProductDao;
	
	@Autowired
	private RedisTemplate<String,Object> redisTemplate;

	private static final String ENCRYPT_KEY = "taojin123456";
	
	private static final String INIT_PASSWORD = "123456";
	
	@Override
	public EngineUserLoginDto loginIn(String username, String password,String sessionId) {			
		EngBusiOperator engBusiOperator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(engBusiOperator,"该用户不存在");
		EngineAssert.checkNotBlank(engBusiOperator.getPassword(),"该用户密码值异常");	
		
		EngineUserLoginDto result = new EngineUserLoginDto();
				
		password = EncryptUtils.encryptMode(password, ENCRYPT_KEY);
		boolean isPass = StringUtils.equals(engBusiOperator.getPassword(), password);
		result.setIsPass(isPass);
		if(isPass){
			result.setLoginId(engBusiOperator.getUsername());
			result.setName(engBusiOperator.getName());
			result.setRole(engBusiOperator.getRole());
			result.setGroupId(engBusiOperator.getGroupId());
			this.userInfoCache(sessionId, result);
		}else{
			logger.info("密码错误,send:{},db:{}",password,engBusiOperator.getPassword());
		}			
		
		return result;
	}

	@Override
	public void operatorLog(String username, String productType,
			String operateType, ResponseEntityDto<?> ret) {
		EngUserLog engUserLog = null;
		try{
			engUserLog = new EngUserLog(username, productType, operateType, ret);
			engUserLogDao.insert(engUserLog);
		}catch(Exception e){
			logger.error("记录操作员行为失败，但不会影响正常流程,logInfo:+"+engUserLog,e);
		}
		
	}

	@Override
	public void addUserInfo(String username,String realname,String role,String productIds,String groupId){		
		EngBusiOperator operator = new EngBusiOperator();
		
		EngineAssert.checkNotBlank(username,"用户名为空");			
		EngineAssert.checkNotBlank(realname,"操作员姓名为空");
		EngineAssert.checkNotBlank(role,"角色属性为空");
		
		EngineUtil.checkNameFormat(realname,0,50,"姓名");
		EngineAssert.checkNotBlank(groupId,"分组id为空");
		
		if(engBusiOperatorDao.queryLastOneByUsername(username) != null)
			throw new EngineException("用户名已经存在!");
		
		String password = EncryptUtils.encryptMode(INIT_PASSWORD, ENCRYPT_KEY);
		operator.setUsername(username);
		operator.setPassword(password);
		operator.setRole(role);
		operator.setName(realname);
		operator.setGroupId(groupId);
		
		productIds = productIds==null?"":productIds;
		operator.setProductIds(productIds);
		engBusiOperatorDao.insert(operator);
	}

	@Override
	public void updateUserInfo(EngBusiOperator engBusiOperator){
		this.updateUserInfo(engBusiOperator.getUsername(), engBusiOperator.getName(), engBusiOperator.getRole(), engBusiOperator.getProductIds(), engBusiOperator.getGroupId());
	}
	
	@CacheEvict(value="rule_default",key="'showProductList'+#username")
	@Override
	public void updateUserInfo(String username,String realname,String role,String productIds,String groupId){
		EngineAssert.checkNotBlank(username,"用户名为空");			
		EngineAssert.checkNotBlank(realname,"操作员姓名为空");
		EngineAssert.checkNotBlank(role,"角色属性为空");
		EngineAssert.checkNotBlank(groupId,"用户分组id为空");
		
		EngineUtil.checkNameFormat(realname,0,50,"姓名");
		
		EngBusiOperator operator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(operator,"该用户不存在");
		
		operator.setUsername(username);		
		operator.setRole(role);
		operator.setName(realname);
		operator.setGroupId(groupId);
		
		productIds = productIds==null?"":productIds;
		operator.setProductIds(productIds);
		engBusiOperatorDao.update(operator);
	}

	@Override
	public void updateUserPswd(String username, String oldPassword,
			String newPassword) {
		EngineAssert.checkNotBlank(username,"用户名为空");			
		EngBusiOperator operator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(operator,"该用户不存在");
				
		String dbOldPassword = operator.getPassword();
		dbOldPassword = EncryptUtils.decryptMode(dbOldPassword, ENCRYPT_KEY);
		
		if(!StringUtils.equals(dbOldPassword, oldPassword)){
			logger.warn("上送旧密码不正确，无法改密,数据库里的:{},上送的:{}",dbOldPassword,oldPassword);
			throw new EngineException("上送旧密码不正确，无法改密");
		}
		
		newPassword = EncryptUtils.encryptMode(newPassword, ENCRYPT_KEY);
		operator.setPassword(newPassword);
		engBusiOperatorDao.update(operator);		
	}


	@Override
	public void resetUserPswd(String username) {
		EngBusiOperator operator = engBusiOperatorDao.queryLastOneByUsername(username);
		EngineAssert.checkNotNull(operator,"该用户不存在");
		
		operator.setPassword(EncryptUtils.encryptMode(INIT_PASSWORD, ENCRYPT_KEY));
		engBusiOperatorDao.update(operator);	
	}

	@Override
	public Collection<EngineUserInfoDto> showUserList(final String productType,
			final String username, final String realname,final String groupId) {
		
		EngProduct product = engProductDao.queryLastOneByProductType(productType);
		final String queryProductId;
		if(product != null)
			queryProductId = product.getId().toString();
		else
			queryProductId = null;
		
		List<EngBusiOperator> userList = engBusiOperatorDao.queryByField(new EngBusiOperator());
		Collection<EngBusiOperator> filterList = Collections2.filter(userList, new Predicate<EngBusiOperator>(){
			@Override
			public boolean apply(EngBusiOperator input) {
				if(StringUtils.isNoneBlank(productType)){
					String productIds = input.getProductIds();
					if(!StringUtils.contains(productIds, queryProductId)) return false;
				}				
				if(StringUtils.isNoneBlank(username)){
					if(!StringUtils.equals(input.getUsername(), username)) return false;
				}
				if(StringUtils.isNoneBlank(realname)){
					if(!StringUtils.equals(input.getName(), realname)) return false;
				}
				if(StringUtils.isNoneBlank(groupId)){
					if(!StringUtils.equals(input.getGroupId(), groupId)) return false;
				}
				return true;
			}
		});
		
		return Collections2.transform(filterList, new EngineUserInfoDtoFunction());
	}

	/**
	 * 用户信息加入缓存
	 * @param username
	 * @param modelType
	 */
	private void userInfoCache(String sessionId,EngineUserLoginDto dto){
		EngineAssert.checkNotBlank(dto,"用户信息为空!");
		EngineAssert.checkNotBlank(sessionId,"sessionId为空!");
		
		String relKey = RULE_USER_SESSION_REL + "_" + dto.getLoginId();
		Object idObj = redisTemplate.opsForValue().get(relKey);
		if(idObj!=null){
			if(!(idObj instanceof String)){
				throw new EngineException("session关系类型异常["+idObj.getClass()+"]");
			}
			String idStr=(String) idObj;
			if(!StringUtils.equals(sessionId, idStr)){
				redisTemplate.delete(RULE_USER_SESSION + "_" +idStr);
			}
		}
		redisTemplate.opsForValue().set(relKey, sessionId);
		redisTemplate.expire(relKey,1,TimeUnit.HOURS);	
		
		
		String redisKey = RULE_USER_SESSION + "_" + sessionId;			
		Object chacheUser = redisTemplate.opsForValue().get(redisKey);//从redis当中获取权限校验结果
		
		if(chacheUser != null){
			if(!(chacheUser instanceof EngineUserLoginDto)){
				throw new EngineException("用户类型异常["+chacheUser.getClass()+"]");
			}
			EngineUserLoginDto userResult = (EngineUserLoginDto) chacheUser;		
			if(!StringUtils.endsWith(dto.getLoginId(), userResult.getLoginId())){
//				throw new EngineException("与内存中原有用户冲突！"+userDto.getLoginId()+"与"+userResult.getLoginId());
				throw new EngineException("请先退出用户！"+userResult.getLoginId());
			} 
		}
		redisTemplate.opsForValue().set(redisKey, dto);
		redisTemplate.expire(redisKey,1,TimeUnit.HOURS);		
	}
	
	@Override
	public EngineUserLoginDto getUserInfoBySeesionId(String sessionId) {
		EngineAssert.checkNotBlank(sessionId);
		String redisKey = RULE_USER_SESSION + "_" + sessionId;				
		Object verifyObj = redisTemplate.opsForValue().get(redisKey);//从redis当中获取权限校验结果
		if(verifyObj != null){
			if(!(verifyObj instanceof EngineUserLoginDto)){
				throw new EngineException("用户类型异常["+verifyObj.getClass()+"]");
			}
			EngineUserLoginDto userResult = (EngineUserLoginDto) verifyObj;	
			redisTemplate.opsForValue().set(redisKey, userResult);
			redisTemplate.expire(redisKey,1,TimeUnit.HOURS);	
			return userResult;
		}	
		return null;
	}
	
	@Override
	public void userLoginOut(String username,String sessionId) {
		EngineAssert.checkNotBlank(username);
		EngineAssert.checkNotBlank(sessionId);
		String redisKey = RULE_USER_SESSION + "_" + sessionId;				
		Object verifyObj = redisTemplate.opsForValue().get(redisKey);//从redis当中获取权限校验结果
		if(verifyObj != null){
			if(!(verifyObj instanceof EngineUserLoginDto)){
				throw new EngineException("用户类型异常["+verifyObj.getClass()+"]");
			}
			EngineUserLoginDto userResult = (EngineUserLoginDto) verifyObj;	
			if(StringUtils.endsWith(username, userResult.getLoginId())){
				redisTemplate.delete(redisKey);
			}
		}	
	}
	
	public static void main(String[] args) {
		String password = EncryptUtils.encryptMode(INIT_PASSWORD, ENCRYPT_KEY);
		System.out.println(password);
	}
}
