package io.github.marcperez06.java_parser.resources.objects.swagger;

public class SwaggerEndpointObjectInfo {
	
	private String name;
	private String in;
	private String description;
	private boolean required;
	private String type;
	private String format;
	private SwaggerSchema schema;
	
	public SwaggerEndpointObjectInfo() {
		
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIn() {
		return this.in;
	}

	public void setIn(String in) {
		this.in = in;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRequired() {
		return this.required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public SwaggerSchema getSchema() {
		return this.schema;
	}

	public void setSchema(SwaggerSchema schema) {
		this.schema = schema;
	}

}