package io.github.marcperez06.java_parser.scripts.mobile;

import java.lang.reflect.Field;
import java.util.List;

import com.github.javaparser.ast.Modifier.Keyword;

import io.appium.java_client.MobileElement;
import io.github.marcperez06.java_utilities.reflection.ReflectionUtils;
import sogeti.testing_framework_base.core.application.components.mobile.MobileComponent;
import sogeti.testing_framework_base.core.shared.java_parser.core.MyJavaParser;
import sogeti.testing_framework_base.utils.Utils;

public class MobileComponentActionsGenerator extends AbstractMobileActionsGenerator {
	
	private static final String PREFIX = "this.";
	private static final String INIT_ELEMENTS = "super.initElements();";
	
	public MobileComponentActionsGenerator(Class<?> baseClass) {
		super(baseClass);
		super.parser = new MyJavaParser(baseClass.getSimpleName(), baseClass.getPackage().getName());
	}
	
	public MobileComponentActionsGenerator(Class<?> baseClass, Class<?> destinationClass) {
		super(baseClass);
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
			} else if (returnType.equals(MobileElement.class)) {
				createReturnVariable = "MobileElement doAction = null;";
			}
		}
		return createReturnVariable + INIT_ELEMENTS;
	}
	
	@Override
	public void execute() {
		
		super.parseDestinationClass();
		
		if (super.baseClass != null) {
			
			//this.extendComponentClassIfNeeded();
			super.addImports();
			
			List<Field> fields = ReflectionUtils.getFieldsOfClass(super.baseClass);
			
			for (Field field : fields) {
				
				this.generateGetMethodOfField(field);
				
				Class<?> fieldType = ReflectionUtils.getGenericTypeOfField(field);
				
				if (!MobileComponent.class.isAssignableFrom(fieldType)) {
					super.generateMethodsBasedOnAnnotations(field);
				} else {
					MobileComponentActionsGenerator componentGenerator = new MobileComponentActionsGenerator(fieldType);
					componentGenerator.setPackageScope(super.packageScope);
					componentGenerator.execute();
				}
				
			}

			super.parser.deleteAndSaveClass();
		}
	}
	
	private void extendComponentClassIfNeeded() {
		Class<?> parentClass = this.baseClass.getSuperclass();
		String parentClassName = parentClass.getSimpleName();
		
		if (!parentClassName.equals("Object")) {
			String baseName = Utils.cutStringWithOtherString(parentClassName, "Elements", 0);
			
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
		String methodName = "get" + Utils.capitalizeWord(propertyName);
		String methodBody = INIT_ELEMENTS + "return this." + propertyName + ";";
		parser.createMethodIfNotExist(methodName, methodBody, null, variableType, Keyword.PUBLIC);
	}

}