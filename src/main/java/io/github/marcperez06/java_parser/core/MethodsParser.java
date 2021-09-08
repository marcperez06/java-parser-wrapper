package io.github.marcperez06.java_parser.core;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.CallableDeclaration.Signature;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

class MethodsParser extends CallableParser {
	
	public MethodsParser(ClassOrInterfaceDeclaration classToParse) {
		super(classToParse);	
	}
	
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
		
		if (super.classToParse != null) {
			MethodDeclaration method = this.createMethodDeclaration(methodName, returnType, modifiers);
			this.fillMethod(method, methodBody, parameters);
			this.addMethod(method);
		}
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
		
		if (super.classToParse != null) {
			MethodDeclaration method = this.createMethodDeclaration(methodName, returnType, modifiers);
			this.fillMethod(method, methodBody, parameters);
			this.addMethodIfNotExist(method);
		}
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
		
		if (super.classToParse != null) {
			MethodDeclaration method = this.createMethodDeclaration(methodName, returnType, modifiers);
			this.fillMethod(method, methodBody, parameters);
			this.addMethod(method);
		}
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
		
		if (super.classToParse != null) {
			MethodDeclaration method = this.createMethodDeclaration(methodName, returnType, modifiers);
			this.fillMethod(method, methodBody, parameters);
			this.addMethodIfNotExist(method);
		}
	}
	
	private void fillMethod(MethodDeclaration method, String methodBody, List<Parameter> classParameters) {
		this.addParametersToCallable(method, classParameters);
		BlockStmt block = super.parseStatement(methodBody);
		this.setBodyMethod(method, block);
	}
	
	/**
	 * Create a method declaration
	 * @param methodName - String
	 * @param returnTypeClass - Class&lt;?&gt;
	 * @param modifiers - Keyword [enum] (Optional)
	 * @return MethodDeclaration
	 */
	public MethodDeclaration createMethodDeclaration(String methodName, Class<?> returnTypeClass, Keyword...modifiers) {		
		MethodDeclaration method = new MethodDeclaration();
		this.setMethodDeclarationNameAndModifiers(method, methodName, modifiers);
		method.setType(returnTypeClass);
		return method;
	}

	/**
	 * Create a method declaration
	 * @param methodName - String
	 * @param returnType - String
	 * @param modifiers - Keyword [enum] (Optional)
	 * @return MethodDeclaration
	 */
	public MethodDeclaration createMethodDeclaration(String methodName, String returnType, Keyword...modifiers) {		
		MethodDeclaration method = new MethodDeclaration();
		this.setMethodDeclarationNameAndModifiers(method, methodName, modifiers);
		method.setType(returnType);
		return method;
	}
	
	private void setMethodDeclarationNameAndModifiers(MethodDeclaration methodDeclaration, String methodName, Keyword...modifiers) {
		methodDeclaration.setName(methodName);
		methodDeclaration.setModifiers(modifiers);
	}

	public void setBodyMethod(MethodDeclaration method, BlockStmt block) {
		if (method != null) {
			method.setBody(block);
		}
	}
	
	public void addMethod(MethodDeclaration method) {
		if (super.classToParse != null) {
			super.classToParse.addMember(method);
		}
	}
	
	public void addMethodIfNotExist(MethodDeclaration method) {
		if (!this.existMethod(method)) {
			super.classToParse.addMember(method);
		}
	}
	
	public boolean existMethod(MethodDeclaration method) {
		boolean existMethod = false;
		if (super.classToParse != null) {
			List<MethodDeclaration> existentMethods = super.classToParse.getMethods();

			for (int i = 0; i < existentMethods.size() && !existMethod; i++) {
				MethodDeclaration existentMethod = existentMethods.get(i);
				Signature existentMethodSignature = existentMethod.getSignature();
				Signature methodSignature = method.getSignature();
				existMethod = methodSignature.equals(existentMethodSignature);
			}
			
		}
		return existMethod;
	}

}
