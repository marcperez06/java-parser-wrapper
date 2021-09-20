package io.github.marcperez06.java_parser.scripts.examples.web;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.Parameter;

import io.github.marcperez06.java_parser.core.JavaParserWrapper;
import io.github.marcperez06.java_parser.core.factory.ParametersFactory;
import io.github.marcperez06.java_utilities.strings.StringUtils;

public abstract class AbstractWebActionsGenerator {
	
	protected final String importBasePackage = "example.package.";
	
	protected Class<?> baseClass;
	protected Class<?> destinationClass;
	protected JavaParserWrapper parser;
	protected static final String PREFIX = "this.elements.";
	protected static final String FOUND_ELEMENTS = "foundElements";
	protected static final String WAIT_ELEMENT = "waitElement";
	protected static final String WAIT_ELEMENTS = "waitElements";
	public static final String REPORTED_ACTIONS = "ReportedWebActions";
	public static final String ACTIONS = "WebActions";
	protected String currentActionClass;
	protected String packageScope;
	
	public AbstractWebActionsGenerator(Class<?> baseClass) {
		this.baseClass = baseClass;
		this.destinationClass = baseClass;
		this.currentActionClass = REPORTED_ACTIONS;
		this.packageScope = "main";
	}
	
	public AbstractWebActionsGenerator(Class<?> baseClass, Class<?> destinationClass) {
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
		tryCode += this.currentActionClass + ".scrollIntoView(" + FOUND_ELEMENTS + ".get(0));";
		return tryCode;
	}
	
