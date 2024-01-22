/*
 * Copyright (c) 2008 Skyon Technology Ltd.
 * All rights reserved.
 *
 * project: skyon-sample
 * create: 2011-4-19
 * cvs: $Id: $
 */
package com.mobanker.engine.framkwork.util;

/**
 * TODO (2011-4-19 下午03:43:51) 请添加 XmlUtil 类的注释。
 * @author maning 
 * @version $Revision: 1.1 $
 */

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 报文解析工具类
 * 
 * @author tiger
 * @alter huangjiang to do : add comment  date:2011-7-12 16:58:46
 */
public class XmlUtil {

	private static final String STR = "String";
	private static final String NUMBER = "Number";

	/**
	 * 将请求报文解析Map对象
	 * @param xml 待解析的请求报文
	 * @return  Map封装成的请求对象
	 * @throws Exception
	 */
	public static Map<String, Map<String, Object>> parseRequestXml(String xml) throws Exception {
		Map<String, Map<String, Object>> map = new LinkedHashMap<String, Map<String, Object>>();
		
		map.put(ConstField.FIELD_REQUEST_CONTROL, parseFieldXml(xml, ConstField.DATAAREA_CONTROL));
		map.put(ConstField.FIELD_REQUEST_INPUTFIELDS, parseFieldXml(xml, ConstField.DATAAREA_INPUT));
		
		return map;
	}
	/**
	 * 将响应报文解析成Map对象
	 * @param xml 待解析的响应报文
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Map<String, Object>> parseResponseXml(String xml) throws Exception {
		Map<String, Map<String, Object>> map = new LinkedHashMap<String, Map<String, Object>>();
		
		map.put(ConstField.FIELD_RESPONSE_CONTROL, parseFieldXml(xml, ConstField.FIELD_RESPONSE_CONTROL));
		map.put(ConstField.FIELD_RESPONSE_DAERROR, parseFieldXml(xml, ConstField.FIELD_RESPONSE_DAERROR));
		map.put(ConstField.FIELD_RESPONSE_OUTPUTFIELDS, parseFieldXml(xml, ConstField.FIELD_RESPONSE_OUTPUTFIELDS));
		
		return map;
	}
	
	/**
	 * 解析DA文件成Map类型
	 * @param xml 待解析DA文件，格式是XML
	 * @param dataAreaName 数据域标识（控制域、输入域）
	 * @return 报文的Map对象表示形式
	 * @throws Exception
	 */
	private static Map<String, Object> parseFieldXml(String xml, String dataAreaName) throws Exception {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		
		if (xml == null) throw new Exception("xml is null !");		
		if (xml.length() == 0) throw new Exception("xml length is zero !");
		if (dataAreaName == null) throw new Exception("dataAreaName is null !");
		if (dataAreaName.length() == 0) throw new Exception("dataAreaName length is zero !");
		
		int strStart = xml.indexOf("<" + dataAreaName + ">");
		if (strStart == -1) throw new Exception("xml have not <" + dataAreaName + "> !");
		int strEnd = xml.indexOf("</"+dataAreaName+">");
		if (strEnd == -1) throw new Exception("xml have not </" + dataAreaName + "> !");
		
		String fieldXml = xml.substring(strStart + dataAreaName.length() + 2 , strEnd);
		String[] fields = fieldXml.split("</"+ConstField.FIELD_FIELD+">");
		for(int i=0; i<fields.length;i++){
			
			int nameStart = fields[i].indexOf(ConstField.FIELD_NAME + "=\"");
			if (nameStart != -1){
				int nameEnd = fields[i].indexOf("\" " + ConstField.FIELD_TYPE + "=\"");//空格
				if (nameEnd == -1) nameEnd = fields[i].indexOf("\">");
				String fieldName = fields[i].substring(nameStart + ConstField.FIELD_NAME.length() +2, nameEnd);
				
				String fieldType = "";
				int typeStart = fields[i].indexOf(ConstField.FIELD_TYPE + "=\"");
				if (typeStart != -1){
					int typeEnd = fields[i].indexOf("\" "+ConstField.FIELD_NAME+"=\"");
					if (typeEnd == -1) typeEnd = fields[i].indexOf("\">");
					fieldType = fields[i].substring(typeStart + ConstField.FIELD_TYPE.length() +2, typeEnd);
				}
				
				int valueStart = fields[i].indexOf("\">");
				String fieldValue = fields[i].substring(valueStart+2);
					
				map.put(fieldName, XmlUtil.cloneObject(fieldValue, fieldType));
			}			
		}		
		return map;
	}
	
