package io.github.marcperez06.java_parser.resources.objects.swagger;

import java.util.List;
import java.util.Map;

public class SwaggerSchema {
	
	private String type;
	private String $ref;
	private String format;
	private SwaggerSchema items;
	private List<String> required;
	private Map<String, SwaggerSchema> properties;
	
	public SwaggerSchema() {

	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String get$ref() {
		return this.$ref;
	}
	
	public void set$ref(String $ref) {
		this.$ref = $ref;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public SwaggerSchema getItems() {
		return this.items;
	}
	
	public void setItems(SwaggerSchema items) {
		this.items = items;
	}

	public List<String> getRequired() {
		return this.required;
	}

	public void setRequired(List<String> required) {
		this.required = required;
	}

	public Map<String, SwaggerSchema> getProperties() {
		return this.properties;
	}

	public void setProperties(Map<String, SwaggerSchema> properties) {
		this.properties = properties;
	}

}