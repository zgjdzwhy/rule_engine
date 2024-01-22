package com.mobanker.engine.rpc.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mobanker.engine.rpc.exception.EngInvokeException;


/**
* <p>Title: CallBackParam</p>
* <p>Description: 引擎返回参数类
* 提供相应参数类型获取方法， 类似jsonObject</p>
* <p>Company: mobanker</p> 
* @author zhujj
* @date 2017年4月18日
* @version 1.0 
*/
public class EngineOutput implements Map<String, Object>,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private static final int  DEFAULT_INITIAL_CAPACITY = 16;

    private final Map<String, Object> map;

    public EngineOutput(){
        this(DEFAULT_INITIAL_CAPACITY, false);
    }

    public EngineOutput(Map<String, Object> map){
        this.map = map;
    }

    public EngineOutput(boolean ordered){
        this(DEFAULT_INITIAL_CAPACITY, ordered);
    }

    public EngineOutput(int initialCapacity){
        this(initialCapacity, false);
    }

    public EngineOutput(int initialCapacity, boolean ordered){
        if (ordered) {
            map = new LinkedHashMap<String, Object>(initialCapacity);
        } else {
            map = new HashMap<String, Object>(initialCapacity);
        }
    }

	@Override
	public int size() {
	   return map.size();
	}


	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}


	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}


	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Object get(Object key) {
        return map.get(key);
    }
	
	@Override
	public Object put(String key, Object value) {
        return map.put(key, value);
    }

	@Override
	public Object remove(Object key) {
		return map.remove(key);
	}


	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		map.putAll(m);
	}


	@Override
	public void clear() {
		map.clear();
	}


	@Override
	public Set<String> keySet() {
		return map.keySet();
	}


	@Override
	public Collection<Object> values() {
		return map.values();
	}


	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return map.entrySet();
	}

	@Override
    public boolean equals(Object obj) {
        return this.map.equals(obj);
    }

	@Override
    public int hashCode() {
        return this.map.hashCode();
    }


    public List<?> getList(String key) {
        Object value = map.get(key);
        if(value==null){
        	return null;
        }
		if(value instanceof List){
			List<?> objList = (List<?>) value;
			return objList;
		}
		
		throw new EngInvokeException("can not cast to List, value : " + value);
    }

    
    public List<String> getStringList(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
      
        if (value instanceof List) {
        	List<String> retLit=new ArrayList<String>();
        	List<?> objList = (List<?>) value;
        	for(Object obj:objList){
        		retLit.add(castToString(obj));
			}
        	return retLit;
        }
        throw new EngInvokeException("can not cast to List<String>, value : " + value);
    }

    public List<BigDecimal> getBigDecimalList(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
      
        if (value instanceof List) {
        	List<BigDecimal> retLit=new ArrayList<BigDecimal>();
        	List<?> objList = (List<?>) value;
        	for(Object obj:objList){
        		retLit.add(castToBigDecimal(obj));
			}
        	return retLit;
        }
        
        throw new EngInvokeException("can not cast to List<String>, value : " + value);
    }
    
    public Boolean getBoolean(String key) {
        Object value = get(key);

        if (value == null) {
            return null;
        }

        return castToBoolean(value);
    }

    public boolean getBooleanValue(String key) {
        Object value = get(key);

        if (value == null) {
            return false;
        }

        return castToBoolean(value).booleanValue();
    }


    public Integer getInteger(String key) {
        Object value = get(key);

        return castToInt(value);
    }

    public int getIntValue(String key) {
        Object value = get(key);

        if (value == null) {
            return 0;
        }

        return castToInt(value).intValue();
    }

    public Long getLong(String key) {
        Object value = get(key);

        return castToLong(value);
    }

    public long getLongValue(String key) {
        Object value = get(key);

        if (value == null) {
            return 0L;
        }

        return castToLong(value).longValue();
    }


    public Double getDouble(String key) {
        Object value = get(key);

        return castToDouble(value);
    }

    public double getDoubleValue(String key) {
        Object value = get(key);

        if (value == null) {
            return 0D;
        }

        return castToDouble(value);
    }

    public BigDecimal getBigDecimal(String key) {
        Object value = get(key);

        return castToBigDecimal(value);
    }

    public String getString(String key) {
        Object value = get(key);

        if (value == null) {
            return null;
        }

        return value.toString();
    }

    public java.sql.Date getSqlDate(String key) {
        Object value = get(key);

        return castToSqlDate(value);
    }

    public java.sql.Timestamp getTimestamp(String key) {
        Object value = get(key);

        return castToTimestamp(value);
    }
    
 
    private Boolean castToBoolean(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue() == 1;
        }

        if (value instanceof String) {
            String strVal = (String) value;

            if (strVal.length() == 0 //
                || "null".equals(strVal) //
                || "NULL".equals(strVal)) {
                return null;
            }

            if ("true".equalsIgnoreCase(strVal) //
                || "1".equals(strVal)) {
                return Boolean.TRUE;
            }
            
            if ("false".equalsIgnoreCase(strVal) //
                || "0".equals(strVal)) {
                return Boolean.FALSE;
            }
        }

        throw new EngInvokeException("can not cast to boolean, value : " + value);
    }
    
    private Integer castToInt(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        if (value instanceof String) {
            String strVal = (String) value;

            if (strVal.length() == 0 //
                || "null".equals(strVal) //
                || "NULL".equals(strVal)) {
                return null;
            }
            
            if (strVal.indexOf(',') != 0) {
                strVal = strVal.replaceAll(",", "");
            }

            return Integer.parseInt(strVal);
        }

        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue() ? 1 : 0;
        }

        throw new EngInvokeException("can not cast to int, value : " + value);
    }
    
    private Long castToLong(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 //
                || "null".equals(strVal) //
                || "NULL".equals(strVal)) {
                return null;
            }
            
            if (strVal.indexOf(',') != 0) {
                strVal = strVal.replaceAll(",", "");
            }

            try {
                return Long.parseLong(strVal);
            } catch (NumberFormatException ex) {
                //
            }
        }

        throw new EngInvokeException("can not cast to long, value : " + value);
    }
    
    private Double castToDouble(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value instanceof String) {
            String strVal = value.toString();
            if (strVal.length() == 0 //
                || "null".equals(strVal) //
                || "NULL".equals(strVal)) {
                return null;
            }

            if (strVal.indexOf(',') != 0) {
                strVal = strVal.replaceAll(",", "");
            }

            return Double.parseDouble(strVal);
        }

        throw new EngInvokeException("can not cast to double, value : " + value);
    }
    
    private BigDecimal castToBigDecimal(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }

        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }

        String strVal = value.toString();
        if (strVal.length() == 0) {
            return null;
        }

        return new BigDecimal(strVal);
    }

    private java.sql.Date castToSqlDate(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof java.sql.Date) {
            return (java.sql.Date) value;
        }

        if (value instanceof java.util.Date) {
            return new java.sql.Date(((java.util.Date) value).getTime());
        }

        if (value instanceof Calendar) {
            return new java.sql.Date(((Calendar) value).getTimeInMillis());
        }

        long longValue = 0;

        if (value instanceof Number) {
            longValue = ((Number) value).longValue();
        }

        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 //
                || "null".equals(strVal) //
                || "NULL".equals(strVal)) {
                return null;
            }

            longValue = Long.parseLong(strVal);
        }

        if (longValue <= 0) {
            throw new EngInvokeException("can not cast to Date, value : " + value); // TODO 忽略 1970-01-01 之前的时间处理？
        }

        return new java.sql.Date(longValue);
    }

    private java.sql.Timestamp castToTimestamp(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Calendar) {
            return new java.sql.Timestamp(((Calendar) value).getTimeInMillis());
        }

        if (value instanceof java.sql.Timestamp) {
            return (java.sql.Timestamp) value;
        }

        if (value instanceof java.util.Date) {
            return new java.sql.Timestamp(((java.util.Date) value).getTime());
        }

        long longValue = 0;

        if (value instanceof Number) {
            longValue = ((Number) value).longValue();
        }

        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 //
                || "null".equals(strVal) //
                || "NULL".equals(strVal)) {
                return null;
            }

            longValue = Long.parseLong(strVal);
        }

        if (longValue <= 0) {
            throw new EngInvokeException("can not cast to Date, value : " + value);
        }

        return new java.sql.Timestamp(longValue);
    }

    private String castToString(Object value) {
        if (value == null) {
            return null;
        }

        return value.toString();
    }
    
    

}
