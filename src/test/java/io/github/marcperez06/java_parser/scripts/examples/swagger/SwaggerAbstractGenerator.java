/**
 * @author Marc Pérez Rodríguez
 */
package io.github.marcperez06.java_parser.scripts.examples.swagger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import io.github.marcperez06.java_parser.resources.objects.swagger.SwaggerDocumentation;
import io.github.marcperez06.java_parser.resources.objects.swagger.SwaggerEndpoint;
import io.github.marcperez06.java_parser.resources.objects.swagger.SwaggerRequestInfo;
import io.github.marcperez06.java_utilities.api.request.Request;
import io.github.marcperez06.java_utilities.api.request.Response;
import io.github.marcperez06.java_utilities.api.request.enums.HttpMethodEnum;
import io.github.marcperez06.java_utilities.api.rest.UnirestClient;
import io.github.marcperez06.java_utilities.file.FileUtils;
import io.github.marcperez06.java_utilities.json.GsonUtils;
import io.github.marcperez06.java_utilities.logger.Logger;
import io.github.marcperez06.java_utilities.strings.StringUtils;

public abstract class SwaggerAbstractGenerator {
	
	protected String packageName;
	protected String swaggerDocumentUri;
	protected SwaggerDocumentation swaggerDocumentation;
	protected String packageScope;
	
	protected SwaggerAbstractGenerator(String packageName, String swaggerDocumentUri) {
		this.packageName = packageName;
		this.swaggerDocumentUri = swaggerDocumentUri;
		this.swaggerDocumentation = null;
		this.packageScope = "main";
	}
	
	public SwaggerDocumentation getSwaggerDocumentation() {
		return this.swaggerDocumentation;
	}

	public void setSwaggerDocumentation(SwaggerDocumentation swaggerDocumentation) {
		this.swaggerDocumentation = swaggerDocumentation;
	}
	
	public void setPackageScope(String packageScope) {
		if (packageScope != null) {
			this.packageScope = packageScope;
		}
	}

	protected void requestSwaggerDocumentation() {
		if (this.swaggerDocumentUri != null && !this.swaggerDocumentUri.isEmpty()) {
			if (this.swaggerUriIsUrl()) {
				this.requestUrlOfSwaggerDocumentation();
			} else {
				this.requestFileOfSwaggerDocumentation();
			}
		}
	}
	
	private boolean swaggerUriIsUrl() {
		boolean isUrl = false;
		isUrl = this.swaggerDocumentUri.startsWith("https://");
		isUrl |= this.swaggerDocumentUri.startsWith("http://");
		return isUrl;
	}
	
	private void requestUrlOfSwaggerDocumentation() {
		try {
			UnirestClient api = new UnirestClient();
			Request request = new Request(HttpMethodEnum.GET);
			request.setURL(this.swaggerDocumentUri);
			Response<SwaggerDocumentation> response = api.send(request);
			if (response.isSuccess()) {
				Optional<SwaggerDocumentation> documentation = response.getResponseObject();
				if (documentation.isPresent()) {
					this.swaggerDocumentation = documentation.get();	
				}
				
			}
		} catch (Exception e) {
			Logger.error("Can not obtain swagger information \n" + e.toString());
		}
	}
	
	private void requestFileOfSwaggerDocumentation() {
		String swaggerJson = FileUtils.getStringOfFile(this.swaggerDocumentUri);
		if (!swaggerJson.isEmpty()) {
			this.swaggerDocumentation = GsonUtils.returnJsonObject(swaggerJson, SwaggerDocumentation.class);
		} else {
			Logger.error("Can not obtain swagger information from file");
		}
	}
	
	protected String getPackageNameForClass(String className) {
		String extendPackage = this.getStartingWordInLowerCase(className);
		
		if (StringUtils.isReservedKeyword(extendPackage)) {
			extendPackage += "_objects";
		}
		
		String packageForClass = this.packageName + "." + extendPackage;
		
		return packageForClass;
	}
	
	protected String getStartingWordInLowerCase(String word) {
		StringBuilder stringBuilder = new StringBuilder();
		String startingWord = "";
		boolean endStartingWord = false;
		
		if (word != null && !word.isEmpty()) {
			
			for (int i = 0; i < word.length() && !endStartingWord; i++) {
				String charValue = String.valueOf(word.charAt(i));
				
				stringBuilder.append(charValue);
				
				if (i < word.length() - 1) {
					String nextCharValue = String.valueOf(word.charAt(i + 1));
					endStartingWord = nextCharValue.matches("^[A-Z0-9]+$");
					endStartingWord |= nextCharValue.equals("_");
					endStartingWord |= nextCharValue.equals("-");
				}

			}
			
		}

		startingWord = stringBuilder.toString();
		return startingWord.toLowerCase();
	}
	
	protected String formatToCamelCase(String word) {
		String camelCaseWord = "";
		StringBuilder wordBuilder = new StringBuilder();
		boolean transformCharToUpperCase = false;
		
		if (word != null && !word.isEmpty()) {
			for (int i = 0; i < word.length(); i++) {
				String currentChar = String.valueOf(word.charAt(i));
				
				if (transformCharToUpperCase) {
					currentChar = currentChar.toUpperCase();
					transformCharToUpperCase = false;
				}
				
				if (this.isSplitter(currentChar)) {
					transformCharToUpperCase = true;
				} else {
					wordBuilder.append(currentChar);
				}
				
			}
		}

		camelCaseWord = wordBuilder.toString().trim();
		camelCaseWord = StringUtils.clearSpecialCharacters(camelCaseWord);

		return camelCaseWord;
	}
	
