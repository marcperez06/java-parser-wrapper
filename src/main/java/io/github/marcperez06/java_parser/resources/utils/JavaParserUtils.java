package io.github.marcperez06.java_parser.resources.utils;

public class JavaParserUtils {
	
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	public static String getSavePath(String className, String packageName, String packageScope) {
		
		String baseDir = System.getProperty("user.dir") + FILE_SEPARATOR;
		baseDir += "src" + FILE_SEPARATOR + packageScope + FILE_SEPARATOR + "java" + FILE_SEPARATOR;
		String packagePath = packageName.replaceAll("\\.", "\\" + FILE_SEPARATOR);
		String savePath = baseDir + packagePath + FILE_SEPARATOR + className + ".java";
		return savePath;
	}
	
	protected String getSavePath(Class<?> destinationClass, String packageScope) {
		String savePath = "";
		if (destinationClass != null) {
			String baseDir = System.getProperty("user.dir") + FILE_SEPARATOR;
			baseDir += "src" + FILE_SEPARATOR + packageScope + FILE_SEPARATOR + "java" + FILE_SEPARATOR;
			String packageName = destinationClass.getPackage().getName();
			String packagePath = packageName.replaceAll("\\.", "\\" + FILE_SEPARATOR);
			String className = destinationClass.getSimpleName();
			savePath = baseDir + packagePath + FILE_SEPARATOR + className + ".java";
		}
		return savePath;
	}

}