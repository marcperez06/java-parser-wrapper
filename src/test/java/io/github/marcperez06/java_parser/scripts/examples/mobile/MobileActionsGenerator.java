package io.github.marcperez06.java_parser.scripts.examples.mobile;

import java.lang.reflect.Field;
import java.util.List;

import io.github.marcperez06.java_parser.core.JavaParserWrapper;
import io.github.marcperez06.java_utilities.reflection.ReflectionUtils;

public class MobileActionsGenerator extends AbstractMobileActionsGenerator {
	
	private static final String PREFIX = "this.elements.";
	
	public MobileActionsGenerator(Class<?> baseClass) {
		super(baseClass);
		super.parser = new JavaParserWrapper(baseClass.getName(), baseClass.getPackage().getName());
	}
	
	public MobileActionsGenerator(Class<?> baseClass, Class<?> destinationClass) {
		super(baseClass, destinationClass);
		super.parser = new JavaParserWrapper(destinationClass.getName(), destinationClass.getPackage().getName());
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
				super.parser.addImport("java.util.ArrayList");
				createReturnVariable = "List doAction = new ArrayList();";
			}/* else if (returnType.equals(MobileElement.class)) {
				createReturnVariable = "MobileElement doAction = null;";
			}*/
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
				
				//Class<?> fieldType = ReflectionUtils.getGenericTypeOfField(field);
				
				super.generateMethodsBasedOnAnnotations(field);
				
				/*
				if (!MobileComponent.class.isAssignableFrom(fieldType)) {
					super.generateMethodsBasedOnAnnotations(field);
				} else {
					MobileComponentActionsGenerator componentGenerator = new MobileComponentActionsGenerator(fieldType);
					componentGenerator.setPackageScope(super.packageScope);
					componentGenerator.execute();
				}
				*/
				
			}

			String savePath = super.getSavePath();
			super.parser.deleteAndSaveClass(savePath);
			
		}
	}

}