package io.github.marcperez06.java_parser.scripts.test.strategy;

import java.util.ArrayList;
import java.util.List;

import sogeti.testing_framework_base.core.shared.java_parser.core.MyJavaParser;

public class ApiStrategyTestGenerator extends StrategyTestGenerator {

	@Override
	public List<String> getImports() {
		List<String> imports = new ArrayList<String>();
		imports.add("sogeti.testing_framework_base.core.shared.test.ApiTest");
		return imports;
	}

	@Override
	public void createVariables(MyJavaParser parser) {
	}

	@Override
	public String getTestMethod() {
		return "";
	}

	@Override
	public String getAfterMethod() {
		return "";
	}

}
