package io.github.marcperez06.java_parser.scripts.examples.mobile;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.Parameter;

import io.github.marcperez06.java_parser.core.JavaParserWrapper;
import io.github.marcperez06.java_parser.core.factory.ParametersFactory;
import io.github.marcperez06.java_utilities.strings.StringUtils;

public abstract class AbstractMobileActionsGenerator {
	
	protected final String importBasePackage = "example.package.";

	protected Class<?> baseClass;
	protected Class<?> destinationClass;
	protected JavaParserWrapper parser;
	protected String currentActionClass;
	protected String packageScope;
	
	protected static final String FOUND_ELEMENTS = "foundElements";
	protected static final String WAIT_ELEMENT = "waitElement";
	protected static final String WAIT_ELEMENTS = "waitElements";
	public static final String REPORTED_ACTIONS = "ReportedMobileActions";
	public static final String ACTIONS = "MobileActions";
	
	public AbstractMobileActionsGenerator(Class<?> baseClass) {
		this.baseClass = baseClass;
		this.destinationClass = baseClass;
		this.currentActionClass = REPORTED_ACTIONS;
		this.packageScope = "main";
	}
	
	public AbstractMobileActionsGenerator(Class<?> baseClass, Class<?> destinationClass) {
		this.baseClass = baseClass;
		this.destinationClass = destinationClass;
		this.currentActionClass = REPORTED_ACTIONS;
		this.packageScope = "main";
	}
	
	public void setActionClass(String actionClass) {
		this.currentActionClass = actionClass;
	}
	
	public void setPackageScope(String packageScope) {
		this.packageScope = packageScope;
		this.parser.setPackageScope(this.packageScope);
	}
	
	// ---------------------------- Abstract Methods ------------------------------
	
	protected abstract String getPropertyPrefix();
	protected abstract String getCodeForCreateReturnVariable(Class<?> returnType);
	public abstract void execute();
	
	// ----------------------- Wrapp of methods ---------------------------------------
	
	private String getReturnCode(Class<?> returnType) {
		return (!returnType.equals(Void.TYPE)) ? "return doAction;" : "";
	}
	
	private String getCodeForTryList() {
		String tryCode = "try {";
		tryCode += this.currentActionClass + ".scrollToElement(" + FOUND_ELEMENTS + ".get(0));";
		return tryCode;
	}
	
	private String getCodeForCatchList(String action, String element, Class<?> returnType) {
		String actionText = action + " element " + element;
		String catchCode = "} catch (Throwable e) {";
		
		/*
		if (!returnType.equals(Void.TYPE)) {
			if (returnType.equals(Boolean.TYPE)) {
				catchCode += "doAction = false;";
			} else if (returnType.equals(String.class)) {
				catchCode += "doAction = \"\";";
			} else if (returnType.equals(List.class)) {
				catchCode += "doAction = new ArrayList();";
			}
		}
		*/
		
		catchCode += "CanNotPerformActionException exception = ";
		catchCode += "new CanNotPerformActionException(\"" + actionText + "\", foundElements, e);";
		catchCode += "Logger.debug(exception.getMessage());";
		catchCode += "throw exception;";
		
		catchCode += "}";
		return catchCode;
	}
	
	private String getCodeForTry(String element) {
		String tryCode = "try {";
		if (element != null && !element.isEmpty()) {
			tryCode += this.currentActionClass + ".scrollToElement(" + element + ");";
		}
		return tryCode;
	}
	
	private String getCodeForCatch(String action, String element) {
		String fieldName = "null";
		
		if (element != null && !element.isEmpty()) {
			fieldName = element.replace(this.getPropertyPrefix(), "").trim();
		}
		
		String actionText = action + " element " + fieldName;
		String catchCode = "} catch (Throwable e) {";

		catchCode += "CanNotPerformActionException exception = ";
		catchCode += "new CanNotPerformActionException(\"" + actionText + "\", " + element + ", e);";
		catchCode += "Logger.debug(exception.getMessage());";
		catchCode += "throw exception;";
		
		catchCode += "}";
		return catchCode;
	}
	
	
	// ----------------------- Autogeneration of methods ---------------------------
	
