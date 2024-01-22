package com.mobanker.engine.framkwork.cpnt.baseinfo;

import java.io.Serializable;

/**
 * 组件接口，注意所有继承该线的类中的logger必须定义成final static，否则反序列化会有问题
 * <p>Company: mobanker.com</p>
 * @author taojinn
 * @date 2016年9月11日
 * @version 1.0
 */
public interface EngineCpnt extends Serializable{
	public String id();
	public String name();
	public String right();
	public String updatetime();
	public String type();
}