	private boolean isSplitter(String charValue) {
		boolean isSplitter = (charValue.equals(".") || charValue.equals(","));
		//isSplitter |= charValue.matches("^[A-Z0-9]+$");
		isSplitter |= (charValue.equals("{") || charValue.equals("}"));
		isSplitter |= (charValue.equals("(") || charValue.equals(")"));
		isSplitter |= charValue.equals("-");
		isSplitter |= charValue.equals("/");
		isSplitter |= charValue.equals(" ");
		return isSplitter;
	}
	
	protected Map<String, Map<String, SwaggerEndpoint>> getClustersOfEndpoitns() {
		Map<String, Map<String, SwaggerEndpoint>> clusters = new HashMap<String, Map<String, SwaggerEndpoint>>();
		
		for (Entry<String, SwaggerEndpoint> endpointEntry : this.swaggerDocumentation.getPaths().entrySet()) {
			
			SwaggerEndpoint endpoint = endpointEntry.getValue();
			String endpointUrl = endpointEntry.getKey();
			//String endpointName = this.getEndpointName(endpointUrl);
			//String constantName = this.transformToConstantName(endpointName);
			
			if (endpoint.haveAnyRequestInfo()) {
				
				SwaggerRequestInfo requestInfo = endpoint.getRequestInfo();
				
				for (int i = 0; i < requestInfo.getTags().size(); i++) {
					String tag = requestInfo.getTags().get(i);
					if (!clusters.containsKey(tag)) {
						Map<String, SwaggerEndpoint> cluster = new HashMap<String, SwaggerEndpoint>();
						clusters.put(tag, cluster);
					}
					if (!clusters.get(tag).containsKey(endpointUrl)) {
						clusters.get(tag).put(endpointUrl, endpoint);
					}
				}
				
			}

		}
		
		return clusters;
	}
	
	protected String getEndpointName(String endpoint) {
		StringBuilder endpointBuilder = new StringBuilder("/");
		String[] partsOfEndpoint = endpoint.split("/");
		boolean firstApiIgnored = false;

		if (partsOfEndpoint.length > 0) {
			
			for (int i = 0; i < partsOfEndpoint.length; i++) {
				String endpointPart = partsOfEndpoint[i];
				boolean notIsApi = !endpointPart.isEmpty();
				notIsApi &= (!firstApiIgnored) ? !endpointPart.equals("api") : true;
				
				if (notIsApi) {
					endpointBuilder.append(partsOfEndpoint[i]);
					endpointBuilder.append("/");
					firstApiIgnored = true;
				}
			}

			endpointBuilder.deleteCharAt(endpointBuilder.length() - 1);
			
		}

		return endpointBuilder.toString();
	}
	
	protected String transformToConstantName(String variableName) {
		StringBuilder stringBuilder = new StringBuilder();
		String constantName = "";
		
		if (variableName != null && !variableName.isEmpty()) {
			
			for (int i = 0; i < variableName.length(); i++) {
				
				String letter = String.valueOf(variableName.charAt(i));
				
				if (this.isValidChar(letter)) {
					String constantLetter = this.getConstantLetter(letter, variableName, i);
					stringBuilder.append(constantLetter);
				}

			}
		}

		int lastCharIndex = stringBuilder.length() - 1;
		
		if (lastCharIndex > 0) {
			String lastChar = String.valueOf(stringBuilder.charAt(lastCharIndex));
			
			if (lastChar.equals("_")) {
				stringBuilder.deleteCharAt(lastCharIndex);
			}
		}

		constantName = stringBuilder.toString().toUpperCase();
		return constantName;
	}
	
	private boolean isValidChar(String charValue) {
		boolean isValidChar = (!charValue.equals("{") && !charValue.equals("}"));
		isValidChar &= (!charValue.equals("(") && !charValue.equals(")"));
		isValidChar &= (!charValue.equals("[") && !charValue.equals("]"));
		isValidChar &= (!charValue.equals("+") && !charValue.equals("-"));
		isValidChar &= (!charValue.equals("*") && !charValue.equals("="));
		isValidChar &= (!charValue.equals("'") && !charValue.equals("\""));
		isValidChar &= (!charValue.equals(".") && !charValue.equals(","));
		isValidChar &= (!charValue.equals("/") && !charValue.equals("\\"));
		return isValidChar;
	}
	
	private String getConstantLetter(String letter, String variableName, int index) {
		String constantLetter = letter;

		if (index < variableName.length() - 1) {
			
			String nextLetter = String.valueOf(variableName.charAt(index + 1));
			
			boolean haveSplitter = this.isSplitter(nextLetter);

			if (haveSplitter) {
				constantLetter += "_";
			}
			
		}

		return constantLetter;
	}
	
	protected void deletePackage(String packageName) {
		if (packageName != null && !packageName.isEmpty()) {
			String fileSeparator = System.getProperty("file.separator");
			String baseDir = System.getProperty("user.dir") + fileSeparator;
			baseDir += "src" + fileSeparator + this.packageScope + fileSeparator + "java" + fileSeparator;
			String pathOfPackage = packageName.replaceAll("\\.", "\\" + fileSeparator);
			String pathOfDirectory = baseDir + pathOfPackage;
			File directory = new File(pathOfDirectory);
			this.deleteDirectory(directory);
		}
	}
	
	private void deleteDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					if (file.isDirectory()) {
						this.deleteDirectory(file);
					} else {
						files[i].delete();
					}
				}
			}
			directory.delete();
		}
	}

}