	protected void parseDestinationClass() {
		if (this.destinationClass != null) {
			this.parser.parseClass(this.destinationClass);
		}
	}
	
	protected void extendActionsClassIfNeeded() {
		Class<?> parentClass = this.baseClass.getSuperclass();
		String parentClassName = parentClass.getSimpleName();
		
		if (!parentClassName.equals("Object")) {
			String baseName = StringUtils.cutStringWithOtherString(parentClassName, "Elements", 0);
			
			if (!baseName.equals("Screen")) {
				String parentActionsName = baseName + "Actions";
				String importName = parentClass.getPackage().getName() + "." + parentActionsName;
				this.parser.extendClass(parentActionsName);
				this.parser.addImport(importName);
			}
		}
		
	}
	
	protected void addImports() {
		this.parser.addImport(this.importBasePackage + "core.shared.execptions.application.CanNotPerformActionException");
		this.parser.addImport(this.importBasePackage + "core.application.actions.mobile." + this.currentActionClass);
		this.parser.addImport(this.importBasePackage + "utils.Logger");
		
		this.parser.addImport("io.appium.java_client.MobileElement");
		this.parser.addImport("org.openqa.selenium.support.ui.ExpectedConditions");
	}
	
	protected void generateMethodsBasedOnAnnotations(Field field) {
		
		if (field != null) {
		
			String fieldName = field.getName();
			
			Annotation[] annotations = field.getDeclaredAnnotations();
			boolean isMethodList = false;
			
			for (int i = 0; i < annotations.length && isMethodList == false; i++) {
				
				Annotation annotation = annotations[i];
				
				String annotationName = annotation.annotationType().getSimpleName();
				
				if (annotationName.equals("WebElementList") == true) {
					if (annotations.length >= 1) {
						annotationName = annotations[1].annotationType().getSimpleName();
						this.generateListMethods(fieldName, annotationName);
						isMethodList = true;
					}
				} else {
					this.generateSimpleMethods(fieldName, annotationName);
				}
			
			}

		}
	}
	
	// ----------------------- Generation of List Methods -------------------------
	
	private void generateListMethods(String fieldName, String annotationName) {
		this.parser.addImport("java.util.List");

		if (annotationName.equals("Input") == true) {
			
			this.generateListOfInputMethods(fieldName);
		
		} else if (annotationName.equals("Button") == true
					|| annotationName.equals("Link") == true
					|| annotationName.equals("A") == true
					|| annotationName.equals("Label") == true
					|| annotationName.equals("Span") == true
					|| annotationName.equals("Tab") == true) {
			
			this.generateListOfClickableElementsMethods(fieldName);
		}

	}
	
	private void generateListOfInputMethods(String fieldName) {
		this.generateFillListMethod(fieldName);
		this.generateClearListMethod(fieldName);
		this.generateCheckListMethod(fieldName);
		this.generateUncheckListMethod(fieldName);
		this.generateClickElementOfListMethod(fieldName);
	}
	
	private String getCodeForWaitUntilFirstElementsIsVisible(String elementsName) {
		this.parser.addImport("org.openqa.selenium.WebElement");
		this.parser.addImport(this.importBasePackage + "core.application.actions.mobile.MobileActions");
		/*
		String transformToWebElements = "List<WebElement> transformedFoundElements = ";
		transformToWebElements += "MobileActions.mobileElementListToWebElementList(" + elementsName + ");";
		transformToWebElements += "\n";
		
		String auxWaitUntil = "List<WebElement> " + WAIT_ELEMENTS + "Auxiliar = ";
		auxWaitUntil += "this.wait.untilElementsAreVisible(transformedFoundElements);";
		auxWaitUntil += "\n";
		
		String transformToMobileElements = "List<MobileElement> " + WAIT_ELEMENTS + " = ";
		transformToMobileElements += "MobileActions.webElementListToMobileElementList(" + WAIT_ELEMENTS + "Auxiliar);";
		transformToMobileElements += "\n";
		
		String waitUntil = transformToWebElements + auxWaitUntil + transformToMobileElements;
		
		return waitUntil;
		*/

		String transformToWebElements = "List<WebElement> transformedFoundElements = ";
		transformToWebElements += "MobileActions.mobileElementListToWebElementList(" + elementsName + ");";
		
		String expectedCondition = "ExpectedConditions.visibilityOf(transformedFoundElements.get(0))";
		String auxWaitUntil = "this.wait.until(" + expectedCondition + ");";
		
		String waitUntil = transformToWebElements + auxWaitUntil;
		
		return waitUntil;
	}
	
