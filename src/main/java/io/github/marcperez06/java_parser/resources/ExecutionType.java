package io.github.marcperez06.java_parser.resources;

public enum ExecutionType {
	NULL(null),
	API("api"),
	WEB("web"),
	MOBILE("mobile");
	
	private String value;
	
	ExecutionType(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
	
	public static ExecutionType getEnum(String value) {
		ExecutionType executionType = ExecutionType.NULL;
		if (value != null && !value.isEmpty()) {
			
			if (value.equalsIgnoreCase("api")) {
				executionType = ExecutionType.API;
			} else if (value.equalsIgnoreCase("web")) {
				executionType = ExecutionType.WEB;
			} else if (value.equalsIgnoreCase("mobile")) {
				executionType = ExecutionType.MOBILE;
			}
			
		}
		return executionType;
	}

}