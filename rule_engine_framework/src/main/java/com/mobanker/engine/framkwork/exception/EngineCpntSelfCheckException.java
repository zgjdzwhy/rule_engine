package com.mobanker.engine.framkwork.exception;

public class EngineCpntSelfCheckException extends EngineException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3658216696054712345L;

	 /**
     * Error message.
     */
    public String message;
    
    public EngineCpntSelfCheckException(){}
    
    public EngineCpntSelfCheckException(String message){
    	super(message);
    }
    
    public EngineCpntSelfCheckException(String message,Throwable e) {
    	super("调用决策引擎异常，必要参数缺失，"+message,e);
    }
    
    public EngineCpntSelfCheckException(Throwable e) {
    	super(e);
    }
}
