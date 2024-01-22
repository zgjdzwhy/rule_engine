package com.mobanker.engine.framkwork.exception;

public class EngineParamMissException extends EngineException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3658216696054712345L;

	 /**
     * Error message.
     */
    public String message;
    
    public EngineParamMissException(){}
    
    public EngineParamMissException(String message){
    	super(message);
    }
    
    public EngineParamMissException(String message,Throwable e) {
    	super("调用决策引擎异常，必要参数缺失，"+message,e);
    }
    
    public EngineParamMissException(Throwable e) {
    	super(e);
    }
}
