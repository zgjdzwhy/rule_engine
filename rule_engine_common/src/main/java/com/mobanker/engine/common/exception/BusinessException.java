package com.mobanker.engine.common.exception;



public class BusinessException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3658216696054712345L;

	 /**
     * Error message.
     */
    public String message;
    
    public BusinessException(){}
    
    public BusinessException(String message){
    	super(message);
    }
    
    public BusinessException(String message,Throwable e) {
    	super(message,e);
    }
    
    public BusinessException(Throwable e) {
    	super(e);
    }
}
