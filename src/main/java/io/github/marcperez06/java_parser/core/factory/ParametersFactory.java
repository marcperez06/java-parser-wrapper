package io.github.marcperez06.java_parser.core.factory;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.Parameter;

public class ParametersFactory {
	
	private ParametersFactory() {};

	/**
	 * Create a List of parameters with one parameter with the parameter name specified and of the parameter type specified
	 * @param paramName - String parameter name
	 * @param paramType - String parameter type
	 * @return List&lt;Parameter&gt; - List of parameters
	 */
	public static List<Parameter> createListWithOneParameter(String paramName, String paramType) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		Parameter param = createParameter(paramName, paramType);
		
		if (param != null) {
			parameters.add(param);
		}
		
		return parameters;
	}
	
	/**
	 * Create a Parameter of the type specified and with the name specified
	 * @param paramName - String parameter name
	 * @param paramType - String parameter type
	 * @return Parameter - Parameter with the name and type specified
	 */
	public static Parameter createParameter(String paramName, String paramType) {
		Parameter param = createBaseParameter(paramName);
		
		if (param != null) {
			param.setType(paramType);
		}
		
		return param;
	}
	
	/**
	 * Create a List of parameters with one parameter with the parameter name specified and of the parameter type specified
	 * @param paramName - String parameter name
	 * @param paramType - Class&lt;?&gt; parameter type
	 * @return List&lt;Parameter&gt; - List of parameters
	 */
	public static List<Parameter> createListWithOneParameter(String paramName, Class<?> paramType) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		Parameter param = createParameter(paramName, paramType);
		
		if (param != null) {
			parameters.add(param);
		}
		
		return parameters;
	}
	
	/**
	 * Create a Parameter of the type specified and with the name specified
	 * @param paramName - String parameter name
	 * @param paramType - Class&lt;?&gt; parameter type
	 * @return Parameter - Parameter with the name and type specified
	 */
	public static Parameter createParameter(String paramName, Class<?> paramType) {
		Parameter param = createBaseParameter(paramName);
		
		if (param != null) {
			param.setType(paramType);
		}
	
		return param;
	}

	private static Parameter createBaseParameter(String paramName) {
		Parameter param = null;
		if (paramName != null && !paramName.isEmpty()) {
			param = new Parameter();
			param.setName(paramName);
		}
		return param;
	}
	
	/**
	 * Create a list of parameters with the parameters specified
	 * @param params - Parameter (Optional) from 0 to N
	 * @return List&lt;Parameter&gt; - List of parameters
	 */
	public static List<Parameter> createListOfParameters(Parameter...params) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		
		for (int i = 0; i < params.length; i++) {
			parameters.add(params[i]);
		}
		
		return parameters;
	}
	
}