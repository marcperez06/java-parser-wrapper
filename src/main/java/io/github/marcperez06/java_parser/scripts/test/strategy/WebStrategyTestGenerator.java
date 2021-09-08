package io.github.marcperez06.java_parser.scripts.test.strategy;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier.Keyword;

import sogeti.testing_framework_base.core.application.drivers.web.WebDriverFactory;
import sogeti.testing_framework_base.core.shared.java_parser.core.MyJavaParser;

public class WebStrategyTestGenerator extends StrategyTestGenerator {

	@Override
	public List<String> getImports() {
		List<String> imports = new ArrayList<String>();
		imports.add("sogeti.testing_framework_base.core.shared.test.WebTest");
		//imports.add("sogeti.testing_framework_base.core.application.drivers.Driver");
		imports.add("sogeti.testing_framework_base.core.application.drivers.web.WebDriverFactory");
		imports.add("org.openqa.selenium.WebDriver");
		return imports;
	}

	@Override
	public void createVariables(MyJavaParser parser) {
		if (parser != null) {
			parser.createVariable("globalDriver", "WebDriver", Keyword.PRIVATE);
		}
	}

	@Override
	public String getTestMethod() {
		String testMethod = "this.globalDriver = WebDriverFactory.createDriver();";
		return testMethod;
	}

	@Override
	public String getAfterMethod() {
		String afterMethod = "if (this.globalDriver != null) {";
		afterMethod += "this.globalDriver.close();";
		afterMethod += "}";
		return afterMethod;
	}

}
