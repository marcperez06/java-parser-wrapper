package io.github.marcperez06.java_parser.scripts.examples.test;

import java.util.List;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.utils.Utils;

import io.github.marcperez06.java_parser.core.JavaParserWrapper;
import io.github.marcperez06.java_parser.resources.ExecutionType;
import io.github.marcperez06.java_parser.scripts.examples.test.strategy.ApiStrategyTestGenerator;
import io.github.marcperez06.java_parser.scripts.examples.test.strategy.BaseStrategyTestGenerator;
import io.github.marcperez06.java_parser.scripts.examples.test.strategy.MobileStrategyTestGenerator;
import io.github.marcperez06.java_parser.scripts.examples.test.strategy.StrategyTestGenerator;
import io.github.marcperez06.java_parser.scripts.examples.test.strategy.WebStrategyTestGenerator;

public class TestGenerator {
	
	private String testName;
	private String packageName;
	private String packageScope;
	private ExecutionType executionType;
	private StrategyTestGenerator strategy;
	
	public TestGenerator(String testName, String packageName) {
		boolean haveInformation = (testName != null && !testName.isEmpty());
		haveInformation &= (packageName != null && !packageName.isEmpty());
		
		if (haveInformation) {
			this.testName = this.clearTestName(testName);
			this.packageName = packageName;
			this.packageScope = "test";
			this.executionType = ExecutionType.NULL;
			this.strategy = null;
		}
		
	}
	
	public TestGenerator(String testName, String packageName, ExecutionType executionType) {
		this(testName, packageName);
		this.executionType = executionType;
	}
	
	public String clearTestName(String testName) {
		String clearedName = testName.replaceAll(" ", "").trim();
		clearedName = Utils.capitalize(clearedName);
		return clearedName;
	}
	
	public void setPackageScope(String packageScope) {
		if (packageScope != null) {
			this.packageScope = packageScope;
		}
	}
	
	public void setExecutionType(ExecutionType executionType) {
		this.executionType = executionType;
	}

	public void execute() {
		if (this.testName != null && !this.testName.isEmpty()) {
			JavaParserWrapper parser = new JavaParserWrapper(this.testName, this.packageName);
			parser.setPackageScope(this.packageScope);
			parser.parseOrCreateClass();
			this.chooseTestStrategy();
			this.extendTestClass(parser);
			this.addImports(parser);
			this.createTestInfoAnnotation(parser);
			this.createVariables(parser);
			this.createBeforeMethod(parser);
			this.createTestMethod(parser);
			this.createAfterMethod(parser);
			String savePath = this.getSavePath(this.packageName, this.testName);
			parser.saveClassIfNotExist(savePath);
		}
	}
	
	private void chooseTestStrategy() {
		if (this.executionType == ExecutionType.NULL) {
			this.strategy = new BaseStrategyTestGenerator();
		} else if (this.executionType == ExecutionType.API) {
			this.strategy = new ApiStrategyTestGenerator();
		} else if (this.executionType == ExecutionType.WEB) {
			this.strategy = new WebStrategyTestGenerator();
		} else if (this.executionType == ExecutionType.MOBILE) {
			this.strategy = new MobileStrategyTestGenerator();
		}
	}
	
	private boolean isStrategyDefined() {
		return (this.strategy != null);
	}
	
	private void addImports(JavaParserWrapper parser) {
		parser.addImport("sogeti.testing_framework_base.core.shared.annotations.TestInfo");
		parser.addImport("sogeti.testing_framework_base.core.shared.report.ReportTestManager");
		if (this.isStrategyDefined()) {
			List<String> imports = this.strategy.getImports();
			parser.addImports(imports);
		}
	}
	
	private void extendTestClass(JavaParserWrapper parser) {
		if (this.executionType == ExecutionType.NULL) {
			parser.extendClass("BaseTest");
		} else if (this.executionType == ExecutionType.API) {
			parser.extendClass("ApiTest");
		} else if (this.executionType == ExecutionType.WEB) {
			parser.extendClass("WebTest");
		} else if (this.executionType == ExecutionType.MOBILE) {
			parser.extendClass("MobileTest");
		}
	}
	
	private void createTestInfoAnnotation(JavaParserWrapper parser) {
		parser.createAnnotationClass("TestInfo");
	}
	
	private void createVariables(JavaParserWrapper parser) {
		if (this.isStrategyDefined()) {
			this.strategy.createVariables(parser);
		}
	}
	
