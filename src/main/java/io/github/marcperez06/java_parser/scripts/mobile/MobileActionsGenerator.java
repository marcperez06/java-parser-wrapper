package io.github.marcperez06.java_parser.scripts.mobile;

import java.lang.reflect.Field;
import java.util.List;

import io.appium.java_client.MobileElement;
import io.github.marcperez06.java_utilities.reflection.ReflectionUtils;
import sogeti.testing_framework_base.core.application.components.mobile.MobileComponent;
import sogeti.testing_framework_base.core.shared.java_parser.core.MyJavaParser;

public class MobileActionsGenerator extends AbstractMobileActionsGenerator {
	
	private static final String PREFIX = "this.elements.";
	
	public MobileActionsGenerator(Class<?> baseClass) {
		super(baseClass);
		super.parser = new MyJavaParser();
	}
	
	public MobileActionsGenerator(Class<?> baseClass, Class<?> destinationClass) {
		super(baseClass, destinationClass);
		super.parser = new MyJavaParser();
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
		return createReturnVariable;
	}
	
	@Override
	public void execute() {
		
		super.parseDestinationClass();
		
		if (super.baseClass != null) {
			
			super.extendActionsClassIfNeeded();
			super.addImports();
			
			List<Field> fields = ReflectionUtils.getFieldsOfClass(this.baseClass);
			
			for (Field field : fields) {
				
				Class<?> fieldType = ReflectionUtils.getGenericTypeOfField(field);
				
				if (!MobileComponent.class.isAssignableFrom(fieldType)) {
					super.generateMethodsBasedOnAnnotations(field);
				} else {
					MobileComponentActionsGenerator componentGenerator = new MobileComponentActionsGenerator(fieldType);
					componentGenerator.setPackageScope(super.packageScope);
					componentGenerator.execute();
				}
				
			}

			String savePath = super.getSavePath();
			super.parser.deleteAndSaveClass(savePath);
			
		}
	}

}