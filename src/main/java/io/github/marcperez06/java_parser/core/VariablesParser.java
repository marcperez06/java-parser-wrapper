package io.github.marcperez06.java_parser.core;

import java.util.NoSuchElementException;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.StringLiteralExpr;

class VariablesParser {

	private ClassOrInterfaceDeclaration classToParse;
	
	public VariablesParser(ClassOrInterfaceDeclaration classToParse) {
		this.classToParse = classToParse;
	}
	
	public void createStringConstant(String name, String expression, Keyword visibility) {
		if (expression != null && !expression.isEmpty()) {
			StringLiteralExpr initializer = new StringLiteralExpr(expression);
			this.createVariableWithInitializer(name, String.class, initializer, visibility, Keyword.STATIC, Keyword.FINAL);
		}
		
	}

	public void createVariableWithInitializer(String name, String variableType, Expression expression, Keyword...modifiers) {
		boolean canCreateVariable = this.canCreateVariable(name);
		if (canCreateVariable && expression != null) {
			this.classToParse.addFieldWithInitializer(variableType, name, expression, modifiers);
		}
	}
	
	public void createVariableWithInitializer(String name, Class<?> variableType, Expression expression, Keyword...modifiers) {
		boolean canCreateVariable = this.canCreateVariable(name);
		if (canCreateVariable && expression != null) {
			this.classToParse.addFieldWithInitializer(variableType, name, expression, modifiers);
		}
	}
	
	public void createVariable(String name, String variableType, Keyword...modifiers) {
		boolean canCreateVariable = this.canCreateVariable(name);
		if (canCreateVariable) {
			this.classToParse.addField(variableType, name, modifiers);
		}
	}
	
	public void createVariable(String name, Class<?> variableType, Keyword...modifiers) {
		boolean canCreateVariable = this.canCreateVariable(name);
		if (canCreateVariable) {
			this.classToParse.addField(variableType, name, modifiers);
		}
	}
	
	private boolean canCreateVariable(String name) {
		boolean canCreateVariable = false;
		if (this.classToParse != null) {
			try {
				this.classToParse.getFieldByName(name).get();
				canCreateVariable = false;
			} catch (NoSuchElementException e) {
				canCreateVariable = true;
			}
		}
		return canCreateVariable;
	}
	
}
