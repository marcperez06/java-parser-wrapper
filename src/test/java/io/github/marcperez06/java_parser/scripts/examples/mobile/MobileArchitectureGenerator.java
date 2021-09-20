package io.github.marcperez06.java_parser.scripts.examples.mobile;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;

import io.github.marcperez06.java_parser.core.MyJavaParser;
import io.github.marcperez06.java_parser.core.factory.ParametersFactory;


public class MobileArchitectureGenerator {
	
	private final String importBasePackage = "example.package.";

	private String className;
	private String packageName;
	private String actionsClassName;
	private String elementsClassName;
	private String platformName;
	private String packageScope;
	
	public MobileArchitectureGenerator(String className, String packageName, String platformName) {
		this.className = className;
		this.packageName = packageName;
		this.actionsClassName = this.className + "Actions";
		this.elementsClassName = this.className + "Elements";
		this.platformName = platformName;
		this.packageScope = "main";
	}
	
	public void setPackageScope(String packageScope) {
		if (packageScope != null) {
			this.packageScope = packageScope;
		}
	}
	
	public void execute() {
		if (this.className != null && !this.className.isEmpty()) {
			/*
			this.generateActivity();
			this.generateElements();
			this.generateActions();
			this.generateProcess();
			*/
		}
	}
	
	private MyJavaParser initJavaParser(String className) {
		return initJavaParser(className, null);
	}
	
	private MyJavaParser initJavaParser(String className, List<String> imports) {
		MyJavaParser parser = new MyJavaParser(className, this.packageName);
		parser.setImports(imports);
		parser.generateClass();
		return parser;
	}
	
	private List<String> getRequiredImports() {
		List<String> imports = new ArrayList<String>();
		imports.add("io.appium.java_client.AppiumDriver");
		return imports;
	}
	
	private String getSavePath(String className, String packageName) {
		String fileSeparator = System.getProperty("file.separator");
		String baseDir = System.getProperty("user.dir") + fileSeparator;
		baseDir += "src" + fileSeparator + this.packageScope + fileSeparator + "java" + fileSeparator;
		String packagePath = packageName.replaceAll("\\.", "\\" + fileSeparator);
		String savePath = baseDir + packagePath + fileSeparator + className + ".java";
		return savePath;
	}
	
