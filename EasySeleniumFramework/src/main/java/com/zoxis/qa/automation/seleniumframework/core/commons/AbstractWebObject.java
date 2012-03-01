package com.zoxis.qa.automation.seleniumframework.core.commons;

import org.testng.Assert;

import com.zoxis.qa.automation.seleniumframework.core.Selenium;
import com.zoxis.qa.automation.seleniumframework.core.utilities.SeleniumProperties;

/**
 * 
 * @author Ravidev Gill (ravi.gill@spindriftgroup.com)
 * 
 */
public class AbstractWebObject extends Assert {
	protected Selenium selenium;
	protected SeleniumProperties uiProperties;

	/**
	 * Constructor
	 * 
	 * @param selenium
	 * @param uiProperties2
	 */
	public AbstractWebObject(Selenium selenium, SeleniumProperties uiProperties2) {
		this.selenium = selenium;
		this.uiProperties = uiProperties2;
	}

	/**
	 * Log stuff to the Report
	 * 
	 * @param e
	 */
	/*
	 * public static void log(Object e){ spindriftgroupLogger.logToReport(e); }
	 */
}
