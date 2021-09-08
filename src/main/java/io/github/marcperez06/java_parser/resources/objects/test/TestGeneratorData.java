package io.github.marcperez06.java_parser.resources.objects.test;

public class TestGeneratorData {
	
	private String testName;
	private String packageName;
	private String testType;
	
	public TestGeneratorData() {
		this.testName = "";
		this.packageName = "";
		this.testType = "";
	}

	public String getTestName() {
		return this.testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getPackageName() {
		return this.packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getTestType() {
		return this.testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

}
