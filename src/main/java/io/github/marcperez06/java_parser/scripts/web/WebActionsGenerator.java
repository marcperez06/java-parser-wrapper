package io.github.marcperez06.java_parser.scripts.web;

import java.lang.reflect.Field;
import java.util.List;

import org.openqa.selenium.WebElement;

import io.github.marcperez06.java_utilities.reflection.ReflectionUtils;
import sogeti.testing_framework_base.core.application.components.web.WebComponent;
import sogeti.testing_framework_base.core.shared.java_parser.core.MyJavaParser;

public class WebActionsGenerator extends AbstractWebActionsGenerator{

	private static final String PREFIX = "this.elements.";
	
	public WebActionsGenerator(Class<?> baseClass) {
		super(baseClass);
		super.parser = new MyJavaParser();
	}
	
	public WebActionsGenerator(Class<?> baseClass, Class<?> destinationClass) {
		super(baseClass, destinationClass);
		super.parser = new MyJavaParser();
	}
	
	public void setActionClass(String actionClass) {
		this.currentActionClass = actionClass;
	}
	
	public void setPackageScope(String packageScope) {
		this.packageScope = packageScope;
		this.parser.setPackageScope(this.packageScope);
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
	
	public void execute() {

		this.parseDestinationClass();
		
		if (this.baseClass != null) {
			
			super.extendActionsClassIfNeeded();
			super.addImports();

			List<Field> fields = ReflectionUtils.getFieldsOfClass(this.baseClass);
			
			for (Field field : fields) {
				
				Class<?> fieldType = ReflectionUtils.getGenericTypeOfField(field);
				
				if (!WebComponent.class.isAssignableFrom(fieldType)) {
					super.generateMethodsBasedOnAnnotations(field);
				} else {
					WebComponentActionsGenerator componentGenerator = new WebComponentActionsGenerator(fieldType);
					componentGenerator.setPackageScope(super.packageScope);
					componentGenerator.execute();
				}
				
				this.generateMethodsBasedOnAnnotations(field);
			}

			String savePath = super.getSavePath();
			this.parser.deleteAndSaveClass(savePath);
			
		}
	}
}