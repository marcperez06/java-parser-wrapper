package io.github.marcperez06.java_parser.scripts.examples.cucumber;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

import io.github.marcperez06.java_parser.core.JavaParserWrapper;
import io.github.marcperez06.java_parser.core.factory.ParametersFactory;
import io.github.marcperez06.java_parser.resources.ExecutionType;
import io.github.marcperez06.java_parser.scripts.examples.cucumber.strategy.ApiStrategyCucumberTestGenerator;
import io.github.marcperez06.java_parser.scripts.examples.cucumber.strategy.BaseStrategyCucumberTestGenerator;
import io.github.marcperez06.java_parser.scripts.examples.cucumber.strategy.MobileStrategyCucumberTestGenerator;
import io.github.marcperez06.java_parser.scripts.examples.cucumber.strategy.StrategyCucumberTestGenerator;
import io.github.marcperez06.java_parser.scripts.examples.cucumber.strategy.WebStrategyCucumberTestGenerator;
import io.github.marcperez06.java_utilities.file.FileUtils;
import io.github.marcperez06.java_utilities.gherkin.GherkinParser;
import io.github.marcperez06.java_utilities.gherkin.objects.GherkinCriteria;
import io.github.marcperez06.java_utilities.gherkin.objects.GherkinObject;
import io.github.marcperez06.java_utilities.gherkin.objects.GherkinVariable;

public class CucumberGenerator {

	private static final String BACKGROUND = "Background";

	private String packageName;
	private String gherkinFilePath;
	private String packageScope;
	private ExecutionType executionType;
	private StrategyCucumberTestGenerator strategy;

	public CucumberGenerator(String packageName, String gherkinFilePath) {
		
		boolean haveInformation = (gherkinFilePath != null && !gherkinFilePath.isEmpty());
		haveInformation &= (packageName != null && !packageName.isEmpty());
		
		if (haveInformation) {
			this.packageName = packageName;
			this.gherkinFilePath = gherkinFilePath;
			this.packageScope = "test";
			this.executionType = ExecutionType.NULL;
			this.strategy = null;
		}
		
	}
	
	public CucumberGenerator(String packageName, String gherkinFilePath, ExecutionType executionType) {
		this(packageName, gherkinFilePath);
		this.executionType = executionType;
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
		this.chooseCucumberTestStrategy();

		List<GherkinObject> gherkinObjects = GherkinParser.getGherkinObjects(this.gherkinFilePath);
		
		for (GherkinObject gherkin : gherkinObjects) {
			this.createCucumberSteps(gherkin);
			this.createCucumberBackgroundSteps(gherkin);
		}
		
		CucumberStepsGenerator stepsGenerator = new CucumberStepsGenerator(this.packageName, this.executionType, this.packageScope);
		stepsGenerator.execute();
		
		CucumberRunnerGenerator runnerGenerator = new CucumberRunnerGenerator(this.packageName, this.executionType, this.packageScope);
		runnerGenerator.execute();
	}
	
	private void chooseCucumberTestStrategy() {
		if (this.executionType == ExecutionType.NULL) {
			this.strategy = new BaseStrategyCucumberTestGenerator(this.packageName);
		} else if (this.executionType == ExecutionType.API) {
			this.strategy = new ApiStrategyCucumberTestGenerator(this.packageName);
		} else if (this.executionType == ExecutionType.WEB) {
			this.strategy = new WebStrategyCucumberTestGenerator(this.packageName);
		} else if (this.executionType == ExecutionType.MOBILE) {
			this.strategy = new MobileStrategyCucumberTestGenerator(this.packageName);
		}
	}
	
	private void createCucumberSteps(GherkinObject gherkin) {
		String featurePackage = this.transformToPackageName(gherkin.getFeature().getLine());
		String packageName = this.packageName + ".tests." + featurePackage;
		JavaParserWrapper parser = new JavaParserWrapper(gherkin.getScenarioName(), packageName);
		parser.setPackageScope(this.packageScope);
		parser.parseOrCreateClass();
		
		parser.addImports(this.strategy.getImports());
		this.strategy.createVariables(parser);
		parser.createConstructor(this.strategy.getBodyConstructor(), null, Keyword.PUBLIC);
		
		this.createCucumberGivenSteps(parser, gherkin);
		this.createCucumberWhenSteps(parser, gherkin);
		this.createCucumberThenSteps(parser, gherkin);

		String savePath = this.getSavePath(packageName, gherkin.getScenarioName());
		parser.deleteAndSaveClass(savePath);
	}
	
