

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.design.busi.configma.EngineConfigManageService;
import com.mobanker.engine.design.busi.workspace.EngineWorkspaceManager;
import com.mobanker.engine.design.busi.workspace.EngineWorkspaceManagerImpl;
import com.mobanker.engine.design.dao.EngProductReleaseFlowDao;
import com.mobanker.engine.design.dto.EngineProductDto;
import com.mobanker.engine.design.hotdeploy.EngineRuleModelHotDeployService;
import com.mobanker.engine.design.mongo.EngRuleModelDao;
import com.mobanker.engine.design.mongo.EngScriptTemplateDao;
import com.mobanker.engine.design.mongo.assist.RuleModelTable;
import com.mobanker.engine.framkwork.api.params.EngineParam;
import com.mobanker.engine.framkwork.cpnt.baseinfo.EngineCpnt;
import com.mobanker.engine.framkwork.cpnt.container.EngineCpntContainer;
import com.mobanker.engine.framkwork.cpnt.step.impl.EngineScoreCardV2Step;
import com.mobanker.engine.framkwork.data.entity.EngineStepInfo;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.parse.manager.EngineParseManager;
import com.mobanker.engine.framkwork.util.EngineUtil;



/**
 * 
 * <p>
 * Title: TestTjTemp.java<／p>
 * <p>
 * 
 * <p>
 * Company: mobanker.com<／p>
 * 
 * @author 
 * @date 
 * @version 1.0
 */
public class TestZjj extends TestBase {

	private Logger logger = LoggerFactory.getLogger(this.getClass());	

	static EngineWorkspaceManager engineWorkspaceManager = getBean(EngineWorkspaceManager.class);
	static EngRuleModelDao engRuleModelDao = getBean(EngRuleModelDao.class);
	static EngScriptTemplateDao engScriptTemplateDao = getBean(EngScriptTemplateDao.class);
	static EngProductReleaseFlowDao engProductReleaseFlowDao = getBean(EngProductReleaseFlowDao.class);
	static EngineConfigManageService engineConfigManageService = getBean(EngineConfigManageService.class);
	static EngineRuleModelHotDeployService engineRuleModelHotDeployService = (EngineRuleModelHotDeployService) getBean("engineRuleModelHotDeployService");
	private RedisTemplate redisTemplate  = (RedisTemplate) getBean("redisTemplate");
	//private StringRedisTemplate stringRedisTemplate  = (StringRedisTemplate) getBean("redisTemplate");
	//private RedisCacheUtil redisCacheUtil  = (RedisCacheUtil) getBean("redisCacheUtil");
	
	@BeforeClass
	public static void init() {

	}
	

	
	@Test
	public void run() throws Exception {		

		redisTemplate.opsForValue().set("test_zjj", "111");
		redisTemplate.expire("test_zjj",1,TimeUnit.HOURS);
		
		
		String verifyResult = (String) redisTemplate.opsForValue().get("test_zjj");
		
		System.out.println("#######"+verifyResult);
		
		
		TestSringDataJedis jedis = new TestSringDataJedis(redisTemplate);
	    System.out.println("test-string = " + jedis.get("test-string"));
	    System.out.println("test-user = " + jedis.get("test-user"));
	    System.out.println("test-user:name = " + ((EngineProductDto)jedis.get("test-user")).getName());
	    String key_list = "test-list";
	    RedisCacheUtil redisCacheUtil =new RedisCacheUtil(redisTemplate);
	    List<String> test_list = (List<String>)redisCacheUtil.getCacheList(key_list);
	    for (int i = 0; i < test_list.size(); i++) {
	        System.out.println(i + " = " + test_list.get(i));
	    }
	    String key_map = "test-map";
	    Map<String,Object> getMap = (Map<String,Object>)jedis.getHash(key_map);
	    for(String key:getMap.keySet()){
	    	String resl=(String) getMap.get(key);
	    	 System.out.println(key + " = " + resl);
	    }
	}	
	
