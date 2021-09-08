package io.github.marcperez06.java_parser.resources.objects.swagger;

import java.util.List;
import java.util.Map;

public class SwaggerObjectDefinition {
	
	private String description;
	private String type;
	private Map<String, SwaggerObjectDefinitionSchema> properties;
	private List<String> required;
	
	public SwaggerObjectDefinition() {
		
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, SwaggerObjectDefinitionSchema> getProperties() {
		return this.properties;
	}

	public void setProperties(Map<String, SwaggerObjectDefinitionSchema> properties) {
		this.properties = properties;
	}

	public List<String> getRequired() {
		return this.required;
	}

	public void setRequired(List<String> required) {
		this.required = required;
	}

}