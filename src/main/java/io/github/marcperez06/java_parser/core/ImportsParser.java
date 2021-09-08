package io.github.marcperez06.java_parser.core;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;

class ImportsParser {
	
	private CompilationUnit compilationUnit;
	private List<String> imports;

	public ImportsParser(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
		this.imports = new ArrayList<String>();
	}
	
	public ImportsParser(CompilationUnit compilationUnit, List<String> imports) {
		this.compilationUnit = compilationUnit;
		this.imports = imports;
	}
	
	public List<String> getImports() {
		return this.imports;
	}

	public void setImports(List<String> imports) {
		this.imports = imports;
	}
	
	public void addImports(List<String> imports) {
		this.setImports(imports);
		this.addImports();
	}
	
	public void addImports() {
		if (this.imports != null) {
			for (String importName : this.imports) {
				this.addImport(importName);
			}
		}
	}
	
	public void addImport(String importName) {
		if (this.compilationUnit != null && this.notExistImport(importName)) {
			this.compilationUnit.addImport(importName);
		}
	}
	
	public boolean notExistImport(String importName) {
		return !this.existImport(importName);
	}
	
	public boolean existImport(String importName) {
		boolean existImport = false;
		if (this.compilationUnit != null) {
			NodeList<ImportDeclaration> imports = this.compilationUnit.getImports();
			for (int i = 0; i < imports.size() && !existImport; i++) {
				ImportDeclaration importDeclaration = imports.get(i);
				existImport = importName.equals(importDeclaration.getNameAsString());
			}
		}
		return existImport;
	}

}
