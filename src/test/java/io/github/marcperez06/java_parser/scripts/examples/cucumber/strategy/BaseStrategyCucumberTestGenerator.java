package io.github.marcperez06.java_parser.scripts.examples.cucumber.strategy;

import java.util.ArrayList;
import java.util.List;

import io.github.marcperez06.java_parser.core.MyJavaParser;

public class BaseStrategyCucumberTestGenerator extends StrategyCucumberTestGenerator {
	
	public BaseStrategyCucumberTestGenerator(String packageName) {
		super(packageName);
	}

	@Override
	public List<String> getImports() {
		List<String> imports = new ArrayList<String>();
		imports.add("example.package.core.shared.report.ReportTestManager");
		imports.add(super.packageName + ".tests.BaseSteps");
		return imports;
	}
	
	@Override
	public void createVariables(MyJavaParser parser) {
	}
	
	@Override
	public String getBodyConstructor() {
		return "";
	}

	@Override
	public String getReportStepCode(String line) {
		return super.reportInfo("BaseSteps.STEP + \"" + line + "\"");
	}

	@Override
	public String getReportDataCode(String line) {
		return super.reportInfo("BaseSteps.DATA + \"" + line + "\"");
	}
	
	@Override
	public String getTryCatchCode() {
		String tryCatchCode = "try {";
		tryCatchCode += "} catch (Exception e) {";
		tryCatchCode += "BaseSteps.isCrashed(true);";
		tryCatchCode += "throw e;";
		tryCatchCode += "}";
		return tryCatchCode;
	}
	
}
