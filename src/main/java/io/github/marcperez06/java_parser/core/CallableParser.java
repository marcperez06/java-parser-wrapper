package io.github.marcperez06.java_parser.core;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

class CallableParser {
	
	protected ClassOrInterfaceDeclaration classToParse;
	
	public CallableParser(ClassOrInterfaceDeclaration classToParse) {
		this.classToParse = classToParse;
	}
	
	public BlockStmt parseStatement(String statement) {
		BlockStmt block = null;
		if (statement.isEmpty() == false) {
			block = JavaParser.parseBlock("{" + statement + "}");
		}
		return block;
	}
	
	public void addParametersToCallable(CallableDeclaration<?> callable, List<Parameter> parameters) {
		if (parameters != null && parameters.isEmpty() == false) {
			
			for (int i = 0; i < parameters.size(); i++) {
				Parameter param = parameters.get(i);
				callable.addParameter(param);
			}
			
		}
	}
	
	public void addBodyToCallable(CallableDeclaration<?> callable, String body) {
		if (callable != null && callable.isMethodDeclaration() == true) {
			MethodDeclaration method = (MethodDeclaration) callable;
			
			BlockStmt addedBlock = this.parseStatement(body);
			if(addedBlock != null) {
				
				Optional<BlockStmt> optMethod = method.getBody();
				
				if (optMethod.isPresent() == true) {
					BlockStmt bodyBlock = optMethod.get();
					for (Statement statement : addedBlock.getStatements()) {
						bodyBlock.addStatement(statement);
					}
					method.setBody(bodyBlock);
				}

			}
		}
	}
	
	public void callSuperMethod(CallableDeclaration<?> callable) {
		this.callSuperMethod(callable, null);
	}
	
	public void callSuperMethod(CallableDeclaration<?> callable, List<Parameter> parameters) {
		if (!this.classToParse.getExtendedTypes().isEmpty()) {
			MethodCallExpr superMethod = new MethodCallExpr();
			
			if (callable.isMethodDeclaration()) {
				superMethod.setName("super." + callable.getNameAsString());
			} else if (callable.isConstructorDeclaration()) {
				superMethod.setName("super");
			}
			
			if (parameters != null) {
				for (Parameter param : parameters) {
					superMethod.addArgument(param.getNameAsString());
				}
			}
			
			BlockStmt body = new BlockStmt();
			body.addStatement(superMethod);
			
			if (callable.isMethodDeclaration()) { 
				MethodDeclaration method = (MethodDeclaration) callable;
				method.setBody(body);
			} else if (callable.isConstructorDeclaration()) {
				ConstructorDeclaration constructor = (ConstructorDeclaration) callable;
				constructor.setBody(body);
			}
			
		}
	}

}
