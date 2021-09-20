/**
 * @author Marc Pérez Rodríguez
 */
package io.github.marcperez06.java_parser.scripts.examples.swagger;

import java.util.Map;
import java.util.Map.Entry;

import com.github.javaparser.ast.Modifier.Keyword;

import io.github.marcperez06.java_parser.core.JavaParserWrapper;
import io.github.marcperez06.java_parser.resources.objects.swagger.SwaggerEndpoint;
import io.github.marcperez06.java_parser.resources.objects.swagger.SwaggerObjectDefinition;
import io.github.marcperez06.java_parser.resources.objects.swagger.SwaggerRequestInfo;
import io.github.marcperez06.java_utilities.strings.StringUtils;

public class SwaggerInformationGenerator extends SwaggerAbstractGenerator {
	
	private String endpointsPackage;
	private String objectsPackage;
	private String actionsPackage;

	/**
	 * Constructor of class, need the base package where endpoitns and domain are created and Url or path of json of swagger
	 * @param basePackageName - String base package where endpoints and domain are created
	 * @param swaggerDocumentUri - String Url of swagger or path of Json
	 */
	public SwaggerInformationGenerator(String basePackageName, String swaggerDocumentUri) {
		super(basePackageName, swaggerDocumentUri);
		this.endpointsPackage = basePackageName + ".endpoints";
		this.objectsPackage = basePackageName + ".domain.objects";
		this.actionsPackage = basePackageName + ".domain.actions";
	}
	
	public void execute() {
		super.requestSwaggerDocumentation();
		this.createEndpointsAndDomainObjects();
	}
	
	public void createEndpointsAndDomainObjects() {
		if (super.swaggerDocumentation != null) {
			this.createEndpoints();
			this.createObjects();
			this.createActions();
		}
	}
	
	private void createEndpoints() {
		
		Map<String, Map<String, SwaggerEndpoint>> endpointsClusters = super.getClustersOfEndpoitns();
		
		super.deletePackage(endpointsPackage);
		
		for (Entry<String, Map<String, SwaggerEndpoint>> cluster : endpointsClusters.entrySet()) {
			
			String tag = cluster.getKey();
			String tagWithoutErrors = super.formatToCamelCase(tag);
			String className = StringUtils.capitalizeWord(tagWithoutErrors) + "Endpoints";
			
			JavaParserWrapper parser = new JavaParserWrapper(className, this.endpointsPackage);
			parser.setPackageScope(super.packageScope);
			parser.parseOrCreateClass();
			
			for (Entry<String, SwaggerEndpoint> endpoint : cluster.getValue().entrySet()) {
				this.createEndpoint(parser, endpoint.getKey(), endpoint.getValue());
			}
			
			parser.deleteAndSaveClass();
		}
		
	}
	
	private void createEndpoint(JavaParserWrapper parser, String endpoint, SwaggerEndpoint swaggerEndpoint) {
		if (swaggerEndpoint.haveAnyRequestInfo()) {
			SwaggerRequestInfo requestInfo = swaggerEndpoint.getRequestInfo();
			for (int i = 0; i < requestInfo.getTags().size(); i++) {
				String endpointName = super.getEndpointName(endpoint);
				String constantName = super.transformToConstantName(endpointName);
				String constantValue = this.transformRouteParamsOfEndpoint(endpoint);
				parser.createStringConstant(constantName, constantValue, Keyword.PUBLIC);
			}
		}
	}
	
	/**
	 * Transform the route params names, to numeric route params
	 * @param endpoint - String endpoint (Example: /api/Employee/{id} --> api/Employee/{0})
	 * @return String - endpoint with the route params transformed
	 */
	private String transformRouteParamsOfEndpoint(String endpoint) {
		StringBuilder stringBuilder = new StringBuilder();
		String endpointTransformed = "";
		boolean isRouteParam = false;
		boolean changeRouteParam = false;
		int countRouteParam = 0;
		
		if (endpoint != null && !endpoint.isEmpty()) {
			
			for (int i = 0; i < endpoint.length(); i++) {
				
				String charValue = String.valueOf(endpoint.charAt(i));

				if (!isRouteParam) {
					
					stringBuilder.append(charValue);
					
					if (charValue.equals("{")) {
						isRouteParam = true;
					}
					
				} else {
					
					if (!changeRouteParam) {
						stringBuilder.append(String.valueOf(countRouteParam));
						changeRouteParam = true;
						countRouteParam++;
					}
					
					if (charValue.equals("}")) {
						stringBuilder.append(charValue);
						isRouteParam = false;
						changeRouteParam = false;
					}
					
				}
				
			}
			
		}
		
		endpointTransformed = stringBuilder.toString();
		return endpointTransformed;
	}
	
	private void createObjects() {
		SwaggerObjectsGenerator objectsGenerator = new SwaggerObjectsGenerator(this.objectsPackage, "");
		objectsGenerator.packageScope = super.packageScope;
		objectsGenerator.deletePackage(this.objectsPackage);
		
		for (Entry<String, SwaggerObjectDefinition> objectDefinition : super.swaggerDocumentation.getDefinitions().entrySet()) {
			String className = StringUtils.capitalizeWord(super.formatToCamelCase(objectDefinition.getKey()));
			objectsGenerator.createObjectDefinitionArchitecture(className, objectDefinition.getValue());
		}
	}
	
	private void createActions() {
		SwaggerActionsGenerator actionsGenerator = new SwaggerActionsGenerator(this.actionsPackage, "");
		actionsGenerator.packageScope = super.packageScope;
		actionsGenerator.setSwaggerDocumentation(super.swaggerDocumentation);
		actionsGenerator.deletePackage(this.actionsPackage);
		actionsGenerator.execute();
	}
	
	/**
	 * Generates swagger information from url or file
	 * @param swaggerUrlOrFile - String url or file path
	 * @param packageName - String base package where the swagger information will be created
	 * @param packageScope - String (Optional), allows to create the architecture in "main" package or "test" package
	 */
	public static void generateInformation(String swaggerUrlOrFile, String packageName, String...packageScope) {
		SwaggerInformationGenerator generator = new SwaggerInformationGenerator(packageName, swaggerUrlOrFile);
		
		if (packageScope != null && packageScope.length > 0) {
			generator.setPackageScope(packageScope[0]);	
		}
		
		generator.execute();
	}
	
}