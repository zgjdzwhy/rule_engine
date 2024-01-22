package com.mobanker.engine.framkwork.exception;



public class EngineScriptException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3658216696054712345L;

	 /**
     * Error message.
     */
    public String message;
    
    public EngineScriptException(){}
    
    public EngineScriptException(String message){
    	super(message);
    }
    
    public EngineScriptException(String message,Throwable e) {
    	super(message,e);
    }
    
    public EngineScriptException(Throwable e) {
    	super(e);
    }
}
