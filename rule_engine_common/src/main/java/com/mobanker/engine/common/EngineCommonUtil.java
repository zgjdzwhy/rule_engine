package com.mobanker.engine.common;

import java.util.Date;

import com.mobanker.framework.utils.DateUtils;

public abstract class EngineCommonUtil {
	public static String get14CurrentDateTime(){
		return DateUtils.convert(new Date(), DateUtils.DATE_TIMESTAMP_SHORT_FORMAT);
	}
}
