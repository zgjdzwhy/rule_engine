

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.mobanker.engine.design.busi.admin.AdminToolService;
import com.mobanker.engine.design.busi.configma.EngineConfigManageService;
import com.mobanker.engine.design.busi.workspace.EngineWorkspaceManager;
import com.mobanker.engine.design.dao.EngProductReleaseFlowDao;
import com.mobanker.engine.design.dao.EngRuleRepositoryDao;
import com.mobanker.engine.design.hotdeploy.EngineRuleModelHotDeployService;
import com.mobanker.engine.design.mongo.assist.RuleModelTable;
import com.mobanker.engine.design.pojo.EngRuleModel;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.exception.EngineException;
import com.mobanker.engine.inner.call.dto.SnapshotBatchRunDto;
import com.mobanker.framework.dto.ResponseEntity;
import com.mobanker.safe.business.contract.DecryptManager;
import com.mobanker.safe.business.contract.EncryptManager;
import com.mobanker.safe.business.dto.DecryptRequest;
import com.mobanker.safe.business.dto.EncryptRequest;
import com.mobanker.safe.business.dto.EncryptResponse;



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
public class TestTjDesign extends TestBase {
 
	private Logger logger = LoggerFactory.getLogger(this.getClass());	

	static EngineWorkspaceManager engineWorkspaceManager = getBean(EngineWorkspaceManager.class);
	//static EngRuleModelDao engRuleModelDao = getBean(EngRuleModelDao.class);
	static EngProductReleaseFlowDao engProductReleaseFlowDao = getBean(EngProductReleaseFlowDao.class);
	static EngineConfigManageService engineConfigManageService = getBean(EngineConfigManageService.class);
	static EngineRuleModelHotDeployService engineRuleModelHotDeployService = (EngineRuleModelHotDeployService) getBean("engineRuleModelHotDeployService");
	static EncryptManager encryptManager = getBean(EncryptManager.class);
	static DecryptManager decryptManager = getBean(DecryptManager.class);
	static RedisTemplate<String,Object> redisTemplate = getBean(RedisTemplate.class);
	static AdminToolService adminToolService = getBean(AdminToolService.class);
	
	
	static Client client = getBean(Client.class);
	static final String INDEX = "test_tj";
	static final String TYPE = "mytype";		
	
	@BeforeClass
	public static void init() {

	}
	
	@Test
	public void run16() throws Exception {
		adminToolService.initSystemFunction();
	}

	@Test
	public void run15() throws Exception {
		adminToolService.functionEs2Hbase();
	}

	@Test
	public void run13() throws Exception {
		engineWorkspaceManager.getRuleZipBytes();
	}

	@Test
	public void run14() throws Exception {
		File zipFile=new File("C:\\Users\\zhujunjie\\AppData\\Local\\Temp\\RuleEngineModelFile\\RuleZipFile\\engineRuleModel.zip");
		byte[] buffer = null;
		
		FileInputStream fis =null;
		ByteArrayOutputStream bos =null;
        try {
			fis = new FileInputStream(zipFile);  
			bos = new ByteArrayOutputStream();  
			byte[] b = new byte[1024];  
			int n;  
			while ((n = fis.read(b)) != -1)  
			{  
			    bos.write(b, 0, n);  
			}  
			buffer = bos.toByteArray(); 
		} catch (IOException e) {
			throw new EngineException("将Zip文件转换成Byte失败",e);
		}finally{
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}  
			}
			if(bos!=null){
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		MultipartFile multiFile=new MockMultipartFile("engineRuleModel.zip","engineRuleModel.zip","zip",buffer);
		engineWorkspaceManager.zipFileUpload(multiFile);;
	}
	
	@Test
	public void run12() throws Exception {
		engineWorkspaceManager.parseProduct(RuleModelTable.SELF, "shoujidai");
		logger.info("测试完成无异常");
	}
	@Test
	public void run2() throws Exception {		
		String product = "shoujidai";
		JSONObject one = new JSONObject();
		one.put("id", "dqdStartStage");
		one.put("type", "EngineStandardStage");
		one.put("name", "test");
		one.put("right", "wangguangming");
		one.put("updatetime", "20160923121212");
		engineWorkspaceManager.removeRuleModel("taojin", product, "dqdZr2ScriptStep");		
	
		
	}	
	
	@Test
	public void urgencyRelease() throws Exception {				
		engineWorkspaceManager.urgencyRelease("taojin", "shoujidai");
	}		
	
	
	@Test
	public void moduleTest() throws Exception {				
		JSONObject input = new JSONObject();		
		input.put("userId", 134234);
		input.put("phone", "13524366905");
		JSONObject output = new JSONObject();
		output.put("score", 11);				
		logger.info("moduleTest result : {}",engineWorkspaceManager.moduleTest("taojin", "shoujidai", "deccaea3-4c01-4cbc-a7d5-ad1ae37285c5", input.toJSONString(), output.toJSONString()));
	}
	
	@Test
	public void wholeTest() throws Exception {				
		JSONObject input = new JSONObject();		
		
		input.put("userId", "56455");
		input.put("borrowNid", "65656565555555");	
		input.put("is_first", true);
		input.put("age", 38);		
		input.put("qh_score", 550);
		input.put("zm_score", 700);
				
		JSONObject output = new JSONObject();
		//output.put("score", 11);				
		logger.info("moduleTest result : {}",engineWorkspaceManager.wholeTest("taojin", "taojinZy2", input.toJSONString(), output.toJSONString()));
	}	