	@Test
	public void run2() throws Exception {	
	    TestSringDataJedis jedis = new TestSringDataJedis(redisTemplate);
	    //String
	    jedis.set("test-string", "good-中国抗战胜利");
	    System.out.println("test-string = " + jedis.get("test-string"));
	    //POJO
	    EngineProductDto dto =new EngineProductDto(1212L,"邓洋","sdfsdfs","1");
	    jedis.set("test-user", dto);
	    System.out.println("test-user = " + jedis.get("test-user"));
	    System.out.println("test-user:name = " + ((EngineProductDto)jedis.get("test-user")).getName());
	    dto.setName("天天");
	    jedis.set("test-user", dto);
	    System.out.println("test-user = " + jedis.get("test-user"));
	    System.out.println("test-user:name = " + ((EngineProductDto)jedis.get("test-user")).getName());
	    //List
	    List<String> list = new ArrayList<String>();
	    list.add("张三");
	    list.add("李四");
	    list.add("麻子");
	    String key_list = "test-list";
	    jedis.set(key_list, list);
	    List<String> test_list = (List<String>)jedis.get(key_list);
	    for (int i = 0; i < test_list.size(); i++) {
	        System.out.println(i + " = " + test_list.get(i));
	    }
	    list.add("水电费水电费");
	    jedis.set(key_list, list);
	    test_list = (List<String>)jedis.get(key_list);
	    for (int i = 0; i < test_list.size(); i++) {
	        System.out.println(i + " = " + test_list.get(i));
	    }
	    
	    //Map
	    String key_map = "test-map";
	    Map<String,Object> map = new HashMap<String, Object>();
	    map.put("map1","map-张三");
	    map.put("map2","map-李四");
	    map.put("map3", "map-麻子");
	    jedis.setHash(key_map, map);
	    Map<String,Object> getMap = (Map<String,Object>)jedis.getHash(key_map);
	    for(String key:getMap.keySet()){
	    	String resl=(String) getMap.get(key);
	    	 System.out.println(key + " = " + resl);
	    }
	    map.put("map4", dto);
	    jedis.setHash(key_map, map);
	    getMap = (Map<String,Object>)jedis.getHash(key_map);
	    for(String key:getMap.keySet()){
	    	if(!"map4".equals(key)){
	    		String resl=(String) getMap.get(key);
	    		System.out.println(key + " = " + resl);
	    	}else{
	    		EngineProductDto resl=(EngineProductDto) getMap.get(key);
	    		System.out.println(key + " = " + resl);
	    	}
	    	
	    }
	   
	}
	
	@Test
	public void run3() throws Exception {	
		TestSringDataJedis jedis = new TestSringDataJedis(redisTemplate);
	    String key_list = "test-list1";
//	    List<String> list = new ArrayList<String>();
//	    list.add("张三");
//	    list.add("李四");
//	    list.add("麻子");
//	    TestList testlist=new TestList();
//	    testlist.setDatalist(list);
//	    jedis.set(key_list, list);
	    List<String> asda=(List<String>) jedis.get(key_list);
//	    TestList l=(TestList) jedis.get(key_list);
	    for (int i = 0; i < asda.size(); i++) {
	        System.out.println(i + " = " + asda.get(i));
	    }
	    
//	    List<String> test_list = (List<String>)jedis.getLeftList(key_list);
//	    for (int i = 0; i < test_list.size(); i++) {
//	        System.out.println(i + " = " + test_list.get(i));
//	    }
//	    test_list = (List<String>)jedis.getRightList(key_list);
//	    for (int i = 0; i < test_list.size(); i++) {
//	        System.out.println(i + " = " + test_list.get(i));
//	    }
	}
	