	private void generateFillListMethod(String fieldName) {
		Parameter elementText = ParametersFactory.createParameter("elementText", String.class);
		Parameter textToWrite = ParametersFactory.createParameter("textToWrite", String.class);
		List<Parameter> parameters = ParametersFactory.createListOfParameters(elementText, textToWrite);
		
		String wait = this.getCodeForWaitUntilFirstElementsIsVisible(FOUND_ELEMENTS);
		String listMethodsBody = wait + this.currentActionClass + ".writeOnList(" + FOUND_ELEMENTS + ", textToWrite);";
		String listByIndexMethodBody = wait + "doAction = " + this.currentActionClass;
		listByIndexMethodBody += ".writeOnListByIndex(" + FOUND_ELEMENTS + ", textToWrite, index);";
		
		this.generateListMethods("fill", fieldName, listMethodsBody, parameters, Void.TYPE);
		this.generateListByIndexMethod("fillOn", fieldName, listByIndexMethodBody, parameters, Boolean.TYPE);
	}
	
	private void generateClearListMethod(String fieldName) {
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("elementText", String.class);
		String wait = this.getCodeForWaitUntilFirstElementsIsVisible(FOUND_ELEMENTS);
		String listMethodsBody = wait + this.currentActionClass + ".writeOnList(" + FOUND_ELEMENTS + ", \"\");";
		String listByIndexMethodBody = wait + "doAction = " + this.currentActionClass;
		listByIndexMethodBody += ".writeOnListByIndex(" + FOUND_ELEMENTS + ", \"\", index);";
		
		this.generateListMethods("clear", fieldName, listMethodsBody, parameters, Void.TYPE);
		this.generateListByIndexMethod("clearOn", fieldName, listByIndexMethodBody, parameters, Boolean.TYPE);
	}

	private void generateCheckListMethod(String fieldName) {
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("elementText", String.class);
		String wait = this.getCodeForWaitUntilFirstElementsIsVisible(FOUND_ELEMENTS);
		String listMethodsBody = wait + this.currentActionClass + ".checkList(" + FOUND_ELEMENTS + ");";
		String listByIndexMethodBody = wait + "doAction = " + this.currentActionClass;
		listByIndexMethodBody += ".checkOnListByIndex(" + FOUND_ELEMENTS + ", index);";
		
		this.generateListMethods("check", fieldName, listMethodsBody, parameters, Void.TYPE);
		this.generateListByIndexMethod("checkOn", fieldName, listByIndexMethodBody, parameters, Boolean.TYPE);
	}
	
	private void generateUncheckListMethod(String fieldName) {
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("elementText", String.class);
		String wait = this.getCodeForWaitUntilFirstElementsIsVisible(FOUND_ELEMENTS);
		String listMethodsBody = wait + this.currentActionClass + ".uncheckList(" + FOUND_ELEMENTS + ");";
		String listByIndexMethodBody = wait + "doAction = " + this.currentActionClass;
		listByIndexMethodBody += ".uncheckOnListByIndex(" + FOUND_ELEMENTS + ", index);";
		
		this.generateListMethods("uncheck", fieldName, listMethodsBody, parameters, Void.TYPE);
		this.generateListByIndexMethod("uncheckOn", fieldName, listByIndexMethodBody, parameters, Boolean.TYPE);
	}
	
