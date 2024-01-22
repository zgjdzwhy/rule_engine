package com.mobanker.engine.exectest;


import java.io.FileNotFoundException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
	
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
    
    
//    public static void main(String[] args) throws FileNotFoundException {
//    	String dir = System.getProperty("user.dir");
//    	String filePath = dir+File.separator+"xmlTest"+File.separator+"testProduct.xml";
//    	System.out.println(dir);
//		File file = new File(filePath);
//		Map<String,EngineCpnt> allCpnt = new EngineParseManager().parseAll(new FileInputStream(file));
//		
//		System.out.println(allCpnt.size());	
//		System.out.println(allCpnt);
//	}
   
    public static void main(String[] args) throws FileNotFoundException {
 
	}    
}
