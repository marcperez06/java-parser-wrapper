package io.github.marcperez06.java_parser.scripts.swagger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.Parameter;

import sogeti.testing_framework_base.core.shared.java_parser.core.MyJavaParser;
import sogeti.testing_framework_base.core.shared.java_parser.core.ParametersFactory;
import sogeti.testing_framework_base.core.shared.java_parser.objects.swagger.SwaggerObjectDefinition;
import sogeti.testing_framework_base.core.shared.java_parser.objects.swagger.SwaggerObjectDefinitionSchema;
import sogeti.testing_framework_base.utils.Utils;

public class SwaggerObjectsGenerator extends SwaggerAbstractGenerator {
	
	public SwaggerObjectsGenerator(String packageName, String swaggerDocumentUri) {
		super(packageName, swaggerDocumentUri);
	}
	
	public void execute() {
		super.requestSwaggerDocumentation();
		
		if (super.swaggerDocumentation != null) {
			for (Entry<String, SwaggerObjectDefinition> objectDefinition : super.swaggerDocumentation.getDefinitions().entrySet()) {
				String className = Utils.capitalizeWord(super.formatToCamelCase(objectDefinition.getKey()));
				this.createObjectDefinitionArchitecture(className, objectDefinition.getValue());
			}
		}

	}

	public void createObjectDefinitionArchitecture(String className, SwaggerObjectDefinition objectDefinition) {
		String[] splittedClassName = className.split("\\[");
		className = splittedClassName[0];

		if (this.canCreateClass(className, objectDefinition)) {
			String packageForClass = super.getPackageNameForClass(className);
			
			MyJavaParser parser = new MyJavaParser(className, packageForClass);
			parser.setPackageScope(super.packageScope);
			parser.parseOrCreateClass();
			this.createObjectDefinition(objectDefinition, parser);
			parser.deleteAndSaveClass();
		}
		
	}
	
	private boolean canCreateClass(String className, SwaggerObjectDefinition objectDefinition) {
		boolean canCreateClass = (!className.contains("{") && !className.contains("}"));
		canCreateClass &= (objectDefinition != null);
		if (canCreateClass) {
			String type = objectDefinition.getType();
			canCreateClass = (type != null && type.equals("object"));
			
			if (!canCreateClass) {
				canCreateClass = (objectDefinition.getProperties() != null && !objectDefinition.getProperties().isEmpty());
			}
			
		}
		return canCreateClass;
	}

	private void createObjectDefinition(SwaggerObjectDefinition objectDefinition, MyJavaParser parser) {
	
		Map<String, SwaggerObjectDefinitionSchema> properties = objectDefinition.getProperties();
		
		if (properties != null && !properties.isEmpty()) {
			
			// Create Properties of Class
			for (Entry<String, SwaggerObjectDefinitionSchema> property : properties.entrySet()) {
				this.createObjectDefinitionVariables(property, parser);
			}
			
			// Create Empty constructor for class
			parser.createDefaultConstructor(Keyword.PUBLIC);
			
			// Create Getters and Setters
			for (Entry<String, SwaggerObjectDefinitionSchema> property : properties.entrySet()) {
				this.createObjectDefinitionMethods(property, parser);
			}
			
		}
		
	}
	
	private void createObjectDefinitionVariables(Entry<String, SwaggerObjectDefinitionSchema> property,
													MyJavaParser parser) {
		
		String propertyName = super.formatToCamelCase(property.getKey());
		SwaggerObjectDefinitionSchema schema = property.getValue();
		
		if (schema != null) {
			Map<String, List<String>> mapForVariableAndImports = this.getVariableTypeAndImports(schema);
			String variableType = mapForVariableAndImports.get("variable").get(0);
			parser.addImports(mapForVariableAndImports.get("imports"));
			parser.createVariable(propertyName, variableType, Keyword.PRIVATE);
		}

	}
	
