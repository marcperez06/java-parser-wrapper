package io.github.marcperez06.java_parser.scripts.examples.cucumber;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import io.github.marcperez06.java_parser.core.JavaParserWrapper;
import io.github.marcperez06.java_parser.resources.ExecutionType;

public class CucumberRunnerGenerator {
	
	private String packageName;
	private String stepsPackageName;
	private String packageScope;
	private ExecutionType executionType;
	private String className;
	private String cucumberTest;
	private String importCucumberTest;
	private String propertyName;
	
	public CucumberRunnerGenerator(String packageName, ExecutionType executionType) {
		this.packageName = packageName + ".runners";
		this.stepsPackageName = packageName + ".tests";
		this.executionType = executionType;
		this.packageScope = "test";
		this.setClassNameAndCucumberTest();
	}
	
	public CucumberRunnerGenerator(String packageName, ExecutionType executionType, String packageScope) {
		this(packageName, executionType);
		this.packageScope = packageScope;
	}
	
	public void setPackageScope(String packageScope) {
		this.packageScope = packageScope;
	}
	
	public void setExecutionType(ExecutionType executionType) {
		this.executionType = executionType;
	}
	
	private void setClassNameAndCucumberTest() {
		String cucumber = "Cucumber";
		String importPackage = "exemple.package.core.shared.test.cucumber.test.";

		if (this.executionType == ExecutionType.API) {
			this.className = "CucumberApiRunner";
			this.cucumberTest = "ApiCucumberTest";
			this.propertyName = "api";
		} else if (this.executionType == ExecutionType.WEB) {
			this.className = "CucumberWebRunner";
			this.cucumberTest = "WebCucumberTest";
			this.propertyName = "web";
		} else if (this.executionType == ExecutionType.MOBILE) {
			this.className = "CucumberMobileRunner";
			this.cucumberTest = "MobileCucumberTest";
			this.propertyName = "mobile";
		} else {
			this.className = "CucumberBaseRunner";
			this.cucumberTest = "BaseCucumberTest";
			this.propertyName = "base";
		}
		
		this.propertyName += cucumber;
		this.importCucumberTest = importPackage + this.cucumberTest;
	}
	
	public void execute() {
		JavaParserWrapper parser = new JavaParserWrapper(this.className, this.packageName);
		
		if (!parser.existClass()) {
			parser.setPackageScope(this.packageScope);
			parser.parseOrCreateClass();
			
			this.addImports(parser);
			
			this.addCucumberAnnotations(parser);
			
			this.createProperties(parser);

			this.createSetUpMethod(parser);
			this.createTearDownMethod(parser);

			parser.saveClassIfNotExist();
		}

	}
	
	private void addImports(JavaParserWrapper parser) {
		parser.addImport(this.importCucumberTest);
		parser.addImport("org.junit.AfterClass");
		parser.addImport("org.junit.BeforeClass");
		parser.addImport("org.junit.runner.RunWith");
		parser.addImport("io.cucumber.junit.Cucumber");
		parser.addImport("io.cucumber.junit.CucumberOptions");
	}
	
	private void addCucumberAnnotations(JavaParserWrapper parser) {
		this.addRunWithAnnotatioN(parser);
		this.addCucumberOptionsAnnotation(parser);
	}
	
	private void addRunWithAnnotatioN(JavaParserWrapper parser) {
		Name annotationName = new Name("RunWith");
		ClassExpr expression = new ClassExpr();
		expression.setType("Cucumber");
		AnnotationExpr annotationRunWith = new SingleMemberAnnotationExpr(annotationName, expression);
		parser.getNewClass().addAnnotation(annotationRunWith);
	}
	
	private void addCucumberOptionsAnnotation(JavaParserWrapper parser) {
		String format = "\n\t\t\t\t";
		// Initialize list of expresions
		NodeList<Expression> pluginValueList = new NodeList<Expression>();
		NodeList<MemberValuePair> annotationCucumberOptionsParamList = new NodeList<MemberValuePair>();
		
		// Create CucumberOptions Annotation name
		Name annotationName = new Name("CucumberOptions");
		
		// Create plugin parameter
		pluginValueList.add(new StringLiteralExpr("pretty"));
		pluginValueList.add(new StringLiteralExpr("json:report/Cucumber_report.json"));
		
		ArrayInitializerExpr pluginArrayValueExpr = new ArrayInitializerExpr(pluginValueList);
		MemberValuePair pluginValuePair = new MemberValuePair();
		pluginValuePair.setName(new SimpleName(format + "plugin"));
		pluginValuePair.setValue(pluginArrayValueExpr);
		annotationCucumberOptionsParamList.add(pluginValuePair);
		
		// Create glue parameter
		NodeList<Expression> glueValueList = new NodeList<Expression>();
		glueValueList.add(new StringLiteralExpr(this.stepsPackageName));
		
		ArrayInitializerExpr glueArrayValueExpr = new ArrayInitializerExpr(glueValueList);
		MemberValuePair glueValuePair = new MemberValuePair();
		glueValuePair.setName(new SimpleName(format + "glue"));
		glueValuePair.setValue(glueArrayValueExpr);
		annotationCucumberOptionsParamList.add(glueValuePair);
		
		// Create features parameter
		NodeList<Expression> featuresValueList = new NodeList<Expression>();
		featuresValueList.add(new StringLiteralExpr("resources/features"));
		
		ArrayInitializerExpr featureArrayValueExpr = new ArrayInitializerExpr(featuresValueList);
		MemberValuePair featureValuePair = new MemberValuePair();
		featureValuePair.setName(new SimpleName(format + "features"));
		featureValuePair.setValue(featureArrayValueExpr);
		annotationCucumberOptionsParamList.add(featureValuePair);

		// Add the CucumberOptions annotation to class
		AnnotationExpr cucumberOptions = new NormalAnnotationExpr(annotationName, annotationCucumberOptionsParamList);
		parser.getNewClass().addAnnotation(cucumberOptions);
	}
	
	private void createProperties(JavaParserWrapper parser) {
		ObjectCreationExpr expression = new ObjectCreationExpr();
		expression.setType(this.cucumberTest);
		parser.createVariableWithInitializer(this.propertyName, this.cucumberTest, expression, Keyword.PRIVATE, Keyword.STATIC);
	}
	
	private void createSetUpMethod(JavaParserWrapper parser) {
		MethodDeclaration method = parser.createMethodDeclaration("setUp", Void.TYPE, Keyword.PUBLIC, Keyword.STATIC);
		method.addAnnotation("BeforeClass");
		parser.addBodyToMethod(method, this.propertyName + ".cucumberSetUp();");
		parser.addMethodIfNotExist(method);
	}
	
	private void createTearDownMethod(JavaParserWrapper parser) {
		MethodDeclaration method = parser.createMethodDeclaration("tearDown", Void.TYPE, Keyword.PUBLIC, Keyword.STATIC);
		method.addAnnotation("AfterClass");
		parser.addBodyToMethod(method, this.propertyName + ".close();");
		parser.addMethodIfNotExist(method);
	}

}
