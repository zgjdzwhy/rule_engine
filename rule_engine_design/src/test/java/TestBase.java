

import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TestBase {
	static ApplicationContext context;

	//"applicationContext-engine.xml",
	@BeforeClass
	public static void initContext() {
		context = new ClassPathXmlApplicationContext("applicationContext.xml");
	}
	
	public static Object getBean(String id){
		return context.getBean(id);
	}
	
	public static <T> T getBean(Class<T> clazz){
		return context.getBean(clazz);
	}
	
}
