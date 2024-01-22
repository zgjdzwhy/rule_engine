package com.mobanker.engine.framkwork.exception;

public class EngineCpntIsEmptyException extends EngineCpntParseException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3658216696054712345L;

	 /**
     * Error message.
     */
    public String message;
    
    public EngineCpntIsEmptyException(){}
    
    public EngineCpntIsEmptyException(String message){
    	super(message);
    }
    
    public EngineCpntIsEmptyException(String message,Throwable e) {
    	super("组件解析异常，"+message,e);
    }
    
    public EngineCpntIsEmptyException(Throwable e) {
    	super(e);
    }
}
