package com.mobanker.engine.framkwork.exception;



public class EngineException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3658216696054712345L;

	 /**
     * Error message.
     */
    public String message;
    
    public EngineException(){}
    
    public EngineException(String message){
    	super(message);
    }
    
    public EngineException(String message,Throwable e) {
    	super(message,e);
    }
    
    public EngineException(Throwable e) {
    	super(e);
    }
}
