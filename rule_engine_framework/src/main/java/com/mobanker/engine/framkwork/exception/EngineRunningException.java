package com.mobanker.engine.framkwork.exception;

public class EngineRunningException extends EngineException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3658216696054712345L;

	 /**
     * Error message.
     */
    public String message;
    
    public EngineRunningException(){}
    
    public EngineRunningException(String message){
    	super(message);
    }
    
    public EngineRunningException(String message,Throwable e) {
    	super(message,e);
    }
    
    public EngineRunningException(Throwable e) {
    	super(e);
    }
}