	private String getCodeForCatchList(String action, String element, Class<?> returnType) {
		String actionText = action + " element " + element;
		String catchCode = "} catch (Throwable e) {";
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
			tryCode += this.currentActionClass + ".scrollIntoView(" + element + ");";
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
			
			if (!baseName.equals("Page")) {
				String parentActionsName = baseName + "Actions";
				String importName = parentClass.getPackage().getName() + "." + parentActionsName;
				this.parser.extendClass(parentActionsName);
				this.parser.addImport(importName);
			}
		}
		
	}
	
	protected void addImports() {
		this.parser.addImport(this.importBasePackage + "core.shared.execptions.application.CanNotPerformActionException");
		this.parser.addImport(this.importBasePackage + "core.application.actions.web." + this.currentActionClass);
		this.parser.addImport(this.importBasePackage + "utils.Logger");
		
		this.parser.addImport("org.openqa.selenium.WebElement");
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
		this.generateGetValueListMethod(fieldName);
		this.generateCheckListMethod(fieldName);
		this.generateUncheckListMethod(fieldName);
		this.generateClickElementOfListMethod(fieldName);
	}
	
	private String getCodeForWaitUntilFirstElementsIsVisible(String elementsName) {
		//String waitUntil = "List<WebElement> " + WAIT_ELEMENTS + " = ";
		/*
		waitUntil += "this.wait.untilElementsAreVisible(" + elementsName + ");";
		return waitUntil;
		*/
		String expectedCondition = "ExpectedConditions.visibilityOf(" + elementsName + ".get(0))";
		String waitUntil = "this.wait.until(" + expectedCondition + ");";
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
	
	private void generateGetValueListMethod(String fieldName) {
		String startMethodName = "getValueOf";
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("elementText", String.class);
		String wait = this.getCodeForWaitUntilFirstElementsIsVisible(FOUND_ELEMENTS);
		String baseMethodBody = wait + "doAction = " + this.currentActionClass;
		String listMethodsBody = baseMethodBody + ".getInputValueOfList(" + FOUND_ELEMENTS + ");";
		String listByIndexMethodBody = baseMethodBody + ".getInputValueOfListByIndex(" + FOUND_ELEMENTS + ", index);";
		
		this.generateListMethods(startMethodName, fieldName, listMethodsBody, parameters, List.class);
		this.generateListByIndexMethod(startMethodName, fieldName, listByIndexMethodBody, parameters, String.class);
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
		String baseMethodBody = wait + "doAction =  " + this.currentActionClass;
		String listMethodsBody = baseMethodBody + ".getTextOfList(" + FOUND_ELEMENTS + ");";
		String listByIndexMethodBody = baseMethodBody + ".getTextOfListByIndex(" + FOUND_ELEMENTS + ", index);";

		this.generateListMethods(startMethodName, fieldName, listMethodsBody, parameters, List.class);
		this.generateListByIndexMethod(startMethodName, fieldName, listByIndexMethodBody, parameters, String.class);
	}

	private void generateListMethods(String startName, String fieldName, String methodBody, 
											List<Parameter> parameters, Class<?> returnType) {
		
		String firstLineMethod = "";
		String bodyMethod = "";
		String sufixMethodName = StringUtils.capitalizeWord(fieldName);
		String element = this.getPropertyPrefix() + fieldName;
		String methodListName = startName + "List" + sufixMethodName;
		String methodEqualsName = startName + "EqualsList" + sufixMethodName;
		String methodContainsName = startName + "ContainsList" + sufixMethodName;
		String createVariable = this.getCodeForCreateReturnVariable(returnType);
		String returnCode = this.getReturnCode(returnType);
		String tryCode = this.getCodeForTryList();
		String catchCode = this.getCodeForCatchList(startName, fieldName, returnType);
		String foundElements = "List<WebElement> " + FOUND_ELEMENTS + " = ";
		
		/*
		 * Creates an auxiliar list of parameters, starting from parameter 1 to avoid 
		 * the parameter used for serach elements
		 */
		List<Parameter> auxParameters = new ArrayList<Parameter>();
		for (int i = 1; i < parameters.size(); i++) {
			Parameter param = parameters.get(i);
			auxParameters.add(param);
		}

		firstLineMethod = foundElements + element + ";";
		bodyMethod = createVariable + firstLineMethod + tryCode + methodBody + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodListName, bodyMethod, auxParameters, returnType, Keyword.PUBLIC);
		
		firstLineMethod = foundElements + this.currentActionClass + ".findElementsInListByText(" + element + ", elementText);";
		catchCode = this.getCodeForCatchList(startName, "that try to found by text \" + elementText + \" from " + fieldName, returnType);
		bodyMethod = createVariable + firstLineMethod + tryCode + methodBody + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodEqualsName, bodyMethod, parameters, returnType, Keyword.PUBLIC);
		
		firstLineMethod = foundElements + this.currentActionClass + ".findElementsInListIfContainsText(" + element + ", elementText);";
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
		String foundElements = "List<WebElement> " + FOUND_ELEMENTS + " = ";
		
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
			
		} else if (annotationName.equals("Prompt") == true) {
			this.generatePromptMethods(fieldName);
		} else if (annotationName.equals("Select") == true) {
			this.generateSelectMethods(fieldName);
		} else if (annotationName.equals("Iframe") == true) {
			this.generateFrameMethods(fieldName);
		} else if (annotationName.equals("Table")) {
			this.generateTableMethods(fieldName);
		}
	}
	
	private void generateInputMethods(String fieldName) {
		String sufixMethodName = StringUtils.capitalizeWord(fieldName);
		String elementName = this.getPropertyPrefix() + fieldName;
		this.generateFillMethod(sufixMethodName, elementName);
		this.generateClearMethod(sufixMethodName, elementName);
		this.generateGetValueMethod(sufixMethodName, elementName);
		this.generateCheckMethod(sufixMethodName, elementName);
		this.generateUncheckMethod(sufixMethodName, elementName);
		this.generateClickMethod(sufixMethodName, elementName);
	}
	
	private String getCodeForWaitUntilElementIsVisible(String elementName) {
		String waitUntil = "WebElement " + WAIT_ELEMENT + " = ";
		/*
		waitUntil += "this.wait.untilElementIsVisible(" + elementName + ");";
		return waitUntil;
		*/
		String expectedCondition = "ExpectedConditions.visibilityOf(" + elementName + ")";
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
		String catchCode = this.getCodeForCatch("Clear", elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "doAction = " + this.currentActionClass + ".writeOn(" + WAIT_ELEMENT + ", \"\");";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, null, Boolean.TYPE, Keyword.PUBLIC);
	}
	
	private void generateGetValueMethod(String sufixMethodName, String elementName) {
		String methodName = "getValue" + sufixMethodName;
		String createVariable = this.getCodeForCreateReturnVariable(String.class);
		String returnCode = this.getReturnCode(String.class);
		String tryCode = this.getCodeForTry(elementName);
		String catchCode = this.getCodeForCatch("Get input value of", elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "doAction = " + this.currentActionClass + ".getInputValue(" + WAIT_ELEMENT + ");";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, null, String.class, Keyword.PUBLIC);
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
		String elmentName = this.getPropertyPrefix() + fieldName;
		this.generateClickMethod(sufixMethodName, elmentName);
		this.generateGetTextMethod(sufixMethodName, elmentName);
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
	
	private void generatePromptMethods(String fieldName) {
		String sufixMethodName = StringUtils.capitalizeWord(fieldName);
		String elmentName = this.getPropertyPrefix() + fieldName;
		this.generateAcceptPromptMethod(sufixMethodName, elmentName);
		this.generateDismissPromptMethod(sufixMethodName, elmentName);
		this.generateWriteOnPromptMethod(sufixMethodName, elmentName);
		this.generateWriteAndAcceptPromptMethod(sufixMethodName, elmentName);
		this.generateGetTextOfPromptMethod(sufixMethodName, elmentName);
	}
	
	private void generateAcceptPromptMethod(String sufixMethodName, String elementName) {
		String methodName = "accept" + sufixMethodName;
		String createVariable = this.getCodeForCreateReturnVariable(Boolean.TYPE);
		String returnCode = this.getReturnCode(Boolean.TYPE);
		String tryCode = this.getCodeForTry(null);
		String catchCode = this.getCodeForCatch("Accept", elementName);
		String action = elementName + " = " + this.currentActionClass + ".switchToActivePrompt();";
		action += "doAction = " + this.currentActionClass + ".acceptPrompt(" + elementName + ");";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, null, Boolean.TYPE, Keyword.PUBLIC);
	}

	private void generateDismissPromptMethod(String sufixMethodName, String elementName) {
		String methodName = "dismiss" + sufixMethodName;
		String createVariable = this.getCodeForCreateReturnVariable(Boolean.TYPE);
		String returnCode = this.getReturnCode(Boolean.TYPE);
		String tryCode = this.getCodeForTry(null);
		String catchCode = this.getCodeForCatch("Click on", elementName);
		String action = elementName + " = " + this.currentActionClass + ".switchToActivePrompt();";
		action += "doAction = " + this.currentActionClass + ".dismissPrompt(" + elementName + ");";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, null, Boolean.TYPE, Keyword.PUBLIC);
	}
	
	private void generateWriteOnPromptMethod(String sufixMethodName, String elementName) {
		String methodName = "writeOn" + sufixMethodName;
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("text", String.class);
		String createVariable = this.getCodeForCreateReturnVariable(Boolean.TYPE);
		String returnCode = this.getReturnCode(Boolean.TYPE);
		String tryCode = this.getCodeForTry(null);
		String catchCode = this.getCodeForCatch("Click on", elementName);
		String action = elementName + " = " + this.currentActionClass + ".switchToActivePrompt();";
		action += "doAction = " + this.currentActionClass + ".writeOnPrompt(" + elementName + ", text);";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, parameters, Boolean.TYPE, Keyword.PUBLIC);
	}
	
	private void generateWriteAndAcceptPromptMethod(String sufixMethodName, String elementName) {
		String methodName = "writeAndAccept" + sufixMethodName;
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("text", String.class);
		String createVariable = this.getCodeForCreateReturnVariable(Boolean.TYPE);
		String returnCode = this.getReturnCode(Boolean.TYPE);
		String tryCode = this.getCodeForTry(null);
		String catchCode = this.getCodeForCatch("Click on", elementName);
		String action = elementName + " = " + this.currentActionClass + ".switchToActivePrompt();";
		action += "doAction = " + this.currentActionClass + ".writeAndAcceptPrompt(" + elementName + ", text);";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, parameters, Boolean.TYPE, Keyword.PUBLIC);
	}
	
	private void generateGetTextOfPromptMethod(String sufixMethodName, String elementName) {
		String methodName = "getTextOf" + sufixMethodName;
		String createVariable = this.getCodeForCreateReturnVariable(String.class);
		String returnCode = this.getReturnCode(String.class);
		String tryCode = this.getCodeForTry(null);
		String catchCode = this.getCodeForCatch("Get text value of prompt element", elementName);
		String action = elementName + " = " + this.currentActionClass + ".switchToActivePrompt();";
		action += "doAction = " + this.currentActionClass + ".getTextOfPrompt(" + elementName + ");";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, null, String.class, Keyword.PUBLIC);
	}
	
	private void generateSelectMethods(String fieldName) {
		String sufixMethodName = StringUtils.capitalizeWord(fieldName);
		String elmentName = this.getPropertyPrefix() + fieldName;
		this.generateWriteInSelectMethod(sufixMethodName, elmentName);
		this.generateGetSelectedValueMethod(sufixMethodName, elmentName);
		this.generateSelectOptionByOptionValueMethod(sufixMethodName, elmentName);
	}
	
	private void generateWriteInSelectMethod(String sufixMethodName, String elementName) {
		String methodName = "writeIn" + sufixMethodName;
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("text", String.class);
		String tryCode = this.getCodeForTry(elementName);
		String catchCode = this.getCodeForCatch("Write value in select", elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += this.currentActionClass + ".writehInSelect(" + WAIT_ELEMENT + ", text);";
		String methodBody = tryCode + action + catchCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, parameters, Void.TYPE, Keyword.PUBLIC);
	}
	
	private void generateGetSelectedValueMethod(String sufixMethodName, String elementName) {
		String methodName = "getSelectedValueOf" + sufixMethodName;
		String createVariable = this.getCodeForCreateReturnVariable(String.class);
		String returnCode = this.getReturnCode(String.class);
		String tryCode = this.getCodeForTry(elementName);
		String catchCode = this.getCodeForCatch("Get selected value of", elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "doAction = " + this.currentActionClass + ".getSelectedValue(" + WAIT_ELEMENT + ");";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, null, String.class, Keyword.PUBLIC);
	}
	
	private void generateSelectOptionByOptionValueMethod(String sufixMethodName, String elementName) {
		String methodName = "selectByOptionValue" + sufixMethodName;
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("text", String.class);
		String createVariable = this.getCodeForCreateReturnVariable(Boolean.TYPE);
		String returnCode = this.getReturnCode(Boolean.TYPE);
		String tryCode = this.getCodeForTry(elementName);
		String catchCode = this.getCodeForCatch("Select value of", elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "doAction = " + this.currentActionClass + ".selectOptionByOptionValue(" + WAIT_ELEMENT + ", text);";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, parameters, Boolean.TYPE, Keyword.PUBLIC);
	}
	
	private void generateFrameMethods(String fieldName) {
		String sufixMethodName = StringUtils.capitalizeWord(fieldName);
		String elmentName = this.getPropertyPrefix() + fieldName;
		this.generateGoToFrameMethod(sufixMethodName, elmentName);
		this.generateExitFrameMethod();
	}

	private void generateGoToFrameMethod(String sufixMethodName, String elementName) {
		String methodName = "switchTo" + sufixMethodName;
		String tryCode = this.getCodeForTry(elementName);
		String catchCode = this.getCodeForCatch("Switch to ", elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += this.currentActionClass + ".switchToFrame(" + WAIT_ELEMENT + ");";
		String methodBody = tryCode + action + catchCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, null, Void.TYPE, Keyword.PUBLIC);
	}
	
	private void generateExitFrameMethod() {
		String methodName = "switchToDefaultContent";
		String tryCode = this.getCodeForTry(null);
		String catchCode = this.getCodeForCatch("Switch to default content", null);
		String action = this.currentActionClass + ".switchToDefaultContent();";
		String methodBody = tryCode + action + catchCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, null, Void.TYPE, Keyword.PUBLIC);
	}

	private void generateTableMethods(String fieldName) {
		String sufixMethodName = StringUtils.capitalizeWord(fieldName);
		String elementName = this.getPropertyPrefix() + fieldName;
		/*
		this.generateGetTrMethod(sufixMethodName, elementName);
		this.generateGetTdMethod(sufixMethodName, elementName);
		this.generateGetThMethod(sufixMethodName, elementName);
		this.generateGetTableCellMethod(sufixMethodName, elementName);
		*/
		this.generateGetTableCellValueMethod(sufixMethodName, elementName);
		this.generateGetTrByColumnNameWithValueMethod(sufixMethodName, elementName);
		this.generateClickOnColumnOfTableHeaderByNameMethod(sufixMethodName, elementName);
		this.generateGetThIndexByNameMethod(sufixMethodName, elementName);
		this.generateGetTdIndexByNameMethod(sufixMethodName, elementName);
	}
	
	/*
	private void generateGetTrMethod(String sufixMethodName, String elementName) {
		Class<?> returnClass = WebElement.class;
		String methodName = "getTrOf" + sufixMethodName;
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("trIndex", Integer.TYPE);
		
		String createVariable = this.getCodeForCreateReturnVariable(returnClass);
		String returnCode = this.getReturnCode(returnClass);
		String tryCode = this.getCodeForTry(elementName);
		String catchCode = this.getCodeForCatch("Get table row with index \" + trIndex + \" of table", elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "doAction = " + this.currentActionClass + ".getTr(" + WAIT_ELEMENT + ", trIndex);";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, parameters, returnClass, Keyword.PUBLIC);
	}
	
	private void generateGetTdMethod(String sufixMethodName, String elementName) {
		Class<?> returnClass = WebElement.class;
		String methodName = "getTdOf" + sufixMethodName;
		Parameter trIndexParameter = ParametersFactory.createParameter("trIndex", Integer.TYPE);
		Parameter tdIndexParameter = ParametersFactory.createParameter("tdIndex", Integer.TYPE);
		List<Parameter> parameters = ParametersFactory.createListOfParameters(trIndexParameter, tdIndexParameter);

		String createVariable = this.getCodeForCreateReturnVariable(returnClass);
		String returnCode = this.getReturnCode(returnClass);
		String tryCode = this.getCodeForTry(elementName);
		String catchActionMessage = "Get table row with index \" + trIndex + \" and column with index \" + tdIndex + \" of table";
		String catchCode = this.getCodeForCatch(catchActionMessage, elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "WebElement tr = " + this.currentActionClass + ".getTr(" + WAIT_ELEMENT + ", trIndex);";
		action += "doAction = " + this.currentActionClass + ".getTd(tr, tdIndex);";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, parameters, returnClass, Keyword.PUBLIC);
	}
	
	private void generateGetThMethod(String sufixMethodName, String elementName) {
		Class<?> returnClass = WebElement.class;
		String methodName = "getThOf" + sufixMethodName;
		Parameter trIndexParameter = ParametersFactory.createParameter("trIndex", Integer.TYPE);
		Parameter thIndexParameter = ParametersFactory.createParameter("thIndex", Integer.TYPE);
		List<Parameter> parameters = ParametersFactory.createListOfParameters(trIndexParameter, thIndexParameter);

		String createVariable = this.getCodeForCreateReturnVariable(returnClass);
		String returnCode = this.getReturnCode(returnClass);
		String tryCode = this.getCodeForTry(elementName);
		String catchActionMessage = "Get table row with index \" + trIndex + \" and column with index \" + thIndex + \" of table";
		String catchCode = this.getCodeForCatch(catchActionMessage, elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "WebElement tr = " + this.currentActionClass + ".getTr(" + WAIT_ELEMENT + ", trIndex);";
		action += "doAction = " + this.currentActionClass + ".getTh(tr, thIndex);";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, parameters, returnClass, Keyword.PUBLIC);
	}
	
	private void generateGetTableCellMethod(String sufixMethodName, String elementName) {
		Class<?> returnClass = WebElement.class;
		String methodName = "getTableCell" + sufixMethodName;
		Parameter trIndexParameter = ParametersFactory.createParameter("trIndex", Integer.TYPE);
		Parameter tdIndexParameter = ParametersFactory.createParameter("tdIndex", Integer.TYPE);
		List<Parameter> parameters = ParametersFactory.createListOfParameters(trIndexParameter, tdIndexParameter);
		
		String createVariable = this.getCodeForCreateReturnVariable(returnClass);
		String returnCode = this.getReturnCode(returnClass);
		String tryCode = this.getCodeForTry(elementName);
		String catchActionMessage = "Get table cell of row with index \" + trIndex + \" and column with index \" + tdIndex + \" of table";
		String catchCode = this.getCodeForCatch(catchActionMessage, elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "doAction = " + this.currentActionClass + ".getTableCell(" + WAIT_ELEMENT + ", trIndex, tdIndex);";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, parameters, returnClass, Keyword.PUBLIC);
	}
	*/
	
	private void generateGetTableCellValueMethod(String sufixMethodName, String elementName) {
		Class<?> returnClass = String.class;
		String methodName = "getTableCellValue" + sufixMethodName;
		Parameter trIndexParameter = ParametersFactory.createParameter("trIndex", Integer.TYPE);
		Parameter tdIndexParameter = ParametersFactory.createParameter("tdIndex", Integer.TYPE);
		List<Parameter> parameters = ParametersFactory.createListOfParameters(trIndexParameter, tdIndexParameter);
		
		String createVariable = this.getCodeForCreateReturnVariable(returnClass);
		String returnCode = this.getReturnCode(returnClass);
		String tryCode = this.getCodeForTry(elementName);
		String catchActionMessage = "Get table cell value of row with index \" + trIndex + \" and column with index \" + tdIndex + \" of table";
		String catchCode = this.getCodeForCatch(catchActionMessage, elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "doAction = " + this.currentActionClass + ".getTableCellValue(" + WAIT_ELEMENT + ", trIndex, tdIndex);";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, parameters, returnClass, Keyword.PUBLIC);
	}
	
	private void generateGetTrByColumnNameWithValueMethod(String sufixMethodName, String elementName) {
		Class<?> returnClass = List.class;
		String methodName = "getTrOf" + sufixMethodName + "ByColumnNameWithValue";
		Parameter columnNameParameter = ParametersFactory.createParameter("columnName", String.class);
		Parameter valueParameter = ParametersFactory.createParameter("value", String.class);
		List<Parameter> parameters = ParametersFactory.createListOfParameters(columnNameParameter, valueParameter);
		
		String createVariable = this.getCodeForCreateReturnVariable(returnClass);
		String returnCode = this.getReturnCode(returnClass);
		String tryCode = this.getCodeForTry(elementName);
		String catchActionMessage = "Get list of rows that in column \" + columnName + \" have the value \" + value + \"";
		String catchCode = this.getCodeForCatch(catchActionMessage, elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "doAction = " + this.currentActionClass + ".getTrByColumnNameWithValue(" + WAIT_ELEMENT + ", columnName, value);";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, parameters, returnClass, Keyword.PUBLIC);
	}
	
	private void generateClickOnColumnOfTableHeaderByNameMethod(String sufixMethodName, String elementName) {
		Class<?> returnClass = Void.TYPE;
		String methodName = "clickOnColumn" + sufixMethodName + "ByName";
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("columnName", String.class);
		
		String createVariable = this.getCodeForCreateReturnVariable(returnClass);
		String returnCode = this.getReturnCode(returnClass);
		String tryCode = this.getCodeForTry(elementName);
		String catchActionMessage = "Click on table header with name \" + columnName + \"";
		String catchCode = this.getCodeForCatch(catchActionMessage, elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += this.currentActionClass + ".clickOnColumnOfTableHeaderByName(" + WAIT_ELEMENT + ", columnName);";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, parameters, returnClass, Keyword.PUBLIC);
	}
	
	private void generateGetThIndexByNameMethod(String sufixMethodName, String elementName) {
		Class<?> returnClass = Integer.TYPE;
		String methodName = "getThIndexOf" + sufixMethodName + "ByName";
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("columnName", String.class);
		
		String createVariable = this.getCodeForCreateReturnVariable(returnClass);
		String returnCode = this.getReturnCode(returnClass);
		String tryCode = this.getCodeForTry(elementName);
		String catchActionMessage = "Get th index of column with name \" + columnName + \"";
		String catchCode = this.getCodeForCatch(catchActionMessage, elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "doAction = " + this.currentActionClass + ".getThIndexByName(" + WAIT_ELEMENT + ", columnName);";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, parameters, returnClass, Keyword.PUBLIC);
	}
	
	private void generateGetTdIndexByNameMethod(String sufixMethodName, String elementName) {
		Class<?> returnClass = Integer.TYPE;
		String methodName = "getTdIndexOf" + sufixMethodName + "ByName";
		List<Parameter> parameters = ParametersFactory.createListWithOneParameter("columnName", String.class);
		
		String createVariable = this.getCodeForCreateReturnVariable(returnClass);
		String returnCode = this.getReturnCode(returnClass);
		String tryCode = this.getCodeForTry(elementName);
		String catchActionMessage = "Get td index of column with name \" + columnName + \"";
		String catchCode = this.getCodeForCatch(catchActionMessage, elementName);
		String action = this.getCodeForWaitUntilElementIsVisible(elementName);
		action += "doAction = " + this.currentActionClass + ".getTdIndexByName(" + WAIT_ELEMENT + ", columnName);";
		String methodBody = createVariable + tryCode + action + catchCode + returnCode;
		this.parser.createMethodIfNotExist(methodName, methodBody, parameters, returnClass, Keyword.PUBLIC);
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