	private void generateClickElementOfListMethod(String fieldName) {
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("elementText", String.class);
		String wait = this.getCodeForWaitUntilFirstElementsIsVisible(FOUND_ELEMENTS);
		String baseMethodBody = wait + "doAction = " + this.currentActionClass;
		String listMethodsBody = baseMethodBody + ".clickOnFirstElementOfList(" + FOUND_ELEMENTS + ");";
		String listByIndexMethodBody = baseMethodBody + ".clickOnListByIndex(" + FOUND_ELEMENTS + ", index);";
		
		this.generateListMethods("clickFirstOf", fieldName, listMethodsBody, parameters, Boolean.TYPE);
		this.generateListByIndexMethod("clickOn", fieldName, listByIndexMethodBody, parameters, Boolean.TYPE);
	}
	
	private void generateListOfClickableElementsMethods(String fieldName) {
		this.generateClickElementOfListMethod(fieldName);
		this.generateGetTextListMethod(fieldName);
	}
	
	private void generateGetTextListMethod(String fieldName) {
		String startMethodName = "getTextOf";
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("elementText", String.class);
		String wait = this.getCodeForWaitUntilFirstElementsIsVisible(FOUND_ELEMENTS);
		String baseMethodBody = wait + "doAction = " + this.currentActionClass;
		String listMethodsBody = baseMethodBody + ".getTextOfList(" + FOUND_ELEMENTS + ");";
		String listByIndexMethodBody = baseMethodBody + ".getTextOfListByIndex(" + FOUND_ELEMENTS + ", index);";

		this.generateListMethods(startMethodName, fieldName, listMethodsBody, parameters, List.class);
		this.generateListByIndexMethod(startMethodName, fieldName, listByIndexMethodBody, parameters, String.class);
	}

	private void generateListMethods(String startName, String fieldName, 
										String methodBody, List<Parameter> parameters, Class<?> returnType) {

		String firstLineMethod = "";
		String bodyMethod = "";
		String sufixMethodName = StringUtils.capitalizeWord(fieldName);
		String elementName = this.getPropertyPrefix() + fieldName;
		String methodListName = startName + "List" + sufixMethodName;
		String methodEqualsName = startName + "EqualsList" + sufixMethodName;
		String methodContainsName = startName + "ContainsList" + sufixMethodName;
		String createVariable = this.getCodeForCreateReturnVariable(returnType);
		String returnCode = this.getReturnCode(returnType);
		String tryCode = this.getCodeForTryList();
		String catchCode = this.getCodeForCatchList(startName, fieldName, returnType);
		String foundElements = "List<MobileElement> " + FOUND_ELEMENTS + " = ";
		
		/*
		 * Creates an auxiliar list of parameters, starting from parameter 1 to avoid
		 * the parameter used for serach elements
		 */
		List<Parameter> auxParameters = new ArrayList<Parameter>();
		for (int i = 1; i < parameters.size(); i++) {
			Parameter param = parameters.get(i);
			auxParameters.add(param);
		}

		firstLineMethod = foundElements + elementName + ";";
		bodyMethod = createVariable + firstLineMethod + tryCode + methodBody + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodListName, bodyMethod, auxParameters, returnType, Keyword.PUBLIC);
		
