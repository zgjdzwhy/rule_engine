package com.mobanker.engine.framkwork.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * threadLocal测试
 * <p>Title: EngineParamCurrencyTest.java<／p>
 * <p>Description: <／p>
 * <p>Company: mobanker.com<／p>
 * @author taojinn
 * @date 2016年2月2日
 * @version 1.0
 */
public class EngineThreadLocalTest {
	
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	
//	private static ThreadLocal<Integer> curInteger = new ThreadLocal<Integer>(){
//		@Override
//		protected Integer initialValue(){
//			return 0;
//		}	
//	};
	private static ThreadLocal<Integer> curInteger = new InheritableThreadLocal<Integer>(){
		@Override
		protected Integer initialValue(){
			return 0;
		}	
	};
	
	
	public static void main(String[] args) {
		ExecutorService executorService = new ThreadPoolExecutor(10, 20, 30, 
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		
		
		curInteger.set(101);
		System.out.println(Thread.currentThread().getName()+"->"+curInteger.get());
		
		for(int i=0;i<12;i++){
			Runnable test = new TestRun();		
			executorService.submit(test);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		System.out.println(Thread.currentThread().getName()+"->"+curInteger.get());
		
	}
	
	
	private static class TestRun implements Runnable{
		
		@Override
		public void run() {
			Integer i = curInteger.get();
			System.out.println(Thread.currentThread().getName()+"->"+i);
			curInteger.set(i+1);

		}
	}	
	
}
