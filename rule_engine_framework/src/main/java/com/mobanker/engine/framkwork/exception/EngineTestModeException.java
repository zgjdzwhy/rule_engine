package com.mobanker.engine.framkwork.exception;

/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年10月15日
 * @version 1.0
 */
public class EngineTestModeException extends EngineException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3658216696054712345L;

	 /**
     * Error message.
     */
    public String message;
    
    public EngineTestModeException(){}
    
    public EngineTestModeException(String message){
    	super(message);
    }
    
    public EngineTestModeException(String message,Throwable e) {
    	super(message,e);
    }
    
    public EngineTestModeException(Throwable e) {
    	super(e);
    }
}
