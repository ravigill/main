package com.zoxis.qa.automation.seleniumframework.core;

public enum Browser {
  FIREFOX("*firefox"), 
  IE("*iexplore"),
  GOOGLE_CHROME("*googlechrome"), 
  HTML_UNIT("*htmlunit");

	private String browser;

	private Browser(String browser) {
		this.browser = browser;
	}

	public String toString() {
		return this.browser;
	}
}