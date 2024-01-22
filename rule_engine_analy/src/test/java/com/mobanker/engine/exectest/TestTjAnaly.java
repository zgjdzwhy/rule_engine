package com.mobanker.engine.exectest;


import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.mobanker.engine.exec.business.snapshot.chartanaly.impl.EngineChartAnalyRowBarRatiosum;
import com.mobanker.engine.exec.business.tasktrace.EngineTaskBatchRun;
import com.mobanker.engine.exec.business.tasktrace.EngineTaskTraceService;
import com.mobanker.engine.exec.pojo.EngineTaskQco;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;



/**
 * 
 * <p>
 * Title: TestTjTemp.java<／p>
 * <p>
 * Description: 临时用用<／p>
 * <p>
 * Company: mobanker.com<／p>
 * 
 * @author taojinn
 * @date 2015年9月24日
 * @version 1.0
 */
public class TestTjAnaly extends TestBase {

	private Logger logger = LoggerFactory.getLogger(this.getClass());	

	static EngineTaskTraceService engineTaskTraceService = getBean(EngineTaskTraceService.class);
	
	static EngineTaskBatchRun engineTaskBatchRun = getBean(EngineTaskBatchRun.class);
	
	static RedisTemplate<String, Object> redisFlagTemplate = getBean(RedisTemplate.class);
	
	@BeforeClass
	public static void init() {
		
	}
	
	@Test
	public void run1() throws Exception {		
		EngineTaskQco qco = new EngineTaskQco();	
		qco.setPage(1, 100000);
		qco.setProductType("lk_oldZr1");
		qco.setBeginTime("2012-11-13 13:45:23");
		qco.setEndTime("2018-11-13 13:45:23");
//		qco.setAppRequestId("201611131344382");
//		qco.setAppParams("\"age\":29");
//		qco.setStatus("2");
//		qco.setAssignStep(2);
//		qco.setRuleVersion("2014");
		
		List<EngineTaskInfo> list = engineTaskTraceService.queryTaskInfo(qco);		
		System.out.println("size="+list.size());
//		for(EngineTaskInfo task:list){
//			logger.info("aaaaaaaaaaa"+task);	
//		}		
	}

	@Test
	public void testDisplay() throws Exception {	
		long t = System.currentTimeMillis();
		System.out.println("result:"+engineTaskBatchRun.displayFieldChartResult("taojinZy", "test", EngineChartAnalyRowBarRatiosum.class.getSimpleName()));
		System.out.println("耗时:"+(System.currentTimeMillis()-t));
	}
	
	
	@Test
	public void redisKeys() throws Exception {	
		System.out.println(redisFlagTemplate.keys("*batchRunKey_taojinZy4*"));
	}
}
