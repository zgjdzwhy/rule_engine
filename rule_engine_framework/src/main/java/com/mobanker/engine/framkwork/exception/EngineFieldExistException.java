package com.mobanker.engine.framkwork.exception;

public class EngineFieldExistException extends EngineException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3658216696054712345L;

	 /**
     * Error message.
     */
    public String message;
    
    public EngineFieldExistException(){}
    
    public EngineFieldExistException(String message){
    	super(message);
    }
    
    public EngineFieldExistException(String message,Throwable e) {
    	super("组件解析异常，"+message,e);
    }
    
    public EngineFieldExistException(Throwable e) {
    	super(e);
    }
}
