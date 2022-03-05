package io.github.marcperez06.java_parser.core;

import java.io.File;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

class ClassParser {
	
	private String packageScope;
	private String className;
	private String packageName;
	private CompilationUnit compilationUnit;
	private ClassOrInterfaceDeclaration classToParse;
	
	public ClassParser(String className, String packageName) {
		this.packageScope = "main";
		this.className = className;
		this.packageName = packageName;
		this.compilationUnit = null;
		this.classToParse = null;
	}
	
	public CompilationUnit getCompilationUnit() {
		return this.compilationUnit;
	}

	public void setCompilationUnit(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPackageName() {
		return this.packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public ClassOrInterfaceDeclaration getNewClass() {
		return this.classToParse;
	}

	public void setNewClass(ClassOrInterfaceDeclaration classToParse) {
		this.classToParse = classToParse;
	}

	public String getPackageScope() {
		return this.packageScope;
	}

	public void setPackageScope(String packageScope) {
		if (packageScope != null) {
			this.packageScope = packageScope;	
		}
	}
	
	public CompilationUnit parseOrCreateClass() {
		try {
			this.parseClass(Class.forName(this.packageName + "." + this.className));
			
			if (this.compilationUnit == null) {
				this.generateClass();
			}
			
		} catch (ClassNotFoundException e) {
			this.generateClass();
		}
		return this.compilationUnit;
	}
	
	public CompilationUnit parseClass(Class<?> clazz) {
		if (clazz != null) {
			try {
				String classPath = this.getClassPath(clazz);
				File classFile = new File(classPath);
				this.compilationUnit = JavaParser.parse(classFile);

				Optional<ClassOrInterfaceDeclaration> optClass = this.compilationUnit.getClassByName(clazz.getSimpleName());
				if (optClass.isPresent() == true) {
					this.classToParse = optClass.get();
				}
				
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return this.compilationUnit;
	}
	
	private <T> String getClassPath(Class<T> clazz) {
		String fileSeparator = System.getProperty("file.separator");
		String baseDir = System.getProperty("user.dir") + fileSeparator;
		baseDir += "src" + fileSeparator + this.packageScope + fileSeparator + "java" + fileSeparator;
		String packageName = clazz.getPackage().getName();
		String packagePath = packageName.replaceAll("\\.", "\\" + fileSeparator);
		String classPath = baseDir + packagePath + fileSeparator + clazz.getSimpleName() + ".java";
		return classPath;
	}
	
	public void extendClass(Class<?> parent) {
		if (this.classToParse != null) {
			this.classToParse.addExtendedType(parent);
		}
	}
	
	public void extendClass(String parent) {
		if (this.classToParse != null && parent.isEmpty() == false) {
			this.classToParse.addExtendedType(parent);
		}
	}

	public CompilationUnit generateClass() {
		this.createCompilationUnit();
		this.createClass();
		return this.compilationUnit;
	}
	
	private void createCompilationUnit() {
		if (compilationUnit == null) {
			this.compilationUnit = new CompilationUnit();
			this.compilationUnit.setPackageDeclaration(this.packageName);	
		}
	}

	private void createClass() {
		if (this.compilationUnit != null && this.classToParse == null) {
			this.classToParse = this.compilationUnit.addClass(this.className);
		}
	}
	
	public void clearClass() {
		this.compilationUnit = null;
		this.classToParse = null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void createAnnotationClass(Class classOfAnnotation) {
		if (this.classToParse != null) {
			this.classToParse.addAnnotation(classOfAnnotation);
		}
	}
	
	public void createAnnotationClass(String classOfAnnotation) {
		if (this.classToParse != null) {
			this.classToParse.addAnnotation(classOfAnnotation);
			
		}
	}

}