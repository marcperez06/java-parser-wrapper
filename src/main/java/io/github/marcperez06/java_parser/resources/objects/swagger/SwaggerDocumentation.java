package io.github.marcperez06.java_parser.resources.objects.swagger;

import java.util.List;
import java.util.Map;

public class SwaggerDocumentation {

	private String swagger;
	private SwaggerInfo info;
	private String host;
	private List<String> schemes;
	private List<String> consumes;
	private List<String> produces;
	private Map<String, SwaggerEndpoint> paths;
	private Map<String, SwaggerObjectDefinition> definitions;
	
	public SwaggerDocumentation() {

	}

	public String getSwagger() {
		return this.swagger;
	}

	public void setSwagger(String swagger) {
		this.swagger = swagger;
	}

	public SwaggerInfo getInfo() {
		return this.info;
	}

	public void setInfo(SwaggerInfo info) {
		this.info = info;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public List<String> getSchemes() {
		return this.schemes;
	}

	public void setSchemes(List<String> schemes) {
		this.schemes = schemes;
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

	public Map<String, SwaggerEndpoint> getPaths() {
		return this.paths;
	}

	public void setPaths(Map<String, SwaggerEndpoint> paths) {
		this.paths = paths;
	}

	public Map<String, SwaggerObjectDefinition> getDefinitions() {
		return this.definitions;
	}

	public void setDefinitions(Map<String, SwaggerObjectDefinition> definitions) {
		this.definitions = definitions;
	}

}