package io.github.marcperez06.java_parser.test;

import java.util.ArrayList;
import java.util.List;

import io.github.marcperez06.java_parser.scripts.examples.test.TestGenerator;
import io.github.marcperez06.java_parser.scripts.examples.web.WebArchitectureGenerator;

public class JavaParserTest {
	
	public static void main(String[] args) {
		
		/*
		List<String> imports = new ArrayList<String>();
		imports.add("org.openqa.selenium.WebElement");
		
		MyJavaParser parser = new MyJavaParser("test", "sogeti.testing_framework_base.core.java_parser");
		parser.setImports(imports);
		CompilationUnit y = parser.generateClass();
		parser.saveClass(System.getProperty("user.dir") + "/src/main/java/sogeti/testing_framework_base/Test.java");
		System.out.println(y.toString());
		
		CompilationUnit x = parser.parseClass(App.class);
		parser.createMethod("test", "System.out.println(\"Hellow\");", null, Void.TYPE, Keyword.PUBLIC);
		System.out.println(x.toString());
		*/
		
		// GENERATE ARCHITECTURE
		//ArchitectureGenerator.generateArchitectureForOnePage("LoginPage", "sogeti.testing_framework_base.core.java_parser.test");
		
		//ActionsGenerator actionsGenerator = new ActionsGenerator(Elements.class, Actions.class);
		//actionsGenerator.execute();
		
		/*
		LoginPage x = new LoginPage();
		x.open("www.google.com");
		System.out.println("finish");
		*/
		
		// GENERATE Web ARCHITECTURE
		//WebArchitectureGenerator.generateArchitectureForOnePage("LoginPage", "sogeti.testing_framework_base.core.java_parser.test");
		//WebArchitectureGenerator.generateArchitecture(Paths.ARCHITECTURE_DATA + "architectureData_taidi_web.xls");
		
		// CUCUMBER GENERATOR
		//String cucumberFilePath = Paths.CUCUMBER_DIR + "shout.feature";
		//CucumberGenerator cucumber = new CucumberGenerator("sogeti.testing_framework_base.core.java_parser.test", cucumberFilePath);
		//cucumber.execute();
		
		// GENERATE Mobile ARCHITECTURE
		//MobileArchitectureGenerator.generateArchitectureForOneActivity("InitializeActivity", "sogeti.testing_framework_base.test_of_activities.teatre_mallorca", "android");
		//MobileArchitectureGenerator.generateArchitecture(Paths.EXCEL_ARCHITECTURE_DATA);
		
		// GENERATE Test ARCHITECTURE
		/*
		String testName = "Test Of Test Generator";
		String packageName = "sogeti.testing_framework_base.test.auto_generated";
		TestGenerator.generateTest(testName, packageName, ExecutionType.WEB);
		*/
		//TestGenerator.generateTestsFromFile(Paths.ARCHITECTURE_DATA + "test_autogeneration.xls");
	}

}
