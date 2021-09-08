package io.github.marcperez06.java_parser.scripts.test.strategy;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier.Keyword;

import sogeti.testing_framework_base.core.shared.java_parser.core.MyJavaParser;

public class MobileStrategyTestGenerator extends StrategyTestGenerator {

	@Override
	public List<String> getImports() {
		List<String> imports = new ArrayList<String>();
		imports.add("sogeti.testing_framework_base.core.shared.test.MobileTest");
		//imports.add("sogeti.testing_framework_base.core.application.drivers.Driver");
		imports.add("sogeti.testing_framework_base.core.application.drivers.mobile.MobileDriverFactory");
		imports.add("io.appium.java_client.AppiumDriver");
		return imports;
	}

	@Override
	public void createVariables(MyJavaParser parser) {
		if (parser != null) {
			parser.createVariable("globalDriver", "AppiumDriver<?>", Keyword.PRIVATE);
		}
	}

	@Override
	public String getTestMethod() {
		String testMethod = "this.globalDriver = MobileDriverFactory.createDriver();";
		return testMethod;
	}

	@Override
	public String getAfterMethod() {
		String afterMethod = "if (this.globalDriver != null) {";
		afterMethod += "this.globalDriver.closeApp();";
		afterMethod += "}";
		return afterMethod;
	}

}