	/**
	 * 组装请求报文，并转换成String对象的表现形式
	 * @param map 请求报文的Map对象表现形式
	 * @return  返回String表示的请求报文
	 * @throws Exception
	 */
	public static String createRequestXml(Map<String, Map<String, Object>> map) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<"+ConstField.FIELD_REQUEST_SERVICE+">");
		sb.append("<"+ConstField.FIELD_REQUEST_CONTROL+">"+createFieldXml(map.get(ConstField.FIELD_REQUEST_CONTROL)) + "</"+ConstField.FIELD_REQUEST_CONTROL+">");
		sb.append("<"+ConstField.FIELD_REQUEST_INPUTFIELDS+">"+createFieldXml(map.get(ConstField.FIELD_REQUEST_INPUTFIELDS)) + "</"+ConstField.FIELD_REQUEST_INPUTFIELDS+">");
		sb.append("</"+ConstField.FIELD_REQUEST_SERVICE+">");
		return sb.toString();
	}	
	
	/**
	 * 生成完整xml形式
	 */
	public static String createResponseXml(Map<String, Map<String, Object>> map) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<"+ConstField.FIELD_REQUEST_SERVICE+">");
		sb.append("<"+ConstField.FIELD_RESPONSE_CONTROL+">"+createFieldXml(map.get(ConstField.FIELD_RESPONSE_CONTROL)) + "</"+ConstField.FIELD_RESPONSE_CONTROL+">");
		sb.append("<"+ConstField.FIELD_RESPONSE_DAERROR+">"+createFieldXml(map.get(ConstField.FIELD_RESPONSE_DAERROR)) + "</"+ConstField.FIELD_RESPONSE_DAERROR+">");
		sb.append("<"+ConstField.FIELD_RESPONSE_OUTPUTFIELDS+">"+createFieldXml(map.get(ConstField.FIELD_RESPONSE_OUTPUTFIELDS)) + "</"+ConstField.FIELD_RESPONSE_OUTPUTFIELDS+">");
		sb.append("</"+ConstField.FIELD_REQUEST_SERVICE+">");
		return sb.toString();
	}
	
	/**
	 * 解析请求报文，将Map对象形式转换成String的形式
	 * @param map 请求报文Map对象的表示形式
	 * @return 请求报文String对象的表示形式
	 * @throws Exception
	 */
	private static String createFieldXml(Map<String, Object> map) throws Exception {
		StringBuffer sb = new StringBuffer();
		if(map != null){
			Set<String> set = map.keySet();
			for(String fieldName : set){
				Object value = map.get(fieldName);
				String fieldType = "";
				String fieldValue = "";
				
				if (value != null){
					if (String.class.getName().equals(value.getClass().getName())){
						fieldType = STR;
					}else{
						fieldType = NUMBER;
					}
					
					if (BigDecimal.class.getName().equals(value.getClass().getName())) {
						fieldValue = ((BigDecimal) value).toPlainString();
					} else{
						fieldValue = value.toString();
					}					
				}
				sb.append("<"+ConstField.FIELD_FIELD+" "+ConstField.FIELD_NAME+"=\"" + fieldName + "\" "+ConstField.FIELD_TYPE+"=\"" + fieldType + "\">" + fieldValue + "</"+ConstField.FIELD_FIELD+">");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 根据value和type生成对象，如果是数字类型则转换成BigDecimal
	 * @param value 数据域中<field>节点的值
	 * @param type  数据域中<field>节点的type表示类型
	 * @return 返回value封装成type类型对象
	 */
	public static Object cloneObject(Object value, String type){
		Object cloneObject = null;
		if (value != null) {
			if (BigDecimal.class.getName().equals(type)) {
				cloneObject = new BigDecimal(validNumber(value.toString()));
			} else if (BigInteger.class.getName().equals(type)) {
				cloneObject = new BigDecimal(validNumber(value.toString()));
			} else if (Byte.class.getName().equals(type)) {
				cloneObject = new BigDecimal(validNumber(value.toString()));
			} else if (Double.class.getName().equals(type)) {
				cloneObject = new BigDecimal(validNumber(value.toString()));
			} else if (Float.class.getName().equals(type)) {
				cloneObject = new BigDecimal(validNumber(value.toString()));
			}else if (Integer.class.getName().equals(type)) {
				cloneObject = new BigDecimal(validNumber(value.toString()));
			}else if (Long.class.getName().equals(type)) {
				cloneObject = new BigDecimal(validNumber(value.toString()));
			}else if (Short.class.getName().equals(type)) {
				cloneObject = new BigDecimal(validNumber(value.toString()));
			}else if (NUMBER.equals(type)) {
				cloneObject = new BigDecimal(validNumber(value.toString()));
			}else{
				cloneObject = value.toString();
			}
		}
		return cloneObject;
	}
	
	/**
	 * 验证节点字符数据的是否为空，如果是，则用0替代
	 * @param value 节点字符数据
	 * @return
	 */
	public static String validNumber(String value){
		if (value == null) value = "0";
		if (value.length() == 0) value = "0";
		
		return value;
	}	
}