	private String transformToPackageName(String token) {
		String packageName = "empty";
		String[] specialCharacters = {"\\\\", "\\/", "\\[", "\\]", "\\{", "\\}", "\\(", "\\)", "\\&", "\\:", "@", "\"", "'"};
		
		if (token != null && !token.isEmpty()) {
			packageName = token.trim();
			packageName = packageName.toLowerCase();
			packageName = packageName.replaceAll(" ", "_");
			for (String specialChar : specialCharacters) {
				packageName = packageName.replaceAll(specialChar, "");
			}
			
			packageName = packageName.toLowerCase();
		}

		return packageName;
	}
	
	private void createCucumberGivenSteps(JavaParserWrapper parser, GherkinObject gherkin) {
		List<GherkinCriteria> givens = gherkin.getGivens();
		
		if (givens != null && !givens.isEmpty()) {
			parser.addImport("io.cucumber.java.en.Given");
			this.createCucumberSteps(parser, gherkin, givens);
		}

	}
	
	private void createCucumberWhenSteps(JavaParserWrapper parser, GherkinObject gherkin) {
		List<GherkinCriteria> whens = gherkin.getWhens();
		
		if (whens != null && !whens.isEmpty()) {
			parser.addImport("io.cucumber.java.en.When");
			this.createCucumberSteps(parser, gherkin, whens);
		}

	}
	
	private void createCucumberThenSteps(JavaParserWrapper parser, GherkinObject gherkin) {
		List<GherkinCriteria> thens = gherkin.getThens();
		
		if (thens != null && !thens.isEmpty()) {
			parser.addImport("io.cucumber.java.en.Then");
			this.createCucumberSteps(parser, gherkin, thens);
		}
	}
	
	private void createCucumberBackgroundSteps(GherkinObject gherkin) {
		String packageName = this.packageName + ".tests.background";
		String backgroundClassName = BACKGROUND + "Steps";
		
		JavaParserWrapper parser = new JavaParserWrapper(backgroundClassName, packageName);
		parser.setPackageScope(this.packageScope);
		parser.parseOrCreateClass();
		
		parser.addImports(this.strategy.getImports());
		this.strategy.createVariables(parser);
		
		parser.createConstructor(this.strategy.getBodyConstructor(), null, Keyword.PUBLIC);

		this.createBackgroundSteps(parser, gherkin);
		String savePath = this.getSavePath(packageName, backgroundClassName);
		parser.deleteAndSaveClass(savePath);
	}
	
	private void createBackgroundSteps(JavaParserWrapper parser, GherkinObject gherkin) {
		if (!gherkin.getBackground().isEmpty()) {
			
			List<GherkinCriteria> givens = gherkin.getBackground().get(GherkinCriteria.GIVEN);
			List<GherkinCriteria> whens = gherkin.getBackground().get(GherkinCriteria.WHEN);
			List<GherkinCriteria> thens = gherkin.getBackground().get(GherkinCriteria.THEN);

			if (givens != null && !givens.isEmpty()) {
				this.createCucumberSteps(parser, gherkin, givens);
			}
			
			if (whens != null && !whens.isEmpty()) {
				this.createCucumberSteps(parser, gherkin, whens);
			}
			
			if (thens != null && !thens.isEmpty()) {
				this.createCucumberSteps(parser, gherkin, thens);
			}
			
		}
	}
	
