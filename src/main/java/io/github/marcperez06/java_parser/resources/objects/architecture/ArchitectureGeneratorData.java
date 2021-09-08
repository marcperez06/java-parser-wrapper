package io.github.marcperez06.java_parser.resources.objects.architecture;

public class ArchitectureGeneratorData {
	
	private String pageName;
	private String packageName;

	private String activityName;
	private String platformName;
	
	public ArchitectureGeneratorData() {
		this.pageName = "";
		this.packageName = "";
		this.activityName = "";
		this.platformName = "";
	}

	public String getPageName() {
		return this.pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getPackageName() {
		return this.packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getActivityName() {
		return this.activityName;
	}
	
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	
	public String getPlatformName() {
		return this.platformName;
	}
	
	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

}