package com.mobanker.engine.design.busi.admin;


/**
 * 
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2017年1月9日
 * @version 1.0
 */
public interface AdminToolService {
	public void ruleModelMongo2Es();
	
	public void initSystemFunction();
	
	public void initSystemFunction(String dir);

	void ruleModelEs2Mysql();

	void functionEs2Hbase();

}
