package com.mobanker.engine.framkwork.exception;



public class EngineConditionException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3658216696054712345L;

	 /**
     * Error message.
     */
    public String message;
    
    public EngineConditionException(){}
    
    public EngineConditionException(String message){
    	super(message);
    }
    
    public EngineConditionException(String message,Throwable e) {
    	super(message,e);
    }
    
    public EngineConditionException(Throwable e) {
    	super(e);
    }
}