	@Test
	public void run4() throws Exception {	
		TestSringDataJedis jedis = new TestSringDataJedis(redisTemplate);
		Long taskId=1L;
		Long stepId=1L;
		List<String> list = new ArrayList<String>();
		for(int i=0;i<1000000;i++){
			taskId=i+1L;
			UUID uuid = UUID.randomUUID();
			String rquestId=uuid.toString();
			EngineTaskInfo taskInfo=new EngineTaskInfo();
			taskInfo.setId(taskId);
			taskInfo.setProductType("zjj_test");
			taskInfo.setAppName("uzone");
			taskInfo.setAppRequestId(rquestId);
			taskInfo.setAppCallBack("");
			taskInfo.setAppParams("{\"income\":5000,\"is_balck\":0,\"is_white_lq\":1}");
			taskInfo.setBeginTime(EngineUtil.get14CurrentDateTime());
			taskInfo.setEndTime(EngineUtil.get14CurrentDateTime());
			taskInfo.setAssignStep(2);
			taskInfo.setStatus("2");
			taskInfo.setFailTimes(0);
			taskInfo.setPriority(999);
			taskInfo.setChannel("http");
			taskInfo.setEndStep("12");
			taskInfo.setRuleVersion("20161128152821");
			taskInfo.setAddtime(new Timestamp(System.currentTimeMillis()));
		
			 Map<String,Object> task2step = new HashMap<String, Object>();
			for(int j=0;j<10;j++){
				String stepKey=UUID.randomUUID().toString();
				int stepNum=j+1;
				EngineStepInfo stepInfo=new EngineStepInfo();
				stepInfo.setId(stepId);
				stepInfo.setTaskId(taskId);
				stepInfo.setTaskTime(EngineUtil.get14CurrentDateTime());
				stepInfo.setAppName("uzone");
				stepInfo.setAppRequestId(rquestId);
				stepInfo.setStepNum(stepNum);
				stepInfo.setStepBean("sdfsdf");
				stepInfo.setStepName("sdfsdfs");
				stepInfo.setStatus("1");
				stepInfo.setConsume(20);
				stepInfo.setAddtime(new Timestamp(System.currentTimeMillis()));
				jedis.set("step:"+stepKey, stepInfo);
				task2step.put(rquestId,stepKey);
				stepId++;
			}
			jedis.setHash("task2step:"+rquestId, task2step);
			jedis.set("task:"+rquestId, taskInfo);
			list.add(rquestId);
			System.out.println("插入第："+i);
		}
		jedis.set("zjj-redis-test", list);
		
	}
	
	@Test
	public void run5() throws Exception {	
		TestSringDataJedis jedis = new TestSringDataJedis(redisTemplate);
//	    List<String> asda=(List<String>) jedis.get("zjj-redis-test");
////	    TestList l=(TestList) jedis.get(key_list);
//	    for (int i = 0; i < asda.size(); i++) {
//	        System.out.println(i + " = " + asda.get(i));
//	    }
		
		EngineTaskInfo taskInfo=(EngineTaskInfo)jedis.get("task:"+"224");
		System.out.println(taskInfo);
	    Map<String,Object> getMap = (Map<String,Object>)jedis.getHash(taskInfo.getId().toString());
	    for(String key:getMap.keySet()){
	    	String resl=(String) getMap.get(key);
	    	 System.out.println(key + " = " + resl);
	    	 EngineStepInfo stepInfo=(EngineStepInfo)jedis.get("step:"+key);
	    	 System.out.println(stepInfo);
	    	 
	    }
		
	}
	private void  classreturn(Map<String,T> asd,Object a){

	}
	
	@Test
	public void run6() throws Exception {	
		String jsonStr="{" ;
		jsonStr+=   "\"age_wxx\" : 5464654654564564564564564564456456,";
		jsonStr+=   "\"comp_place_hit_wxx\" : \"kkk\",";
		jsonStr+=   "\"income_wxx\" : true,";
		jsonStr+=   "\"list1\" : [20,30],";
		jsonStr+=   "\"list2\" : [true,false],";
		jsonStr+=   "\"list3\" : [\"aa\",\"bb\"],";
		jsonStr+=   "}";
		String appParamStr=jsonStr;
		EngineParam parameter=new EngineParam(100);
		if(StringUtils.isNotBlank(appParamStr)){
			Map<String,Object> appParams = JSONObject.parseObject(appParamStr);
			parameter.putAll(appParams);
			Object a=parameter.get("age_wxx");
			System.out.println(a.getClass().getName());
			Map<String,Object> sda=new HashMap<String, Object>();
			sda.put("asda", Integer.class);
			System.out.println(((BigDecimal) a));
			if(a instanceof Integer){
				System.out.println(((Integer) a).intValue());
			}
			Object b=parameter.get("income_wxx");
			System.out.println(b.getClass().getName());
			if(b instanceof Boolean){
				System.out.println(((Boolean) b).booleanValue());
			}
			Object c=parameter.get("comp_place_hit_wxx");
			System.out.println(c.getClass().getName());
			if(c instanceof String){
				System.out.println(((String) c).toString());
			}
			Object d=parameter.get("list3");
			System.out.println(d.getClass().getName());
			
			if(d instanceof List<?>){
				System.out.println(((List<?>) d).toString());
			}
			
		}	

	}
	
