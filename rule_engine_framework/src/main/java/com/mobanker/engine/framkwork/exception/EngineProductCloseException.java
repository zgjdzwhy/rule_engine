package com.mobanker.engine.framkwork.exception;

public class EngineProductCloseException extends EngineException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3658216696054712345L;

	 /**
     * Error message.
     */
    public String message;
    
    public EngineProductCloseException(){}
    
    public EngineProductCloseException(String message){
    	super(message);
    }
    
    public EngineProductCloseException(String message,Throwable e) {
    	super(message,e);
    }
    
    public EngineProductCloseException(Throwable e) {
    	super(e);
    }
}
