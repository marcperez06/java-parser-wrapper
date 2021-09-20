package io.github.marcperez06.java_parser.scripts.examples.cucumber.strategy;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier.Keyword;

import io.github.marcperez06.java_parser.core.MyJavaParser;

public class MobileStrategyCucumberTestGenerator extends StrategyCucumberTestGenerator {
	
	public MobileStrategyCucumberTestGenerator(String packageName) {
		super(packageName);
	}

	@Override
	public List<String> getImports() {
		List<String> imports = new ArrayList<String>();
		imports.add("example.package.core.shared.report.ReportTestManager");
		imports.add(super.packageName + ".tests.MobileBaseSteps");
		return imports;
	}
	
	@Override
	public void createVariables(MyJavaParser parser) {
		if (parser != null) {
			parser.addImport("io.appium.java_client.AppiumDriver");
			parser.createVariable("driver", "AppiumDriver<?>", Keyword.PRIVATE);
		}
	}
	
	@Override
	public String getBodyConstructor() {
		String body = "this.driver = MobileBaseSteps.getDriver();";
		return body;
	}

	@Override
	public String getReportStepCode(String line) {
		return super.reportInfo("MobileBaseSteps.STEP + \"" + line + "\", this.driver");
	}

	@Override
	public String getReportDataCode(String line) {
		return super.reportInfo("MobileBaseSteps.DATA + \"" + line + "\", this.driver");
	}
	
	@Override
	public String getTryCatchCode() {
		String tryCatchCode = "try {";
		tryCatchCode += "} catch (Exception e) {";
		tryCatchCode += "MobileBaseSteps.isCrashed(true);";
		tryCatchCode += "throw e;";
		tryCatchCode += "}";
		return tryCatchCode;
	}
	
}
