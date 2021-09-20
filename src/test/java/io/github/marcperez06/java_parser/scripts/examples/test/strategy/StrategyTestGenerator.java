package io.github.marcperez06.java_parser.scripts.examples.test.strategy;

import java.util.List;

import io.github.marcperez06.java_parser.core.JavaParserWrapper;

public abstract class StrategyTestGenerator {
	
	protected final String importBasePackage = "example.package.";

	public abstract List<String> getImports();

	public abstract void createVariables(JavaParserWrapper parser);

	public abstract String getTestMethod();

	public abstract String getAfterMethod();
	
	public String getBeforeMethod() {
		String beforeMethod = "System.setProperty(\"testName\", this.getClass().getSimpleName());";
		beforeMethod += "ReportTestManager.startTest(this.getClass().getSimpleName());";
		beforeMethod += "super.createTestCaseInformation(this.getClass());";
		return beforeMethod;
	}

}
