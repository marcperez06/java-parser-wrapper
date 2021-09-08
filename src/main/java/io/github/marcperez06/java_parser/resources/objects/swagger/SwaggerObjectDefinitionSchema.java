package io.github.marcperez06.java_parser.resources.objects.swagger;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SwaggerObjectDefinitionSchema {

	private String description;
	private String type;
	private String $ref;
	private String format;
	private SwaggerObjectDefinitionSchema items;
	private boolean readOnly;
	
	@SerializedName("enum")
	private List<String> enumValues;
	
	@SerializedName("xs-ms-enum")
	private SwaggerEnumInfo enumObject;
	
	public SwaggerObjectDefinitionSchema() {
		
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

	public SwaggerObjectDefinitionSchema getItems() {
		return this.items;
	}

	public void setItems(SwaggerObjectDefinitionSchema items) {
		this.items = items;
	}

	public boolean getReadOnly() {
		return this.readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public List<String> getEnumValues() {
		return this.enumValues;
	}

	public void setEnumValues(List<String> enumValues) {
		this.enumValues = enumValues;
	}

	public SwaggerEnumInfo getEnumObject() {
		return this.enumObject;
	}

	public void setEnumObject(SwaggerEnumInfo enumObject) {
		this.enumObject = enumObject;
	}

}