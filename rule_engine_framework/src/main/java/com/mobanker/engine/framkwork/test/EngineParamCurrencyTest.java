package com.mobanker.engine.framkwork.test;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobanker.engine.framkwork.api.params.EngineTransferData;

/**
 * 参数线程安全测试
 * <p>Title: EngineParamCurrencyTest.java<／p>
 * <p>Description: <／p>
 * <p>Company: mobanker.com<／p>
 * @author taojinn
 * @date 2016年2月2日
 * @version 1.0
 */
public class EngineParamCurrencyTest {
	
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	public static void main(String[] args) {
	
		
		
	}
	
	private static class MapRun implements Runnable{
		
		private HashMap<String,Object> etd;
		private String name;
		public MapRun(HashMap<String,Object> etd,String name){
			this.etd = etd;
			this.name = name;
		}
		
		@Override
		public void run() {		
			int i = 0;
			System.out.println("["+Thread.currentThread().getName()
					+"]do something!");
			for(;;){
				etd.put(name+(i++), name+i);
				
				etd.remove(name+(i-1));
				
				if(i%100 == 0) System.out.println(Thread.currentThread().getName()+"==>size:"+etd.size());
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}	
	
	private static class TestRun implements Runnable{
		
		private EngineTransferData etd;
		private String name;
		public TestRun(EngineTransferData etd,String name){
			this.etd = etd;
			this.name = name;
		}
		
		@Override
		public void run() {		
			int i = 0;
			System.out.println("["+Thread.currentThread().getName()
					+"]do something!");
			for(;;){
				etd.setInput(name+(i++), name+i);
				if(i%100 == 0) System.out.println(Thread.currentThread().getName()+"==>size:"+etd.getInputParam().size());
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}	
	
}
