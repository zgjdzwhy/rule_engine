package com.mobanker.engine.framkwork.api.params;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 引擎参数，线程安全，底层是AntiCollisionHashMap
 * <p>Title: EngineParam.java<／p>
 * <p>Description: <／p>
 * <p>Company: mobanker.com<／p>
 * @author taojinn
 * @date 2016年1月27日
 * @version 1.0
 */
public class EngineParam extends JSONObject implements Map<String,Object>,Serializable,Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    public EngineParam(int initialCapacity) {
        super(initialCapacity);
    }
	
    public EngineParam() {
        super();
    }
    
    @Override
    public Object put(String key, Object value) {
    	if(value != null)//为了不让null把默认值覆盖 囧
    		return super.put(key, value);
    	else
    		return null;
    }
    

    public <T> void putArr(String key, List<T> list) {
    	int index = 0;
		for(T one : list){
			String keyIndex = key + "[" + (index++) + "]";
			this.put(keyIndex, one);
		}		
    }
    
}
