package io.github.marcperez06.java_parser.resources.objects.swagger;

import java.util.List;
import java.util.Map;

import com.github.javaparser.utils.Utils;

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
	
	public String getClassName() {
		String className = "";
		
		if (this.$ref != null && !this.$ref.isEmpty()) {
			className = this.extractClassName(this.$ref);
		} else if (this.type.equalsIgnoreCase("array") && this.items != null) {
			className = this.items.getClassName();
		} else if (this.type.equalsIgnoreCase("object") && this.properties != null && !this.properties.isEmpty()) {
			className = "Map<String, Object>";
		} else {
			className = this.type;
		}
		
		return Utils.capitalize(className);
	}
	
	private String extractClassName(String ref) {
		String className = "";
		String[] refSplited = ref.split("/");
		
		if (refSplited != null && refSplited.length > 0) {
			className = refSplited[refSplited.length - 1];
		}

		return className;
	}

}