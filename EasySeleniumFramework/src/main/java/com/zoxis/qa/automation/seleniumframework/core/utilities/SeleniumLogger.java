package com.zoxis.qa.automation.seleniumframework.core.utilities;

import org.testng.Reporter;
/**
 * Utility to log to the TestNg report.
 * 
 * @author Ravidev Gill
 *
 */
public final class SeleniumLogger {
	
	/**
	 * Log the given message to the TestNG Report
	 * @param obj
	 */
	public static void logToReport(Object obj){
		Reporter.log(obj.toString());
	}

}
