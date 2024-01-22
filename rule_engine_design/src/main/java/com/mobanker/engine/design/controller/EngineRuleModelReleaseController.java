package com.mobanker.engine.design.controller;


import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mobanker.engine.common.EngineConst;
import com.mobanker.engine.design.busi.configma.EngineConfigManageService;
import com.mobanker.engine.design.busi.user.EngineUserService;
import com.mobanker.engine.design.busi.workspace.EngineWorkspaceManager;
import com.mobanker.engine.design.dao.EngBusiOperatorDao;
import com.mobanker.engine.design.dto.EngineReleaseQryDto;
import com.mobanker.engine.design.hotdeploy.EngineRuleModelHotDeployService;
import com.mobanker.engine.design.hotdeploy.EngineTestRuleHotDeployService;
import com.mobanker.engine.design.hotdeploy.file.EngineFileContent;
import com.mobanker.engine.design.pojo.EngProductReleaseFlow;
import com.mobanker.engine.design.serialize.EngineFileContentSerialize;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.framework.contract.dto.ResponseEntityDto;

/**
 * 规则模型发布
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月4日
 * @version 1.0
 */
@Controller
@RequestMapping("/ruleModelRelease")
public class EngineRuleModelReleaseController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());	

	@Autowired
	private EngineWorkspaceManager engineWorkspaceManager;
	
	@Autowired
	private EngineConfigManageService engineQueryModelService;
	
	@Autowired
	private EngineUserService engineUserService;
	
	@Autowired
	private EngineRuleModelHotDeployService engineRuleModelHotDeployService;
	
	@Autowired
	private EngBusiOperatorDao engBusiOperatorDao;
	
	@Resource(name = "engineTestRuleHotDeployService")
	private EngineTestRuleHotDeployService engineTestRuleHotDeployService;
	
	/**
	 * http://localhost:8080/ruleModelRelease/showReleaseList?username=taojin&productType=shoujidai&status=f&version=20160922
	 * @param username
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/showReleaseList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngProductReleaseFlow>> showReleaseList(@ModelAttribute EngineReleaseQryDto dto) {
		ResponseEntityDto<List<EngProductReleaseFlow>> ret= new ResponseEntityDto<List<EngProductReleaseFlow>>();
		try{
			EngineAssert.checkNotNull(dto,"上送参数dto不可为空");
			String username = dto.getUsername();
			String productType = dto.getProductType();
			String status = dto.getStatus();
			String version = dto.getVersion();
			String applyUser = dto.getApplyUser();
			Boolean isPrd = dto.getIsPrd();
			
			productType = StringUtils.isBlank(productType)?null:productType;
			status = StringUtils.isBlank(status)?null:status;
			version = StringUtils.isBlank(version)?null:version;
			applyUser = StringUtils.isBlank(applyUser)?null:applyUser;
			
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");															
			ret = new ResponseEntityDto<List<EngProductReleaseFlow>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,
					engineWorkspaceManager.showRealeaseList(username,productType,status,version,applyUser,isPrd));		
		}catch(Exception e){
			logger.error("查询发布列表失败",e);
			ret = new ResponseEntityDto<List<EngProductReleaseFlow>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
					
		return ret;
	}		
	
	/**
	 * http://localhost:8080/ruleModelRelease/applyRelease
	 * username=taojin&productType=shoujidai&versionName=测试版本
	 * 
	 * @param operatorId
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/applyRelease", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> applyRelease(@RequestParam String username,@RequestParam String productType,@RequestParam String versionName) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			EngineAssert.checkNotBlank(versionName,"上送参数versionName不可为空");
								
			engineWorkspaceManager.applyRelease(username, productType, versionName);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("申请发布失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		
		engineUserService.operatorLog(username,productType,"applyRelease",ret);	 
		return ret;
	}		
	
	
	/**
	 * http://localhost:8080/ruleModelRelease/auditRelease
	 * username=taojin&productType=shoujidai&flowId=7
	 * 
	 * @param operatorId
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/auditRelease", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> auditRelease(@RequestParam String username,@RequestParam String productType,@RequestParam Long flowId) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotNull(flowId,"上送参数flowId不可为空");						
								
			engineWorkspaceManager.auditRelease1(username, flowId);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("审核发布失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		
		engineUserService.operatorLog(username,productType,"auditRelease",ret);	 
		return ret;
	}		
	
	/**
	 * http://localhost:8080/ruleModelRelease/release
	 * username=taojin&productType=shoujidai&flowId=3
	 * @param username
	 * @param productType
	 * @param flowId
	 * @return
	 */
	@RequestMapping(value = "/release", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> release(@RequestParam String username,@RequestParam String productType,@RequestParam Long flowId) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotNull(flowId,"上送参数flowId不可为空");						
								
			engineWorkspaceManager.release(username, flowId);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("发布失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		
		engineUserService.operatorLog(username,productType,"release",ret);	 
		return ret;
	}		
	
	/**
	 * http://localhost:8080/ruleModelRelease/fastRelease
	 * username=taojin&productType=shoujidai
	 * 
	 * @param operatorId
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/fastRelease", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> fastRelease(@RequestParam String username,@RequestParam String productType) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");			
			//先模拟提交
			engineWorkspaceManager.mergeRuleModel(username, productType);
			//申请发布
			Long flowId = engineWorkspaceManager.applyRelease(username, productType, "快速发布");
			//正式发布
			engineWorkspaceManager.release(username, flowId);			
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("申请发布失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		
		engineUserService.operatorLog(username,productType,"applyRelease",ret);	 
		return ret;
	}		
	
	/**
	 * http://localhost:8080/ruleModelRelease/cancelRelease
	 * username=taojin&productType=shoujidai&flowId=3
	 * @param username
	 * @param productType
	 * @param flowId
	 * @return
	 */
	@RequestMapping(value = "/cancelRelease", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> cancelRelease(@RequestParam String username,@RequestParam String productType,@RequestParam Long flowId) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotNull(flowId,"上送参数flowId不可为空");						
								
			engineWorkspaceManager.cancelRelease(username, flowId);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("发布失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		
		engineUserService.operatorLog(username,productType,"cancelRelease",ret);	 
		return ret;
	}			


	/**
	 * http://localhost:8080/ruleModelRelease/rollbackRelease
	 * username=taojin&productType=shoujidai&flowId=3
	 * 
	 * @param username
	 * @param productType
	 * @param flowId
	 * @return
	 */
	@RequestMapping(value = "/rollbackRelease", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> rollbackRelease(@RequestParam String username,@RequestParam String productType,@RequestParam Long flowId) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotNull(flowId,"上送参数flowId不可为空");						
								
			engineWorkspaceManager.rollbackRelease(username, flowId);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("发布失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		
		engineUserService.operatorLog(username,productType,"restoreRelease",ret);	 
		return ret;
	}		
	
	/**
	 * http://localhost:8080/ruleModelRelease/getCurRuleModelBytes?productType=leadDemo
	 * @param username
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/getCurRuleModelBytes", method = RequestMethod.GET, produces = "application/octet-stream")
	@ResponseBody
	public byte[] getCurRuleModelBytes(@RequestParam String productType) {
		try{
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			EngineFileContent fileContent = engineRuleModelHotDeployService.getCurRuleModelBytes(productType);			
			logger.info("导出规则集内容大小:{}",fileContent.getBytes().length);			
			byte[] data = new EngineFileContentSerialize().serialize(fileContent);
			logger.info("导出规则集文件大小:{}",data.length);
			return data;	
		}catch(Exception e){
			logger.error("查询发布列表失败",e);
		}
					
		return null;
	}		
	
	/**
	 * http://localhost:8080/ruleModelRelease/getRuleBytesByFlow?username=taojin&flowId=1
	 * @param username
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/getRuleBytesByFlow", method = RequestMethod.GET, produces = "application/octet-stream")
	@ResponseBody
	public byte[] getRuleBytesByFlow(@RequestParam String username,@RequestParam Long flowId) {
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(username,"上送参数flowId不可为空");
			
			EngineFileContent fileContent = engineWorkspaceManager.getRuleBytesByFlow(username, flowId);	
			logger.info("获取规则内容大小:{}",fileContent.getBytes().length);			
			byte[] data = new EngineFileContentSerialize().serialize(fileContent);
			logger.info("获取规则内容大小:{}",data.length);
			return data;	
		}catch(Exception e){
			logger.error("获取规则内容失败",e);
		}
					
		return null;
	}		
	
	/**
	 * 
	 * @param username
	 * @param file 文件内容
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/uploadRuleModelFile", method=RequestMethod.POST)
	@ResponseBody		
	public ResponseEntityDto<?> uploadRuleModelFile(@RequestParam String username,
			@RequestParam(value = "qrFile", required = false) MultipartFile file) throws Exception {						
		ResponseEntityDto<?> ret;
		try{					
			String fileName = file.getOriginalFilename();			
			EngineAssert.checkArgument(StringUtils.endsWith(fileName, ".qr"), "请上传.qr文件");			
			byte[] bytes = IOUtils.toByteArray(file.getInputStream());
			
			EngineFileContent ruleModelData = new EngineFileContentSerialize().unserialize(bytes);
			
			engineWorkspaceManager.uploadRelease(username, ruleModelData);	
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("上传qr包失败",e);
			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		engineUserService.operatorLog(username, "null", "uploadRuleModelFile",ret);
		return ret;
	}		
	/**
	 * http://localhost:8080/ruleModelRelease/backUpAllRuleModelRam
	 * 备份所有规则模型对象
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/backUpAllRuleModelRam", method=RequestMethod.GET)
	@ResponseBody		
	public ResponseEntityDto<?> backUpAllRuleModelRam() throws Exception {						
		ResponseEntityDto<?> ret;
		try{					
			engineRuleModelHotDeployService.backUpRuleModel();	
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("备份所有模型对象失败",e);
			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}		
		return ret;
	}			
	
	/**
	 * 
	 * http://localhost:8080/ruleModelRelease/restoreRuleModelRam
	 * 恢复所有规则模型对象
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/restoreRuleModelRam", method=RequestMethod.GET)
	@ResponseBody		
	public ResponseEntityDto<?> restoreRuleModelRam() throws Exception {						
		ResponseEntityDto<?> ret;
		try{					
			engineRuleModelHotDeployService.restoreRuleModel();	
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("备份所有模型对象失败",e);
			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}		
		return ret;
	}		

	/**
	 * 
	 * http://localhost:8080/ruleModelRelease/currentRelease
	 * 当前版本重新发布，由于版本迭代如果改到核心代码，则用于上线时候用
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/currentRelease", method=RequestMethod.GET)
	@ResponseBody		
	public ResponseEntityDto<?> currentRelease() throws Exception {						
		ResponseEntityDto<?> ret;
		try{					
			engineWorkspaceManager.currentRelease();	
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("当前版本重新发布失败",e);
			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}		
		return ret;
	}			
	
	/**
	 * 
	 * http://localhost:8080/ruleModelRelease/fatTest
	 * username=liangxiaojing&productType=leadDemo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/fatTest", method=RequestMethod.POST)
	@ResponseBody		
	public ResponseEntityDto<?> fatTest(@RequestParam String username,@RequestParam String productType) throws Exception {						
		ResponseEntityDto<?> ret;
		try{					
			engineWorkspaceManager.fatTest(productType,username);	
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("触发FAT环境测试失败",e);
			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}		
		return ret;
	}			
	
	
	
	
//	@RequestMapping(value="/uploadSdsSer2Test", method=RequestMethod.POST)
//	@ResponseBody	
//	@Deprecated
//	public ResponseEntityDto<?> uploadSdsSer2Test(@RequestParam String username,@RequestParam String productType,
//			@RequestParam(value = "serFile", required = false) MultipartFile file) throws Exception {						
//		ResponseEntityDto<?> ret;
//		try{					
//			String fileName = file.getOriginalFilename();			
//			EngineAssert.checkArgument(StringUtils.endsWith(fileName, ".ser"), "请上传.ser文件");			
//			byte[] bytes = IOUtil.toByteArray(file.getInputStream());
//			
//			EngineFileContent efc = new EngineFileContent();
//			efc.setBytes(bytes);
//			efc.setFileName(fileName);
//						
//			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,null);
//		}catch(Exception e){
//			logger.error("上传ser包失败",e);
//			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
//		}
//		engineUserService.operatorLog(username, "null", "uploadSdsSer",ret);
//		return ret;
//	}				
//	
//	@RequestMapping(value="/postSdsSer2Test", method=RequestMethod.POST)
//	@ResponseBody	
//	@Deprecated
//	public ResponseEntityDto<?> postSdsSer2Test(@RequestParam String username,
//			@RequestParam String productType,
//			@RequestParam String serFileName,
//			@RequestParam String serFileContent) throws Exception {						
//		ResponseEntityDto<?> ret;
//		try{								
//			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
//			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
//			EngineAssert.checkNotBlank(serFileName,"上送参数serFileName不可为空");
//			EngineAssert.checkNotBlank(serFileContent,"上送参数serFileContent不可为空");				
//			EngineAssert.checkArgument(StringUtils.endsWith(serFileName, ".ser"), "请上传.ser文件");
//			
//			byte[] bytes = serFileContent.getBytes("UTF-8");
//			
//			EngineFileContent efc = new EngineFileContent();
//			efc.setBytes(bytes);
//			efc.setFileName(serFileName);
//			
//			
//			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,null);
//		}catch(Exception e){
//			logger.error("上传ser包失败",e);
//			ret = new ResponseEntityDto<Object>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
//		}
//		engineUserService.operatorLog(username, "null", "uploadSdsSer",ret);
//		return ret;
//	}		
	
	/**
	 * @param username
	 * @param modelType
	 * @return
	 */
	@RequestMapping(value = "/getRuleZipBytes", method = RequestMethod.GET, produces = "application/octet-stream")
	@ResponseBody
	public byte[] getRuleZipBytes() {
		try{
			byte[] fileBytes= engineWorkspaceManager.getRuleZipBytes();			
			logger.info("导出zip内容大小:{}",fileBytes.length);			
			return fileBytes;	
		}catch(Exception e){
			logger.error("zip规则文件导出失败",e);
		}
					
		return null;
	}		
	
	/**
	 * 
	 * @param username
	 * @param file 文件内容
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/zipFileUpload", method=RequestMethod.POST)
	@ResponseBody		
	public ResponseEntityDto<Boolean> zipFileUpload(@RequestParam String username,
			@RequestParam(value = "mrFile", required = false) MultipartFile file) throws Exception {						
		ResponseEntityDto<Boolean> ret;
		try{					

			String fileName = file.getOriginalFilename();			
			EngineAssert.checkArgument(StringUtils.endsWith(fileName, ".zip"), "请上传.zip文件");			
			byte[] bytes = IOUtils.toByteArray(file.getInputStream());
			logger.info("zip文件大小:{}",file.getBytes().length);		
			engineWorkspaceManager.zipFileUpload(file);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		
		}catch(Exception e){
			logger.error("上传mr包失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		
		engineUserService.operatorLog(username, "null", "zipFileUpload",ret);
		return ret;
	}		
	
	
	/**
	 * 
	 * @param username
	 * @param file 文件内容
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/deployAllTestZk", method=RequestMethod.POST)
	@ResponseBody		
	public ResponseEntityDto<Boolean> deployAllTestZk(@RequestParam String username) throws Exception {	
		ResponseEntityDto<Boolean> ret;
		try{					
			List<EngineFileContent> ruleContentList=engineRuleModelHotDeployService.getAllRuleModelBytes();
			engineWorkspaceManager.deployRuleModel2Test(ruleContentList);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		
		}catch(Exception e){
			logger.error("规则同步测试环境失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		
		engineUserService.operatorLog(username, "null", "deployAllTestZk",ret);
		return ret;
	}
	
	/**
	 * 
	 * @param username
	 * @param file 文件内容
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/deployTestZkByAddress", method=RequestMethod.POST)
	@ResponseBody		
	public ResponseEntityDto<Boolean> deployTestZkByAddress(@RequestParam String username,@RequestParam String zkAddress) throws Exception {	
		ResponseEntityDto<Boolean> ret;
		try{					
			engineWorkspaceManager.deployRuleModel2Test(zkAddress);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		
		}catch(Exception e){
			logger.error("规则同步测试环境失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		
		engineUserService.operatorLog(username, "null", "deployTestZkByAddress",ret);
		return ret;
	}
	
	/**
	 * 
	 * @param username
	 * @param file 文件内容
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/checkZkAddress", method=RequestMethod.GET)
	@ResponseBody		
	public ResponseEntityDto<Boolean> checkZkAddress(@RequestParam String zkAddress) throws Exception {	
		ResponseEntityDto<Boolean> ret;
		try{					
			engineTestRuleHotDeployService.checkZkAddress(zkAddress);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		
		}catch(Exception e){
			logger.error("测试zk地址联通性失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		
		return ret;
	}
}
