package com.mobanker.engine.framkwork.util;

/**
 * 常量定义类，为了方便维护，提取通用常量统一管理
 * @author huangjiang add comment
 *
 */
public class ConstField {
	
	public static final String DATAAREA_CONTROL = "Control";
	public static final String DATAAREA_INPUT= "InputFields";
	public static final String DATAAREA_OUTPUT = "OutputFields";
	public static final String DATAAREA_PROCESS = "Process";
	
	public static final String FIELD_NAME = "name";	
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_FIELD = "Field";
	
	public static final String FIELD_REQUEST_SERVICE = "Service";	
	public static final String FIELD_REQUEST_CONTROL = DATAAREA_CONTROL;
	
	public static final String FIELD_REQUEST_CONTROL_ALIAS = "ALIAS";
	public static final String FIELD_REQUEST_CONTROL_SIGNATURE = "SIGNATURE";
	public static final String FIELD_REQUEST_CONTROL_INPUTDATAAREA = "InputDataArea";
	public static final String FIELD_REQUEST_CONTROL_OUTPUTDATAAREA = "OutputDataArea";
	public static final String FIELD_REQUEST_CONTROL_TRACELEVEL = "TraceLevel";
	public static final String FIELD_REQUEST_CONTROL_SYSTEMID = "SystemID";
	public static final String FIELD_REQUEST_CONTROL_POSITION = "Position";
	public static final String FIELD_REQUEST_CONTROL_SYSTEMDATE = "SystemDate";
	public static final String FIELD_REQUEST_CONTROL_SYSTEMTIME = "SystemTime";
	public static final String FIELD_REQUEST_CONTROL_SEQID = "SEQID";
	
	public static final String FIELD_REQUEST_INPUTFIELDS = DATAAREA_INPUT;
	
	public static final String FIELD_RESPONSE_SERVICE = FIELD_REQUEST_SERVICE;	
	public static final String FIELD_RESPONSE_CONTROL = FIELD_REQUEST_CONTROL;	
	public static final String FIELD_RESPONSE_CONTROL_RETURNCODE = "RETURNCODE";
	public static final String FIELD_RESPONSE_CONTROL_ERRMESSAGE = "ERRMESSAGE";
	public static final String FIELD_RESPONSE_CONTROL_SYSTEMID = FIELD_REQUEST_CONTROL_SYSTEMID;
	public static final String FIELD_RESPONSE_CONTROL_EDITION = "EDITION";
	public static final String FIELD_RESPONSE_CONTROL_EDITIONDATE = "EDITIONDATE";
	public static final String FIELD_RESPONSE_CONTROL_SEQID = FIELD_REQUEST_CONTROL_SEQID;
	
	public static final String FIELD_RESPONSE_DAERROR = "DAError";
	public static final String FIELD_RESPONSE_DAERROR_ERRORCOUNT = "ERRORCOUNT";
	public static final String FIELD_RESPONSE_DAERROR_DAERRMSG = "DAERRMSG";
	
	public static final String FIELD_RESPONSE_OUTPUTFIELDS = DATAAREA_OUTPUT;
	
	public static final String FIELD_CONTROL_ERROR = "ERROR";
	public static final String FIELD_CONTROL_ERRORCODE = FIELD_RESPONSE_DAERROR_DAERRMSG;
	
	public static final String[] DATAAREA_CONTROLS = {
		FIELD_REQUEST_CONTROL_ALIAS, FIELD_REQUEST_CONTROL_SIGNATURE,
		FIELD_REQUEST_CONTROL_INPUTDATAAREA, FIELD_REQUEST_CONTROL_OUTPUTDATAAREA,
		FIELD_REQUEST_CONTROL_TRACELEVEL, FIELD_REQUEST_CONTROL_SYSTEMID,
		FIELD_REQUEST_CONTROL_POSITION, FIELD_REQUEST_CONTROL_SYSTEMDATE,
		FIELD_REQUEST_CONTROL_SYSTEMTIME, FIELD_REQUEST_CONTROL_SEQID,
		FIELD_RESPONSE_DAERROR_ERRORCOUNT, FIELD_RESPONSE_DAERROR_DAERRMSG,
		FIELD_RESPONSE_CONTROL_RETURNCODE, FIELD_RESPONSE_CONTROL_ERRMESSAGE, 
		FIELD_RESPONSE_CONTROL_EDITION,	FIELD_RESPONSE_CONTROL_EDITIONDATE
		
	};	
}