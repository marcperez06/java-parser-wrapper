package io.github.marcperez06.java_parser.scripts.web;

import java.lang.reflect.Field;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.github.javaparser.ast.Modifier.Keyword;

import io.github.marcperez06.java_utilities.reflection.ReflectionUtils;
import io.github.marcperez06.java_utilities.strings.StringUtils;
import sogeti.testing_framework_base.core.application.components.web.WebComponent;
import sogeti.testing_framework_base.core.shared.java_parser.core.MyJavaParser;

public class WebComponentActionsGenerator extends AbstractWebActionsGenerator {

	private static final String PREFIX = "this.";
	
	public WebComponentActionsGenerator(Class<?> baseClass) {
		super(baseClass);
		super.parser = new MyJavaParser(baseClass.getSimpleName(), baseClass.getPackage().getName());
	}
	
	public WebComponentActionsGenerator(Class<?> baseClass, Class<?> destinationClass) {
		super(baseClass, destinationClass);
		super.parser = new MyJavaParser(destinationClass.getSimpleName(), destinationClass.getPackage().getName());
	}
	
	@Override
	protected String getPropertyPrefix() {
		return PREFIX;
	}
	
	@Override
	protected String getCodeForCreateReturnVariable(Class<?> returnType) {
		String createReturnVariable = "";
		if (!returnType.equals(Void.TYPE)) {
			if (returnType.equals(Boolean.TYPE)) {
				createReturnVariable = "boolean doAction = false;";
			} else if (returnType.equals(Integer.TYPE)){
				createReturnVariable = "int doAction = -1;";
			} else if (returnType.equals(String.class)) {
				createReturnVariable = "String doAction = \"\";";
			} else if (returnType.equals(List.class)) {
				this.parser.addImport("java.util.ArrayList");
				createReturnVariable = "List doAction = new ArrayList();";
			} else if (returnType.equals(WebElement.class)) {
				createReturnVariable = "WebElement doAction = null;";
			}
		}
		return createReturnVariable;
	}
	
	@Override
	public void execute() {

		super.parseDestinationClass();
		
		if (this.baseClass != null) {
			
			//this.extendComponentClassIfNeeded();
			super.addImports();

			List<Field> fields = ReflectionUtils.getFieldsOfClass(this.baseClass);
			
			for (Field field : fields) {
				
				this.generateGetMethodOfField(field);
				
				Class<?> fieldType = ReflectionUtils.getGenericTypeOfField(field);
				
				if (!WebComponent.class.isAssignableFrom(fieldType)) {
					super.generateMethodsBasedOnAnnotations(field);
				} else {
					WebComponentActionsGenerator componentGenerator = new WebComponentActionsGenerator(fieldType);
					componentGenerator.setPackageScope(super.packageScope);
					componentGenerator.execute();
				}

			}

			super.parser.deleteAndSaveClass();
		}
	}

	protected void parseDestinationClass() {
		if (this.destinationClass != null) {
			this.parser.parseClass(this.destinationClass);
		}
	}
	
	private void extendComponentClassIfNeeded() {
		Class<?> parentClass = this.baseClass.getSuperclass();
		String parentClassName = parentClass.getSimpleName();
		
		if (!parentClassName.equals("Object")) {
			String baseName = StringUtils.cutStringWithOtherString(parentClassName, "Elements", 0);
			
			if (!baseName.equals("Page")) {
				String parentActionsName = baseName + "Actions";
				String importName = parentClass.getPackage().getName() + "." + parentActionsName;
				this.parser.extendClass(parentActionsName);
				this.parser.addImport(importName);
			}
		}
		
	}
	
	private void generateGetMethodOfField(Field field) {
		if (field != null) {
			String fieldName = field.getName();
			String variableType = this.getVariableType(field);
			this.createGetMethod(this.parser, fieldName, variableType);
		}
	}
	
	private String getVariableType(Field field) {
		String variableType = "";
		
		Class<?> fieldType = field.getType();
		
		if (fieldType.isAssignableFrom(List.class)) {
			Class<?> genericFieldType = ReflectionUtils.getGenericTypeOfField(field);
			
			variableType = "List<";
			
			if (genericFieldType != null) {
				variableType += genericFieldType.getSimpleName();
			} else {
				variableType += fieldType.getSimpleName();
			}
			
			variableType += ">";
			
			
		} else if (fieldType.isArray()) {
			variableType = fieldType.getSimpleName() + "[]";
		} else {
			variableType = fieldType.getSimpleName();
		}
		
		return variableType;
	}
	
	private void createGetMethod(MyJavaParser parser, String propertyName, String variableType) {
		String methodName = "get" + StringUtils.capitalizeWord(propertyName);
		String methodBody = "return this." + propertyName + ";";
		parser.createMethodIfNotExist(methodName, methodBody, null, variableType, Keyword.PUBLIC);
	}
	
}