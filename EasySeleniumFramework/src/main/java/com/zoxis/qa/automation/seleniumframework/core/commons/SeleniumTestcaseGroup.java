package com.zoxis.qa.automation.seleniumframework.core.commons;

public enum SeleniumTestcaseGroup {
	ALL(0),
	SANITY(0),
	REGRESSION(0),
	MONITORING(0),
	UNIQUE(0);
	
	private SeleniumTestcaseGroup(int type) {
		this.type = type;
	}
	
	private int type;
	
	public int toInt(){
		return type;
	}
}