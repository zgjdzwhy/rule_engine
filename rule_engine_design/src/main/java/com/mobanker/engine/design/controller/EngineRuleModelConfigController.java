package com.mobanker.engine.design.controller;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.common.EngineConst;
import com.mobanker.engine.design.busi.configma.EngineConfigManageService;
import com.mobanker.engine.design.busi.user.EngineUserService;
import com.mobanker.engine.design.busi.workspace.EngineWorkspaceManagerImpl;
import com.mobanker.engine.design.dto.EngineDefineFieldDto;
import com.mobanker.engine.design.dto.EngineGroupDto;
import com.mobanker.engine.design.dto.EngineMemberDto;
import com.mobanker.engine.design.dto.EngineProductDto;
import com.mobanker.engine.design.dto.EngineTestZkDto;
import com.mobanker.engine.design.dto.EngineUserInfoDto;
import com.mobanker.engine.design.dto.EngineUserListQco;
import com.mobanker.engine.design.pojo.EngFatTest;
import com.mobanker.engine.design.pojo.EngScriptFunction;
import com.mobanker.engine.design.pojo.EngScriptTemplate;
import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.framework.contract.dto.ResponseEntityDto;

/**
 * 规则模型配置组件管理
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月4日
 * @version 1.0
 */
@Controller
@RequestMapping("/ruleModelConfig")
public class EngineRuleModelConfigController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());	

	private final static Map<String,Object> FieldTypeEm = new HashMap<String,Object>();
	static{
		FieldTypeEm.put(String.class.getSimpleName(), 1);
		FieldTypeEm.put(Long.class.getSimpleName(), 1);
		FieldTypeEm.put(BigDecimal.class.getSimpleName(), 1);
	}
	
	@Autowired
	private EngineWorkspaceManagerImpl engineWorkspaceManager;
	
	@Autowired
	private EngineConfigManageService engineConfigManageService;
	
	@Autowired
	private EngineUserService engineUserService;
	
	/**
	 * 获取引擎参数列表
	 * http://localhost:8080/ruleModelConfig/getEngineParamList?username=taojin&productType=shoujidai
	 * @param username
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/getEngineParamList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineDefineFieldDto>> getEngineParamList(@RequestParam String username,@RequestParam String productType) {
		ResponseEntityDto<List<EngineDefineFieldDto>> ret= new ResponseEntityDto<List<EngineDefineFieldDto>>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			List<EngineDefineFieldDto> list = engineConfigManageService.getEngineParamList(username, productType);
			ret = new ResponseEntityDto<List<EngineDefineFieldDto>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,list);		
		}catch(Exception e){
			logger.error("查询输出字段异常",e);
			ret = new ResponseEntityDto<List<EngineDefineFieldDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}	
	
	
	
	/**
	 * http://localhost:8080/ruleModelConfig/getDefineFieldList?username=taojin&productType=shoujidai
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/getDefineFieldList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineDefineFieldDto>> getDefineFieldList(@RequestParam String username,@RequestParam String productType) {
		ResponseEntityDto<List<EngineDefineFieldDto>> ret= new ResponseEntityDto<List<EngineDefineFieldDto>>();
		try{
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			List<EngineDefineFieldDto> list = engineConfigManageService.getDefineFieldList(username, productType);
			ret = new ResponseEntityDto<List<EngineDefineFieldDto>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,list);		
		}catch(Exception e){
			logger.error("查询输出字段异常",e);
			ret = new ResponseEntityDto<List<EngineDefineFieldDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}		
	
	/**
	 * http://localhost:8080/ruleModelConfig/insertDefineField
	 * username=taojin&productType=shoujidai&fieldType=Long&fieldKey=testMy1&fieldName=测试输出1&isArr=0&fieldUse=input&fieldRefValue=??
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/insertDefineField", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> insertDefineField(@RequestParam String username,@RequestParam String productType,@ModelAttribute EngineDefineFieldDto dto) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			engineConfigManageService.insertDefineField(username, productType, dto);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("创建字段异常",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		engineUserService.operatorLog(username, productType, "insertOutputField",ret);
		return ret;
	}		
	
	
	/**
	 * http://localhost:8080/ruleModelConfig/batchInsertDefineField
	 * username=taojin&productType=shoujidai&fieldList=[
	 * 	{'fieldType':'Long','fieldKey':'testMy1','fieldName':'测试输出1','isArr':'0','fieldUse':'input','fieldRefValue':'中文'},
	 * 	{'fieldType':'Long','fieldKey':'testMy2','fieldName':'测试输出2','isArr':'0','fieldUse':'input','fieldRefValue':'中文'},
	 * 	{'fieldType':'Long','fieldKey':'testMy3','fieldName':'测试输出3','isArr':'0','fieldUse':'input','fieldRefValue':'中文'}
	 * ]
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/batchInsertDefineField", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> batchInsertDefineField(@RequestParam String username,@RequestParam String productType,@RequestParam String fieldList) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			List<EngineDefineFieldDto> dtoList = JSONObject.parseArray(fieldList, EngineDefineFieldDto.class);
			for(EngineDefineFieldDto dto : dtoList){
				engineConfigManageService.insertDefineField(username, productType, dto);
			}						
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("创建字段异常",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		engineUserService.operatorLog(username, productType, "insertOutputField",ret);
		return ret;
	}	
	
	
	
	
	/**
	 * http://localhost:8080/ruleModelConfig/importDefineField
	 * username=taojin&productType=shoujidai&excel=file
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/importDefineField", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntityDto<Boolean> importDefineField(@RequestParam String username,@RequestParam String productType,
			@RequestParam(value = "excel", required = false) MultipartFile file) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{			
			List<EngineDefineFieldDto> dtoList = new LinkedList<EngineDefineFieldDto>();
			//文件转换
			InputStream ins = file.getInputStream();
	        @SuppressWarnings("resource")
			XSSFWorkbook hssfWorkbook = new XSSFWorkbook(ins);
	        XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
	        //从第二行开始，第一行是title
	        for(int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++){
	        	XSSFRow hssfRow = hssfSheet.getRow(rowNum);
	        	int col = 0;
	        	if(hssfRow == null) continue;  
	        	
	        	//原始额度值	src_limit	Long	否	1000	input	
	        	String fieldName = getCellVal(hssfRow.getCell(col++));
	        	if(StringUtils.isBlank(fieldName)){
	        		logger.warn("发现空行,行数:{}",rowNum);	        
	        		continue;
	        	}
	        	
	        	EngineDefineFieldDto dto = new EngineDefineFieldDto();
	        	String fieldKey = getCellVal(hssfRow.getCell(col++));
	        	EngineAssert.checkNotBlank(fieldKey, "格式不正确,fieldKey不存在");
	        	
	        	String fieldType = getCellVal(hssfRow.getCell(col++));
	        	
	        	EngineAssert.checkNotNull(FieldTypeEm.get(fieldType),"上送的参数类型不正确，类型:%s",fieldType);
	        	
	        	String isArr = getCellVal(hssfRow.getCell(col++));
	        	EngineAssert.checkNotBlank(isArr, "格式不正确,isArr不存在");	       
	        	
	        	if("是".equals(isArr.trim())){
	        		isArr = "1";	
	        	}else if("否".equals(isArr.trim())){
	        		isArr = "0";
	        	}else
	        		throw new EngineException("格式不正确,isArr内容错误"+isArr);
	        	
	        	String fieldRefValue = getCellVal(hssfRow.getCell(col++));
	        	String fieldUse = getCellVal(hssfRow.getCell(col++));
	        	fieldUse = fieldUse.trim();
	        	if("input".equals(fieldUse)){	        		
	        	}else if("output".equals(fieldUse)){	        		
	        	}else
	        		throw new EngineException("格式不正确,使用方面请用input或者output");
	        	
	        	dto.setFieldKey(fieldKey);
	        	dto.setFieldName(fieldName);
	        	dto.setFieldRefValue(fieldRefValue);
	        	dto.setFieldType(fieldType);
	        	dto.setFieldUse(fieldUse);
	        	dto.setIsArr(isArr);
	        	dto.setUsername(username);
	        	dtoList.add(dto);
	        }						
	        logger.info("转换字段成功，转换数量:{}",dtoList.size());	        
			engineConfigManageService.importDefineField(username, productType, dtoList);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,"一共导入"+dtoList.size()+"条数据",true);		
		}catch(Exception e){
			logger.error("倒入字段异常",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		engineUserService.operatorLog(username, productType, "importDefineField",ret);
		return ret;
	}			

	private String getCellVal(Cell cell){
		if(cell==null) return "";
		String value="";
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_BLANK:
			break;
		case Cell.CELL_TYPE_NUMERIC:			
			Object inputValue = null;// 单元格值
	        Long longVal = Math.round(cell.getNumericCellValue());
	        Double doubleVal = cell.getNumericCellValue();
	        if(Double.parseDouble(longVal + ".0") == doubleVal){   //判断是否含有小数位.0
	        	inputValue = longVal;
	        }
	        else{
	        	inputValue = doubleVal;
	        }	  
	        value = inputValue.toString();
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			value = cell.getBooleanCellValue()+"";
			break;
		default:
			break;
		}
		return value.trim();
		
	}
	
	/**
	 * http://localhost:8080/ruleModelConfig/importDefineField
	 * username=taojin&productType=shoujidai
	 * @param username
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/exportDefineField",  method = RequestMethod.GET, produces = "application/octet-stream")
	@ResponseBody
	public byte[] exportDefineField(@RequestParam String username,@RequestParam String productType,HttpServletResponse response) {
//		ResponseEntityDto<byte[]> ret= new ResponseEntityDto<byte[]>();  
		OutputStream fOut = null;  
		byte[] bfile={};
	     try  
	     {  
	    	 fOut=response.getOutputStream();
	         // 产生工作簿对象  
	         XSSFWorkbook workbook = new XSSFWorkbook();  
	         //产生工作表对象  
	         XSSFSheet sheet = workbook.createSheet();  
	         //获取模块字段
	         List<EngineDefineFieldDto> list = engineConfigManageService.getDefineFieldList(username, productType);  
	         
	         XSSFRow row = sheet.createRow(0);//创建一行  
	         XSSFCell cell = row.createCell((int)0);//创建一列  
             cell.setCellType(CellType.STRING.getCode());  
             cell.setCellValue("名称");  
             cell = row.createCell(1);//创建一列  
             cell.setCellType(CellType.STRING.getCode());  
             cell.setCellValue("字段");  
             cell = row.createCell(2);//创建一列  
             cell.setCellType(CellType.STRING.getCode());  
             cell.setCellValue("类型");  
             cell = row.createCell(3);//创建一列  
             cell.setCellType(CellType.STRING.getCode());  
             cell.setCellValue("是否数组");  
             cell = row.createCell(4);//创建一列  
             cell.setCellType(CellType.STRING.getCode());  
             cell.setCellValue("参考值");  
             cell = row.createCell(5);//创建一列  
             cell.setCellType(CellType.STRING.getCode());  
             cell.setCellValue("使用方式");  
             cell = row.createCell(6);//创建一列  
             cell.setCellType(CellType.STRING.getCode());  
             cell.setCellValue("备注");
        	 
	         //构造表格
	         for (int i = 0; i < list.size(); i++)  
	         {  	    
	        	 EngineDefineFieldDto dto=list.get(i);

	        	 row = sheet.createRow(i+1);//创建一行  
	        	 cell = row.createCell(0);//创建一列  
	             cell.setCellType(CellType.STRING.getCode());  
	             cell.setCellValue(dto.getFieldName());  
	        	 cell = row.createCell(1);//创建一列  
	             cell.setCellType(CellType.STRING.getCode());  
	             cell.setCellValue(dto.getFieldKey());  
	        	 cell = row.createCell(2);//创建一列  
	             cell.setCellType(CellType.STRING.getCode());  
	             cell.setCellValue(dto.getFieldType());  
	        	 cell = row.createCell(3);//创建一列  
	             cell.setCellType(CellType.STRING.getCode()); 
	             if("1".equals(dto.getIsArr())){
	            	 cell.setCellValue("是");  
	             }else if("0".equals(dto.getIsArr())){
	            	 cell.setCellValue("否");  
	             }
	        	 cell = row.createCell(4);//创建一列  
	             cell.setCellType(CellType.STRING.getCode());  
	             cell.setCellValue(dto.getFieldRefValue());  
	        	 cell = row.createCell(5);//创建一列  
	             cell.setCellType(CellType.STRING.getCode());  
	             cell.setCellValue(dto.getFieldUse());  
	        	 
	         }    
	         //表格对象转换成流
	         workbook.write(fOut);  
	         //转换成byte
	         fOut.write(bfile);
//	         ret = new ResponseEntityDto<byte[]>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,bfile);
	     }  
	     catch (Exception e)  
	     {
	    	 logger.error("导出字段异常",e);
	     }  
	     finally  
	     {  
	         try  
	         {  
	             fOut.flush();  
	             fOut.close();  
	         }  
	         catch (IOException e)  
	         {}  
	         
	     } 
	     
	     return bfile;
	}
	
	/**
	 * http://localhost:8080/ruleModelConfig/updateDefineField
	 * username=taojin&productType=shoujidai&id=1&fieldType=Long&fieldKey=testMy2&fieldName=测试输出1&isArr=0&fieldUse=input
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/updateDefineField", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> updateDefineField(@RequestParam String username,@RequestParam String productType,@ModelAttribute EngineDefineFieldDto dto) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			engineConfigManageService.updateDefineField(username, productType, dto);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("更新字段异常",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		engineUserService.operatorLog(username, productType, "updateOutputField",ret);
		return ret;
	}			
	
	/**
	 * http://localhost:8080/ruleModelConfig/deleteDefineField
	 * username=taojin&productType=shoujidai&id=1&id=1
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/deleteDefineField", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> deleteDefineField(@RequestParam String username,@RequestParam String productType,@RequestParam Long id) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			engineConfigManageService.deleteDefineField(username, productType, id);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("删除字段异常",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		engineUserService.operatorLog(username, productType, "deleteDefineField",ret);
		return ret;
	}	
	
	/**
	 * http://localhost:8080/ruleModelConfig/showScriptTemplate?username=taojin&productType=shoujidai
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/showScriptTemplate", method=RequestMethod.GET)
	@ResponseBody	
	public ResponseEntityDto<List<EngScriptTemplate>> showScriptTemplate(@RequestParam String username,@RequestParam String productType) throws Exception {						
		ResponseEntityDto<List<EngScriptTemplate>> ret;
		try{		
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");			
			List<EngScriptTemplate> list = engineConfigManageService.showScriptTemplate(productType);
			ret = new ResponseEntityDto<List<EngScriptTemplate>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,list);
		}catch(Exception e){
			logger.error("显示脚本模板信息失败",e);
			ret = new ResponseEntityDto<List<EngScriptTemplate>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;
	}	
	
	/**
	 * http://localhost:8080/ruleModelConfig/addScriptTemplate
	 * username=taojin&productType=shoujidai&label=testLabel&name=测试模板&template=aaabbbccc
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/addScriptTemplate", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<Boolean> addScriptTemplate(@ModelAttribute EngScriptTemplate entity) throws Exception {						
		ResponseEntityDto<Boolean> ret;
		try{				
			engineConfigManageService.addScriptTemplate(entity);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);
		}catch(Exception e){
			logger.error("增加脚本模板信息失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;
	}	
	
	/**
	 * http://localhost:8080/ruleModelConfig/modScriptTemplate
	 * username=taojin&productType=shoujidai&label=testLabel&name=测试模板&template=aaabbbccc
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/modScriptTemplate", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<Boolean> modScriptTemplate(@ModelAttribute EngScriptTemplate entity) throws Exception {						
		ResponseEntityDto<Boolean> ret;
		try{				
			engineConfigManageService.modScriptTemplate(entity);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);
		}catch(Exception e){
			logger.error("修改脚本模板信息失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;
	}	
	
	
	/**
	 * http://localhost:8080/ruleModelConfig/delScriptTemplate
	 * username=taojin&productType=shoujidai&label=testLabel
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/delScriptTemplate", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<Boolean> delScriptTemplate(@RequestParam String username,@RequestParam String productType,@RequestParam String label) throws Exception {						
		ResponseEntityDto<Boolean> ret;
		try{				
			engineConfigManageService.delScriptTemplate(username,productType,label);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);
		}catch(Exception e){
			logger.error("删除脚本模板信息失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;
	}	
	
	
	
	/**
	 * http://localhost:8080/ruleModelConfig/showScriptFunction?username=taojin&productType=shoujidai
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/showScriptFunction", method=RequestMethod.GET)
	@ResponseBody	
	public ResponseEntityDto<List<EngScriptFunction>> showScriptFunction(@RequestParam String username,@RequestParam String productType) throws Exception {						
		ResponseEntityDto<List<EngScriptFunction>> ret;
		try{		
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");			
			List<EngScriptFunction> list = engineConfigManageService.showScriptFunction(productType);
			ret = new ResponseEntityDto<List<EngScriptFunction>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,list);
		}catch(Exception e){
			logger.error("显示脚本函数信息失败",e);
			ret = new ResponseEntityDto<List<EngScriptFunction>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;
	}	
	

	/**
	 * http://localhost:8080/ruleModelConfig/showAllFunction?username=taojin&productType=shoujidai
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/showAllFunction", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<List<EngScriptFunction>> showAllFunction(@RequestParam String username,@RequestParam String productType) throws Exception {						
		ResponseEntityDto<List<EngScriptFunction>> ret;
		try{		
			EngineAssert.checkNotBlank(username,"上送参数username不可为空");
			EngineAssert.checkNotBlank(productType,"上送参数productType不可为空");
			List<EngScriptFunction> list = engineConfigManageService.showCommonFunction();
			list.addAll(engineConfigManageService.showScriptFunction(productType));						
			ret = new ResponseEntityDto<List<EngScriptFunction>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,list);
		}catch(Exception e){
			logger.error("显示脚本函数信息失败",e);
			ret = new ResponseEntityDto<List<EngScriptFunction>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}		
		return ret;
	}		
	
	
	/**
	 * http://localhost:8080/ruleModelConfig/addScriptFunction
	 * username=taojin&productType=shoujidai&name=测试模板&template=aaabbbccc&function=function user_abcb(aa){return aa;}
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/addScriptFunction", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<Boolean> addScriptFunction(@ModelAttribute EngScriptFunction entity) throws Exception {						
		ResponseEntityDto<Boolean> ret;
		try{				
			engineConfigManageService.addScriptFunction(entity);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);
		}catch(Exception e){
			logger.error("新增脚本函数信息失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;
	}	
	
	/**
	 * http://localhost:8080/ruleModelConfig/modScriptFunction
	 * username=taojin&productType=shoujidai&label=testLabel&name=测试模板&function=function user_abcb(aa){return aa;}
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/modScriptFunction", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<Boolean> modScriptFunction(@ModelAttribute EngScriptFunction entity) throws Exception {						
		ResponseEntityDto<Boolean> ret;
		try{				
			engineConfigManageService.modScriptFunction(entity);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);
		}catch(Exception e){
			logger.error("修改脚本函数信息失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;
	}	
	
	
	/**
	 * http://localhost:8080/ruleModelConfig/delScriptFunction
	 * username=taojin&productType=shoujidai&label=testLabel
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/delScriptFunction", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<Boolean> delScriptFunction(@RequestParam String username,@RequestParam String productType,@RequestParam String label) throws Exception {						
		ResponseEntityDto<Boolean> ret;
		try{				
			engineConfigManageService.delScriptFunction(username,productType,label);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);
		}catch(Exception e){
			logger.error("删除脚本函数信息失败",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;
	}		
	

	/**
	 * 显示产品线成员列表
	 * http://localhost:8080/ruleModelConfig/showMemberInfo
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/showMemberInfo", method=RequestMethod.GET)
	@ResponseBody	
	public ResponseEntityDto<List<EngineMemberDto>> showMemberInfo() throws Exception {						
		ResponseEntityDto<List<EngineMemberDto>> ret;
		try{							
			ret = new ResponseEntityDto<List<EngineMemberDto>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,
					engineConfigManageService.showMemberInfo());
		}catch(Exception e){
			logger.error("显示成员信息失败",e);
			ret = new ResponseEntityDto<List<EngineMemberDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;		
	}	
	
	/**
	 * 导出规则数据
	 * http://localhost:8080/ruleModelConfig/exportProduct?username=taojin&productType=shoujidai
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
//	@RequestMapping(value="/exportProduct", method=RequestMethod.GET)
//	@ResponseBody	
//	@Deprecated
//	public ResponseEntityDto<byte[]> exportProduct(@RequestParam String username,@RequestParam String productType) throws Exception {						
//		ResponseEntityDto<byte[]> ret;
//		try{							
//			EngineRuleAllConfigDto dto = engineConfigManageService.exportProduct(productType);
//			EngineProductConfigSerialize serilazie = new EngineProductConfigSerialize();
//			
//			byte[] bytes = serilazie.serialize(dto);
//			logger.info("导出产品，产品:{},字节数:{}",productType,bytes.length);
//			ret = new ResponseEntityDto<byte[]>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,
//					bytes);
//		}catch(Exception e){
//			logger.error("导出产品失败",e);
//			ret = new ResponseEntityDto<byte[]>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
//		}
//		
//		return ret;		
//	}	
	
	/**
	 * 导出规则数据
	 * http://localhost:8080/ruleModelConfig/importProduct?
	 * username=taojin&productContent=xxxxxxxxxxxxxxxxxxx&isReplace=true
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
//	@RequestMapping(value="/importProduct", method=RequestMethod.POST)
//	@ResponseBody	
//	@Deprecated
//	public ResponseEntityDto<Boolean> importProduct(@RequestParam String username,
//			@RequestParam String productContent,@RequestParam Boolean isReplace) throws Exception {						
//		ResponseEntityDto<Boolean> ret;
//		try{						
//			EngineProductConfigSerialize serilazie = new EngineProductConfigSerialize();			
//			EngineRuleAllConfigDto dto = serilazie.unserialize(productContent.getBytes());			
//			engineConfigManageService.importProduct(username,dto,isReplace);								
//			logger.info("导入产品成功");
//			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);
//		}catch(Exception e){
//			logger.error("导入产品失败",e);
//			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
//		}
//		
//		return ret;		
//	}			
	
	/**
	 * http://localhost:8080/ruleModelConfig/showAllProduct
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/showAllProduct", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineProductDto>> showAllProduct() {
		ResponseEntityDto<List<EngineProductDto>> ret= new ResponseEntityDto<List<EngineProductDto>>();
		try{
			List<EngineProductDto> list = engineConfigManageService.showAllProduct();
			ret = new ResponseEntityDto<List<EngineProductDto>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,list);		
		}catch(Exception e){
			logger.error("查询产品列表异常",e);
			ret = new ResponseEntityDto<List<EngineProductDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}			
	
	/**
	 * http://localhost:8080/ruleModelConfig/showAllGroup
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/showAllGroup", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngineGroupDto>> showAllGroup() {
		ResponseEntityDto<List<EngineGroupDto>> ret= new ResponseEntityDto<List<EngineGroupDto>>();
		try{
			List<EngineGroupDto> list = engineConfigManageService.showAllGroup();
			ret = new ResponseEntityDto<List<EngineGroupDto>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,list);		
		}catch(Exception e){
			logger.error("查询产品列表异常",e);
			ret = new ResponseEntityDto<List<EngineGroupDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}			
	
	/**
	 * http://localhost:8080/ruleModelConfig/addUserInfo
	 * username=test1&realname=测试
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/addUserInfo", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> addUserInfo(@RequestParam String username,@RequestParam String realname,@RequestParam String role,@RequestParam String productIds,@RequestParam String groupId) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			engineUserService.addUserInfo(username, realname, role, productIds,groupId);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("增加操作员异常",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}			
		
	/**
	 * http://localhost:8080/ruleModelConfig/updateUserInfo
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> updateUserInfo(@RequestParam String username,@RequestParam String realname,@RequestParam String role,@RequestParam String productIds,@RequestParam String groupId) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			engineUserService.updateUserInfo(username, realname, role, productIds,groupId);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("修改操作员异常",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}	
	
	/**
	 * http://localhost:8080/ruleModelConfig/resetUserPswd
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/resetUserPswd", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> resetUserPswd(@RequestParam String username) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			engineUserService.resetUserPswd(username);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("修改密码异常",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}		
	
	
	/**
	 * 
	 * http://localhost:8080/ruleModelConfig/updateUserPswd
	 * username=taojin&oldPassword=123456&newPassword=123456
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/updateUserPswd", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> updateUserPswd(@RequestParam String username,@RequestParam String oldPassword,@RequestParam String newPassword) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			engineUserService.updateUserPswd(username, oldPassword, newPassword);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("修改密码异常",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}		
	
	/**
	 * http://localhost:8080/ruleModelConfig/showUserList?realname=陶金
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/showUserList", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Collection<EngineUserInfoDto>> showUserList(@ModelAttribute EngineUserListQco qco) {
		ResponseEntityDto<Collection<EngineUserInfoDto>> ret= new ResponseEntityDto<Collection<EngineUserInfoDto>>();
		try{			
			ret = new ResponseEntityDto<Collection<EngineUserInfoDto>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,
					EngineConst.RET_MSG.OK,engineUserService.showUserList(qco.getProductType(), qco.getUsername(), qco.getRealname(),qco.getGroupId()));		
		}catch(Exception e){
			logger.error("显示用户列表异常",e);
			ret = new ResponseEntityDto<Collection<EngineUserInfoDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}			
	
	/**
	 * 
	 * http://localhost:8080/ruleModelConfig/configFatTestAddress
	 * id=0&productType=taojinZy&url=123456
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/configFatTestAddress", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<Boolean> configFatTestAddress(@RequestParam Long id,@RequestParam String productType,@RequestParam String url) {
		ResponseEntityDto<Boolean> ret= new ResponseEntityDto<Boolean>();
		try{
			engineConfigManageService.configFatTestAddress(id, productType, url);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);		
		}catch(Exception e){
			logger.error("修改密码异常",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}		
	
	
	/**
	 * http://localhost:8080/ruleModelConfig/showFatTestConfig?productType=taojinZy
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/showFatTestConfig", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<List<EngFatTest>> showFatTestConfig(@RequestParam String productType) {
		ResponseEntityDto<List<EngFatTest>> ret= new ResponseEntityDto<List<EngFatTest>>();
		try{			
			ret = new ResponseEntityDto<List<EngFatTest>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,
					EngineConst.RET_MSG.OK,engineConfigManageService.showFatTestConfig(productType));		
		}catch(Exception e){
			logger.error("显示FAT测试列表异常",e);
			ret = new ResponseEntityDto<List<EngFatTest>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
	
		return ret;
	}	
	
	/**
	 * 新增zk配置信息
	 * http://localhost:8080/ruleModelConfig/insertTestZk
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/insertTestZk", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<Boolean> insertTestZk(@RequestParam String username,@RequestParam String zkName,
			@RequestParam String zkUrl,@RequestParam String memo) throws Exception {						
		ResponseEntityDto<Boolean> ret;
		try{							
			engineConfigManageService.insertTestZk(username, zkName, zkUrl, memo);
			
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);
		}catch(Exception e){
			logger.error("新增zk配置信息",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		
		engineUserService.operatorLog(username, "null", "insertTestZk",ret);
		return ret;		
	}	
	
	/**
	 * 修改zk配置信息
	 * http://localhost:8080/ruleModelConfig/updateTestZk
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/updateTestZk", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<Boolean> updateTestZk(@RequestParam String username,@RequestParam String id,@RequestParam String zkName,
			@RequestParam String zkUrl,@RequestParam String memo,@RequestParam String status) throws Exception {						
		ResponseEntityDto<Boolean> ret;
		try{							
			engineConfigManageService.updateTestZk(id, zkName, zkUrl, status, memo);
			
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);
		}catch(Exception e){
			logger.error("修改zk配置信息",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		
		engineUserService.operatorLog(username, "null", "updateTestZk",ret);
		return ret;		
	}	
	
	/**
	 * 删除zk配置信息
	 * http://localhost:8080/ruleModelConfig/deleteTestZk
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteTestZk", method=RequestMethod.POST)
	@ResponseBody	
	public ResponseEntityDto<Boolean> deleteTestZk(@RequestParam String username,@RequestParam String id) throws Exception {						
		ResponseEntityDto<Boolean> ret;
		try{							
			engineConfigManageService.deleteTestZk(id);
			
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,true);
		}catch(Exception e){
			logger.error("删除zk配置信息",e);
			ret = new ResponseEntityDto<Boolean>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),false);	
		}
		
		engineUserService.operatorLog(username, "null", "deleteTestZk",ret);
		return ret;		
	}	
	
	/**
	 * 查询所有zk配置信息
	 * http://localhost:8080/ruleModelConfig/showAllTestZk
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/showAllTestZk", method=RequestMethod.GET)
	@ResponseBody	
	public ResponseEntityDto<List<EngineTestZkDto>> showAllTestZk() throws Exception {						
		ResponseEntityDto<List<EngineTestZkDto>> ret;
		try{							
			ret = new ResponseEntityDto<List<EngineTestZkDto>>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,engineConfigManageService.showAllTestZk(null));
		}catch(Exception e){
			logger.error("显示成员信息失败",e);
			ret = new ResponseEntityDto<List<EngineTestZkDto>>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;		
	}	
	
	/**
	 * 根据id查询zk配置信息
	 * http://localhost:8080/ruleModelConfig/queryTestZkById
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/queryTestZkById", method=RequestMethod.GET)
	@ResponseBody	
	public ResponseEntityDto<EngineTestZkDto> queryTestZkById(@RequestParam String id) throws Exception {						
		ResponseEntityDto<EngineTestZkDto> ret;
		try{							
			ret = new ResponseEntityDto<EngineTestZkDto>(EngineConst.RET_STATUS.SUCCESS,EngineConst.ERROR_CODE.SUCCESS,EngineConst.RET_MSG.OK,engineConfigManageService.queryTestZkById(id));
		}catch(Exception e){
			logger.error("显示成员信息失败",e);
			ret = new ResponseEntityDto<EngineTestZkDto>(EngineConst.RET_STATUS.FAIL,EngineConst.ERROR_CODE.UNKOWN,e.getMessage(),null);	
		}
		
		return ret;		
	}	
	
}
