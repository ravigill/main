package com.zoxis.qa.automation.seleniumframework.core;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Properties;
import java.util.regex.Pattern;
import jxl.read.biff.BiffException;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.zoxis.qa.automation.seleniumframework.core.commons.SeleniumResult;
import com.zoxis.qa.automation.seleniumframework.core.commons.SeleniumStep;
import com.zoxis.qa.automation.seleniumframework.core.commons.SeleniumTestcase;
import com.zoxis.qa.automation.seleniumframework.core.utilities.SeleniumExcelDataset;
import com.zoxis.qa.automation.seleniumframework.core.utilities.SeleniumLogger;
import com.zoxis.qa.automation.seleniumframework.core.utilities.SeleniumProperties;

import common.Logger;

/**
 * Base Testcase class to be extended by all TestCases within the Implementation
 * 
 * @author ravigill
 */
public class Testcase {
	protected Selenium selenium;
	protected SeleniumProperties uiProperties;
	protected SeleniumProperties envProperties;
	protected boolean isSeleniumStarted = false;
	private static final boolean THIS_IS_WINDOWS = File.pathSeparator
			.equals(";");
	private boolean captureScreenShotOnFailure = false;
	protected StringBuffer verificationErrors = new StringBuffer();

	public Testcase() {
		super();
	}

	public void deleteAllVisibleCookies() {
		this.selenium.deleteAllVisibleCookies();
	}

	public String[][] getExcelData(String path, String sheetName)
			throws BiffException, IOException {
		return SeleniumExcelDataset.getData(path, sheetName);
	}

	public synchronized Properties getProperties(String propertyFilePath) {
		return SeleniumProperties.getProperties(propertyFilePath);
	}

	public Selenium getSelenium(String propertyFilePath) {
		this.envProperties = new SeleniumProperties(propertyFilePath);
		return getSelenium(this.envProperties);
	}

	public Selenium getSelenium() {
		return getSelenium(this.envProperties);
	}

	public Selenium getSelenium(SeleniumProperties envProperties) {
		this.selenium = Selenium.getInstance(envProperties);

		return this.selenium;
	}

	public void startSelenium() {
		if (!this.isSeleniumStarted) {
			this.selenium.start();
			this.isSeleniumStarted = true;
		} else {
			Reporter.log("Selenium is already running!");
		}
	}

	public void closeSelenium() {
		if (this.isSeleniumStarted) {
			this.selenium.close();
			this.selenium.stop();
			this.isSeleniumStarted = false;
		} else {
			Reporter.log("Selenium is not running!");
		}
	}

	@AfterClass
	public void tearDown() {
		checkForVerificationErrors();
		if (this.selenium != null) {
			this.selenium.close();
			this.selenium.stop();
		}
	}

	@BeforeClass
	public void init() {
		String envPropPath = null;
		try {
			envPropPath = System.getProperty("selenium.properties.env");
			Testcase.class.getResource(envPropPath);
			init(envPropPath);
			Logger.getLogger(Testcase.class).info(
					"Environment property file detected at: " + envPropPath);
		} catch (Exception e) {
			try {
				envPropPath = "/configuration/environment.properties";
				Testcase.class.getResource(envPropPath);
				init(envPropPath);
				Logger.getLogger(Testcase.class)
						.info("Environment property file detected at: "
								+ envPropPath);
			} catch (Exception e1) {
				Logger.getLogger(Testcase.class)
						.error("Environment property file could not be detected, please define a System property: selenium.property.env , or place the property file at: /configuration/environment.properties on the class-path");
			}
		}
	}

	public void init(String envProperties) {
		this.envProperties = new SeleniumProperties(envProperties);
		String uiPropertyFilePath = this.envProperties
				.getProperty("selenium.properties.ui");
		this.uiProperties = new SeleniumProperties(uiPropertyFilePath);
		getSelenium();
	}

	public void checkResult(Boolean test, String errorMessage) {
		if (!test.booleanValue()) {
			SeleniumLogger.logToReport("Failed: " + errorMessage);
			fail("Failed: " + errorMessage);
		}
	}

	public boolean checkResult(SeleniumResult result, boolean passCriteria) {
		boolean ret = false;
		if (result.isPassed() != passCriteria) {
			ret = false;
			String message = "FAILED: Test(" + result.getMessage()
					+ "), it returned " + result.isPassed() + ", Expected was "
					+ passCriteria;
			SeleniumLogger.logToReport(message);
			logSteps(result);
			fail(message);
		} else {
			logSteps(result);
			SeleniumLogger.logToReport("PASSED: " + result.getMessage());
		}
		return ret;
	}

