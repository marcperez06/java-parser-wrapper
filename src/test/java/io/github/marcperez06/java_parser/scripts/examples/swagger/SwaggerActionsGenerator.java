/**
 * @author Marc Pérez Rodríguez
 */
package io.github.marcperez06.java_parser.scripts.examples.swagger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.Parameter;

import io.github.marcperez06.java_parser.core.MyJavaParser;
import io.github.marcperez06.java_parser.core.factory.ParametersFactory;
import io.github.marcperez06.java_parser.resources.objects.swagger.SwaggerEndpoint;
import io.github.marcperez06.java_parser.resources.objects.swagger.SwaggerEndpointObjectInfo;
import io.github.marcperez06.java_parser.resources.objects.swagger.SwaggerRequestInfo;
import io.github.marcperez06.java_utilities.logger.Logger;
import io.github.marcperez06.java_utilities.strings.StringUtils;

public class SwaggerActionsGenerator extends SwaggerAbstractGenerator {
	
	private String tag;
	private boolean applySecurity;
	private static final String importBasePackage = "example.package.";
	
	public SwaggerActionsGenerator(String packageName, String swaggerDocumentUri) {
		super(packageName, swaggerDocumentUri);
		this.tag = "";
		this.applySecurity = false;
	}
	
	public void execute() {
		super.requestSwaggerDocumentation();
		
		if (super.swaggerDocumentation != null) {
			this.createActionsArchitecture();
		}

	}
	
	private void createActionsArchitecture() {
		Map<String, Map<String, SwaggerEndpoint>> endpointsClusters = this.getClustersOfEndpoitns();
		
		for (Entry<String, Map<String, SwaggerEndpoint>> cluster : endpointsClusters.entrySet()) {
			this.createActionClass(cluster.getKey(), cluster.getValue());
		}
	}
	
	private void createActionClass(String endpointTag, Map<String, SwaggerEndpoint> endpointMap) {
		this.tag = endpointTag;
		String tagWithoutErrors = super.formatToCamelCase(this.tag);
		String className = StringUtils.capitalizeWord(tagWithoutErrors) + "Actions";
		String endpointClass = StringUtils.capitalizeWord(tagWithoutErrors) + "Endpoints";
		
		MyJavaParser parser = new MyJavaParser(className, super.packageName);
		parser.setPackageScope(super.packageScope);
		parser.parseOrCreateClass();
		this.extendActionClass(parser);
		parser.addImports(this.getImports());
		
		String importEndpoint = super.packageName.replace(".domain.actions", ".endpoints") + "." + endpointClass;
		parser.addImport(importEndpoint);
		
		String constantValue = "Can not execute request, because rest api is not defined";
		parser.createStringConstant("REST_API_IS_NOT_DEFINED", constantValue, Keyword.PRIVATE);
		
		this.createConstructorsForAction(parser);
		
		for (Entry<String, SwaggerEndpoint> endpoint : endpointMap.entrySet()) {
			String endpointName = super.getEndpointName(endpoint.getKey());
			String constantName = super.transformToConstantName(endpointName);
			String requestEndpoint = endpointClass + "." + constantName;
			this.createMethodsForAction(parser, requestEndpoint, endpoint.getValue());
		}
		
		parser.deleteAndSaveClass();
	}
	
	private void extendActionClass(MyJavaParser parser) {
		//EnvironmentProperties properties = PropertiesManager.getEnvironmentProperties(EnvironmentType.API);
		//String parentClassName = properties.getProperty(ApiEnvProperties.ACTIONS_PARENT_CLASS);
		String parentClassName = "";
		if (parentClassName != null && !parentClassName.isEmpty()) {
			try {
				Class<?> parent = Class.forName(parentClassName);
				parser.addImport(parentClassName);
				parser.extendClass(parent);
			} catch (Exception e) {
				Logger.println(e.toString());
			}
		} else {
			parser.addImport(importBasePackage + "core.api.actions.BaseApiActions");
			parser.extendClass("BaseApiActions");
		}
	}
	
	private void createConstructorsForAction(MyJavaParser parser) {
		List<Parameter> constructorParameters = ParametersFactory.createListWithOneParameter("baseUri", "String");
		parser.createDefaultConstructor(Keyword.PUBLIC);
		parser.createConstructor(constructorParameters, Keyword.PUBLIC);
		
		constructorParameters.add(ParametersFactory.createParameter("config", "RestApiConfig"));
		parser.createConstructor(constructorParameters, Keyword.PUBLIC);
	}
	