	/*
	
	private void generateActivity() {
		List<String> imports = this.getRequiredImports();
		this.addImportsForActivity(imports);
		
		MyJavaParser parser = this.initJavaParser(this.className, imports);

		if (this.platformName.equalsIgnoreCase("android")) {
			parser.extendClass(AndroidDevice.class);
		} else if (this.platformName.equalsIgnoreCase("ios")) {
			parser.extendClass(IOSDevice.class);
		}
		
		parser.createVariable("actions", this.actionsClassName, Keyword.PUBLIC);
		parser.createVariable("elements", this.elementsClassName, Keyword.PUBLIC);
		parser.createDefaultConstructor(Keyword.PUBLIC);
		
		String extraBodyInConstructor = "this.initializeActivity();";
		
		this.createAppiumDriverConstructor(parser, extraBodyInConstructor);
		this.createInitializeActivityMethod(parser);
		
		String savePath = this.getSavePath(this.className, this.packageName);
		parser.deleteAndSaveClass(savePath);
	}
	
	private void addImportsForActivity(List<String> imports) {
		//imports.add(this.importBasePackage + "core.drivers.Driver");
		imports.add("io.appium.java_client.pagefactory.AppiumFieldDecorator");
		imports.add("org.openqa.selenium.support.PageFactory");
		imports.add(this.importBasePackage + "core.application.screen.WebDriverManager");
	}
	
	private void createAppiumDriverConstructor(MyJavaParser parser, String extraBody) {
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("driver", AppiumDriver.class);
		parser.createConstructor(extraBody, parameters, Keyword.PUBLIC);
	}
	
	private void createInitializeActivityMethod(MyJavaParser parser) {
		String methodName = "initializeActivity";

		//String methodBody = "String packageName = this.getClass().getPackage().getName();\n";
		//methodBody += "String key = packageName + \"." + this.actionsClassName + "\";\n";
		String methodBody = "WebDriverManager.addWebDriver(this.driver);\n";
		methodBody += "this.elements = new " + this.elementsClassName + "();\n";
		// Need to call initElements, because test class only can have 1 empty constructor
		methodBody += "this.elements.initElements(this.driver);"; 
		methodBody += "this.actions = new " + this.actionsClassName + "(this.elements);\n";
		
		parser.createMethodIfNotExist(methodName, methodBody, null, Void.TYPE, Keyword.PRIVATE);
	}
	
	private void generateElements() {
		List<String> imports = new ArrayList<String>();
		this.addImportsForElements(imports);

		MyJavaParser parser = this.initJavaParser(this.elementsClassName, imports);
		// Extend elements class from ScreenElements
		parser.extendClass("ScreenElements");
		
		parser.createDefaultConstructor(Keyword.PUBLIC);
		
		MethodDeclaration method = parser.createMethodDeclaration("generateActions", Void.TYPE, Keyword.PUBLIC);
		method.addMarkerAnnotation("Test");
		
		String constructorParams = this.elementsClassName + ".class, " + this.actionsClassName + ".class";

		String body = "MobileActionsGenerator actionsGenerator = new MobileActionsGenerator(" + constructorParams + ");";
		
		if (this.packageScope.equals("test")) {
			body += "actionsGenerator.setPackageScope(\"test\");";
		}
		
		body += "actionsGenerator.execute();";
		BlockStmt block = parser.parseStatement(body);
		parser.setBodyMethod(method, block);
		
		parser.addMethod(method);

		String savePath = this.getSavePath(this.elementsClassName, this.packageName);
		parser.saveClassIfNotExist(savePath);
	}
	
	private void addImportsForElements(List<String> imports) {
		imports.add("org.junit.Test");
		imports.add(this.importBasePackage + "core.shared.java_parser.scripts.mobile.MobileActionsGenerator");
		imports.add(this.importBasePackage + "core.application.screen.mobile.ScreenElements");
	}
	
	private void generateActions() {
		List<String> imports = new ArrayList<String>();
		this.addImportsForActions(imports);
		
		MyJavaParser parser = this.initJavaParser(this.actionsClassName);
		parser.addImports(imports);
		parser.createVariable("elements", this.elementsClassName, Keyword.PRIVATE);
		//parser.createVariable("wait", Wait.class, Keyword.PRIVATE);
		parser.createVariable("wait", WebDriverWait.class, Keyword.PRIVATE);
		//parser.createDefaultConstructor(Keyword.PUBLIC);
		
		String webDriver = "WebDriverManager.getDriver()";
		//String extraBodyInConstructor = "this.wait = new Wait(" + webDriver + ");";
		String extraBodyInConstructor = "this.wait = new WebDriverWait(" + webDriver + ", 5);";
		parser.createConstructor(extraBodyInConstructor, null, Keyword.PUBLIC);
		
		extraBodyInConstructor += "this.elements = elements;";
		this.createActionsConstructor(parser, extraBodyInConstructor);
		
		String savePath = this.getSavePath(this.actionsClassName, this.packageName);
		parser.deleteAndSaveClass(savePath);
	}
	
	private void addImportsForActions(List<String> imports) {
		imports.add(this.importBasePackage + "core.application.screen.WebDriverManager");
		//imports.add(this.importBasePackage + "core.application.wait.Wait");
		imports.add("org.openqa.selenium.support.ui.WebDriverWait");
		imports.add("org.openqa.selenium.support.ui.ExpectedConditions");
	}
	
	private void createActionsConstructor(MyJavaParser parser, String extraBody) {
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("elements", this.elementsClassName);
		parser.createConstructor(extraBody, parameters, Keyword.PUBLIC);
	}
	
	private void generateProcess() {
		String processName = this.className + "Process";

		List<String> imports = this.getRequiredImports();
		this.addImportsForProcess(imports);

		MyJavaParser parser = this.initJavaParser(processName, imports);
		parser.extendClass("MobileBaseProcess");
		
		String variableName = StrinUtils.uncapitalizeWord(this.className);
		parser.createVariable(variableName, this.className, Keyword.PRIVATE);
		
		//parser.createDefaultConstructor(Keyword.PUBLIC);
		
		String extraBodyInConstructor = "this." + variableName + " = new " + this.className + "(driver);";
		this.createAppiumDriverConstructor(parser, extraBodyInConstructor);
		
		MethodDeclaration method = parser.createMethodDeclaration("execute", Void.TYPE, Keyword.PUBLIC);
		method.addMarkerAnnotation("Override");
		method.addThrownException(Exception.class);
		parser.addMethod(method);
		
		String savePath = this.getSavePath(processName, this.packageName);
		parser.saveClassIfNotExist(savePath);
	}
	
	private void addImportsForProcess(List<String> imports) {
		imports.add(this.importBasePackage + "core.application.process.mobile.MobileBaseProcess");
	}

	/**
	 * Generates the architecture for every page, reading an excel or csv file 
	 * (the data extractor must be indicated in configuration.properties), 
	 * where the first line have the value of properties. (Example in csv: "activityName, packageName")
	 * @param path - String path of file with the required data, for create architecture.
	 * @param packageScope - String (Optional), allows to create the architecture in "main" package or "test" package
	 *-/
	public static void generateArchitecture(String path, String...packageScope) {
		EnvironmentProperties properties = PropertiesManager.getInstance().getMobileProperties();
		String extractorName = properties.getConfigurationProperty(MobileEnvProperties.EXTRACTOR);
		TestDataExtractor extractor = TestDataExtractorFactory.createTestDataExtractor(extractorName);
		generateArchitecture(path, extractor, packageScope);
	}
	
	/**
	 * Generates the architecture for every page in the file specified, using the extractor specified,
	 * where the first line have the value of properties. (Example in csv: "activityName, packageName")
	 * @param path - String path of file with the required data, for create architecture.
	 * @param extractor - TestDataExtractor
	 * @param packageScope - String (Optional), allows to create the architecture in "main" package or "test" package
	 *-/
	public static void generateArchitecture(String path, TestDataExtractor extractor, String...packageScope) {
		EnvironmentProperties properties = PropertiesManager.getInstance().getMobileProperties();
		String platformName = properties.getDeviceProperty(MobileEnvProperties.PLATFORM_NAME);
		extractor.setTestDataType(ArchitectureGeneratorData.class.getName());
		List<ArchitectureGeneratorData> listOfAarchitectureData = extractor.getTestData(path);
		for (ArchitectureGeneratorData architectureData : listOfAarchitectureData) {
			String activityName = architectureData.getActivityName();
			String packageName = architectureData.getPackageName();
			generateArchitectureForOneActivity(activityName, packageName, platformName, packageScope);
		}
	}
	
	/**
	 * Generates 4 java files (activityName, activityNameElements, activityNameActions and activityNameProcess) 
	 * and save it in the package specified
	 * @param activityName - String name of the activity
	 * @param packageName - String package where the pages will be created.
	 * @param platformName - String name of mobile platform (Android, IOS, Windows, etc...)
	 * @param packageScope - String (Optional), allows to create the architecture in "main" package or "test" package
	 *-/
	public static void generateArchitectureForOneActivity(String activityName, String packageName,
														String platformName, String...packageScope) {
		
		MobileArchitectureGenerator generator = new MobileArchitectureGenerator(activityName, packageName, platformName);
		
		if (packageScope != null && packageScope.length > 0) {
			generator.setPackageScope(packageScope[0]);	
		}
		
		generator.execute();
	}

	*/
	
}
