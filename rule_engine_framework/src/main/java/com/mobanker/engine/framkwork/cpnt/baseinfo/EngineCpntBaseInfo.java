package com.mobanker.engine.framkwork.cpnt.baseinfo;


/**
 * 组件基本信息
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月1日
 * @version 1.0
 */
public abstract class EngineCpntBaseInfo implements EngineCpnt{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String id;
	protected String name = "未定义";
	protected String updatetime;
	protected String right;
	
	@Override
	public String name(){
		return name;
	}
	
	@Override
	public String right(){
		return right;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public String updatetime() {
		return updatetime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public String getRight() {
		return right;
	}

	public void setRight(String right) {
		this.right = right;
	}
	
	
	
}
