package com.mobanker.engine.framkwork.exception;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月21日
 * @version 1.0
 */
public class EngineAssert {
	/**
	 * @see com.google.common.base.Preconditions
	 * @param expression
	 * @param errorMessage
	 */
	public static void checkArgument(boolean expression,Object errorMessage) {
		try{
			Preconditions.checkArgument(expression, errorMessage);
		}catch(IllegalArgumentException e){
			throw new EngineException(e.getMessage(),e);
		}   
	}
	
	/**
	 * @see com.google.common.base.Preconditions
	 * @param expression
	 * @param errorMessageTemplate
	 * @param errorMessageArgs
	 */
	public static void checkArgument(boolean expression,
		      String errorMessageTemplate,
		      Object... errorMessageArgs) {
		try{
			Preconditions.checkArgument(expression, errorMessageTemplate, errorMessageArgs);
		}catch(IllegalArgumentException e){
			throw new EngineException(e.getMessage(),e);
		}
	}

	/**
	 * @see com.google.common.base.Preconditions
	 * @param reference
	 * @param errorMessage
	 * @return
	 */
	public static <T> T checkNotNull(T reference) {
		try{
			return Preconditions.checkNotNull(reference);
		}catch(NullPointerException e){
			throw new EngineException(e.getMessage(),e);
		}
	}		
	
	
	/**
	 * @see com.google.common.base.Preconditions
	 * @param reference
	 * @param errorMessage
	 * @return
	 */
	public static <T> T checkNotNull(T reference, Object errorMessage) {
		try{
			return Preconditions.checkNotNull(reference, errorMessage);
		}catch(NullPointerException e){
			throw new EngineException(e.getMessage(),e);
		}
	}	
	

	
	/**
	 * @see com.google.common.base.Preconditions
	 * @param reference
	 * @param errorMessageTemplate
	 * @param errorMessageArgs
	 * @return
	 */
	public static <T> T checkNotNull(T reference,String errorMessageTemplate,Object... errorMessageArgs) {
		try{
			return Preconditions.checkNotNull(reference, errorMessageTemplate,errorMessageArgs);
		}catch(NullPointerException e){
			throw new EngineException(e.getMessage(),e);
		}
	}
	
	/**
	 * @see com.google.common.base.Preconditions
	 * @param reference
	 * @param errorMessage
	 * @return
	 */
	public static <T> T checkNotBlank(T reference, Object errorMessage) {
		try{
			T t = Preconditions.checkNotNull(reference, errorMessage);
			if(StringUtils.isBlank(t.toString())){				
				throw new EngineException(String.valueOf(errorMessage));
			}				
			return t;			
		}catch(NullPointerException e){
			throw new EngineException(e.getMessage(),e);
		}
	}	
		
	
	/**
	 * @see com.google.common.base.Preconditions
	 * @param reference
	 * @param errorMessage
	 * @return
	 */
	public static <T> T checkNotBlank(T reference) {
		try{
			T t = Preconditions.checkNotNull(reference);
			if(StringUtils.isBlank(t.toString())){				
				throw new EngineException("object is null!");
			}				
			return t;			
		}catch(NullPointerException e){
			throw new EngineException(e.getMessage(),e);
		}
	}	
	
	
	/**
	 * @see com.google.common.base.Preconditions
	 * @param reference
	 * @param errorMessageTemplate
	 * @param errorMessageArgs
	 * @return
	 */
	public static <T> T checkNotBlank(T reference,String errorMessageTemplate,Object... errorMessageArgs) {
		try{
			T t = Preconditions.checkNotNull(reference, errorMessageTemplate,errorMessageArgs);
			if(StringUtils.isBlank(t.toString()))
				Preconditions.checkNotNull(null, errorMessageTemplate,errorMessageArgs);
			return t;
		}catch(NullPointerException e){
			throw new EngineException(e.getMessage(),e);
		}
	}	

	public static void wrapException(Exception e,String errorMessageTemplate,Object... errorMessageArgs) throws EngineException{
		throw new EngineException(format(errorMessageTemplate,errorMessageArgs),e);
		
	}

	
	
	/**
	 * 
	 * 
	 * @param template
	 * @param args
	 * @return
	 */
	private static String format(String template, Object... args) {
		template = String.valueOf(template); // null -> "null"

		// start substituting the arguments into the '%s' placeholders
		StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
		int templateStart = 0;
		int i = 0;
		while (i < args.length) {
			int placeholderStart = template.indexOf("%s", templateStart);
			if (placeholderStart == -1) {
				break;
			}
			builder.append(template.substring(templateStart, placeholderStart));
			builder.append(args[i++]);
			templateStart = placeholderStart + 2;
		}
		builder.append(template.substring(templateStart));

		// if we run out of placeholders, append the extra args in square braces
		if (i < args.length) {
			builder.append(" [");
			builder.append(args[i++]);
			while (i < args.length) {
				builder.append(", ");
				builder.append(args[i++]);
			}
			builder.append(']');
		}

		return builder.toString();
	}

	
	public static boolean checkInParams(Object param,Object... checkParams) {
		for(Object checkParam : checkParams){
			if(param.equals(checkParam)) return true;
		}
		return false;
	}	
	
}
