

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;



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
public class TestTjEs extends TestBase {

	private Logger logger = LoggerFactory.getLogger(this.getClass());	


	static Client client = getBean(Client.class);
	static final String INDEX = "test_tj";
	static final String TYPE = "mytype";		
	
	@BeforeClass
	public static void init() {

	}
	
	public static void main(String[] args) {

	}
	
	@Test
	public void testEsCreate() throws Exception {
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaa");
		IndexResponse indexResponse = client.prepareIndex(INDEX, TYPE).setCreate(true).execute().get();
		logger.info("索引创建,id:"+indexResponse.getId());
	
	}	
	
	@Test
	public void testEsUpd() throws Exception {
		
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaa");
		
		JSONObject json = new JSONObject();
					
		XContentFactory.jsonBuilder().startObject().field("interest", "music").endObject();
				
		UpdateResponse updateResponse = client.prepareUpdate("test_tj", "mytype","myId")
				.setDoc(XContentFactory.jsonBuilder().startObject()
						.field("interest", "music")
						.field("newList", Arrays.asList(new String[]{"new1","new2"}))
						.endObject())
				.execute()
				.get();
		
		logger.info("索引数据成功,id:"+updateResponse.getId());
		
		
	}	
	
	
	@Test
	public void testEsIns() throws Exception {
		
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaa");
		
		JSONObject json = new JSONObject();
					
		
		
		json.put("stringTest", "abc");
		json.put("integerTest", 123);
		json.put("longTest", 12300000000000L);
		json.put("listTest", Arrays.asList(new String[]{"list1","list2"}));
		json.put("arrTest", new String[]{"array1","array2","array3"});
		json.put("dateTest", new Date(1495176385503L)); //2017-05-19 14:46
		json.put("textTest", "我是陶金，我在前隆工作");
		json.put("textTest6", "我是陶金，我在前隆工作");
		
		json.put("phoneListTest", new String[]{"13524366905","2222222","33333333","1235456",});
		
		
		json.put("arrTxtTest", new String[]{"我是陶金","我在前隆工作","上班很忙但很开心"});
		
		
		
		
		IndexResponse indexResponse = client.prepareIndex("test_tj", "mytype","myId")				
		        .setSource(json.toJSONString())
		        .get();
		
		logger.info("索引数据成功,id:"+indexResponse.getId());
		
		
	}		
	
	
	@Test 
	public void testEsGetMapping() throws Exception {
		
		GetMappingsRequest request = new GetMappingsRequest();
		request.indices(INDEX);
		request.types(TYPE);
		
		GetMappingsResponse res = client.admin().indices().getMappings(request).get();
		
		ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> mappings = res.mappings();
	
		Iterator<String> indexes = mappings.keysIt();
		while(indexes.hasNext()){
			String index = indexes.next();
			ImmutableOpenMap<String, MappingMetaData> indexMappings = mappings.get(index);
			
			Iterator<String> types = indexMappings.keysIt();
			while(types.hasNext()){
				String type = types.next();
				MappingMetaData mappingMetaData = indexMappings.get(type);
				
				Object o = mappingMetaData.sourceAsMap().get("properties");
				Map<String,Object> map = (Map<String,Object>) o;
				for(String field : map.keySet()){
					System.out.println("field==>"+field+":"+map.get(field));
				}
	
			}	
		}
	
	}
	
	@Test 
	public void testEsGetMapping2() throws Exception {
		
	    ClusterState cs = client.admin().cluster().state(new ClusterStateRequest().indices(INDEX)).get().getState();
	    IndexMetaData imd = cs.getMetaData().index(INDEX);  
	    MappingMetaData mappingMetaData = imd.mapping(TYPE);  			
		Object o = mappingMetaData.sourceAsMap().get("properties");
		Map<String,Object> map = (Map<String,Object>) o;
		for(String field : map.keySet()){
			System.out.println("field==>"+field+":"+map.get(field));
		}

	}
	
	
	@Test
	public void testEsQuery() throws Exception {
		
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaa");
		BoolQueryBuilder bqb = QueryBuilders.boolQuery();
		
		QueryBuilder qb = null;
		QueryBuilder filter = null;
		
		//数组和列表搜索
		//数组英文完全匹配
//		qb = bqb.must(QueryBuilders.termQuery("arrTest", "array1"));
		//数组英文模糊查询
//		qb = QueryBuilders.queryStringQuery("arrTest:arr*ay*");
	
		//中文相等搜索，必须把mapping设置为keyword，否则每个中文都会进行分词
//		qb = bqb.must(QueryBuilders.termQuery("textTest6", "我是陶金，我在前隆工作"));
//		//中文like搜索
//		qb = bqb.must(QueryBuilders.matchQuery("textTest6", "我是陶金，我"));
		//模糊匹配keyword
		//qb = QueryBuilders.queryStringQuery("textTest6:我是陶金*");
		
		
		//时间搜索 暂时没发现支持时间点的搜索，比如15点到17点
//		qb = QueryBuilders.rangeQuery("dateTest").gte("2017-05-19").timeZone("+08:00");
//		qb = QueryBuilders.rangeQuery("dateTest").gte("2017-05-19T14:47:25").timeZone("+08:00");
//		qb = QueryBuilders.queryStringQuery("dateTest:[2017-05-19 TO 2017-05-29]").timeZone("+08:00");
		
		//查询含有某些电话的用户
//		qb = QueryBuilders.queryStringQuery("phoneListTest:13524366905|abc|13624366905");
		
		qb = QueryBuilders.queryStringQuery("integerTest:[110 TO 150] AND stringTest:adbc|abc");		
		
		//filter = QueryBuilders.queryStringQuery("integerTest:[110 TO 150] AND stringTest:adbc");
		
//		qb = QueryBuilders.queryStringQuery("dateTest:[2017-05-19T14:47:25 TO 2017-05-19T15:47:25]").timeZone("+08:00");
						
		
		SearchRequestBuilder srb = client.prepareSearch(INDEX)
				.setTypes(TYPE)
                .setSearchType(SearchType.QUERY_AND_FETCH)
                
                .setFrom(1)
                .setSize(100)
                .setExplain(true);
		
		if(qb != null) srb.setQuery(qb);
		if(filter != null) srb.setPostFilter(filter);
		
		SearchResponse searchresponse = srb.execute().actionGet();  
		
		long hitNum = searchresponse.getHits().getTotalHits();
		List<JSONObject> list = new LinkedList<JSONObject>();
		if(hitNum > 0){			
			for(SearchHit hit : searchresponse.getHits().getHits()){		
				
				System.out.println(hit.getSource());
	
			}
		}
		
	}		
}
