package io.github.marcperez06.java_parser.core;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

import io.github.marcperez06.java_utilities.file.FileUtils;

public class JavaParserWrapper {
	
	public static final String MAIN = "main";
	public static final String TEST = "test";

	private ClassParser classParser;
	private ImportsParser imports;
	private VariablesParser properties;
	private MethodsParser methods;
	private ConstructorsParser constructors;
	
	public JavaParserWrapper(String className, String packageName) {
		this.classParser = new ClassParser(className, packageName);
		this.generateClass();
		this.initParsers();
	}

	// -------------------- GETTERS AND SETTERS ---------------------

	public CompilationUnit getCompilationUnit() {
		return this.classParser.getCompilationUnit();
	}

	public void setCompilationUnit(CompilationUnit compilationUnit) {
		this.classParser.setCompilationUnit(compilationUnit);
	}

	public String getClassName() {
		return this.classParser.getClassName();
	}

	public void setClassName(String className) {
		this.classParser.setClassName(className);
	}

	public String getPackageName() {
		return this.classParser.getPackageName();
	}

	public void setPackageName(String packageName) {
		this.classParser.setPackageName(packageName);
	}

	public ClassOrInterfaceDeclaration getNewClass() {
		return this.classParser.getNewClass();
	}

	public void setNewClass(ClassOrInterfaceDeclaration classToParse) {
		this.classParser.setNewClass(classToParse);
	}

	public String getPackageScope() {
		return this.classParser.getPackageScope();
	}

	public void setPackageScope(String packageScope) {
		this.classParser.setPackageScope(packageScope);
	}

	// ------------- CLASS PARSER ------------------------
	
	public CompilationUnit generateClass() {
		this.classParser.generateClass();
		return this.classParser.getCompilationUnit();
	}
	
	private void initParsers() {
		boolean canInitParsers = (this.classParser.getCompilationUnit() != null && this.classParser.getNewClass() != null);
		if (canInitParsers) {
			this.imports = new ImportsParser(this.classParser.getCompilationUnit());
			this.properties = new VariablesParser(this.classParser.getNewClass());
			this.methods = new MethodsParser(this.classParser.getNewClass());
			this.constructors = new ConstructorsParser(this.classParser.getNewClass(), this.classParser.getClassName());
			
			this.imports.addImports();
		}
	}
	
	public BlockStmt parseStatement(String statement) {
		return this.methods.parseStatement(statement);
	}
	
	public CompilationUnit parseOrCreateClass() {
		return this.classParser.parseOrCreateClass();
	}
	
	public CompilationUnit parseClass(Class<?> clazz) {
		return this.classParser.parseClass(clazz);
	}
	
	public void extendClass(String parent) {
		this.classParser.extendClass(parent);
	}
	
	public void extendClass(Class<?> parent) {
		this.classParser.extendClass(parent);
	}

	public void createAnnotationClass(Class classOfAnnotation) {
		this.classParser.createAnnotationClass(classOfAnnotation);
	}
	
	public void createAnnotationClass(String classOfAnnotation) {
		this.classParser.createAnnotationClass(classOfAnnotation);
	}
	
	// ---------------------- IMPORTS ------------------
	
	public List<String> getImports() {
		return this.imports.getImports();
	}

	public void setImports(List<String> imports) {
		this.imports.setImports(imports);
	}
	
	public void addImport(String importName) {
		this.imports.addImport(importName);
	}
	
	public boolean notExistImport(String importName) {
		return this.imports.notExistImport(importName);
	}
	
	public boolean existImport(String importName) {
		return this.imports.existImport(importName);
	}
	
	public void addImports(List<String> imports) {
		this.imports.addImports(imports);
	}
	
	// ---------------------- PROPERTIES ---------------
	
	public void createStringConstant(String name, String expression, Keyword visibility) {
		this.properties.createStringConstant(name, expression, visibility);
	}
	
	public void createVariableWithInitializer(String name, String variableType, Expression expression, Keyword...modifiers) {
		this.properties.createVariableWithInitializer(name, variableType, expression, modifiers);
	}
	
	public void createVariableWithInitializer(String name, Class<?> variableType, Expression expression, Keyword...modifiers) {
		this.properties.createVariableWithInitializer(name, variableType, expression, modifiers);
	}
	