	private List<String> getImports() {
		List<String> imports = new ArrayList<String>();
		imports.add("io.restassured.response.Response");
		imports.add("io.restassured.http.Method");
		imports.add(importBasePackage + "utils.Logger");
		imports.add(importBasePackage + "core.api.endpoints.Endpoint");
		imports.add(importBasePackage + "core.api.rest.RestApiConfig");
		return imports;
	}

	private void createMethodsForAction(MyJavaParser parser, String endpoint, SwaggerEndpoint swaggerEndpoint) {
		
		Map<String, SwaggerRequestInfo> endpointActions = swaggerEndpoint.getAllRequestInfo();
		
		for (Entry<String, SwaggerRequestInfo> requestEntry : endpointActions.entrySet()) {
			this.createMethodForAction(parser, endpoint, requestEntry);
		}

	}
	
	private void createMethodForAction(MyJavaParser parser, String endpoint, Entry<String, SwaggerRequestInfo> requestEntry) {
		if (requestEntry != null && requestEntry.getValue() != null) {
			SwaggerRequestInfo requestInfo = requestEntry.getValue();
			String methodName = this.getMethodName(requestEntry.getKey(), requestInfo);
			List<Parameter> parameters = this.createParametersMethodOfAction(requestInfo);
			parser.addImports(this.getImportsFromParameters(parameters));
			String methodBody = this.createBodyMethodOfAction(parser, endpoint, requestEntry);
			parser.createMethodIfNotExist(methodName, methodBody, parameters, "Response", Keyword.PUBLIC);
		}
	}
	
	private String getMethodName(String requestMethod, SwaggerRequestInfo requestInfo) {
		String methodName = super.formatToCamelCase(requestInfo.getOperationId());
		
		if (methodName.isEmpty()) {
			methodName = super.formatToCamelCase(requestInfo.getSummary());
			
			if (methodName.isEmpty()) {
				methodName = super.formatToCamelCase(requestInfo.getDescription());
			}
			
		}
		
		String finalName = requestMethod.toLowerCase() + StringUtils.capitalizeWord(methodName);
		
		return finalName;
	}
	
	private List<Parameter> createParametersMethodOfAction(SwaggerRequestInfo requestInfo) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		String mapType = "Map<String, String>";
		
		if (!requestInfo.getAllHeadersParameters().isEmpty()) {
			Parameter headerParams = ParametersFactory.createParameter("headerParams", mapType);
			parameters.add(headerParams);
		}
		
		if (!requestInfo.getAllPathParameters().isEmpty()) {
			Parameter pathParams = ParametersFactory.createParameter("pathParams", mapType);
			parameters.add(pathParams);
		}
		
		if (!requestInfo.getAllQueryParameters().isEmpty()) {
			Parameter queryParams = ParametersFactory.createParameter("queryParams", mapType);
			parameters.add(queryParams);
		}
		
		if (!requestInfo.getAllBodyParameters().isEmpty()) {
			SwaggerEndpointObjectInfo param = requestInfo.getAllBodyParameters().get(0);
			
			if (param != null && param.getSchema() != null) {
				String paramClass = param.getSchema().getClassName();
				Parameter bodyParam = ParametersFactory.createParameter("bodyParam", paramClass);
				parameters.add(bodyParam);
			}
			
		}
		
