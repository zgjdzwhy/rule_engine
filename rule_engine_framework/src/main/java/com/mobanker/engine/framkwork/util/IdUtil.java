/**
* <p>Title: IdUtil</p>
* <p>Description: </p>
* <p>Company: </p> 
* @author zhujunjie
* @date 下午3:31:39
*/
package com.mobanker.engine.framkwork.util;

import org.apache.commons.lang3.StringUtils;

import com.mobanker.engine.framkwork.exception.EngineAssert;
import com.mobanker.engine.framkwork.exception.EngineException;

public class IdUtil {
	// 最长10位
	private static final int ROWKEY_MAX_LENGTH = 12;
	public static final String ID_SEPARATOR = "-";
	
	public static String getRowkeyId(String cid,String productType) {
		String id = StringUtils.reverse(cid);
		if (id.length() > ROWKEY_MAX_LENGTH) {
			throw new EngineException("id位数超过最大长度！");
		}
		return StringUtils.rightPad(id, ROWKEY_MAX_LENGTH, '0')+ ID_SEPARATOR +productType;
	}
	
	public static String getRegex(String[] conditions) {
		String regex = ".*";

		for (String condition : conditions) {
			regex += ID_SEPARATOR;
			if (StringUtils.isBlank(condition)) {
				regex += ".*";
			} else {
				regex += ".*" + condition + ".*";
			}
		}

		regex += "$";
		return regex;

	}

	public static String getFunctionId(String label,String productType) {
		EngineAssert.checkNotBlank(label);
		EngineAssert.checkNotBlank(productType);
		return label + ID_SEPARATOR + productType;
	}
}
