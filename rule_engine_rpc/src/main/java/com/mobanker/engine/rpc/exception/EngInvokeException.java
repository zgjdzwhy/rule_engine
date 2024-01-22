package com.mobanker.engine.rpc.exception;



public class EngInvokeException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3658216696054712345L;

	 /**
     * Error message.
     */
    public String message;
    
    public EngInvokeException(){}
    
    public EngInvokeException(String message){
    	super(message);
    }
    
    public EngInvokeException(String message,Throwable e) {
    	super(message,e);
    }
    
    public EngInvokeException(Throwable e) {
    	super(e);
    }
}