	public void createVariable(String name, String variableType, Keyword...modifiers) {
		this.properties.createVariable(name, variableType, modifiers);
	}
	
	public void createVariable(String name, Class<?> variableType, Keyword...modifiers) {
		this.properties.createVariable(name, variableType, modifiers);
	}
	
	// ---------------------- CONSTRUCTORS ---------------
	
	/**
	 * Create a constructor declaration without params
	 * @param modifiers - Keyword [enum] (Optional)
	 * @return ConstructorDeclaration
	 */
	public ConstructorDeclaration createConstructorDeclaration(Keyword...modifiers) {		
		return this.constructors.createConstructorDeclaration(modifiers);
	}
	
	/**
	 * Create a constructor declaration
	 * @param parameters - List&lt;Parameter&gt; parameters of constructor
	 * @param modifiers - Keyword [enum] (Optional)
	 * @return ConstructorDeclaration
	 */
	public ConstructorDeclaration createConstructorDeclaration(List<Parameter> parameters, Keyword...modifiers) {		
		return this.constructors.createConstructorDeclaration(parameters, modifiers);
	}
	
	public void createConstructor(List<Parameter> parameters, Keyword...modifiers) {
		this.constructors.createConstructor("", parameters, modifiers);
	}
	
	public void createConstructor(String body, List<Parameter> parameters, Keyword...modifiers) {
		this.constructors.createConstructor(body, parameters, modifiers);
	}
	
	public void createDefaultConstructor(Keyword modifier) {
		this.constructors.createDefaultConstructor(modifier);
	}
	
	public boolean existConstructor(ConstructorDeclaration constructor) {
		return this.existConstructor(constructor);
	}
	
	public void callSuperMethod(CallableDeclaration<?> callable) {
		if (callable.isMethodDeclaration()) {
			this.methods.callSuperMethod(callable);
		} else if (callable.isConstructorDeclaration()) {
			this.constructors.callSuperMethod(callable);
		}
	}
	
	public void callSuperMethod(CallableDeclaration<?> callable, List<Parameter> parameters) {
		if (callable.isMethodDeclaration()) {
			this.methods.callSuperMethod(callable, parameters);
		} else if (callable.isConstructorDeclaration()) {
			this.constructors.callSuperMethod(callable, parameters);
		}
	}
	
	public void addBodyToDefaultConstructor(String body) {
		this.constructors.addBodyToDefaultConstructor(body);
	}
	
	public void addBodyToConstructor(ConstructorDeclaration constructor, String body) {
		this.constructors.addBodyToConstructor(constructor, body);
	}
	
	// ---------------------- METHODS ---------------

	/**
	 * Create method in the class
	 * @param methodName - String
	 * @param methodBody - String
	 * @param parameters - List&lt;Parameter&gt; of method
	 * @param returnType - String class name of return type
	 * @param modifiers - Keyword [enum] (Optional)
	 */
	public void createMethod(String methodName, String methodBody, 
								List<Parameter> parameters, String returnType, Keyword...modifiers) {
		
		this.methods.createMethod(methodName, methodBody, parameters, returnType, modifiers);
	}
	
	/**
	 * Create method in the class, if not exist
	 * @param methodName - String
	 * @param methodBody - String
	 * @param parameters - List&lt;Parameter&gt; of method
	 * @param returnType - String class name of return type
	 * @param modifiers - Keyword [enum] (Optional)
	 */
	public void createMethodIfNotExist(String methodName, String methodBody, 
										List<Parameter> parameters, String returnType, Keyword...modifiers) {
		
		this.methods.createMethodIfNotExist(methodName, methodBody, parameters, returnType, modifiers);
	}
	
	/**
	 * Create method in the class
	 * @param methodName - String
	 * @param methodBody - String
	 * @param parameters - List&lt;Parameter&gt; of method
	 * @param returnType - Class&lt;?&gt; of return type
	 * @param modifiers - Keyword [enum] (Optional)
	 */
	public void createMethod(String methodName, String methodBody, 
								List<Parameter> parameters, Class<?> returnType, Keyword...modifiers) {
		
		this.methods.createMethod(methodName, methodBody, parameters, returnType, modifiers);
	}

