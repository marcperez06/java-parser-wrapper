package io.github.marcperez06.java_parser.scripts.examples.cucumber.strategy;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier.Keyword;

import io.github.marcperez06.java_parser.core.JavaParserWrapper;

public class WebStrategyCucumberTestGenerator extends StrategyCucumberTestGenerator {
	
	public WebStrategyCucumberTestGenerator(String packageName) {
		super(packageName);
	}

	@Override
	public List<String> getImports() {
		List<String> imports = new ArrayList<String>();
		imports.add("example.package.core.shared.report.ReportTestManager");
		imports.add(super.packageName + ".tests.WebBaseSteps");
		return imports;
	}
	
	@Override
	public void createVariables(JavaParserWrapper parser) {
		if (parser != null) {
			parser.addImport("org.openqa.selenium.WebDriver");
			parser.createVariable("driver", "WebDriver", Keyword.PRIVATE);
		}
	}
	
	@Override
	public String getBodyConstructor() {
		String body = "this.driver = WebBaseSteps.getDriver();";
		return body;
	}

	@Override
	public String getReportStepCode(String line) {
		return super.reportInfo("WebBaseSteps.STEP + \"" + line + "\", this.driver");
	}

	@Override
	public String getReportDataCode(String line) {
		return super.reportInfo("WebBaseSteps.DATA + \"" + line + "\", this.driver");
	}
	
	@Override
	public String getTryCatchCode() {
		String tryCatchCode = "try {";
		tryCatchCode += "} catch (Exception e) {";
		tryCatchCode += "WebBaseSteps.isCrashed(true);";
		tryCatchCode += "throw e;";
		tryCatchCode += "}";
		return tryCatchCode;
	}
	
}
