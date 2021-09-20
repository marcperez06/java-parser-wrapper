package io.github.marcperez06.java_parser.scripts.examples.cucumber.strategy;

import java.util.List;

import io.github.marcperez06.java_parser.core.JavaParserWrapper;

public abstract class StrategyCucumberTestGenerator {

	protected String packageName;
	
	public StrategyCucumberTestGenerator(String packageName) {
		this.packageName = packageName;
	}
	
	public abstract List<String> getImports();
	
	public abstract void createVariables(JavaParserWrapper parser);
	
	public abstract String getBodyConstructor();

	public abstract String getReportStepCode(String line);
	
	public abstract String getReportDataCode(String line);
	
	public abstract String getTryCatchCode();

	protected String reportInfo(String params) {
		String info = "ReportTestManager.reportInfo(" + params + ");";
		return info;
	}

}