		firstLineMethod = foundElements + this.currentActionClass + ".findElementsInListByText(" + elementName + ", elementText);";
		catchCode = this.getCodeForCatchList(startName, "that try to found by text \" + elementText + \" from " + fieldName, returnType);
		bodyMethod = createVariable + firstLineMethod + tryCode + methodBody + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodEqualsName, bodyMethod, parameters, returnType, Keyword.PUBLIC);
		
		firstLineMethod = foundElements + this.currentActionClass + ".findElementsInListIfContainsText(" + elementName + ", elementText);";
		catchCode = this.getCodeForCatchList(startName, "that contains text \" + elementText + \" from " + fieldName, returnType);
		bodyMethod = createVariable + firstLineMethod + tryCode + methodBody + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodContainsName, bodyMethod, parameters, returnType, Keyword.PUBLIC);
	}
	
	private void generateListByIndexMethod(String startName, String fieldName, String methodBody, 
			List<Parameter> parameters, Class<?> returnType) {

		String firstLineMethod = "";
		String bodyMethod = "";
		String sufixMethodName = StringUtils.capitalizeWord(fieldName);
		String element = this.getPropertyPrefix() + fieldName;
		String methodListByIndexName = startName + "List" + sufixMethodName + "ByIndex";
		String createVariable = this.getCodeForCreateReturnVariable(returnType);
		String returnCode = this.getReturnCode(returnType);
		String tryCode = this.getCodeForTryList();
		String catchCode = this.getCodeForCatchList(startName, fieldName, returnType);
		String foundElements = "List<MobileElement> " + FOUND_ELEMENTS + " = ";
		
		/*
		 * Creates an auxiliar list of parameters, starting from parameter 1 to avoid
		 * the parameter used for serach elements
		 */
		List<Parameter> auxParameters = new ArrayList<Parameter>();
		for (int i = 1; i < parameters.size(); i++) {
			Parameter param = parameters.get(i);
			auxParameters.add(param);
		}

		// Add index param
		auxParameters.add(ParametersFactory.createParameter("index", Integer.TYPE));

		firstLineMethod = foundElements + element + ";";
		bodyMethod = createVariable + firstLineMethod + tryCode + methodBody + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodListByIndexName, bodyMethod, auxParameters, returnType, Keyword.PUBLIC);
	}
	
	// ------------------------ Generation of Simple Methods ------------------------------

	private void generateSimpleMethods(String fieldName, String annotationName) {
		if (annotationName.equals("Input") == true) {
			
			this.generateInputMethods(fieldName);
		
		} else if (annotationName.equals("Button") == true
					|| annotationName.equals("Link") == true
					|| annotationName.equals("A") == true
					|| annotationName.equals("Label") == true
					|| annotationName.equals("Span") == true
					|| annotationName.equals("Tab") == true) {
			
			this.generateClickableElementsMethods(fieldName);
			
		}
	}
	
	private void generateInputMethods(String fieldName) {
		String sufixMethodName = StringUtils.capitalizeWord(fieldName);
		String elementName = this.getPropertyPrefix() + fieldName;
		this.generateFillMethod(sufixMethodName, elementName);
		this.generateClearMethod(sufixMethodName, elementName);
		this.generateCheckMethod(sufixMethodName, elementName);
		this.generateUncheckMethod(sufixMethodName, elementName);
		this.generateClickMethod(sufixMethodName, elementName);
	}
	
	private String getCodeForWaitUntilElementIsVisible(String elementName) {
		/*
		String waitUntil = "MobileElement " + WAIT_ELEMENT + " = (MobileElement) ";
		waitUntil += "this.wait.untilElementIsVisible(" + elementName + ");";
		return waitUntil;
		*/
		String expectedCondition = "ExpectedConditions.visibilityOf(" + elementName + ")";
		String waitUntil = "MobileElement " + WAIT_ELEMENT + " = (MobileElement) ";
		waitUntil += "this.wait.until(" + expectedCondition + ");";
		return waitUntil;
	}
	
	private void generateFillMethod(String sufixMethodName, String elementName) {
		String methodName = "fill" + sufixMethodName;
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("text", String.class);
		String createVariable = this.getCodeForCreateReturnVariable(Boolean.TYPE);
		String returnCode = this.getReturnCode(Boolean.TYPE);
		String tryCode = this.getCodeForTry(elementName);
		String catchCode = this.getCodeForCatch("Write \" + text + \" on", elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "doAction = " + this.currentActionClass + ".writeOn(" + WAIT_ELEMENT + ", text);";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, parameters, Boolean.TYPE, Keyword.PUBLIC);
	}
	
	private void generateClearMethod(String sufixMethodName, String elementName) {
		String methodName = "clear" + sufixMethodName;
		String createVariable = this.getCodeForCreateReturnVariable(Boolean.TYPE);
		String returnCode = this.getReturnCode(Boolean.TYPE);
		String tryCode = this.getCodeForTry(elementName);
		String catchCode = this.getCodeForCatch("Clear value of", elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "doAction = " + this.currentActionClass + ".writeOn(" + WAIT_ELEMENT + ", \"\");";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, null, Boolean.TYPE, Keyword.PUBLIC);
	}
	
	private void generateCheckMethod(String sufixMethodName, String elementName) {
		String methodName = "check" + sufixMethodName;
		String createVariable = this.getCodeForCreateReturnVariable(Boolean.TYPE);
		String returnCode = this.getReturnCode(Boolean.TYPE);
		String tryCode = this.getCodeForTry(elementName);
		String catchCode = this.getCodeForCatch("Check", elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "doAction = " + this.currentActionClass + ".check(" + WAIT_ELEMENT + ");";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, null, Boolean.TYPE, Keyword.PUBLIC);
	}
	
	private void generateUncheckMethod(String sufixMethodName, String elementName) {
		String methodName = "uncheck" + sufixMethodName;
		String createVariable = this.getCodeForCreateReturnVariable(Boolean.TYPE);
		String returnCode = this.getReturnCode(Boolean.TYPE);
		String tryCode = this.getCodeForTry(elementName);
		String catchCode = this.getCodeForCatch("Uncheck", elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "doAction = " + this.currentActionClass + ".uncheck(" + WAIT_ELEMENT + ");";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, null, Boolean.TYPE, Keyword.PUBLIC);
	}
	
	private void generateClickableElementsMethods(String fieldName) {
		String sufixMethodName = StringUtils.capitalizeWord(fieldName);
		String elementName = this.getPropertyPrefix() + fieldName;
		this.generateClickMethod(sufixMethodName, elementName);
		this.generateGetTextMethod(sufixMethodName, elementName);
		this.generateGetContentDescriptionMethod(sufixMethodName, elementName);
	}
	
	private void generateClickMethod(String sufixMethodName, String elementName) {
		String methodName = "click" + sufixMethodName;
		String createVariable = this.getCodeForCreateReturnVariable(Boolean.TYPE);
		String returnCode = this.getReturnCode(Boolean.TYPE);
		String tryCode = this.getCodeForTry(elementName);
		String catchCode = this.getCodeForCatch("Click on", elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "doAction = " + this.currentActionClass + ".clickOn(" + WAIT_ELEMENT + ");";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, null, Boolean.TYPE, Keyword.PUBLIC);
	}
	
	private void generateGetTextMethod(String sufixMethodName, String elementName) {
		String methodName = "getText" + sufixMethodName;
		String createVariable = this.getCodeForCreateReturnVariable(String.class);
		String returnCode = this.getReturnCode(String.class);
		String tryCode = this.getCodeForTry(elementName);
		String catchCode = this.getCodeForCatch("Get text of", elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "doAction = " + this.currentActionClass + ".getText(" + WAIT_ELEMENT + ");";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, null, String.class, Keyword.PUBLIC);
	}
	
	private void generateGetContentDescriptionMethod(String sufixMethodName, String elementName) {
		String methodName = "getContentDescription" + sufixMethodName;
		String createVariable = this.getCodeForCreateReturnVariable(String.class);
		String returnCode = this.getReturnCode(String.class);
		String tryCode = this.getCodeForTry(elementName);
		String catchCode = this.getCodeForCatch("Get content description of", elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "doAction = " + this.currentActionClass + ".getContentDescription(" + WAIT_ELEMENT + ");";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, null, String.class, Keyword.PUBLIC);
	}
	
	protected String getSavePath() {
		String savePath = "";
		if (this.destinationClass != null) {
			String fileSeparator = System.getProperty("file.separator");
			String baseDir = System.getProperty("user.dir") + fileSeparator;
			baseDir += "src" + fileSeparator + this.packageScope + fileSeparator + "java" + fileSeparator;
			String packageName = this.destinationClass.getPackage().getName();
			String packagePath = packageName.replaceAll("\\.", "\\" + fileSeparator);
			String className = this.destinationClass.getSimpleName();
			savePath = baseDir + packagePath + fileSeparator + className + ".java";
		}
		return savePath;
	}
	
}
