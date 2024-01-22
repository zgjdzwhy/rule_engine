import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;


public class TestSringDataJedis {
    private static RedisTemplate redisTemplate;

	public TestSringDataJedis(RedisTemplate redisTemplate){
		this.redisTemplate = redisTemplate;
	}
    

    public void set(String key,Object value){
        //ValueOperations 理解成Map<Object,Object>


//        redisTemplate.opsForValue().set("redis-key","I'm test spring-data-redis");
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key,value);
        redisTemplate.expire(key,1,TimeUnit.HOURS);

        //BoundValueOperations的理解对保存的值做一些细微的操作
//        BoundValueOperations boundValueOperations = redisTemplate.boundValueOps(key);
    }
    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }
    public void setLeftList(String key ,List<?> value){
        //ListOperations可以理解为List<Object>
        ListOperations listOperations= redisTemplate.opsForList();
        listOperations.leftPush(key, value);
//                .leftPushAll(value);
    }
    public void setRightList(String key ,List<?> value){
        //ListOperations可以理解为List<Object>
        ListOperations listOperations= redisTemplate.opsForList();
        listOperations.rightPush(key, value);
//                .leftPushAll(value);
    }
    public Object getLeftList(String key){
        //ListOperations可以理解为List<Object>
        return redisTemplate.opsForList().leftPop(key);
    }
    public Object getRightList(String key){
        //ListOperations可以理解为List<Object>
        return redisTemplate.opsForList().rightPop(key);
    }
    public void setSet(String key ,Set<?> value){
        SetOperations setOperations= redisTemplate.opsForSet();
        setOperations.add(key, value);
    }
    public Object getSet(String key){
        return redisTemplate.opsForSet().members(key);
    }


    public void setHash(String key ,Map<String,?> value){
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(key,value);
        redisTemplate.expire(key,1,TimeUnit.HOURS);
    }
    public Object getHash(String key){
        return redisTemplate.opsForHash().entries(key);
    }


    public void delete(String key){
        redisTemplate.delete(key);
    }
//    public void clearAll(){
//        redisTemplate.multi();
//    }
}