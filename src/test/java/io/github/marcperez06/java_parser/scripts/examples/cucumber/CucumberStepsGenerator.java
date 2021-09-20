package io.github.marcperez06.java_parser.scripts.examples.cucumber;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.MethodDeclaration;

import io.github.marcperez06.java_parser.core.JavaParserWrapper;
import io.github.marcperez06.java_parser.resources.ExecutionType;

public class CucumberStepsGenerator {

	private String packageName;
	private String packageScope;
	private ExecutionType executionType;
	private String className;
	private String parentClassName;
	private String importParentClass;
	
	public CucumberStepsGenerator(String packageName, ExecutionType executionType) {
		this.packageName = packageName + ".tests";
		this.executionType = executionType;
		this.packageScope = "test";
		this.setClassNameAndParentName();
	}
	
	public CucumberStepsGenerator(String packageName, ExecutionType executionType, String packageScope) {
		this(packageName, executionType);
		this.packageScope = packageScope;
	}
	
	public void setPackageScope(String packageScope) {
		this.packageScope = packageScope;
	}
	
	public void setExecutionType(ExecutionType executionType) {
		this.executionType = executionType;
	}
	
	private void setClassNameAndParentName() {
		String abstrac = "Abstract";
		String importPackage = "exemple.package.core.shared.test.cucumber.steps.";

		if (this.executionType == ExecutionType.API) {
			this.className = "ApiBaseSteps";
		} else if (this.executionType == ExecutionType.WEB) {
			this.className = "WebBaseSteps";
		} else if (this.executionType == ExecutionType.MOBILE) {
			this.className = "MobileBaseSteps";
		} else {
			this.className = "BaseSteps";
		}
		
		this.parentClassName = abstrac + this.className;
		this.importParentClass = importPackage + this.parentClassName;
	}
	
	public void execute() {
		JavaParserWrapper parser = new JavaParserWrapper(this.className, this.packageName);
		
		if (!parser.existClass()) {
			parser.setPackageScope(this.packageScope);
			parser.parseOrCreateClass();
			
			this.addImports(parser);
			parser.extendClass(this.parentClassName);

			parser.createDefaultConstructor(Keyword.PUBLIC);
			
			this.createSetUpMethod(parser);
			this.createTearDownMethod(parser);

			parser.saveClassIfNotExist();
		}

	}
	
	private void addImports(JavaParserWrapper parser) {
		parser.addImport(this.importParentClass);
		parser.addImport("io.cucumber.java.Before");
		parser.addImport("io.cucumber.java.After");
		parser.addImport("io.cucumber.core.api.Scenario");
	}
	
	private void createSetUpMethod(JavaParserWrapper parser) {
		MethodDeclaration method = parser.createMethodDeclaration("setUp", Void.TYPE, Keyword.PUBLIC);
		method.addAnnotation("Before");
		method.addAnnotation("Override");
		method.addParameter("Scenario", "scenario");
		parser.addBodyToMethod(method, "super.setUp(scenario);");
		parser.addMethodIfNotExist(method);
	}
	
	private void createTearDownMethod(JavaParserWrapper parser) {
		MethodDeclaration method = parser.createMethodDeclaration("tearDown", Void.TYPE, Keyword.PUBLIC);
		method.addAnnotation("After");
		method.addAnnotation("Override");
		method.addParameter("Scenario", "scenario");
		parser.addBodyToMethod(method, "super.tearDown(scenario);");
		parser.addMethodIfNotExist(method);
	}
	
}