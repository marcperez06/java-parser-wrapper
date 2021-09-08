package io.github.marcperez06.java_parser.resources.objects.swagger;

public class SwaggerEnumInfo {
	
	private String name;
	private boolean modelAsString;
	
	public SwaggerEnumInfo() {
		
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isModelAsString() {
		return this.modelAsString;
	}

	public void setModelAsString(boolean modelAsString) {
		this.modelAsString = modelAsString;
	}

}