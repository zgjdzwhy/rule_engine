package com.mobanker.engine.common;

public abstract class EngineConst {
	
	public final static String CURRENT_APP_DESC = "程序当前版本是";
	public final static String CURRENT_APP_VERSION = "4.0.0";
	
	public interface ZK_DIR{
		/**
		 * 规则模拟对象
		 */
		public final static String RULE_MODEL = "/engine/rule_model";
		
		/**
		 * 规则模拟对象备份地址
		 */
		public final static String RULE_MODEL_BAK = "/engine/rule_model_bak";
		
		/**
		 * 规则模拟对象
		 */
		public final static String 	CHECK_CONNECTIVE = "/engine/connective/check_connective";
		
		/**
		 * 任务分配开关 值为true or false
		 */
		@Deprecated
		public final static String TASK_ASSIGN_SWITCH = "/engine/task_assign_switch";
		/**
		 * 任务分配开关 值为true or false
		 */
		public final static String TASK_EXEC_SWITCH = "/engine/task_exec_switch";
		
		/**
		 * 选举node
		 */
		public final static String TASK_LEADER = "/engine/task_leader";
	}
	
	public interface ERROR_CODE{
		public final static String SUCCESS = "00000000";
		public final static String UNKOWN = "84000001";
	} 
	
	public interface RET_STATUS{
		public final static String SUCCESS = "1";
		public final static String FAIL = "0";
	} 
	
	public interface RET_MSG{
		public final static String OK = "调用成功";		
		public final static String FAIL = "调用失败";
	} 
	
	
	public final static String system_open_flag = "system_open_flag"; 
	
	public static final String ROLE_1 = "1";
	public static final String ROLE_1_NAME = "编辑人员";
	
	public static final String ROLE_2 = "2";
	public static final String ROLE_2_NAME = "复核人员";
	
	public static final String ROLE_3 = "3";
	public static final String ROLE_3_NAME = "发布人员";
	
	public static final String ROLE_5 = "5";
	public static final String ROLE_5_NAME = "管理员";
	
	public static final String ROLE_7 = "7";
	public static final String ROLE_7_NAME = "项目总监";
	
	public static final String ROLE_8 = "8";
	public static final String ROLE_8_NAME = "运维人员";
	
	public static final String ROLE_9 = "9";
	public static final String ROLE_9_NAME = "超级管理员";
	
}
