package com.mobanker.engine.framkwork.exception;

/**
 * 
 * <p>Title: EngineQueryException.java<／p>
 * <p>Description: <／p>
 * <p>Company: mobanker.com<／p>
 * @author taojinn
 * @date 2016年2月23日
 * @version 1.0
 */
public class EngineQueryException extends EngineException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3658216696054712345L;

	 /**
     * Error message.
     */
    public String message;
    
    public EngineQueryException(){}
    
    public EngineQueryException(String message){
    	super(message);
    }
    
    public EngineQueryException(String message,Throwable e) {
    	super(message,e);
    }
    
    public EngineQueryException(Throwable e) {
    	super(e);
    }
}
