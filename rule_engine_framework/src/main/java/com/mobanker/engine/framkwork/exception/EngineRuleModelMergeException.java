package com.mobanker.engine.framkwork.exception;

/**
 * 组件合并异常
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月4日
 * @version 1.0
 */
public class EngineRuleModelMergeException extends EngineException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3658216696054712345L;

	 /**
     * Error message.
     */
    public String message;
    
    public EngineRuleModelMergeException(){}
    
    public EngineRuleModelMergeException(String message){
    	super(message);
    }
    
    public EngineRuleModelMergeException(String message,Throwable e) {
    	super("规则模型合并异常，"+message,e);
    }
    
    public EngineRuleModelMergeException(Throwable e) {
    	super(e);
    }
}
