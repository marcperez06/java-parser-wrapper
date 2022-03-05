package io.github.marcperez06.java_parser.test;

import io.github.marcperez06.java_parser.scripts.examples.swagger.SwaggerInformationGenerator;
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
    	SwaggerInformationGenerator.generateInformation("https://petstore.swagger.io/v2/swagger.json", new JavaParserTest().getClass().getPackage().getName() + "auto_generation", "test");
    	assertTrue( true );
    }
}
