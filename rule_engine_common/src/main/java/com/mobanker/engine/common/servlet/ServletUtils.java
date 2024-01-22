package com.mobanker.engine.common.servlet;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;

/**
 * Http��Servlet������.
 * 
 * @author Stephan Sun
 */
public class ServletUtils {

	//-- Content Type ���� --//
	public static final String TEXT_TYPE = "text/plain";
	public static final String JSON_TYPE = "application/json";
	public static final String XML_TYPE = "text/xml";
	public static final String HTML_TYPE = "text/html";
	public static final String JS_TYPE = "text/javascript";
	public static final String EXCEL_TYPE = "application/vnd.ms-excel";
	
	//-- Header ���� --//
	public static final String AUTHENTICATION_HEADER = "Authorization";

	/**
	 * ���ÿͻ��˻������ʱ�� ��Header.
	 */
	public static void setExpiresHeader(HttpServletResponse response, long expiresSeconds) {
		//Http 1.0 header
		response.setDateHeader("Expires", System.currentTimeMillis() + expiresSeconds * 1000);
		//Http 1.1 header
		response.setHeader("Cache-Control", "private, max-age=" + expiresSeconds);
	}
	
	/**
	 * ���ý�ֹ�ͻ��˻����Header.
	 */
	public static void setDisableCacheHeader(HttpServletResponse response) {
		//Http 1.0 header
		response.setDateHeader("Expires", 1L);
		response.addHeader("Pragma", "no-cache");
		//Http 1.1 header
		response.setHeader("Cache-Control", "no-cache, no-store, max-age=0");
	}
	
	/**
	 * ����LastModified Header.
	 */
	public static void setLastModifiedHeader(HttpServletResponse response, long lastModifiedDate) {
		response.setDateHeader("Last-Modified", lastModifiedDate);
	}
	
	/**
	 * ����Etag Header.
	 */
	public static void setEtag(HttpServletResponse response, String etag) {
		response.setHeader("ETag", etag);
	}
	
	/**
	 * ��������If-Modified-Since Header, �����ļ��Ƿ��ѱ��޸�.
	 * 
	 * ������޸�, checkIfModify����false ,����304 not modify status.
	 * 
	 * @param lastModified ���ݵ�����޸�ʱ��.
	 */
	public static boolean checkIfModifiedSince(HttpServletRequest request, HttpServletResponse response,
			long lastModified) {
		long ifModifiedSince = request.getDateHeader("If-Modified-Since");
		if ((ifModifiedSince != -1) && (lastModified < ifModifiedSince + 1000)) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return false;
		}
		return true;
	}
	
	/**
	 * �������� If-None-Match Header, ����Etag�Ƿ�����Ч.
	 * 
	 * ���Etag��Ч, checkIfNoneMatch����false, ����304 not modify status.
	 * 
	 * @param etag ���ݵ�ETag.
	 */
	public static boolean checkIfNoneMatchEtag(HttpServletRequest request, HttpServletResponse response, String etag) {
		String headerValue = request.getHeader("If-None-Match");
		if (headerValue != null) {
			boolean conditionSatisfied = false;
			if (!"*".equals(headerValue)) {
				StringTokenizer commaTokenizer = new StringTokenizer(headerValue, ",");

				while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) {
					String currentToken = commaTokenizer.nextToken();
					if (currentToken.trim().equals(etag)) {
						conditionSatisfied = true;
					}
				}
			} else {
				conditionSatisfied = true;
			}

			if (conditionSatisfied) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				response.setHeader("ETag", etag);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ������������������ضԻ����Header.
	 * 
	 * @param fileName ���غ���ļ���.
	 */
	public static void setFileDownloadHeader(HttpServletResponse response, String fileName) {
		try {
			//�����ļ���֧��
			String encodedfileName = new String(fileName.getBytes(), "ISO8859-1");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedfileName + "\"");
		} catch (UnsupportedEncodingException e) {
		}
	}
	
	/**
	 * ȡ�ô���ͬǰ׺��Request Parameters.
	 * 
	 * ���صĽ���Parameter����ȥ��ǰ׺.
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getParametersStartingWith(HttpServletRequest request, String prefix) {
		Assert.notNull(request, "Request must not be null");
		Enumeration paramNames = request.getParameterNames();
		Map<String, Object> params = new TreeMap<String, Object>();
		if (prefix == null) {
			prefix = "";
		}
		while (paramNames != null && paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if ("".equals(prefix) || paramName.startsWith(prefix)) {
				String unprefixed = paramName.substring(prefix.length());
				String[] values = request.getParameterValues(paramName);
				if (values == null || values.length == 0) {
					// Do nothing, no values found at all.
				} else if (values.length > 1) {
					params.put(unprefixed, values);
				} else {
					params.put(unprefixed, values[0]);
				}
			}
		}
		
		/**用户信息**/
//		Operator user = (Operator)request.getSession().getAttribute("user");
//		params.put("user", user);
		
		return params;
	}
	
	// ======= added by SunYan on 2011.06.27 18:32 =========
	public static int getParameterStart(ServletRequest request) {
		Assert.notNull(request, "Request must not be null");
		String start = request.getParameter("start");
		return Integer.parseInt(start);
	}
	
	public static int getParameterLimit(ServletRequest request) {
		Assert.notNull(request, "Request must not be null");
		String limit = request.getParameter("limit");
		return Integer.parseInt(limit);
	}
	// ======= =================================== ==========
	
	// ======= added by tj on 2014.01.10 18:32 =========
	public static String getSessionId(HttpServletRequest request){
		Cookie cookie = getCookie(request,"JSESSIONID");
		return cookie==null?null:cookie.getValue(); 
	}
	public static Cookie getCookie(HttpServletRequest request,String cookieName){
		Cookie[] cookies = request.getCookies();
		for(Cookie cookie : cookies){
			if(cookie.getName().trim().equals(cookieName)){
				return cookie;
			}
		}	
		return null;
	}
	
	
	
	/**
	 * ��Http Basic��֤�� Header���б���.
	 */
	public static String encodeHttpBasic(String userName, String password) {
		String encode = userName + ":" + password;
		//return "Basic " + EncodeUtils.base64Encode(encode.getBytes());
		return "";
	}
	
}
