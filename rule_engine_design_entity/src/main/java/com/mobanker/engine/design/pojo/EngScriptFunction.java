package com.mobanker.engine.design.pojo;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Table(name = "rule_script_function")
public class EngScriptFunction extends EngScriptTemplate implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(EngScriptFunction.class);						
	
	private String function;

	/**
	 * 检查基本语法符合函数
	 */
	public void checkBaseSyntax() {
		if(StringUtils.isBlank(this.function))
			throw new RuntimeException("函数主体为空");
		//符合 funciton user_函数名(xxxxx){xxxxxxxxxxxxreturn;}
		String functionStr = "function user_函数名(xxxxx){xxxxxxxxxxxx return}";
		String regex = "^\\s*function\\s+user_\\w+\\(.*\\)\\s*\\{[\\s\\S]*return\\s+\\w+[\\s\\S]*;?\\s*\\}\\s*$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(function);
		boolean b = matcher.matches();
		// 当条件满足时，将返回true，否则返回false
		if(!b){
			logger.warn("函数内容不符合规则,内容:{},表达式:{}",function,regex);
			throw new RuntimeException("函数内容不符合函数规则,规则:"+functionStr);
		}
	}

	/**
	 * 获取函数名 如果解析不出 则返回null
	 * @return
	 */
	public String parseFunctionName() {
		if(StringUtils.isBlank(this.function))
			throw new RuntimeException("函数主体为空");
		
		String regex = "^\\s*function\\s+user_\\w+\\(";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(this.function);
		String functionName = null;
		if(matcher.find()){             
			functionName = matcher.group();       		        
			//去除头和括号
			functionName = StringUtils.replaceOnce(functionName, "function", "");
			functionName = StringUtils.replace(functionName, "(", "");
			//去除空格
			functionName = functionName.replaceAll("\\s", "");
		}
		return functionName;
	}
	
	public String parseTemplate() {
		if(StringUtils.isBlank(this.function))
			throw new RuntimeException("函数主体为空");
		
		String regex = "^\\s*function\\s+user_\\w+\\(.*\\)\\s*\\{";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(this.function);
		String template = null;
		if(matcher.find()){             
			template = matcher.group();       		        
			//去除头和括号
			template = StringUtils.replaceOnce(template, "function", "");
			template = StringUtils.replace(template, "{", "");			
			//去除空格
			template = template.replaceAll("\\s", "");
		}
		return template;
	}	
	
	public static void main(String[] args) {
		
		String aa = "function user_function(xxxx)";
		System.out.println(StringUtils.replaceOnce(aa, "function", ""));
		
	}
	
}