	private void createBeforeMethod(JavaParserWrapper parser) {
		if (this.isStrategyDefined()) {
			parser.addImport("org.junit.Before");
			String methodName = "beforeTest";
			String methodBody = this.strategy.getBeforeMethod();
			MethodDeclaration method = parser.createMethodDeclaration(methodName, Void.TYPE, Keyword.PUBLIC);
			method.addAnnotation("Before");
			parser.addBodyToMethod(method, methodBody);
			parser.addMethodIfNotExist(method);
		}
	}
	
	private void createTestMethod(JavaParserWrapper parser) {
		if (this.isStrategyDefined()) {
			parser.addImport("org.junit.Test");
			String methodName = Utils.decapitalize(this.testName);
			String methodBody = this.strategy.getTestMethod();
			MethodDeclaration method = parser.createMethodDeclaration(methodName, Void.TYPE, Keyword.PUBLIC);
			method.addAnnotation("Test");
			parser.addBodyToMethod(method, methodBody);
			parser.addMethodIfNotExist(method);
		}
	}
	
	private void createAfterMethod(JavaParserWrapper parser) {
		if (this.isStrategyDefined()) {
			String methodName = "tearDown";
			String methodBody = this.strategy.getAfterMethod();
			if (methodBody != null && !methodBody.isEmpty()) {
				parser.addImport("org.junit.After");
				MethodDeclaration method = parser.createMethodDeclaration(methodName, Void.TYPE, Keyword.PUBLIC);
				method.addAnnotation("After");
				parser.addBodyToMethod(method, methodBody);
				parser.addMethodIfNotExist(method);
			}
		}
	}
	
	private String getSavePath(String packageName, String className) {
		String fileSeparator = System.getProperty("file.separator");
		String baseDir = System.getProperty("user.dir") + fileSeparator;
		baseDir += "src" + fileSeparator + this.packageScope + fileSeparator + "java" + fileSeparator;
		String packagePath = packageName.replaceAll("\\.", "\\" + fileSeparator);
		String savePath = baseDir + packagePath + fileSeparator + className + ".java";
		return savePath;
	}
	
	/**
	 * Generates the test classes, reading an excel or csv file 
	 * (the data extractor must be indicated in configuration.properties), 
	 * where the first line have the value of properties. (Example in csv: "testName, packageName, testType")
	 * @param path - String path of file with the required data, for create tests.
	 * @param packageScope - String (Optional), allows to create the architecture in "main" package or "test" package
	 */
	/*
	public static void generateTestsFromFile(String path, String...packageScope) {
		EnvironmentProperties properties =  PropertiesManager.getEnvironmentProperties(EnvironmentType.DEFAULT);
		String extractorName = properties.getConfigurationProperty(CommonEnvProperties.EXTRACTOR);
		TestDataExtractor extractor = TestDataExtractorFactory.createTestDataExtractor(extractorName);
		generateTestsFromFile(path, extractor, packageScope);
	}
	*/
	
	/**
	 * Generates the test classes in the file specified, using the extractor specified,
	 * where the first line have the value of properties. (Example in csv: "testName, packageName, testType")
	 * @param path - String path of file with the required data, for create architecture.
	 * @param extractor - TestDataExtractor
	 * @param packageScope - String (Optional), allows to create the architecture in "main" package or "test" package
	 */
	/*
	public static void generateTestsFromFile(String path, TestDataExtractor extractor, String...packageScope) {
		extractor.setTestDataType(TestGeneratorData.class.getName());
		List<TestGeneratorData> listOfTestData = extractor.getTestData(path);
		for (TestGeneratorData testData : listOfTestData) {
			ExecutionType executionType = ExecutionType.getEnum(testData.getTestType());
			generateTest(testData.getTestName(), testData.getPackageName(), executionType, packageScope);
		}
	}
	*/
	
	/**
	 * Generate the test class with ExecutionType NULL by default
	 * @param testName - String
	 * @param packageName - String
	 * @param packageScope - String (Optional), allows to create the architecture in "main" package or "test" package
	 */
	public static void generateTest(String testName, String packageName, String...packageScope) {
		generateTest(testName, packageName, ExecutionType.NULL, packageScope);
	}
	
	/**
	 * Generate the test class
	 * @param testName - String
	 * @param packageName - String
	 * @param executionType - ExecutionType [enum]
	 * @param packageScope - String (Optional), allows to create the architecture in "main" package or "test" package
	 */
	public static void generateTest(String testName, String packageName, ExecutionType executionType, String...packageScope) {
		TestGenerator generator = new TestGenerator(testName, packageName, executionType);
		
		if (packageScope != null && packageScope.length > 0) {
			generator.setPackageScope(packageScope[0]);	
		}
		
		generator.execute();
	}

}
