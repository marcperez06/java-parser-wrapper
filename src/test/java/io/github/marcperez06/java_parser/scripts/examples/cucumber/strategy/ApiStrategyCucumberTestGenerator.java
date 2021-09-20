package io.github.marcperez06.java_parser.scripts.examples.cucumber.strategy;

import java.util.ArrayList;
import java.util.List;

import io.github.marcperez06.java_parser.core.JavaParserWrapper;

public class ApiStrategyCucumberTestGenerator extends StrategyCucumberTestGenerator {
	
	public ApiStrategyCucumberTestGenerator(String packageName) {
		super(packageName);
	}

	@Override
	public List<String> getImports() {
		List<String> imports = new ArrayList<String>();
		imports.add("example.package.core.shared.report.ReportTestManager");
		imports.add(super.packageName + ".tests.ApiBaseSteps");
		return imports;
	}
	
	@Override
	public void createVariables(JavaParserWrapper parser) {
	}
	
	@Override
	public String getBodyConstructor() {
		return "";
	}

	@Override
	public String getReportStepCode(String line) {
		return super.reportInfo("ApiBaseSteps.STEP + \"" + line + "\"");
	}

	@Override
	public String getReportDataCode(String line) {
		return super.reportInfo("ApiBaseSteps.DATA + \"" + line + "\"");
	}

	@Override
	public String getTryCatchCode() {
		String tryCatchCode = "try {";
		tryCatchCode += "} catch (Exception e) {";
		tryCatchCode += "ApiBaseSteps.isCrashed(true);";
		tryCatchCode += "throw e;";
		tryCatchCode += "}";
		return tryCatchCode;
	}

}