package com.mobanker.engine.exectest;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.mobanker.engine.exec.business.snapshot.EngineParamSnapshotService;
import com.mobanker.engine.exec.controller.EngineExecTest;
import com.mobanker.engine.framkwork.api.params.EngineParam;
import com.mobanker.engine.framkwork.api.params.EngineTransferData;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.data.entity.StepLoginInEntity;
import com.mobanker.engine.framkwork.data.entity.StepLoginOutEntity;
import com.mobanker.engine.framkwork.data.entity.TaskParamEntity;
import com.mobanker.engine.framkwork.data.hbase.StepLoginInHbaseDao;
import com.mobanker.engine.framkwork.data.hbase.StepLoginOutHbaseDao;
import com.mobanker.engine.framkwork.data.hbase.TaskParamHbaseDao;
import com.mobanker.engine.rpc.api.EngineRpcInvoker;
import com.mobanker.engine.rpc.dto.EngineInvokeDto;
import com.mobanker.engine.rpc.dto.EngineOutput;



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
public class TestTjExec extends TestBase {

	private Logger logger = LoggerFactory.getLogger(this.getClass());	

	static EngineExecTest engineExecTest = getBean(EngineExecTest.class);	
//	static EngineResultDealSender engineResultDealSender = getBean(EngineResultDealSender.class);
//	static EngineTaskTraceService engineTaskTraceService = getBean(EngineTaskTraceService.class);
	static StepLoginInHbaseDao stepLoginInHbaseDao = getBean(StepLoginInHbaseDao.class);
	static StepLoginOutHbaseDao stepLoginOutHbaseDao = getBean(StepLoginOutHbaseDao.class);
	static TaskParamHbaseDao taskParamHbaseDao = getBean(TaskParamHbaseDao.class);

	@BeforeClass
	public static void init() {
		
	}
	
	
	@Test
	public void run3() throws Exception {
		TaskParamEntity entity=new TaskParamEntity();
		entity.setAppName("test");
		entity.setAppRequestId("2323");
		entity.setCurStepId(23422234L);
		EngineParam inputParam=new EngineParam();
		inputParam.put("aa", "3453");
		inputParam.put("bb", "3453");
		inputParam.put("cc", "3453");
		inputParam.put("dd", "3453");
		entity.setInputParam(JSON.toJSONString(inputParam));
		
		entity.setCreateTime("20170619");
		entity.setCurStepBean("asda");
		entity.setCurStepName("asdasd");
		entity.setCurTime(new Date().getTime());
		entity.setIpAddress("182");
		entity.setOutputParam(JSON.toJSONString(inputParam));
		entity.setProductType("test");
//		entity.setRuleVersion("12313");
		entity.setTaskId(245463L);
		entity.setTaskTime("20170619");
		entity.setUpdateTime("20170619");
		entity.setRowKey();
		System.out.println("**********");
		System.out.println(entity.getId());
		taskParamHbaseDao.save(entity);
		System.out.println(entity.getId());
		Thread.sleep(10000);
		System.out.println(JSON.toJSONString(taskParamHbaseDao.get(entity.getId())));
		EngineTransferData data=EngineTransferData.getInstance();
		BeanUtils.copyProperties(entity,data);
		System.out.println(JSON.toJSONString(data));
		System.out.println("**********");
	}
	
	@Test
	public void run2() throws Exception {	
		
		List<Long> list=new ArrayList<Long>();
		list.add(232L);
		list.add(234L);
		list.add(234L);
		list.add(885L);
		list.add(4545L);
		
		List<String> list1=new ArrayList<String>();
		list1.add("aaa");
		list1.add("bbb");
		list1.add("ccc");
		list1.add("ddd");
		list1.add("eee");
		
		List<Boolean> list2=new ArrayList<Boolean>();
		list2.add(true);
		list2.add(false);
		
		List<Double> list3=new ArrayList<Double>();
		list3.add(232.34);
		list3.add(234.454);
		list3.add(234.452);
		list3.add(885.23);
		list3.add(4545.54);
		
		
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("list", list);
		map.put("list1", list1);
		map.put("list2", list2);
		map.put("list3", list3);
		
		EngineOutput callBackParam=new EngineOutput(map);
		System.out.println(callBackParam);
		
		for(String kk:callBackParam.getStringList("list1")){
			System.out.println(kk);
		}
		
		for(BigDecimal kk:callBackParam.getBigDecimalList("list3")){
			System.out.println(kk);
		}
	}
	
	
	@Test
	public void run1() throws Exception {		
		EngineInvokeDto dto = new EngineInvokeDto();
		dto.setAppName("testApp");
		
		Map<String,Object> input = new HashMap<String,Object>();	
		input.put("userId", "23434");
		input.put("borrowNid", "TEST33422321333");
		input.put("company_address_check_name", Arrays.asList(new Integer[]{1,0,1}));
		input.put("company_name_check_address", "1");
		dto.setAppParams(input);
		dto.setExecMode(0);
		dto.setProductType("shoujidai");
		System.out.println(getBean(EngineRpcInvoker.class).synInvoke(dto));
	}
	
//	@Test
//	public void sendResultMsg() throws Exception {		
//		engineResultDealSender.sendMq("shoujidai", "5665434", new EngineParam());
//	}
	
	
	
//	@Test
//	public void engineTaskTraceService() throws Exception {		
//		engineTaskTraceService.queryTaskInfo(null);
//	}
	
	@Test
	public void testSnapshot() throws Exception {				
		EngineParamSnapshotService engineParamSnapshotService = getBean(EngineParamSnapshotService.class);
		
		Thread.sleep(5000);
		//插入一些数据做测试
		EngineTaskInfo taskInfo = new EngineTaskInfo();
		taskInfo.setRuleVersion("test_version");
		taskInfo.setId(1L);
		taskInfo.setProductType("testProduct");
		taskInfo.setAppName("testApp");
		taskInfo.setAppRequestId("testRequestId");		
		EngineTransferData etd = EngineTransferData.createByDefault(taskInfo);
		etd.setInput("testkey", "testValue");
		
		System.out.println("开始处理");
		long t = System.currentTimeMillis();
		for(int i = 0;i<100009;i++){
			engineParamSnapshotService.snapshot("rule_step_test", etd);
			if(i%1000==0){
				System.out.println("处理1000条，耗时:"+(System.currentTimeMillis()-t)+"毫秒");
				t = System.currentTimeMillis();
			}				
		}
		System.out.println("处理结束");
		
		
		Thread.sleep(500000000);
		
	}
	
}
