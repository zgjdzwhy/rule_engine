package com.mobanker.engine.framkwork.script.syntax;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import pl.gildur.jshint4j.Error;
import pl.gildur.jshint4j.JsHint;

import com.alibaba.fastjson.JSONObject;

/**
 * javascript语法校验器
 * <p>
 * Company: mobanker.com
 * </p>
 * 
 * @author taojinn
 * @date 2016年10月2日
 * @version 1.0
 */
public class EngineJavaScriptSyntaxCheck {

	private static EngineJavaScriptSyntaxCheck instance = new EngineJavaScriptSyntaxCheck();

	private EngineJavaScriptSyntaxCheck() {
	}

	public static EngineJavaScriptSyntaxCheck getInstance() {
		return instance;
	}

	public List<Error> checkSyntax(String script) {		
		JsHint hs = new JsHint();
		JSONObject json = new JSONObject();
		json.put("freeze", true);//这个选项禁止重写原生对象的原型列如 Array, Date等等
		json.put("-W041", false);//排除 Use '===' to compare with '0'
		//json.put("unused", true);//变量定义未使用
		List<Error> list = hs.lint(script, json.toJSONString());		
		return list;
	}
	


	
	
	
	
	public static void main(String[] args) throws IOException {		
		String file = "F:\\test.js";
		FileReader reader = new FileReader(file);
		
		//System.out.println(EngineJavaScriptSyntaxCheck.getInstance().checkSyntaxContent(readerToString(reader)));
		
		JsHint hs = new JsHint();
		JSONObject json = new JSONObject();
		json.put("freeze", true);//这个选项禁止重写原生对象的原型列如 Array, Date等等
		json.put("-W041", false);		
		//json.put("unused", true);//变量定义未使用
		
		
		
		
		//没有效果
		//json.put("shadow", "inner");//检查变量重复定义 
		
		
		List<Error> list = hs.lint(readerToString(reader), json.toJSONString());
		System.out.println(list.size());
		
		for(Error error : list){
			System.out.println(error.getLine()+"->"+error.getCharacter()+"->"+error.getRaw()+"->"+error.getReason()+"->"+error.getEvidence());
			
		}
		
	}
	
	
	 private static String readerToString(Reader reader) throws IOException {
	        StringBuffer sb = new StringBuffer();
	        int c;
	        while ((c = reader.read()) != -1) {
	            sb.append((char) c);
	        }
	        return sb.toString();
	    }
}