		return parameters;
	}
	
	private List<String> getImportsFromParameters(List<Parameter> parameters) {
		List<String> imports = new ArrayList<String>();
		
		if (parameters != null && !parameters.isEmpty()) {
			
			for (Parameter param : parameters) {
				
				String variableType = param.getTypeAsString();
				
				if (!StringUtils.isJavaVariableType(variableType)) {
					if (variableType.contains("Map")) {
						imports.add("java.util.Map");
					} else if (variableType.equals("List")) {
						imports.add("java.util.List");
					} else {
						String importName = this.getObjectPackage(StringUtils.capitalizeWord(variableType));
						imports.add(importName);
					}
				}
			}
			
		}

		return imports;
	}
	
	private String getObjectPackage(String className) {
		String extendPackage = super.getStartingWordInLowerCase(className);
		
		if (StringUtils.isReservedKeyword(extendPackage)) {
			extendPackage += "_objects";
		}
		
		String replacement = ".objects." + extendPackage + "." + className;
		String packageForClass = super.packageName.replace(".actions", replacement);
		return packageForClass;
	}
	
	private String createBodyMethodOfAction(MyJavaParser parser, String endpoint, Entry<String, SwaggerRequestInfo> requestEntry) {
		String bodyMethod = "";
		String initializeReturnCode = "Response response = null;";
		String ifCode = "if (super.restApiIsDefined()) {";
		String requestMethod = requestEntry.getKey();
		SwaggerRequestInfo requestInfo = requestEntry.getValue();
		String addHeaders = this.addHeaders(requestInfo);
		String relativeUri = this.createRelativeUri(endpoint, requestInfo);
		String security = this.applySecurity(requestInfo);
		String clearSecurity = this.clearSecurity(requestInfo);
		String doRequest = this.doRequest(requestMethod, requestInfo);
		String endIfCode = "} else { Logger.debug(REST_API_IS_NOT_DEFINED); }";
		String returnCode = "return response;";
		
		bodyMethod = initializeReturnCode + ifCode + addHeaders + relativeUri;
		bodyMethod += security + doRequest + clearSecurity + endIfCode + returnCode;
		
		return bodyMethod;
	}
	
	private String addHeaders(SwaggerRequestInfo requestInfo) {
		String addHeaders = "";
		
		if (!requestInfo.getAllHeadersParameters().isEmpty()) {
			addHeaders = "super.addHeadersParameters(headerParams);";
		}

		return addHeaders;
	}

	private String createRelativeUri(String endpoint, SwaggerRequestInfo requestInfo) {
		String pathParams = "null";
		List<SwaggerEndpointObjectInfo> parameters = requestInfo.getAllPathParameters();
		
		String codeEndpoint = "String relativeUri = ";
		
		if (parameters != null && !parameters.isEmpty()) {
			pathParams = "pathParams";
			codeEndpoint += "Endpoint.build(" + endpoint + ", " + pathParams + ");";
		} else {
			codeEndpoint += endpoint + ";";
		}

		return codeEndpoint;
	}
	
	private String applySecurity(SwaggerRequestInfo requestInfo) {
		String security = "";
		List<Map<String, Object>> securityList = requestInfo.getSecurity();
		
		if (securityList != null && !securityList.isEmpty()) {
			Map<String, Object> securityMap = securityList.get(0);
			
			if (securityMap.containsKey("basicAuth")) {
				security = "String user = super.rest.getConfig().getUsername();";
				security += "String password = super.rest.getConfig().getPassword();";
				security += "super.setCredentials(user, password);";
				this.applySecurity = true;
			} else if (securityMap.containsKey("bearerAuth") || securityMap.containsKey("JWT")) {
				security = "String bearerToken = super.rest.getConfig().getBearerToken();";
				security += "super.setBearerToken(bearerToken);";
				this.applySecurity = true;
			} else if (securityMap.containsKey("oauth2")) {
				security = this.applyOauth2Security(securityMap);
				//this.applySecurity = true;
			}
			
		}
		
		return security;
	}
	
	private String applyOauth2Security(Map<String, Object> securityMap) {
		String oauth = "";
		// TODO: request for token for add Bearer token to header.
		return oauth;
	}
	
	private String clearSecurity(SwaggerRequestInfo requestInfo) {
		String clearSecurity = "";

		if (this.applySecurity) {
			clearSecurity = "super.clearCredentials();";
			this.applySecurity = false;
		}
		
		return clearSecurity;
	}
	
	private String doRequest(String requestMethod, SwaggerRequestInfo requestInfo) {
		String parameters = "Method." + requestMethod + ", relativeUri, ";
		
		if (!requestInfo.getAllQueryParameters().isEmpty()) {
			parameters += "queryParams";
		} else {
			parameters += "null";
		}
		
		if (!requestInfo.getAllBodyParameters().isEmpty()) {
			parameters += ", bodyParam";
		}
		
		String doRequest = "response = super.getResponse(" + parameters + ");";
		
		return doRequest;
	}

}
