package io.github.marcperez06.java_parser.core;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.CallableDeclaration.Signature;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

class ConstructorsParser extends CallableParser {

	private String constructorName;
	
	public ConstructorsParser(ClassOrInterfaceDeclaration classToParse, String constructorName) {
		super(classToParse);
		this.constructorName = constructorName;
	}

	/**
	 * Create a constructor declaration without params
	 * @param modifiers - Keyword [enum] (Optional)
	 * @return ConstructorDeclaration
	 */
	public ConstructorDeclaration createConstructorDeclaration(Keyword...modifiers) {		
		ConstructorDeclaration constructor = new ConstructorDeclaration();
		constructor.setModifiers(Modifier.createModifierList(modifiers));
		constructor.setName(this.constructorName);
		return constructor;
	}
	
	/**
	 * Create a constructor declaration
	 * @param parameters - List&lt;Parameter&gt; parameters of constructor
	 * @param modifiers - Keyword [enum] (Optional)
	 * @return ConstructorDeclaration
	 */
	public ConstructorDeclaration createConstructorDeclaration(List<Parameter> parameters, Keyword...modifiers) {		
		ConstructorDeclaration constructor = new ConstructorDeclaration();
		constructor.setModifiers(Modifier.createModifierList(modifiers));
		constructor.setName(this.constructorName);
		super.addParametersToCallable(constructor, parameters);
		return constructor;
	}
	
	public void createConstructor(String body, List<Parameter> parameters, Keyword...modifiers) {
		if (super.classToParse != null) {
			ConstructorDeclaration constructor = this.createConstructorDeclaration(parameters, modifiers);			
			if (!this.existConstructor(constructor)) {
				constructor = super.classToParse.addConstructor(modifiers);
				super.addParametersToCallable(constructor, parameters);
				this.callSuperMethod(constructor, parameters);
				this.addBodyToConstructor(constructor, body);
			}
		}
	}
	
	public void createDefaultConstructor(Keyword modifier) {
		if (super.classToParse != null) {
			ConstructorDeclaration constructor = this.createConstructorDeclaration(modifier);
			if (!this.existConstructor(constructor)) {
				constructor = super.classToParse.addConstructor(modifier);
				this.callSuperMethod(constructor);	
			}
		}
	}
	
	public boolean existConstructor(ConstructorDeclaration constructor) {
		boolean existConstructor = false;
		if (super.classToParse != null) {
			List<ConstructorDeclaration> existentConstructors = super.classToParse.getConstructors();

			for (int i = 0; i < existentConstructors.size() && !existConstructor; i++) {
				ConstructorDeclaration existentConsturctor = existentConstructors.get(i);
				Signature existentConstructorSignature = existentConsturctor.getSignature();
				Signature constructorSignature = constructor.getSignature();
				existConstructor = constructorSignature.equals(existentConstructorSignature);
			}
			
		}
		return existConstructor;
	}

	public void addBodyToDefaultConstructor(String body) {
		if (super.classToParse != null) {
			
			Optional<ConstructorDeclaration> optConstructor = super.classToParse.getDefaultConstructor();
			
			if (optConstructor.isPresent() == true) {
				ConstructorDeclaration constructor = optConstructor.get();
				this.addBodyToConstructor(constructor, body);
			}

		}
	}
	
	public void addBodyToConstructor(ConstructorDeclaration constructor, String body) {
		if (constructor != null) {
			BlockStmt addedBlock = this.parseStatement(body);
			if(addedBlock != null) {
				BlockStmt bodyBlock = constructor.getBody();
				for (Statement statement : addedBlock.getStatements()) {
					bodyBlock.addStatement(statement);
				}
				constructor.setBody(bodyBlock);
			}
		}
	}
	
}
