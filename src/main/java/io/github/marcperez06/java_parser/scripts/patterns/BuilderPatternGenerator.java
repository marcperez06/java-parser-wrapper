package io.github.marcperez06.java_parser.scripts.patterns;

import java.lang.reflect.Field;
import java.util.List;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.Parameter;

import io.github.marcperez06.java_parser.core.JavaParserWrapper;
import io.github.marcperez06.java_parser.core.factory.ParametersFactory;
import io.github.marcperez06.java_utilities.reflection.ReflectionUtils;
import io.github.marcperez06.java_utilities.strings.StringUtils;

public class BuilderPatternGenerator {

	protected Class<?> baseClass;
	protected Class<?> destinationClass;
	protected JavaParserWrapper parser;
	private String className;
	private String packageName;
	private String packageScope;
	
	public BuilderPatternGenerator(Class<?> baseClass) {
		this.baseClass = baseClass;
		this.className = baseClass.getSimpleName() + "Builder";
		this.packageName = baseClass.getPackage().getName() + ".builder";
		this.parser = new JavaParserWrapper(this.className, this.packageName);
		this.setPackageScope("main");
	}
	
	public BuilderPatternGenerator(Class<?> baseClass, String packageName) {
		this.baseClass = baseClass;
		this.className = baseClass.getSimpleName() + "Builder";
		this.packageName = packageName;
		this.parser = new JavaParserWrapper(this.className, this.packageName);
		this.setPackageScope("main");
	}
	
	public void setPackageScope(String packageScope) {
		if (packageScope != null) {
			this.packageScope = packageScope;
			this.parser.setPackageScope(this.packageScope);
		}
	}
	
	public void execute() {
		if (this.className != null && !this.className.isEmpty()) {
			this.generateBuilder();
		}
	}
	
	private void generateBuilder() {
		if (this.parser != null) {
			List<Field> fields = ReflectionUtils.getFieldsOfClass(this.baseClass);
			
			this.parser.generateClass();
			this.parser.addImport(this.baseClass.getCanonicalName());
			this.createProperties(fields);
			
			String constructorBody = "this.buildedObject = null;";
			this.parser.createConstructor(constructorBody, null, Keyword.PUBLIC);
			this.createGettersAndSettersMethods(fields);
			this.createBuildMethod(fields);
			
			//String savePath = this.getSavePath(this.className, this.packageName);
			this.parser.deleteAndSaveClass();
		}
	}
	
	private void createProperties(List<Field> fields) {
		this.parser.createVariable("buildedObject", this.baseClass.getSimpleName(), Keyword.PRIVATE);
		for (Field field : fields) {
			this.createProperty(field);
		}
	}
	
	private void createGettersAndSettersMethods(List<Field> fields) {
		for (Field field : fields) {
			this.createGetMethod(field);
			this.createSetMethod(field);
		}
	}

	private void createProperty(Field field) {
		String propertyName = field.getName();
		Class<?> propertyType = ReflectionUtils.getGenericTypeOfField(field);
		this.parser.createVariable(propertyName, propertyType, Keyword.PRIVATE);
	}
	
	private void createGetMethod(Field field) {
		String methodName = field.getName();
		String methodBody = "return this." + methodName + ";";
		Class<?> paramType = ReflectionUtils.getGenericTypeOfField(field);
		this.parser.createMethodIfNotExist(methodName, methodBody, null, paramType, Keyword.PUBLIC);
	}
	
	private void createSetMethod(Field field) {
		String methodName = field.getName();
		String methodBody = "this." + methodName + " = " + methodName + ";";
		methodBody += "return this;";
		Class<?> paramType = ReflectionUtils.getGenericTypeOfField(field);
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter(methodName, paramType);
		parser.createMethodIfNotExist(methodName, methodBody, parameters, this.className, Keyword.PUBLIC);
	}
	
	private void createBuildMethod(List<Field> fields) {
		String methodName = "build";
		String returnType = this.baseClass.getSimpleName();
		String methodBody = "this.buildedObject = new " + returnType + "();";
		
		for (Field field : fields) {
			methodBody += this.createSetBuildedObjectPropertyForMethodBody(field);
		}
		
		methodBody += "return this.buildedObject;";
		this.parser.createMethodIfNotExist(methodName, methodBody, null, returnType, Keyword.PUBLIC);
	}
	
	private String createSetBuildedObjectPropertyForMethodBody(Field field) {
		String propertyName = field.getName();
		String methodBody = "if (this." + propertyName + " != null) {";
		methodBody += "this.buildedObject.set" + StringUtils.capitalizeWord(propertyName) + "(this." + propertyName + ");";
		methodBody += "}";
		return methodBody;
	}

	/**
	 * Generate the Builder class in a builder package created where the base class is placed
	 * @param baseClass - Class%lt;?&gt; base class used to generate the Builder class
	 * @param packageScope - String (Optional), allows to create the architecture in "main" package or "test" package
	 */
	public static void generateBuilder(Class<?> baseClass, String...packageScope) {
		String packageName = baseClass.getPackage().getName() + ".builder";
		generatBuilder(baseClass, packageName, packageScope);
	}

	/**
	 * Generate the Builder class
	 * @param baseClass - Class%lt;?&gt; base class used to generate the Builder class
	 * @param packageName - String
	 * @param packageScope - String (Optional), allows to create the architecture in "main" package or "test" package
	 */
	public static void generatBuilder(Class<?> baseClass, String packageName, String...packageScope) {
		BuilderPatternGenerator generator = new BuilderPatternGenerator(baseClass, packageName);
		
		if (packageScope != null && packageScope.length > 0) {
			generator.setPackageScope(packageScope[0]);	
		}
		
		generator.execute();
	}

}
