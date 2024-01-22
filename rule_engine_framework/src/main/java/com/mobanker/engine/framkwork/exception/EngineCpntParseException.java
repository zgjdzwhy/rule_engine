package com.mobanker.engine.framkwork.exception;

public class EngineCpntParseException extends EngineException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3658216696054712345L;

	 /**
     * Error message.
     */
    public String message;
    
    public EngineCpntParseException(){}
    
    public EngineCpntParseException(String message){
    	super(message);
    }
    
    public EngineCpntParseException(String message,Throwable e) {
    	super("组件解析异常，"+message,e);
    }
    
    public EngineCpntParseException(Throwable e) {
    	super(e);
    }
}
