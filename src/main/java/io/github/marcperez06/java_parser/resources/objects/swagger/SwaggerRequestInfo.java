package io.github.marcperez06.java_parser.resources.objects.swagger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SwaggerRequestInfo {

	private List<String> tags;
	private String summary;
	private String description;
	private String operationId;
	private List<String> consumes;
	private List<String> produces;
	private List<SwaggerEndpointObjectInfo> parameters;
	private Map<String, SwaggerEndpointObjectInfo> responses;
	private boolean deprecated;
	private List<Map<String, Object>> security;
	
	public SwaggerRequestInfo() {
		
	}

	public List<String> getTags() {
		return this.tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOperationId() {
		return this.operationId;
	}

	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}

	public List<String> getConsumes() {
		return this.consumes;
	}

	public void setConsumes(List<String> consumes) {
		this.consumes = consumes;
	}

	public List<String> getProduces() {
		return this.produces;
	}

	public void setProduces(List<String> produces) {
		this.produces = produces;
	}

	public List<SwaggerEndpointObjectInfo> getParameters() {
		return this.parameters;
	}

	public void setParameters(List<SwaggerEndpointObjectInfo> parameters) {
		this.parameters = parameters;
	}

	public Map<String, SwaggerEndpointObjectInfo> getResponses() {
		return this.responses;
	}

	public void setResponses(Map<String, SwaggerEndpointObjectInfo> responses) {
		this.responses = responses;
	}

	public boolean isDeprecated() {
		return this.deprecated;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	public List<Map<String, Object>> getSecurity() {
		return this.security;
	}

	public void setSecurity(List<Map<String, Object>> security) {
		this.security = security;
	}
	
	public List<SwaggerEndpointObjectInfo> getAllHeadersParameters() {
		return this.getAllParametersOfType("header");
	}
	
	public List<SwaggerEndpointObjectInfo> getAllPathParameters() {
		return this.getAllParametersOfType("path");
	}
	
	public List<SwaggerEndpointObjectInfo> getAllQueryParameters() {
		return this.getAllParametersOfType("query");
	}
	
	public List<SwaggerEndpointObjectInfo> getAllBodyParameters() {
		return this.getAllParametersOfType("body");
	}
	
	private List<SwaggerEndpointObjectInfo> getAllParametersOfType(String type) {
		List<SwaggerEndpointObjectInfo> params = new ArrayList<SwaggerEndpointObjectInfo>();
		
		if (this.parameters != null && !this.parameters.isEmpty()) {

			for (SwaggerEndpointObjectInfo info : parameters) {
				if (info.getIn().equals(type)) {
					params.add(info);
				}
			}
			
		}
		
		return params;
	}

}