	private void createObjectDefinitionMethods(Entry<String, SwaggerObjectDefinitionSchema> property,
													MyJavaParser parser) {
		
		String propertyName = super.formatToCamelCase(property.getKey());
		SwaggerObjectDefinitionSchema schema = property.getValue();
		
		if (schema != null) {
			Map<String, List<String>> mapForVariableAndImports = this.getVariableTypeAndImports(schema);
			String variableType = mapForVariableAndImports.get("variable").get(0);
			String suffixMethodName = Utils.capitalizeWord(propertyName);
			
			this.createGetMethod(parser, suffixMethodName, propertyName, variableType);
			
			if (!schema.getReadOnly()) {
				this.createSetMethod(parser, suffixMethodName, propertyName, variableType);
			}
		}
		
	}
	
	private Map<String, List<String>> getVariableTypeAndImports(SwaggerObjectDefinitionSchema schema) {
		String variableType = "";
		String importList = "java.util.List";
		Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
		resultMap.put("variable", new ArrayList<String>());
		resultMap.put("imports", new ArrayList<String>());
		
		boolean haveType = (schema.getType() != null && !schema.getType().isEmpty());
		boolean haveRef = (schema.get$ref() != null && !schema.get$ref().isEmpty());
		
		if (haveType || haveRef) {
			
			if (haveType) {
				
				String type = schema.getType();
				boolean isList = (type.equalsIgnoreCase("array") || type.equalsIgnoreCase("list"));
				
				if (isList && schema.getItems() == null) {
					resultMap.get("imports").add(importList);
					variableType = "List";
				} else if (isList && schema.getItems() != null) {
					Map<String, List<String>> map = this.getVariableTypeAndImports(schema.getItems());
					
					type = super.formatToCamelCase(map.get("variable").get(0));
					variableType = "List<" + Utils.capitalizeWord(type) + ">";
					
					resultMap.get("imports").add(importList);
					if (!map.get("imports").isEmpty()) {
						for (String importName : map.get("imports")) {
							resultMap.get("imports").add(importName);
						}
					}

				} else {
					
					String[] typeSplitted = type.split("\\.");
					
					if (typeSplitted.length > 1) {
						
						if (typeSplitted[0].equalsIgnoreCase("array") || typeSplitted[0].equalsIgnoreCase("list")) {
							resultMap.get("imports").add(importList);
							variableType = "List<" + Utils.capitalizeWord(typeSplitted[1]) + ">";
						} else {
							variableType = Utils.capitalizeWord(typeSplitted[0]);
						}
						
					} else {
						type = super.formatToCamelCase(type);
						variableType = Utils.capitalizeWord(type);
					}
					
				}
				
			} else if (haveRef) {
				String[] splittedRef = schema.get$ref().split("/");
				variableType = splittedRef[splittedRef.length - 1];
				splittedRef = variableType.split("\\[");
				variableType = super.formatToCamelCase(splittedRef[0]);
				variableType = Utils.capitalizeWord(variableType);

				String packageForClass = super.getPackageNameForClass(variableType) + "." + variableType;
				resultMap.get("imports").add(packageForClass);
			}
			
		}
		
		resultMap.get("variable").add(variableType);
		
		return resultMap;
	}
	
	private void createGetMethod(MyJavaParser parser, String suffixMethodName, String propertyName, String variableType) {
		String methodName = "get" + suffixMethodName;
		String methodBody = "return this." + propertyName + ";";
		parser.createMethodIfNotExist(methodName, methodBody, null, variableType, Keyword.PUBLIC);
	}
	
	private void createSetMethod(MyJavaParser parser, String suffixMethodName, String propertyName, String variableType) {
		String methodName = "set" + suffixMethodName;
		String methodBody = "this." + propertyName + " = " + propertyName + ";";
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter(propertyName, variableType);
		parser.createMethodIfNotExist(methodName, methodBody, parameters, Void.TYPE, Keyword.PUBLIC);
	}
	
	/**
	 * Generates swagger information from url or file
	 * @param swaggerUrlOrFile - String url or file path
	 * @param packageName - String package where the swagger information will be created
	 * @param packageScope - String (Optional), allows to create the architecture in "main" package or "test" package
	 */
	public static void generateDomain(String swaggerUrlOrFile, String packageName, String...packageScope) {
		SwaggerObjectsGenerator generator = new SwaggerObjectsGenerator(packageName, swaggerUrlOrFile);
		
		if (packageScope != null && packageScope.length > 0) {
			generator.setPackageScope(packageScope[0]);	
		}
		
		generator.execute();
	}

}