	private void createCucumberSteps(JavaParserWrapper parser, GherkinObject gherkin, List<GherkinCriteria> criteriaLines) {

		for (GherkinCriteria criteria : criteriaLines) {
			String criteriaType = criteria.getType();
			String line = criteria.getLine().replaceFirst(criteriaType, "").trim();
			String annotationLine = criteria.getAnnotationLine().replaceFirst(criteriaType, "").trim();
			String reportLine = criteriaType + ": " + line.replaceAll("\"", "\\\\\"");
			String dataLine = "";
		
			String methodName = GherkinObject.getMethodName(line);
			String annotationCriteria = GherkinObject.getAnnotationCriteria(annotationLine, gherkin.getExamplesValues());
			MethodDeclaration method = parser.createMethodDeclaration(methodName, Void.TYPE, Keyword.PUBLIC);
			method.addSingleMemberAnnotation(criteriaType, annotationCriteria);
			
			List<GherkinVariable> listOfVariables = gherkin.getVariablesInCriteria(line);
			List<Parameter> parameters = new ArrayList<Parameter>();
			
			for (GherkinVariable var : listOfVariables) {
				dataLine += "| variable: " + var.getVariableName() + " value: \" + " + var.getVariableName() + " + \" | ";
				Parameter param = ParametersFactory.createParameter(var.getVariableName(), var.getVariableType());
				parameters.add(param);
			}
			
			parser.addParametersToCallable(method, parameters);
			
			// Add report line for step
			parser.addBodyToMethod(method, this.strategy.getReportStepCode(reportLine));
			
			// Add report line for data
			if (!dataLine.isEmpty()) {
				parser.addBodyToMethod(method, this.strategy.getReportDataCode(dataLine.trim()));
			}
			
			parser.addBodyToMethod(method, this.strategy.getTryCatchCode());
			
			parser.addMethodIfNotExist(method);
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
	 * Generates the cucumber steps classes with ExecutionType NULL by default for each feature in the directory specified
	 * @param featuresDirectory - String path of features directory
	 * @param packageName - String (after package specified, adds an extra 'cucumber' package)
	 * @param packageScope - String (Optional), allows to create the step classses in "main" package or "test" package
	 */
	public static void generateCucumberStepsFromDirectory(String featuresDirectory, String packageName, String...packageScope) {
		generateCucumberStepsFromDirectory(featuresDirectory, packageName, ExecutionType.NULL, packageScope);
	}
	
	/**
	 * Generates the cucumber steps classes for each feature in the directory specified
	 * @param featuresDirectory - String path of features directory
	 * @param packageName - String (after package specified, adds an extra 'cucumber' package)
	 * @param executionType - ExecutionType [enum]
	 * @param packageScope - String (Optional), allows to create the step classses in "main" package or "test" package
	 */
	public static void generateCucumberStepsFromDirectory(String featuresDirectory, String packageName, 
															ExecutionType executionType, String...packageScope) {
		
		List<String> featuresPath = FileUtils.getListOfAbsolutePathsInDirectory(featuresDirectory);
		for (String cucumberFilePath : featuresPath) {
			if (cucumberFilePath.endsWith(".feature")) {
				generateCucumberSteps(cucumberFilePath, packageName, executionType, packageScope);
			}
		}

	}
	
	/**
	 * Generates the cucumber step class for feature file specified with ExecutionType NULL by default
	 * (If feature contains background steps, also creates a background steps class)
	 * @param cucumberFilePath - String path of feature file
	 * @param packageName - String (after package specified, adds an extra 'cucumber' package)
	 * @param packageScope - String (Optional), allows to create the step classses in "main" package or "test" package
	 */
	public static void generateCucumberSteps(String cucumberFilePath, String packageName, String...packageScope) {
		generateCucumberSteps(cucumberFilePath, packageName, ExecutionType.NULL, packageScope);
	}
	
	/**
	 * Generates the cucumber step class for feature file specified 
	 * (If feature contains background steps, also creates a background steps class)
	 * @param cucumberFilePath - String path of feature file
	 * @param packageName - String (after package specified, adds an extra 'cucumber' package)
	 * @param executionType - ExecutionType [enum]
	 * @param packageScope - String (Optional), allows to create the step classses in "main" package or "test" package
	 */
	public static void generateCucumberSteps(String cucumberFilePath, String packageName, 
												ExecutionType executionType, String...packageScope) {
		
		CucumberGenerator generator = new CucumberGenerator(packageName, cucumberFilePath, executionType);
		
		if (packageScope != null && packageScope.length > 0) {
			generator.setPackageScope(packageScope[0]);	
		}
		
		generator.execute();
	}

}