	@Test
	public void run7() throws Exception {
		System.out.println(new BigDecimal(new Short((short) 1).toString()));
		System.out.println(new BigDecimal(new Integer(1).toString()));
		System.out.println(new BigDecimal(new Long(1L).toString()));
		System.out.println(new BigDecimal(new Float(1L).toString()));
		System.out.println(new BigDecimal(new Double(2.35).toString()));
		System.out.println(new BigDecimal(new BigInteger("45646464").toString()));

	}
	@Test
	public void run8() throws Exception {
		List<JSONObject> selfList = engRuleModelDao.getRuleModel(RuleModelTable.SELF, "zjj_test", null, null);		
		EngineParseManager engineParseManager = new EngineParseManager();
		Map<String,EngineCpnt> map=engineParseManager.parse(selfList, "aa321f89-25d6-40b4-9dad-f7aa41545zjj");
		System.out.println(map.toString());
		//System.out.println((EngineScoreCardV2Step)map.get("b4289c7e-0907-4278-9505-10f6ea02b10e"));
		
		JSONObject input = new JSONObject();		
		input.put("age", 1);
		//input.put("is_new_user_flow", true);
		input.put("job_office", "1");
		input.put("home_address_city", 1234);
		input.put("company_address_city", "1");
		input.put("is_user_phone_match", 1);
		
		JSONObject output = new JSONObject();
		output.put("is_new_user_flow", 3);	
		List<String> list1=new ArrayList<String>();
		list1.add("1");
		output.put("newuser_zr1_result", list1);
		List<String> list2=new ArrayList<String>();
		list2.add("1");
		list2.add("2");
		list2.add("3");
		output.put("olduser_zr1_result", list2);
		List<String> list3=new ArrayList<String>();
		list3.add("1");
		list3.add("2");
		list3.add("3");
		list3.add("4");
		output.put("olduser_zr2_result", list3);
		List<String> list4=new ArrayList<String>();
		list4.add("89");
		output.put("olduser_risk_result", list4);		
		
		logger.info("moduleTest result : {}",
		engineWorkspaceManager.moduleTest("zhujunjie", "zjj_test", "aa321f89-25d6-40b4-9dad-f7aa41545zjj", input.toJSONString(), output.toJSONString()));
	
	}
	
	
	@Test
	public void run9() throws Exception {
	  String [] array=StringUtils.split("asdasda", ",");
	  for(String paramter:array){
		  System.out.println(paramter);
	  }
	  
	  System.out.println(StringUtils.indexOf("aa", "dfgdfgdfgd"));
	  
	  String asd=null;
	  Object ob=asd;
	  String kkk=(String) ob;
	  System.out.println(kkk);
	  
	  String str="[-2.3,345)";
      Pattern p = Pattern.compile("^(\\[|\\()+(\\-?([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)+|\\*),(\\-?([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)+|\\*)(\\]|\\))$");  
      Matcher m = p.matcher(str);  	  
	  System.out.println(m.matches());
	  
	  System.out.println(StringUtils.substring(str, 0));
	  
	  long ggg=34534L;
	  String asda=ggg+"";
	  Map<String,Object> appParams = JSONObject.parseObject("{\"a\":1234234234234234234232342,\"b\":1.1,\"c\":0.25555,\"d\":[0.25555,234,2.4564,234]}");
	  for(String key:appParams.keySet()){
		  Object obj=appParams.get(key);
		  System.out.println(obj.getClass());
	  }
	  
	  EngineParam param = new EngineParam(100);
	  for(String key:param.keySet()){
		  System.out.println("******");
		  System.out.println(param.get(key));
	  }
	  
	  System.out.println(new BigDecimal("01.12000"));

	}
	
}