	private void logSteps(SeleniumResult result) {
		if ((result.getSteps() != null) && (result.getSteps().size() > 0)) {
			SeleniumLogger.logToReport("------ User Actions -------");
			for (SeleniumStep step : result.getSteps()) {
				SeleniumLogger.logToReport(step);
			}
			SeleniumLogger.logToReport("---------------------------");
		}
	}

	public static void fail(String message) {
		throw new AssertionError(message);
	}

	public static void fail(Exception e) {
		throw new AssertionError(e.toString());
	}

	public void checkForVerificationErrors() {
		String verificationErrorString = this.verificationErrors.toString();
		clearVerificationErrors();
		if (!"".equals(verificationErrorString)) {
			SeleniumLogger.logToReport(verificationErrorString);
			fail(verificationErrorString);
		}
	}

	public void clearVerificationErrors() {
		this.verificationErrors = new StringBuffer();
	}

	protected String runtimeBrowserString() {
		String defaultBrowser = System.getProperty("selenium.defaultBrowser");
		if ((defaultBrowser != null) && (defaultBrowser.startsWith("${"))) {
			defaultBrowser = null;
		}
		if (defaultBrowser == null) {
			if (THIS_IS_WINDOWS)
				defaultBrowser = "*iexplore";
			else {
				defaultBrowser = "*firefox";
			}
		}
		return defaultBrowser;
	}

	public static boolean seleniumEquals(String expectedPattern, String actual) {
		if ((actual.startsWith("regexp:")) || (actual.startsWith("regex:"))
				|| (actual.startsWith("regexpi:"))
				|| (actual.startsWith("regexi:"))) {
			String tmp = actual;
			actual = expectedPattern;
			expectedPattern = tmp;
		}

		Boolean b = handleRegex("regexp:", expectedPattern, actual, 0);
		if (b != null) {
			return b.booleanValue();
		}
		b = handleRegex("regex:", expectedPattern, actual, 0);
		if (b != null) {
			return b.booleanValue();
		}
		b = handleRegex("regexpi:", expectedPattern, actual, 2);
		if (b != null) {
			return b.booleanValue();
		}
		b = handleRegex("regexi:", expectedPattern, actual, 2);
		if (b != null) {
			return b.booleanValue();
		}

		if (expectedPattern.startsWith("exact:")) {
			String expectedExact = expectedPattern.replaceFirst("exact:", "");
			if (!expectedExact.equals(actual)) {
				System.out.println("expected " + actual + " to match "
						+ expectedPattern);
				return false;
			}
			return true;
		}

		String expectedGlob = expectedPattern.replaceFirst("glob:", "");
		expectedGlob = expectedGlob.replaceAll(
				"([\\]\\[\\\\{\\}$\\(\\)\\|\\^\\+.])", "\\\\$1");

		expectedGlob = expectedGlob.replaceAll("\\*", ".*");
		expectedGlob = expectedGlob.replaceAll("\\?", ".");

		if (!Pattern.compile(expectedGlob, 32).matcher(actual).matches()) {
			System.out.println("expected \"" + actual + "\" to match glob \""
					+ expectedPattern
					+ "\" (had transformed the glob into regexp \""
					+ expectedGlob + "\"");
			return false;
		}
		return true;
	}

	private static Boolean handleRegex(String prefix, String expectedPattern,
			String actual, int flags) {
		if (expectedPattern.startsWith(prefix)) {
			String expectedRegEx = expectedPattern.replaceFirst(prefix, ".*")
					+ ".*";
			Pattern p = Pattern.compile(expectedRegEx, flags);
			if (!p.matcher(actual).matches()) {
				System.out.println("expected " + actual + " to match regexp "
						+ expectedPattern);
				return Boolean.FALSE;
			}
			return Boolean.TRUE;
		}
		return null;
	}

	public static boolean seleniumEquals(Object expected, Object actual) {
		if (((expected instanceof String)) && ((actual instanceof String))) {
			return seleniumEquals((String) expected, (String) actual);
		}
		return expected.equals(actual);
	}

	protected boolean isCaptureScreenShotOnFailure() {
		return this.captureScreenShotOnFailure;
	}

	protected void setCaptureScreenShotOnFailure(
			boolean captureScreetShotOnFailure) {
		this.captureScreenShotOnFailure = captureScreetShotOnFailure;
	}
}