	@Test
	public void copyCpnt() throws Exception {
//		List<EngineCpntListDisplay> list = engineWorkspaceManager.copyCpnt("taojin", "taojin","shoujidai", "uzone", "EnginePolicyTreeStep_taojin_20161019170958");
//		logger.info("list size:{}",list.size());		
	}	
	
	@Test
	public void removeRuleModel() throws Exception {
		engineWorkspaceManager.removeRuleModel("taojin","uzone","fed6903c-46e1-4fbd-93d8-019e6ca9c31d");
		logger.info("删除完成");		
	}	
	
	
	@Test
	public void testRedisList() throws Exception {
		EngineTaskInfo taskInfo = new EngineTaskInfo();
		taskInfo.setRuleVersion("test_version");
		taskInfo.setId(1L);
		taskInfo.setProductType("testProduct");
		taskInfo.setAppName("testApp");
		taskInfo.setAppRequestId("testRequestId");		
		long t = System.currentTimeMillis();
		long count = 0;
		for(int i = 0;i < 1000000; i++){
			redisTemplate.opsForList().leftPush("testList", taskInfo);			
			count++;
			if(count % 10000 == 0){
				System.out.println("已经插入"+count+",耗时:"+(System.currentTimeMillis()-t));
				t = System.currentTimeMillis();
			}
		}
		redisTemplate.expire("testList", 3, TimeUnit.HOURS);
		
		System.out.println("结束");
		
	}

	@Test
	public void testRedisExists() throws Exception {				
		System.out.println("aaaaaaaaaaaa"+redisTemplate.hasKey("batchRun_taojinZy_nowReturn"));		
	}	
	
	
	@Test
	public void testRedisHash() throws Exception {
		EngineTaskInfo taskInfo = new EngineTaskInfo();
		taskInfo.setRuleVersion("test_version");
		taskInfo.setId(1L);
		taskInfo.setProductType("testProduct");
		taskInfo.setAppName("testApp");
		taskInfo.setAppRequestId("testRequestId");		
		long t = System.currentTimeMillis();
		long count = 0;
		for(int i = 0;i < 1; i++){
			HashOperations<String, String, Object> opsHash = redisTemplate.opsForHash();
			
			Map<String,Object> abc = new HashMap<>();
			abc.put("1", taskInfo);
			abc.put("2", 3L);
			abc.put("3", "55");
			count++;
			opsHash.putAll("testMap", abc);
						
			opsHash.put("testMap", "sum", 1L);

			if(count % 10000 == 0){
				System.out.println("已经插入"+count+",耗时:"+(System.currentTimeMillis()-t));
				t = System.currentTimeMillis();
			}
			
			
			System.out.println(opsHash.get("testMap", "2").getClass());
		}
		//redisTemplate.expire("testList", 3, TimeUnit.HOURS);
		
		
		
		
		System.out.println("结束");
		
	}

	@Test
	public void backUpRuleModel() throws Exception {
		engineRuleModelHotDeployService.backUpRuleModel();
	}
	
	@Test
	public void encrypt() throws Exception {
		EncryptRequest request = new EncryptRequest();
		request.setBusinessCode("qianlong");
		request.setType("_3DES");
		request.setData("asdfasdfasdfasdf{}dafdasdfdsafdsaf");
		request.setBusinessType("dataEncrypt");
		ResponseEntity<EncryptResponse> res = encryptManager.encrypt(request);
		//System.out.println(res);
		
		System.out.println(res.getData().getCiphertext());
		System.out.println(res.getData().getBusinessId());
		System.out.println(res.getData().getCode());
		
		System.out.println("---------------------------------------");
		
		DecryptRequest decryptRequest = new DecryptRequest();
		decryptRequest.setBusinessId(res.getData().getBusinessId());
		decryptRequest.setData(res.getData().getCiphertext());
		ResponseEntity<String> deRes = decryptManager.decrypt(decryptRequest);
		System.out.println(deRes);
		
		
	}
	
	@Test
	public void batchTest() throws Exception {
		SnapshotBatchRunDto dto = new SnapshotBatchRunDto();		
		dto.setProductType("taojinZy");
		dto.setBeginTime("2012-01-13 13:45:23");
		dto.setEndTime("2018-11-13 13:45:23");
		engineWorkspaceManager.snapshotBatchRun(dto);
	}
	


	public void testDao() throws Exception {
		
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaa");
		
		EngRuleRepositoryDao dao = getBean(EngRuleRepositoryDao.class);
		
		EngRuleModel one = new EngRuleModel();
		one.setProductType("test");
		one.setContent("test");
		one.setCpntType("test");
		one.setEncryptKey("test");
		one.setFreshPoint("test");
		one.setRepositoryType("test");
		one.setRuleRight("test");
		one.setRuleId(UUID.randomUUID().toString());
		one.setRuleName("test");
		one.setStatus("0");
		one.setVersion("test");
		int i = dao.insert(one);
		
		System.out.println(i+":"+one.getId());
	}	

	

	
}