	/**
	 * Create method in the class, if not exist
	 * @param methodName - String
	 * @param methodBody - String
	 * @param parameters - List&lt;Parameter&gt; of method
	 * @param returnType - Class&lt;?&gt; of return type
	 * @param modifiers - Keyword [enum] (Optional)
	 */
	public void createMethodIfNotExist(String methodName, String methodBody, 
											List<Parameter> parameters, Class<?> returnType, Keyword...modifiers) {
		
		this.methods.createMethodIfNotExist(methodName, methodBody, parameters, returnType, modifiers);
	}
	
	/**
	 * Create a method declaration
	 * @param methodName - String
	 * @param returnTypeClass - Class&lt;?&gt;
	 * @param modifiers - Keyword [enum] (Optional)
	 * @return MethodDeclaration
	 */
	public MethodDeclaration createMethodDeclaration(String methodName, Class<?> returnTypeClass, Keyword...modifiers) {		
		return this.methods.createMethodDeclaration(methodName, returnTypeClass, modifiers);
	}

	/**
	 * Create a method declaration
	 * @param methodName - String
	 * @param returnType - String
	 * @param modifiers - Keyword [enum] (Optional)
	 * @return MethodDeclaration
	 */
	public MethodDeclaration createMethodDeclaration(String methodName, String returnType, Keyword...modifiers) {		
		return this.createMethodDeclaration(methodName, returnType, modifiers);
	}
	
	public void addParametersToCallable(CallableDeclaration<?> callable, List<Parameter> parameters) {
		this.methods.addParametersToCallable(callable, parameters);
	}
	
	public void setBodyMethod(MethodDeclaration method, BlockStmt block) {
		this.methods.setBodyMethod(method, block);
	}
	
	public void addBodyToMethod(CallableDeclaration<?> callable, String body) {
		this.methods.addBodyToCallable(callable, body);
	}
	
	public void addMethod(MethodDeclaration method) {
		this.methods.addMethod(method);
	}
	
	public void addMethodIfNotExist(MethodDeclaration method) {
		this.methods.addMethodIfNotExist(method);
	}
	
	public boolean existMethod(MethodDeclaration method) {
		return this.methods.existMethod(method);
	}
	
	// --------------------- SAVE COMPILATION UNIT --------------------
	
	/**
	 * Delete existent class and save it in the default path: "src/{packageScope}/{packageName}/{className}.java"
	 * {packageScope} = "main" or "test"
	 * {packageName} = Package specified in the constructor
	 * {className} = Name specified in the constructor
	 */
	public void deleteAndSaveClass() {
		String savePath = this.getDefaultSavePath();
		this.deleteAndSaveClass(savePath);
	}
	
	/**
	 * Save class if not exist in the default path: "src/{packageScope}/{packageName}/{className}.java"
	 * {packageScope} = "main" or "test"
	 * {packageName} = Package specified in the constructor
	 * {className} = Name specified in the constructor
	 */
	public void saveClassIfNotExist() {
		String savePath = this.getDefaultSavePath();
		this.saveClassIfNotExist(savePath);
	}
	
	public boolean existClass() {
		String savePath = this.getDefaultSavePath();
		return FileUtils.existFile(savePath);
	}
	
	public boolean existClass(String savePath) {
		return FileUtils.existFile(savePath);
	}
	
	private String getDefaultSavePath() {
		String fileSeparator = System.getProperty("file.separator");
		String baseDir = System.getProperty("user.dir") + fileSeparator;
		baseDir += "src" + fileSeparator + this.classParser.getPackageScope() + fileSeparator + "java" + fileSeparator;
		String packagePath = this.classParser.getPackageName().replaceAll("\\.", "\\" + fileSeparator);
		String savePath = baseDir + packagePath + fileSeparator + this.classParser.getClassName() + ".java";
		return savePath;
	}
	
	public void deleteAndSaveClass(String savePath) {
		if (this.classParser.getCompilationUnit() != null && savePath.isEmpty() == false) {
			FileUtils.deleteFile(savePath);
			FileUtils.writeTxt(this.classParser.getCompilationUnit().toString(), savePath);
		}
	}
	
	public void saveClassIfNotExist(String savePath) {
		boolean canSaveClass = (this.classParser.getCompilationUnit() != null);
		canSaveClass &= !savePath.isEmpty();
		canSaveClass &= !FileUtils.existFile(savePath);
		if (canSaveClass == true) {
			FileUtils.writeTxt(this.classParser.getCompilationUnit().toString(), savePath);
		}
	}

}