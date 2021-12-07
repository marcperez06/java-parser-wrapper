package io.github.marcperez06.java_parser.scripts.examples.web;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;

import io.github.marcperez06.java_parser.core.JavaParserWrapper;
import io.github.marcperez06.java_parser.core.factory.ParametersFactory;
import io.github.marcperez06.java_parser.resources.objects.architecture.ArchitectureGeneratorData;

public class WebArchitectureGenerator {
	
	private final String importBasePackage = "example.";
	
	private String className;
	private String packageName;
	private String actionsClassName;
	private String elementsClassName;
	private String packageScope;
	
	public WebArchitectureGenerator(String className, String packageName) {
		this.className = className;
		this.packageName = packageName;
		this.actionsClassName = this.className + "Actions";
		this.elementsClassName = this.className + "Elements";
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
			this.generatePage();
			this.generateElements();
			this.generateActions();
			this.generateProcess();
			*/
		}
	}
	
	private JavaParserWrapper initJavaParser(String className, List<String> imports) {
		JavaParserWrapper parser = new JavaParserWrapper(className, this.packageName);
		parser.setImports(imports);
		parser.generateClass();
		return parser;
	}
	
	private List<String> getRequiredImports() {
		List<String> imports = new ArrayList<String>();
		imports.add("org.openqa.selenium.WebDriver");
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
	
	private void generatePage() {
		List<String> imports = this.getRequiredImports();
		this.addImportsForPage(imports);
		
		MyJavaParser parser = this.initJavaParser(this.className, imports);
		parser.extendClass(Page.class);
		parser.createVariable("elements", this.elementsClassName, Keyword.PUBLIC);
		parser.createVariable("actions", this.actionsClassName, Keyword.PUBLIC);
		parser.createDefaultConstructor(Keyword.PUBLIC);

		String extraBodyInConstructor = "this.initializePage();";

		this.createWebDriverConstructor(parser, extraBodyInConstructor);
		this.createEnumDriverConstructor(parser, extraBodyInConstructor);
		this.createStringDriverConstructor(parser, extraBodyInConstructor);
		this.createInitializePageMethod(parser);
		this.createRefreshPageMethod(parser);
		
		String savePath = this.getSavePath(this.className, this.packageName);
		parser.deleteAndSaveClass(savePath);
	}

	private void addImportsForPage(List<String> imports) {
		imports.add(this.importBasePackage + "core.application.drivers.Driver");
		imports.add(this.importBasePackage + "core.application.screen.WebDriverManager");
	}
	
	private void createWebDriverConstructor(MyJavaParser parser, String extraBody) {
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("driver", WebDriver.class);
		parser.createConstructor(extraBody, parameters, Keyword.PUBLIC);
	}
	
	private void createEnumDriverConstructor(MyJavaParser parser, String extraBody) {
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("driverType", Driver.class);
		parser.createConstructor(extraBody, parameters, Keyword.PUBLIC);
	}
	
	private void createStringDriverConstructor(MyJavaParser parser, String extraBody) {
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("driverType", String.class);
		parser.createConstructor(extraBody, parameters, Keyword.PUBLIC);
	}
	
	private void createInitializePageMethod(MyJavaParser parser) {
		String methodName = "initializePage";

		//String methodBody = "String packageName = this.getClass().getPackage().getName();";
		//methodBody += "String key = packageName + \"." + this.actionsClassName + "\";";
		String methodBody = "WebDriverManager.addWebDriver(this.driver);";
		methodBody += "this.elements = new " + this.elementsClassName + "();";
		// Need to call initElements, because test class only can have 1 empty constructor
		methodBody += "this.elements.initElements(this.driver);"; 
		methodBody += "this.actions = new " + this.actionsClassName + "(this.elements);";
		
		parser.createMethodIfNotExist(methodName, methodBody, null, Void.TYPE, Keyword.PRIVATE);
	}
	
	private void createRefreshPageMethod(MyJavaParser parser) {
		MethodDeclaration method = parser.createMethodDeclaration("refresh", Void.TYPE, Keyword.PUBLIC);
		method.addMarkerAnnotation("Override");
		parser.callSuperMethod(method);
		String body = "this.elements = null;";
		body += "this.elements = new " + this.elementsClassName + "();";
		body += "this.elements.initElements(this.driver);";
		parser.addBodyToMethod(method, body);
		parser.addMethod(method);
	}

	private void generateElements() {
		List<String> imports = new ArrayList<String>();
		this.addImportsForElements(imports);

		MyJavaParser parser = this.initJavaParser(this.elementsClassName, imports);
		// Extend elements class from PageElements
		parser.extendClass("PageElements");
		
		// Create default constructor
		parser.createDefaultConstructor(Keyword.PUBLIC);
		
		// Create method for auto generate actions
		MethodDeclaration method = parser.createMethodDeclaration("generateActions", Void.TYPE, Keyword.PUBLIC);
		method.addMarkerAnnotation("Test");
		
		String actionsConstructorParams = this.elementsClassName + ".class, " + this.actionsClassName + ".class";

		String body = "WebActionsGenerator actionsGenerator = new WebActionsGenerator(" + actionsConstructorParams + ");";
		
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
		imports.add("org.openqa.selenium.WebDriver");
		imports.add(this.importBasePackage + ".core.shared.java_parser.scripts.web.WebActionsGenerator");
		imports.add(this.importBasePackage + "core.application.screen.web.PageElements");
	}
	
	private void generateActions() {
		List<String> imports = new ArrayList<String>();
		this.addImportsForActions(imports);
		
		MyJavaParser parser = this.initJavaParser(this.actionsClassName, imports);
		parser.addImports(imports);
		parser.createVariable("elements", this.elementsClassName, Keyword.PRIVATE);
		//parser.createVariable("wait", Wait.class, Keyword.PRIVATE);
		parser.createVariable("wait", WebDriverWait.class, Keyword.PRIVATE);

		String webDriver = "WebDriverManager.getDriver()";
		//String extraBodyInConstructor = "this.wait = new Wait(" + webDriver + ");";
		String extraBodyInConstructor = "this.wait = new WebDriverWait(" + webDriver + ", 5);";
		parser.createConstructor(extraBodyInConstructor, null, Keyword.PUBLIC);
		
		extraBodyInConstructor += "\n";
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
		parser.extendClass("WebBaseProcess");

		String variableName = StringUtils.uncapitalizeWord(this.className);
		parser.createVariable(variableName, this.className, Keyword.PRIVATE);
		
		//parser.createDefaultConstructor(Keyword.PUBLIC);
		
		String extraBodyInConstructor = "this." + variableName + " = new " + this.className + "(driver);";
		this.createWebDriverConstructor(parser, extraBodyInConstructor);
		
		MethodDeclaration method = parser.createMethodDeclaration("execute", Void.TYPE, Keyword.PUBLIC);
		method.addMarkerAnnotation("Override");
		method.addThrownException(Exception.class);
		parser.addMethod(method);
		
		String savePath = this.getSavePath(processName, this.packageName);
		parser.saveClassIfNotExist(savePath);
	}
	
	private void addImportsForProcess(List<String> imports) {
		imports.add(this.importBasePackage + "core.application.process.web.WebBaseProcess");
	}

	/**
	 * Generates the architecture for every page, reading an excel or csv file 
	 * (the data extractor must be indicated in configuration.properties), 
	 * where the first line have the value of properties. (Example in csv: "pageName, packageName")
	 * @param path - String path of file with the required data, for create architecture
	 * @param packageScope - String (Optional), allows to create the architecture in "main" package or "test" package
	 *-/
	public static void generateArchitecture(String path, String...packageScope) {
		EnvironmentProperties properties = PropertiesManager.getInstance().getWebProperties();
		String extractorName = properties.getConfigurationProperty(WebEnvProperties.EXTRACTOR);
		TestDataExtractor extractor = TestDataExtractorFactory.createTestDataExtractor(extractorName);
		generateArchitecture(path, extractor, packageScope);
	}
	
	/**
	 * Generates the architecture for every page in the file specified, using the extractor specified,
	 * where the first line have the value of properties. (Example in csv: "pageName, packageName")
	 * @param path - String path of file with the required data, for create architecture.+
	 * @param extractor - TestDataExtractor
	 * @param packageScope - String (Optional), allows to create the architecture in "main" package or "test" package
	 *-/
	public static void generateArchitecture(String path, TestDataExtractor extractor, String...packageScope) {
		extractor.setTestDataType(ArchitectureGeneratorData.class.getName());
		List<ArchitectureGeneratorData> listOfArchitectureData = extractor.getTestData(path);
		for (ArchitectureGeneratorData architectureData : listOfArchitectureData) {
			generateArchitectureForOnePage(architectureData.getPageName(), 
											architectureData.getPackageName(), packageScope);
		}
	}
	
	/**
	 * Generates 4 java files (pageName, pageNameElements, pageNameActions and pageNameProcess) 
	 * and save it in the package specified
	 * @param pageName - String name of the page
	 * @param packageName - String package where the pages will be created
	 * @param packageScope - String (Optional), allows to create the architecture in "main" package or "test" package
	 *-/
	public static void generateArchitectureForOnePage(String pageName, String packageName, String...packageScope) {
		WebArchitectureGenerator generator = new WebArchitectureGenerator(pageName, packageName);
		
		if (packageScope != null && packageScope.length > 0) {
			generator.setPackageScope(packageScope[0]);	
		}
		
		generator.execute();
	}
	
	*/

}