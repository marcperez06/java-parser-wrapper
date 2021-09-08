package io.github.marcperez06.java_parser.scripts.swagger;

import java.util.Map;
import java.util.Map.Entry;

import com.github.javaparser.ast.Modifier.Keyword;

import sogeti.testing_framework_base.core.shared.java_parser.core.MyJavaParser;
import sogeti.testing_framework_base.core.shared.java_parser.objects.swagger.SwaggerEndpoint;
import sogeti.testing_framework_base.core.shared.java_parser.objects.swagger.SwaggerObjectDefinition;
import sogeti.testing_framework_base.core.shared.java_parser.objects.swagger.SwaggerRequestInfo;
import sogeti.testing_framework_base.utils.Utils;

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
			String className = Utils.capitalizeWord(tagWithoutErrors) + "Endpoints";
			
			MyJavaParser parser = new MyJavaParser(className, this.endpointsPackage);
			parser.setPackageScope(super.packageScope);
			parser.parseOrCreateClass();
			
			for (Entry<String, SwaggerEndpoint> endpoint : cluster.getValue().entrySet()) {
				this.createEndpoint(parser, endpoint.getKey(), endpoint.getValue());
			}
			
			parser.deleteAndSaveClass();
		}
		
	}
	
	private void createEndpoint(MyJavaParser parser, String endpoint, SwaggerEndpoint swaggerEndpoint) {
		if (swaggerEndpoint.haveAnyRequestInfo()) {
			SwaggerRequestInfo requestInfo = swaggerEndpoint.getRequestInfo();
			for (int i = 0; i < requestInfo.getTags().size(); i++) {
				String endpointName = super.getEndpointName(endpoint);
				String constantName = super.transformToConstantName(endpointName);
				String constantValue = this.transformRouteParamsOfEndpoint(endpoint);
				parser.createStringConstant(constantName, String.class, endpoint, Keyword.PUBLIC);
			}
		}
	}
	
	/*
	private Map<String, Map<String, SwaggerEndpoint>> getClustersOfEndpoitns2() {
		Map<String, Map<String, SwaggerEndpoint>> clusters = new HashMap<String, Map<String, SwaggerEndpoint>>();
		
		for (Entry<String, SwaggerEndpoint> endpointEntry : super.swaggerDocumentation.getPaths().entrySet()) {
			
			SwaggerEndpoint endpoint = endpointEntry.getValue();
			String endpointUrl = endpointEntry.getKey();
			
			if (endpoint.haveAnyRequestInfo()) {
				
				SwaggerRequestInfo requestInfo = endpoint.getRequestInfo();
				
				for (int i = 0; i < requestInfo.getTags().size(); i++) {
					String tag = requestInfo.getTags().get(i);
					if (!clusters.containsKey(tag)) {
						Map<String, SwaggerEndpoint> cluster = new HashMap<String, SwaggerEndpoint>();
						clusters.put(tag, cluster);
					}
					if (!clusters.get(tag).containsKey(endpointUrl)) {
						clusters.get(tag).put(endpointUrl, endpoint);
					}
				}
				
			}

		}
		
		return clusters;
	}
	
	private String getEndpointName(String endpoint) {
		StringBuilder endpointBuilder = new StringBuilder("/");
		String[] partsOfEndpoint = endpoint.split("/");
		boolean firstApiIgnored = false;

		if (partsOfEndpoint.length > 0) {
			
			for (int i = 0; i < partsOfEndpoint.length; i++) {
				String endpointPart = partsOfEndpoint[i];
				boolean notIsApi = !endpointPart.isEmpty();
				notIsApi &= (!firstApiIgnored) ? !endpointPart.equals("api") : true;
				
				if (notIsApi) {
					endpointBuilder.append(partsOfEndpoint[i]);
					endpointBuilder.append("/");
					firstApiIgnored = true;
				}
			}

			endpointBuilder.deleteCharAt(endpointBuilder.length() - 1);
			
		}

		return endpointBuilder.toString();
	}
	
	private String transformToConstantName(String variableName) {
		StringBuilder stringBuilder = new StringBuilder();
		String constantName = "";
		
		if (variableName != null && !variableName.isEmpty()) {
			
			for (int i = 0; i < variableName.length(); i++) {
				
				String letter = String.valueOf(variableName.charAt(i));
				
				if (this.isValidChar(letter)) {
					String constantLetter = this.getConstantLetter(letter, variableName, i);
					stringBuilder.append(constantLetter);
				}

			}
		}

		int lastCharIndex = stringBuilder.length() - 1;
		String lastChar = String.valueOf(stringBuilder.charAt(lastCharIndex));
		
		if (lastChar.equals("_")) {
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
		
		constantName = stringBuilder.toString().toUpperCase();
		return constantName;
	}
	
	private boolean isValidChar(String charValue) {
		boolean isValidChar = (!charValue.equals("{") && !charValue.equals("}"));
		isValidChar &= (!charValue.equals("(") && !charValue.equals(")"));
		isValidChar &= (!charValue.equals("[") && !charValue.equals("]"));
		isValidChar &= (!charValue.equals("+") && !charValue.equals("-"));
		isValidChar &= (!charValue.equals("*") && !charValue.equals("="));
		isValidChar &= (!charValue.equals("'") && !charValue.equals("\""));
		isValidChar &= (!charValue.equals(".") && !charValue.equals(","));
		isValidChar &= (!charValue.equals("/") && !charValue.equals("\\"));
		return isValidChar;
	}
	
	private String getConstantLetter(String letter, String variableName, int index) {
		String constantLetter = letter;

		if (index < variableName.length() - 1) {
			
			String nextLetter = String.valueOf(variableName.charAt(index + 1));
			
			boolean haveSplitter = this.isSplitter(nextLetter);

			if (haveSplitter) {
				constantLetter += "_";
			}
			
		}

		return constantLetter;
	}
	
	private boolean isSplitter(String charValue) {
		boolean isSplitter = (charValue.equals(".") || charValue.equals(","));
		isSplitter |= charValue.matches("^[A-Z0-9]+$");
		isSplitter |= (charValue.equals("{") || charValue.equals("}"));
		isSplitter |= (charValue.equals("(") || charValue.equals(")"));
		isSplitter |= charValue.equals("-");
		isSplitter |= charValue.equals("/");
		return isSplitter;
	}
	*/
	
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
			String className = Utils.capitalizeWord(super.formatToCamelCase(objectDefinition.getKey()));
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