package com.zoxis.qa.automation.seleniumframework.core;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverCommandProcessor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.internal.WrapsDriver;
import org.testng.Reporter;
import com.google.common.base.Supplier;
import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;
import com.zoxis.qa.automation.seleniumframework.core.utilities.RegexHelper;
import com.zoxis.qa.automation.seleniumframework.core.utilities.SeleniumLogger;
import com.zoxis.qa.automation.seleniumframework.core.utilities.SeleniumProperties;

public class Selenium extends DefaultSelenium implements WrapsDriver,
		com.thoughtworks.selenium.Selenium {
	/*
	 * spindriftgroupSelenium.java is a singeloton hence hold a instance of
	 * itself.
	 */
	public static Selenium selenium;
	/*
	 * Constants
	 */
	public static final boolean DEFAULT_IS_HIGHLIGHT = false;
	public static final int DEFAULT_HIGHLIGHT_CNT = 1;
	public static int DEFAULT_HIGHLIGHT_TIME = 500;
	public static int DEFAULT_TIMEOUT = 120000; // Web standard is 120 seconds
	public static int DEFAULT_TIME_BETWEEN_CHECKS = 1000;
	public static final boolean DEFAULT_IS_WINDOW_MAXIMIZE = false;
	public static final int DEFAULT_SPEED = 500;
	public static final boolean IS_LOG_STEPS = false;
	public static final String PROPERTY_UI_FILEPATH = "selenium.properties.ui";

	/*
	 * Instance variables, selenium properties
	 */
	private boolean isStarted = false;
	private boolean isHighlight = DEFAULT_IS_HIGHLIGHT;
	private int highlightCnt = DEFAULT_HIGHLIGHT_CNT;
	private int highLightTime = DEFAULT_HIGHLIGHT_TIME;
	private int timeout = DEFAULT_TIMEOUT;
	private int timeBetweenChecks = DEFAULT_TIME_BETWEEN_CHECKS;
	private boolean isWindowMaximize = DEFAULT_IS_WINDOW_MAXIMIZE;
	private int speed = DEFAULT_SPEED;
	private boolean isLogSteps = IS_LOG_STEPS;

	/**
	 * Constructor takes a {@link CommandProcessor}
	 * 
	 * @param processor
	 */
	public Selenium(CommandProcessor processor) {
		super(processor);
	}

	public Selenium(Supplier<WebDriver> maker, String baseUrl) {
		super(new WebDriverCommandProcessor(baseUrl, maker));
	}

	public Selenium(WebDriver baseDriver, String baseUrl) {
		super(new WebDriverCommandProcessor(baseUrl, baseDriver));
	}

	public WebDriver getWrappedDriver() {
		return ((WrapsDriver) commandProcessor).getWrappedDriver();
	}

	/**
	 * Constructor
	 * 
	 * @param serverHost
	 * @param serverPort
	 * @param browserStartCommand
	 * @param browserURL
	 */
	private Selenium(String serverHost, int serverPort,
			String browserStartCommand, String browserURL) {
		super(serverHost, serverPort, browserStartCommand, browserURL);
	}

	/**
	 * Start selenium and add assign parameters to it: {@link
	 * this#isWindowMaximize} & {@link this#speed}
	 */
	@Override
	public void start() {
		log("selenium.start();");
		/**
		 * Deprecated because the new webdriver doesnt have this, it starts on
		 * selenium.open();
		 */
		// super.start();
		if (this.isWindowMaximize) {
			super.windowMaximize();
		}
		if (this.speed > 0) {
			String str = String.valueOf(speed);
			super.setSpeed(str);
		}
		this.setStarted(true);
	}

	public static Selenium getInstance(SeleniumProperties envProperties) {
		if (selenium == null) {
			selenium = getNewInstance(envProperties);
		}
		return selenium;
	}

	public static Selenium getNewInstance(String baseUrl, String basePort,
			String browser) {
		selenium = new Selenium(new FirefoxDriver(), baseUrl + ":" + basePort);
		return selenium;
	}

	public static Selenium getNewInstance(SeleniumProperties envProperties) {
		SeleniumProperties props = envProperties;
		try {
			String browserStartCommand = props.getProperty(
					"selenium.server.browser", "*firefox");
			String serverHost = props.getProperty("selenium.server.host",
					"localhost");
			String serverPort = props.getProperty("selenium.server.port",
					"4444");
			String browserURL = props.getProperty("selenium.server.url",
					"http://spindriftgroup.com");

			selenium = new Selenium(getWebDriver(browserStartCommand),
					browserURL);
			try {
				int speed = Integer
						.valueOf(props.getProperty("selenium.speed"))
						.intValue();
				selenium.setSpeed(speed);
				boolean isHighlight = Boolean.valueOf(
						props.getProperty("selenium.highlight"));

				selenium.setHighlight(isHighlight);
				boolean isWindowMaximize = Boolean.valueOf(
						props.getProperty("selenium.windowmaximize"))
						.booleanValue();

				selenium.setWindowMaximize(isWindowMaximize);
				boolean isLogSteps = Boolean.valueOf(
						props.getProperty("selenium.log.steps")).booleanValue();

				selenium.setLogSteps(isLogSteps);
				int highLightTime = Integer.valueOf(
						props.getProperty("selenium.highlight.time"))
						.intValue();

				selenium.setHighLightTime(highLightTime);
				int highlightCnt = Integer.valueOf(
						props.getProperty("selenium.highlight.cnt")).intValue();

				selenium.setHighlightCnt(highlightCnt);
				int timeBetweenChecks = Integer.valueOf(
						props.getProperty("selenium.timebetweenchecks"))
						.intValue();

				selenium.setTimeBetweenChecks(timeBetweenChecks);
				int timeout = Integer.valueOf(
						props.getProperty("selenium.timeout")).intValue();

				selenium.setTimeout(timeout);
			} catch (Exception e) {
				Reporter.log(Selenium.class.toString()
						+ ": Something went wrong with selenium (optional) initialization: "
						+ e.getStackTrace());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.log(Selenium.class.getClass().toString()
					+ ": Something went wrong with selenium (required) initialization: "
					+ e.getStackTrace());
			System.exit(1);
		}
		return selenium;
	}

	private static WebDriver getWebDriver(String browserType) {
		for (Browser browser : Browser.values()) {
			if (browser.toString().equals(browserType)) {
				switch (browser) {
				case FIREFOX:
					return new FirefoxDriver();
				case GOOGLE_CHROME:
					return new ChromeDriver();
				case IE:
					return new InternetExplorerDriver();
				}
				return new HtmlUnitDriver();
			}
		}

		throw new SeleniumException("The given browser \"" + browserType
				+ "\" is not compatible!");
	}

	public String getStatus() {
		StringBuilder ret = new StringBuilder();
		ret.append("Highlight: " + this.isHighlight);
		ret.append("Speed: " + this.speed);
		ret.append("Maximize Window: " + this.isWindowMaximize);
		ret.append("Log Selenium steps: " + this.isLogSteps);
		ret.append("Timeout: " + this.timeout);
		return ret.toString();
	}

	public String[] getAllDropDowns() {
		String dropDowns = "var d = selenium.browserbot.getCurrentWindow().document.getElementsByTagName('select');var ids= \"\" ; for (var i=0; i<d.length;i++){ ids= ids + d[i].id + ',' ; }ids;";

		String ids = selenium.getEval(dropDowns);

		if (ids != null) {
			String[] returnValue = ids.split(",");
			return returnValue;
		}
		return new String[0];
	}

	public String[] GetAllCheckBoxes() {
		String dr = "var d = selenium.browserbot.getCurrentWindow().document.getElementsByTagName('input');var cbId = \"\" ; for (var i=0; i<d.length;i++){ if (d[i].type == 'checkbox') { cbId = cbId + d[i].id + ',' ; } }cbId;";

		String ids = selenium.getEval(dr);

		if (ids != null) {
			String[] returnValue = ids.split(",");
			return returnValue;
		}
		return new String[0];
	}

	public boolean isEnabled(String controlId) throws Exception {
		boolean retValue = false;
		try {
			String disabled = selenium
					.getEval("selenium.browserbot.getCurrentWindow().getElementById('"
							+ controlId + "').disabled");
			retValue = disabled.toLowerCase() != "true";
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
		return retValue;
	}
	/**
	 * Set the number of blinks to the highlight.
	 * 
	 * @Params Integer count for the highlight
	 */
	public void setHighlightCnt(int highlightCnt) {
		if (highlightCnt > 0)
			this.highlightCnt = highlightCnt;
	}

	/**
	 * Set the waiting time between the highlight blinks
	 * 
	 * @Params Integer highlightTime
	 */
	public void setHighLightTime(int highLightTime) {
		if (highLightTime > 0)
			this.highLightTime = highLightTime;
	}

	/**
	 * Check if selenium started
	 * 
	 * @return
	 */
	public boolean isStarted() {
		return isStarted;
	}

	/**
	 * Set isSeleniumStarted to true
	 * 
	 * @param isSeleniumStarted
	 */
	private void setStarted(boolean isSeleniumStarted) {
		this.isStarted = isSeleniumStarted;
	}

	/**
	 * Get boolean for whether highlight is enabled or not
	 * 
	 * @Return true/false
	 */
	public boolean isHighlight() {
		return isHighlight;
	}

	/**
	 * Check if each selenium step needs to be logged
	 * 
	 * @return
	 */
	public boolean isLogSteps() {
		return isLogSteps;
	}

	/**
	 * Set the logging of selenium steps
	 * 
	 * @param isLogSteps
	 */
	public void setLogSteps(boolean isLogSteps) {
		this.isLogSteps = isLogSteps;
	}

	/**
	 * Set whether to highlight or not
	 * 
	 * @Params Boolean
	 * @Default false
	 */
	public void setHighlight(boolean isHighlight) {
		this.isHighlight = isHighlight;
	}

	/**
	 * Sets the selenium timeout before failing the sample
	 * 
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		if (timeout >= 0)
			this.timeout = timeout;
	}

	/**
	 * The wait time between checks untill the element is renderd
	 * 
	 * @param timeBetwenChecks
	 */
	public void setTimeBetweenChecks(int timeBetweenChecks) {
		if (timeBetweenChecks >= 0 && timeBetweenChecks < (1000 * 60 * 60))
			this.timeBetweenChecks = timeBetweenChecks;
	}

	/**
	 * Maximize the browser window
	 * 
	 * @param isWindowMaximize
	 */
	public void setWindowMaximize(boolean isWindowMaximize) {
		this.isWindowMaximize = isWindowMaximize;
	}

	/**
	 * Speed of selenium, hence the waiting time between actions
	 * 
	 * @param speed
	 */
	public void setSpeed(int speed) {
		if (speed > 0)
			this.speed = speed;
	}

	/**
	 * Override {@link DefaultSelenium#click(String)} to include {@link
	 * this#highlight(String)} as a part of it
	 */
	@Override
	public void click(String locator) {
		log("selenium.click(\"" + locator + "\");");
		if (isHighlight) {
			this.highlight(locator);
		}
		super.click(locator);
	}

	/**
	 * Click GWT button
	 * 
	 * @param over
	 * @param down
	 * @param up
	 */

	public void clickGwtButton(String over, String down, String up)
			throws SeleniumException {
		log("selenium.clickGwtButton(\"" + over + "\",\"" + down + "\",\"" + up
				+ "\");");
		this.mouseOver(over);
		this.mouseDown(down);
		this.mouseUp(up);
	}

	/**
	 * Return a instance of this object
	 * 
	 * @return
	 */
	public DefaultSelenium getSelenium() {
		return this;
	}

	/**
	 * Highlight the mentioned item
	 * 
	 * @param String
	 *            locator to highlight.
	 */
	@Override
	public void highlight(String locator) {
		try {
			for (int i = 0; i < highlightCnt; i++) {
				super.highlight(locator);
				Thread.sleep(highLightTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SeleniumException("HAHA");
		}
	}

	/**
	 * Wait for the given time before continuing
	 * 
	 * @param millis
	 */
	public void waitFor(int millis) throws SeleniumException {
		log("selenium.waitFor(" + millis + ");");
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			Reporter.log(this.getClass().toString() + "Exception: waitFor(\""
					+ millis + "\") threw a exception;");
			throw new SeleniumException("selenium.waitFor(\"" + millis + "\")");
		}
	}

	/**
	 * Waits dynamically, checking for text every second. Will stop when the
	 * text is found or will fail with a timeout after a second
	 * 
	 * @param text
	 *            - text to wait for
	 */
	public void waitForTextPresent(String text) throws SeleniumException {
		log("selenium.waitForTextPresent(\"" + text + "\");");
		waitForText(text, true);
	}

	/**
	 * Waits dynamically, checking for text every second. Will stop when the
	 * text is found or will fail with a timeout after a second
	 * 
	 * @param text
	 *            - text to wait for
	 */
	public void waitForTextNotPresent(String text) throws SeleniumException {
		log("selenium.waitForTextNotPresent(\"" + text + "\");");
		waitForText(text, false);
	}

	/**
	 * Waits dynamically, checking for the element every second. Will stop when
	 * the element is found or will fail with a timeout after 60 seconds
	 * 
	 * @param locator
	 *            - locator of element to wait for
	 */
	public void waitForElementPresent(String locator) throws SeleniumException {
		log("selenium.waitForElementPresent(\"" + locator + "\");");
		waitForElement(locator, true);
	}

	/**
	 * Waits dynamically, checking for the element every second. Will stop when
	 * the element is not found or will fail with a timeout after 60 seconds
	 * 
	 * @param locator
	 *            - locator of element to wait for
	 */
	public void waitForElementNotPresent(String locator)
			throws SeleniumException {
		log("selenium.waitForElementNotPresent(\"" + locator + "\");");
		waitForElement(locator, false);
	}

	public void waitForElementVisible(String locator) throws SeleniumException {
		log("selenium.waitForElementVisible(\"" + locator + "\");");
		waitForElementVisible(locator, true);
	}

	public void waitForElementNotVisible(String locator)
			throws SeleniumException {
		log("selenium.waitForElemenNotVisible(\"" + locator + "\");");
		waitForElementVisible(locator, false);
	}

	private void waitForText(final String text, final boolean checkForPresence)
			throws SeleniumException {
		SeleniumCommand command = new SeleniumCommand() {
			public boolean execute() {
				return isTextPresent(text) == checkForPresence;
			}
		};
		try {
			waitFor(command);
		} catch (SeleniumException e) {
			throw new SeleniumException("selenium.waitForText(\"" + text
					+ "\",\"" + checkForPresence + "\")");
		}
	}

	private void waitForElement(final String locator,
			final boolean checkForPresence) throws SeleniumException {
		SeleniumCommand command = new SeleniumCommand() {
			public boolean execute() {
				return isElementPresent(locator) == checkForPresence;
			}
		};
		try {
			waitFor(command);
		} catch (SeleniumException e) {
			throw new SeleniumException("selenium.waitForElement(\"" + locator
					+ "\",\"" + checkForPresence + "\")");
		}
	}

	private void waitForElementVisible(final String locator,
			final boolean checkForPresence) {
		SeleniumCommand command = new SeleniumCommand() {

			public boolean execute() {
				return isVisible(locator) == checkForPresence;
			}

		};
		try {
			waitFor(command);
		} catch (SeleniumException e) {
			throw new SeleniumException("selenium.waitForElementVisible(\""
					+ locator + "\",\"" + checkForPresence + "\")");
		}
	}

	private void waitFor(SeleniumCommand seleniumCommand)
			throws AssertionException {
		long currentTime = new Date().getTime();
		long endTime = currentTime + timeout;
		while ((currentTime = new Date().getTime()) < endTime) {
			if (seleniumCommand.execute()) {
				break;
			}
			try {
				Thread.sleep(timeBetweenChecks);
			} catch (InterruptedException e) {
				throw new SeleniumException("Element didnt became visible after check: "+e.getStackTrace().toString());
			}
		}

		if (currentTime >= endTime) {
			throw new AssertionException(
					"Timeout: Element didnt became visible!");
		}
	}

	private interface SeleniumCommand {
		public boolean execute();
	}

	public void assertTrue(boolean bool) throws AssertionException {
		log("selenium.assertTrue(\"" + bool + "\");");
		if (!bool) {
			throw new AssertionException("Selenium assertTrue returned false");
		}
	}

	public void assertFalse(boolean bool) throws AssertionException {
		log("selenium.assertFalse(\"" + bool + "\");");
		if (bool) {
			throw new AssertionException("Selenium assertTrue returned false");
		}
	}

	/**
	 * Log Selenium steps
	 * 
	 * @param string
	 */
	private void log(String string) {
		if (isLogSteps) {
			SeleniumLogger.logToReport(string);
			System.out.println(string);
		}
	}

	private void log(String method, boolean status) {
		String message = "Failed";
		if (status) {
			message = "Passed";
		}
		log("Selenium Action: " + method + "\t Status: " + message);
	}

	/*
	 * Override standard selenium methods to include extra information for the
	 * exceptions and add logging..
	 */

	@Override
	public void setExtensionJs(String extensionJs) throws SeleniumException {
		log("setExtendsionJS(\"" + extensionJs.toString() + "\");");
		super.setExtensionJs(extensionJs);
	}

	@Override
	public void start(String optionsString) throws SeleniumException {
		log("selenium.start(\"" + optionsString + "\");");
		super.start(optionsString);
	}

	@Override
	public void start(Object optionsObject) throws SeleniumException {
		log("start(\"" + optionsObject.toString() + "\");");
		super.start(optionsObject);
	}

	@Override
	public void stop() throws SeleniumException {
		log("selenium.stop();");
		super.stop();
	}

	@Override
	public void showContextualBanner() throws SeleniumException {
		log("selenium.showContextualBanner();");
		try {
			super.showContextualBanner();
		} catch (Exception e) {

			throw new SeleniumException("selenium.showContextualBanner()");
		}
	}

	@Override
	public void showContextualBanner(String className, String methodName)
			throws SeleniumException {
		log("selenium.showContextualBanner(\"" + className + "\",\""
				+ methodName + "\");");
		try {
			super.showContextualBanner(className, methodName);
		} catch (Exception e) {

			throw new SeleniumException("selenium.showContextualBanner(\""
					+ className + "\",\"" + methodName + "\")");
		}
	}

	@Override
	public void doubleClick(String locator) throws SeleniumException {
		log("selenium.doubleClick(\"" + locator + "\");");
		try {
			super.doubleClick(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.doubleClick(\"" + locator
					+ "\")");
		}
	}

	@Override
	public void contextMenu(String locator) throws SeleniumException {
		log("selenium.contextMenu(\"" + locator + "\");");
		try {
			super.contextMenu(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.contextMenu(\"" + locator
					+ "\")");
		}
	}

	@Override
	public void clickAt(String locator, String coordString)
			throws SeleniumException {
		log("selenium.clickAt(\"" + locator + "\",\"" + coordString + "\");");
		try {
			super.clickAt(locator, coordString);
		} catch (Exception e) {

			throw new SeleniumException("selenium.clickAt(\"" + locator
					+ "\",\"" + coordString + "\")");
		}
	}

	@Override
	public void doubleClickAt(String locator, String coordString)
			throws SeleniumException {
		log("selenium.doubleClickAt(\"" + locator + "\",\"" + coordString
				+ "\");");
		try {
			super.doubleClickAt(locator, coordString);
		} catch (Exception e) {

			throw new SeleniumException("selenium.doubleClickAt(\"" + locator
					+ "\",\"" + coordString + "\")");
		}
	}

	@Override
	public void contextMenuAt(String locator, String coordString)
			throws SeleniumException {
		log("selenium.contextMenuAt(\"" + locator + "\",\"" + coordString
				+ "\");");
		try {
			super.contextMenuAt(locator, coordString);
		} catch (Exception e) {

			throw new SeleniumException("selenium.contextMenuAt(\"" + locator
					+ "\",\"" + coordString + "\")");
		}
	}

	@Override
	public void fireEvent(String locator, String eventName)
			throws SeleniumException {
		log("selenium.fireEvent(\"" + locator + "\",\"" + eventName + "\");");
		try {
			super.fireEvent(locator, eventName);
		} catch (Exception e) {

			throw new SeleniumException("selenium.fireEvent(\"" + locator
					+ "\",\"" + eventName + "\")");
		}
	}

	@Override
	public void focus(String locator) throws SeleniumException {
		log("selenium.focus(\"" + locator + "\");");
		try {
			super.focus(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.focus(\"" + locator + "\")");
		}
	}

	@Override
	public void keyPress(String locator, String keySequence)
			throws SeleniumException {
		log("selenium.keyPress(\"" + locator + "\",\"" + keySequence + "\");");
		try {
			super.keyPress(locator, keySequence);
		} catch (Exception e) {

			throw new SeleniumException("selenium.keyPress(\"" + locator
					+ "\",\"" + keySequence + "\")");
		}
	}

	@Override
	public void shiftKeyDown() throws SeleniumException {
		log("selenium.shiftKeyDown();");
		try {
			super.shiftKeyDown();
		} catch (Exception e) {

			throw new SeleniumException("selenium.shiftKeyDown()");
		}
	}

	@Override
	public void shiftKeyUp() throws SeleniumException {
		log("selenium.shiftKeyUp();");
		try {
			super.shiftKeyUp();
		} catch (Exception e) {

			throw new SeleniumException("selenium.shiftKeyUp()");
		}
	}

	@Override
	public void metaKeyDown() throws SeleniumException {
		log("selenium.metaKeyDown();");
		try {
			super.metaKeyDown();
		} catch (Exception e) {

			throw new SeleniumException("selenium.metaKeyDown()");
		}
	}

	@Override
	public void metaKeyUp() throws SeleniumException {
		log("selenium.metaKeyUp();");
		try {
			super.metaKeyUp();
		} catch (Exception e) {

			throw new SeleniumException("selenium.metaKeyUp()");
		}
	}

	@Override
	public void altKeyDown() throws SeleniumException {
		log("selenium.altKeyDown();");
		try {
			super.altKeyDown();
		} catch (Exception e) {

			throw new SeleniumException("selenium.altKeyDown()");
		}
	}

	@Override
	public void altKeyUp() throws SeleniumException {
		log("selenium.altKeyUp();");
		try {
			super.altKeyUp();
		} catch (Exception e) {

			throw new SeleniumException("selenium.altKeyUp()");
		}
	}

	@Override
	public void controlKeyDown() throws SeleniumException {
		log("selenium.controlKeyDown();");
		try {
			super.controlKeyDown();
		} catch (Exception e) {

			throw new SeleniumException("selenium.controlKeyDown()");
		}
	}

	@Override
	public void controlKeyUp() throws SeleniumException {
		log("selenium.controlKeyUp();");
		try {
			super.controlKeyUp();
		} catch (Exception e) {

			throw new SeleniumException("selenium.controlKeyUp()");
		}
	}

	@Override
	public void keyDown(String locator, String keySequence)
			throws SeleniumException {
		log("selenium.keyDown(\"" + locator + "\",\"" + keySequence + "\");");
		try {
			super.keyDown(locator, keySequence);
		} catch (Exception e) {

			throw new SeleniumException("selenium.keyDown(\"" + locator
					+ "\",\"" + keySequence + "\")");
		}
	}

	@Override
	public void keyUp(String locator, String keySequence)
			throws SeleniumException {
		log("selenium.keyUp(\"" + locator + "\",\"" + keySequence + "\");");
		try {
			super.keyUp(locator, keySequence);
		} catch (Exception e) {

			throw new SeleniumException("selenium.keyUp(\"" + locator + "\",\""
					+ keySequence + "\")");
		}
	}

	@Override
	public void mouseOver(String locator) throws SeleniumException {
		log("selenium.mouseOver(\"" + locator + "\");");
		try {
			try {
				super.mouseOver(locator);
			} catch (Exception e) {

				throw new SeleniumException("selenium.mouseOver(\"" + locator
						+ "\")");
			}
		} catch (Exception e) {
			throw new RuntimeException("selenium.mouseOver(\""
					+ locator.toString() + "\") FAILED because:", e);
		}
	}

	@Override
	public void mouseOut(String locator) throws SeleniumException {
		log("selenium.mouseOut(\"" + locator + "\");");
		try {
			super.mouseOut(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.mouseOut(\"" + locator
					+ "\")");
		}
	}

	@Override
	public void mouseDown(String locator) throws SeleniumException {
		log("selenium.mouseDown(\"" + locator + "\");");
		try {
			super.mouseDown(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.mouseDown(\"" + locator
					+ "\")");
		}
	}

	@Override
	public void mouseDownRight(String locator) throws SeleniumException {
		log("selenium.mouseDownRight(\"" + locator + "\");");
		try {
			super.mouseDownRight(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.mouseDownRight(\"" + locator
					+ "\")");
		}
	}

	@Override
	public void mouseDownAt(String locator, String coordString)
			throws SeleniumException {
		log("selenium.mouseDownAt(\"" + locator + "\",\"" + coordString
				+ "\");");
		try {
			super.mouseDownAt(locator, coordString);
		} catch (Exception e) {

			throw new SeleniumException("selenium.mouseDownAt(\"" + locator
					+ "\",\"" + coordString + "\")");
		}
	}

	@Override
	public void mouseDownRightAt(String locator, String coordString)
			throws SeleniumException {
		log("selenium.mouseDownRightAt(\"" + locator + "\",\"" + coordString
				+ "\");");
		try {
			super.mouseDownRightAt(locator, coordString);
		} catch (Exception e) {

			throw new SeleniumException("selenium.mouseDownRightAt(\""
					+ locator + "\",\"" + coordString + "\")");
		}
	}

	@Override
	public void mouseUp(String locator) throws SeleniumException {
		log("selenium.mouseUp(\"" + locator + "\");");
		try {
			super.mouseUp(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.mouseUp(\"" + locator + "\")");
		}

	}

	@Override
	public void mouseUpRight(String locator) throws SeleniumException {
		log("selenium.mouseUpRight(\"" + locator + "\");");
		try {
			super.mouseUpRight(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.mouseUpRight(\"" + locator
					+ "\")");
		}
	}

	@Override
	public void mouseUpAt(String locator, String coordString)
			throws SeleniumException {
		log("selenium.mouseUpAt(\"" + locator + "\",\"" + coordString + "\");");
		try {
			super.mouseUpAt(locator, coordString);
		} catch (Exception e) {

			throw new SeleniumException("selenium.mouseUpAt(\"" + locator
					+ "\",\"" + coordString + "\")");
		}
	}

	@Override
	public void mouseUpRightAt(String locator, String coordString)
			throws SeleniumException {
		log("selenium.mouseUpRightAt(\"" + locator + "\",\"" + coordString
				+ "\");");
		try {
			super.mouseUpRightAt(locator, coordString);
		} catch (Exception e) {

			throw new SeleniumException("selenium.mouseUpRightAt(\"" + locator
					+ "\",\"" + coordString + "\")");
		}
	}

	@Override
	public void mouseMove(String locator) throws SeleniumException {
		log("selenium.mouseMove(\"" + locator + "\");");
		try {
			super.mouseMove(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.mouseMove(\"" + locator
					+ "\")");
		}
	}

	@Override
	public void mouseMoveAt(String locator, String coordString)
			throws SeleniumException {
		log("selenium.mouseMoveAt(\"" + locator + "\",\"" + coordString
				+ "\");");
		try {
			super.mouseMoveAt(locator, coordString);
		} catch (Exception e) {

			throw new SeleniumException("selenium.mouseMoveAt(\"" + locator
					+ "\",\"" + coordString + "\")");
		}
	}

	@Override
	public void type(String locator, String value) throws SeleniumException {
		log("selenium.type(\"" + locator + "\",\"" + value + "\");");
		try {
			super.type(locator, value);
		} catch (Exception e) {

			throw new SeleniumException("selenium.type(\"" + locator + "\",\""
					+ value + "\")");
		}
	}

	@Override
	public void typeKeys(String locator, String value) throws SeleniumException {
		log("selenium.typeKeys(\"" + locator + "\",\"" + value + "\");");
		try {
			super.typeKeys(locator, value);
		} catch (Exception e) {

			throw new SeleniumException("selenium.typeKeys(\"" + locator
					+ "\",\"" + value + "\")");
		}
	}

	@Override
	public void setSpeed(String value) throws SeleniumException {
		log("selenium.setSpeed(\"" + value + "\");");
		try {
			/**
			 * Deprecated in the new selenium hence we will use what webdriver
			 * uses.
			 */
			// super.setSpeed(value);
			WebDriver driver = ((WrapsDriver) selenium).getWrappedDriver();
			long timeOut = Long.valueOf(value);
			driver.manage().timeouts()
					.implicitlyWait(timeOut, TimeUnit.MILLISECONDS);

		} catch (Exception e) {

			throw new SeleniumException("selenium.setSpeed(\"" + value + "\")");
		}
	}

	@Override
	public String getSpeed() throws SeleniumException {
		log("selenium.getSpeed();");
		String ret = "FAILED";
		try {
			ret = super.getSpeed();
		} catch (Exception e) {

			throw new SeleniumException("selenium.getSpeed()");
		}
		return ret;
	}

	@Override
	public void check(String locator) throws SeleniumException {
		log("selenium.check(\"" + locator + "\");");
		try {
			super.check(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.check(\"" + locator + "\")");
		}
	}

	@Override
	public void uncheck(String locator) throws SeleniumException {
		log("selenium.uncheck(\"" + locator + "\");");
		try {
			super.uncheck(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.uncheck(\"" + locator + "\")");
		}
	}

	@Override
	public void select(String selectLocator, String optionLocator)
			throws SeleniumException {
		log("selenium.select(\"" + selectLocator + "\",\"" + optionLocator
				+ "\");");
		try {
			super.select(selectLocator, optionLocator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.select(\"" + selectLocator
					+ "\",\"" + optionLocator + "\")");
		}
	}

	@Override
	public void addSelection(String locator, String optionLocator)
			throws SeleniumException {
		log("selenium.addSelection(\"" + locator + "\",\"" + optionLocator
				+ "\");");
		try {
			super.addSelection(locator, optionLocator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.addSelection(\"" + locator
					+ "\",\"" + optionLocator + "\")");
		}
	}

	@Override
	public void removeSelection(String locator, String optionLocator)
			throws SeleniumException {
		log("selenium.removeSelection(\"" + locator + "\",\"" + optionLocator
				+ "\");");
		try {
			super.removeSelection(locator, optionLocator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.removeSelection(\"" + locator
					+ "\",\"" + optionLocator + "\")");
		}
	}

	@Override
	public void removeAllSelections(String locator) throws SeleniumException {
		log("selenium.removeAllSelections(\"" + locator + "\");");
		try {
			super.removeAllSelections(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.removeAllSelections(\""
					+ locator + "\")");
		}
	}

	@Override
	public void submit(String formLocator) throws SeleniumException {
		log("selenium.submit(\"" + formLocator + "\");");
		try {
			super.submit(formLocator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.submit(\"" + formLocator
					+ "\")");
		}
	}

	@Override
	public void open(String url) throws SeleniumException {
		log("selenium.open(\"" + url + "\");");
		try {
			super.open(url);
		} catch (Exception e) {

			throw new SeleniumException("selenium.open(\"" + url + "\")");
		}
	}

	@Override
	public void openWindow(String url, String windowID)
			throws SeleniumException {
		log("selenium.openWindow(\"" + url + "\",\"" + windowID + "\");");
		try {
			super.openWindow(url, windowID);
		} catch (Exception e) {

			throw new SeleniumException("selenium.openWindow(\"" + url
					+ "\",\"" + windowID + "\")");
		}
	}

	@Override
	public void selectWindow(String windowID) throws SeleniumException {
		log("selenium.selectWindow(\"" + windowID + "\");");
		try {
			super.selectWindow(windowID);
		} catch (Exception e) {

			throw new SeleniumException("selenium.selectWindow(\"" + windowID
					+ "\")");
		}
	}

	@Override
	public void selectPopUp(String windowID) throws SeleniumException {
		log("selenium.selectPopUp(\"" + windowID + "\");");
		try {
			super.selectPopUp(windowID);
		} catch (Exception e) {

			throw new SeleniumException("selenium.selectPopUp(\"" + windowID
					+ "\")");
		}
	}

	@Override
	public void deselectPopUp() throws SeleniumException {
		log("selenium.deselectPopUp();");
		try {
			super.deselectPopUp();
		} catch (Exception e) {

			throw new SeleniumException("selenium.deselectPopUp()");
		}
	}

	@Override
	public void selectFrame(String locator) throws SeleniumException {
		log("selenium.selectFrame(\"" + locator + "\");");
		try {
			super.selectFrame(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.selectFrame(\"" + locator
					+ "\")");
		}
	}

	@Override
	public boolean getWhetherThisFrameMatchFrameExpression(
			String currentFrameString, String target) {
		boolean ret = super.getWhetherThisFrameMatchFrameExpression(
				currentFrameString, target);
		log("selenium.getWhetherThisFrameMatchFrameExpression(\""
				+ currentFrameString + "\",\"" + target + "\")", ret);
		return ret;
	}

	@Override
	public boolean getWhetherThisWindowMatchWindowExpression(
			String currentWindowString, String target) {
		boolean ret = super.getWhetherThisWindowMatchWindowExpression(
				currentWindowString, target);
		log("selenium.getWhetherThisWindowMatchWindowExpression(\""
				+ currentWindowString + "\",\"" + target + "\")", ret);
		return ret;
	}

	@Override
	public void waitForPopUp(String windowID, String timeout)
			throws SeleniumException {
		log("selenium.waitForPopUp(\"" + windowID + "\",\"" + timeout + "\");");
		try {
			super.waitForPopUp(windowID, timeout);
		} catch (Exception e) {

			throw new SeleniumException("selenium.waitForPopUp(\"" + windowID
					+ "\",\"" + timeout + "\")");
		}
	}

	@Override
	public void chooseCancelOnNextConfirmation() throws SeleniumException {
		log("selenium.chooseCancelOnNextConfirmation();");
		try {
			super.chooseCancelOnNextConfirmation();
		} catch (Exception e) {

			throw new SeleniumException(
					"selenium.chooseCancelOnNextConfirmation()");
		}
	}

	@Override
	public void chooseOkOnNextConfirmation() throws SeleniumException {
		log("selenium.chooseOkOnNextConfirmation();");
		try {
			super.chooseOkOnNextConfirmation();
		} catch (Exception e) {

			throw new SeleniumException("selenium.chooseOkOnNextConfirmation()");
		}
	}

	@Override
	public void answerOnNextPrompt(String answer) throws SeleniumException {
		log("selenium.answerOnNextPrompt(\"" + answer + "\");");
		try {
			super.answerOnNextPrompt(answer);
		} catch (Exception e) {

			throw new SeleniumException("selenium.answerOnNextPrompt(\""
					+ answer + "\")");
		}
	}

	@Override
	public void goBack() throws SeleniumException {
		log("selenium.goBack();");
		try {
			super.goBack();
		} catch (Exception e) {

			throw new SeleniumException("selenium.goBack()");
		}
	}

	@Override
	public void refresh() throws SeleniumException {
		log("selenium.refresh();");
		try {
			super.refresh();
		} catch (Exception e) {

			throw new SeleniumException("selenium.refresh()");
		}
	}

	@Override
	public void close() throws SeleniumException {
		log("selenium.close();");
		try {
			super.close();
		} catch (Exception e) {

			throw new SeleniumException("selenium.close()");
		}
	}

	@Override
	public boolean isAlertPresent() throws SeleniumException {
		boolean ret = super.isAlertPresent();
		log("selenium.isAlertPresent()", ret);
		return ret;
	}

	@Override
	public boolean isPromptPresent() throws SeleniumException {
		boolean ret = super.isPromptPresent();
		log("selenium.isPromptPresent()", ret);
		return ret;
	}

	@Override
	public boolean isConfirmationPresent() throws SeleniumException {
		boolean ret = super.isConfirmationPresent();
		log("selenium.isConfirmationPresent()", ret);
		return ret;
	}

	@Override
	public String getAlert() throws SeleniumException {
		log("selenium.getAlert();");
		String ret = "FAILED";
		try {
			ret = super.getAlert();
		} catch (Exception e) {

			throw new SeleniumException("selenium.getAlert()");
		}
		return ret;
	}

	@Override
	public String getConfirmation() throws SeleniumException {
		log("selenium.getConfirmation();");
		String ret = "FAILED";
		try {
			ret = super.getConfirmation();
		} catch (Exception e) {

			throw new SeleniumException("selenium.getConfirmation()");
		}
		return ret;
	}

	@Override
	public String getPrompt() throws SeleniumException {
		log("selenium.getPrompt();");
		String ret = "FAILED";
		try {
			ret = super.getPrompt();
		} catch (Exception e) {

			throw new SeleniumException("selenium.getPrompt()");
		}
		return ret;
	}

	@Override
	public String getLocation() throws SeleniumException {
		log("selenium.getLocation();");
		String ret = "FAILED";
		try {
			ret = super.getLocation();
		} catch (Exception e) {

			throw new SeleniumException("selenium.getLocation()");
		}
		return ret;
	}

	@Override
	public String getTitle() throws SeleniumException {
		log("selenium.getTitle();");
		String ret = "FAILED";
		try {
			ret = super.getTitle();
		} catch (Exception e) {

			throw new SeleniumException("selenium.getTitle()");
		}
		return ret;
	}

	@Override
	public String getBodyText() throws SeleniumException {
		log("selenium.getBodyText();");
		String ret = "FAILED";
		try {
			ret = super.getBodyText();
		} catch (Exception e) {

			throw new SeleniumException("selenium.getBodyText()");
		}
		return ret;
	}

	@Override
	public String getValue(String locator) throws SeleniumException {
		log("selenium.getValue(\"" + locator + "\");");
		String ret = "FAILED";
		try {
			ret = super.getValue(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getValue(\"" + locator
					+ "\")");
		}
		return ret;
	}

	@Override
	public String getText(String locator) throws SeleniumException {
		String ret = super.getText(locator);
		// log("getText(\"" + locator.toString() + "\"), Returned: " + ret);
		log("selenium.getText(\"" + locator.toString() + "\")");
		return ret;
	}

	@Override
	public String getEval(String script) throws SeleniumException {
		log("selenium.getEval(\"" + script + "\");");
		String ret = "FAILED";
		try {
			ret = super.getEval(script);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getEval(\"" + script + "\")");
		}
		return ret;
	}

	@Override
	public boolean isChecked(String locator) throws SeleniumException {
		log("selenium.isChecked(\"" + locator + "\");");
		boolean ret = false;
		try {
			ret = super.isChecked(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.isChecked(\"" + locator
					+ "\")");
		}
		return ret;
	}

	@Override
	public String getTable(String tableCellAddress) throws SeleniumException {
		log("selenium.getTable(\"" + tableCellAddress + "\");");
		String ret = "FAILED";
		try {
			ret = super.getTable(tableCellAddress);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getTable(\"\""
					+ tableCellAddress + "\"\")");
		}
		return ret;
	}

	@Override
	public String[] getSelectedLabels(String selectLocator)
			throws SeleniumException {
		log("selenium.getSelectedLabels(\"" + selectLocator + "\");");
		String ret[] = { "FAILED" };
		try {
			ret = super.getSelectedLabels(selectLocator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getSelectedLabels(\""
					+ selectLocator + "\")");
		}
		return ret;
	}

	@Override
	public String getSelectedLabel(String selectLocator)
			throws SeleniumException {
		log("selenium.getSelectedLabel(\"" + selectLocator + "\");");
		String ret = "FAILED";
		try {
			ret = super.getSelectedLabel(selectLocator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getSelectedLabel(\""
					+ selectLocator + "\")");
		}
		return ret;
	}

	@Override
	public String[] getSelectedValues(String selectLocator)
			throws SeleniumException {
		log("selenium.getSelectedValues(\"" + selectLocator + "\");");
		String ret[] = { "FAILED" };
		try {
			ret = super.getSelectedValues(selectLocator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getSelectedValues(\""
					+ selectLocator + "\")");
		}
		return ret;
	}

	@Override
	public String getSelectedValue(String selectLocator)
			throws SeleniumException {
		log("selenium.getSelectedValue(\"" + selectLocator + "\");");
		String ret = "FAILED";
		try {
			ret = super.getSelectedValue(selectLocator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getSelectedValue(\""
					+ selectLocator + "\")");
		}
		return ret;
	}

	@Override
	public String[] getSelectedIndexes(String selectLocator)
			throws SeleniumException {
		log("selenium.getSelectedIndexes(\"" + selectLocator + "\");");
		String ret[] = { "FAILED" };
		try {
			ret = super.getSelectedIndexes(selectLocator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getSelectedIndexes(\""
					+ selectLocator + "\")");
		}
		return ret;
	}

	@Override
	public String getSelectedIndex(String selectLocator)
			throws SeleniumException {
		log("selenium.getSelectedIndex(\"" + selectLocator + "\");");
		String ret = "FAILED";
		try {
			ret = super.getSelectedIndex(selectLocator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getSelectedIndex(\""
					+ selectLocator + "\")");
		}
		return ret;
	}

	@Override
	public String[] getSelectedIds(String selectLocator)
			throws SeleniumException {
		log("selenium.getSelectedIds(\"" + selectLocator + "\");");
		String ret[] = { "FAILED" };
		try {
			ret = super.getSelectedIds(selectLocator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getSelectedIds(\""
					+ selectLocator + "\")");
		}
		return ret;
	}

	@Override
	public String getSelectedId(String selectLocator) throws SeleniumException {
		log("selenium.getSelectedId(\"" + selectLocator + "\");");
		String ret = "FAILED";
		try {
			ret = super.getSelectedId(selectLocator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getSelectedId(\""
					+ selectLocator + "\")");
		}
		return ret;
	}

	@Override
	public boolean isSomethingSelected(String selectLocator)
			throws SeleniumException {
		log("selenium.isSomethingSelected(\"" + selectLocator + "\");");
		boolean ret = false;
		try {
			ret = super.isSomethingSelected(selectLocator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.isSomethingSelected(\""
					+ selectLocator + "\")");
		}
		return ret;
	}

	@Override
	public String[] getSelectOptions(String selectLocator)
			throws SeleniumException {
		log("selenium.getSelectOptions(\"" + selectLocator + "\");");
		String ret[] = { "FAILED" };
		try {
			ret = super.getSelectOptions(selectLocator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getSelectOptions(\""
					+ selectLocator + "\")");
		}
		return ret;
	}

	@Override
	public String getAttribute(String attributeLocator)
			throws SeleniumException {
		log("selenium.getAttribute(\"" + attributeLocator + "\");");
		String ret = "FAILED";
		try {
			ret = super.getAttribute(attributeLocator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getAttribute(\""
					+ attributeLocator + "\")");
		}
		return ret;
	}

	@Override
	public boolean isTextPresent(String pattern) throws SeleniumException {
		log("selenium.isTextPresent(\"" + pattern + "\");");
		boolean ret = false;
		try {
			/**
			 * Deprecated as webdriver can be used to do this more efficently
			 */
			// ret = super.isTextPresent(pattern);
			WebDriver driver = ((WrapsDriver) selenium).getWrappedDriver();
			WebElement bodyTag = driver.findElement(By.tagName("body"));
			if (bodyTag.getText().contains(pattern)) {
				ret = true;
			}
		} catch (Exception e) {

			throw new SeleniumException("selenium.isTextPresent(\"" + pattern
					+ "\")");
		}
		return ret;
	}

	@Override
	public boolean isElementPresent(String locator) throws SeleniumException {
		log("selenium.isElementPresent(\"" + locator + "\");");
		boolean ret = false;
		try {
			ret = super.isElementPresent(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.isElementPresent(\""
					+ locator + "\")");
		}
		return ret;
	}

	@Override
	public boolean isVisible(String locator) throws SeleniumException {
		log("selenium.isVisible(\"" + locator + "\");");
		boolean ret = false;
		try {
			ret = super.isVisible(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.isVisible(\"" + locator
					+ "\")");
		}
		return ret;
	}

	@Override
	public boolean isEditable(String locator) throws SeleniumException {
		log("selenium.isEditable(\"" + locator + "\");");
		boolean ret = false;
		try {
			ret = super.isEditable(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.isEditable(\"" + locator
					+ "\")");
		}
		return ret;
	}

	@Override
	public String[] getAllButtons() throws SeleniumException {
		log("selenium.getAllButtons();");
		String ret[] = { "FAILED" };
		try {
			ret = super.getAllButtons();
		} catch (Exception e) {

			throw new SeleniumException("selenium.getAllButtons()");
		}
		return ret;
	}

	@Override
	public String[] getAllLinks() throws SeleniumException {
		log("selenium.getAllLinks();");
		String ret[] = { "FAILED" };
		try {
			ret = super.getAllLinks();
		} catch (Exception e) {

			throw new SeleniumException("selenium.getAllLinks()");
		}
		return ret;
	}

	@Override
	public String[] getAllFields() throws SeleniumException {
		log("selenium.getAllFields();");
		String ret[] = { "FAILED" };
		try {
			ret = super.getAllFields();
		} catch (Exception e) {

			throw new SeleniumException("selenium.getAllFields()");
		}
		return ret;
	}

	@Override
	public String[] getAttributeFromAllWindows(String attributeName)
			throws SeleniumException {
		log("selenium.getAttributeFromAllWindows(\"" + attributeName + "\");");
		String ret[] = { "FAILED" };
		try {
			ret = super.getAttributeFromAllWindows(attributeName);
		} catch (Exception e) {

			throw new SeleniumException(
					"selenium.getAttributeFromAllWindows(\"" + attributeName
							+ "\")");
		}
		return ret;
	}

	@Override
	public void dragdrop(String locator, String movementsString)
			throws SeleniumException {
		log("selenium.dragdrop(\"" + locator + "\",\"" + movementsString
				+ "\");");
		try {
			super.dragdrop(locator, movementsString);
		} catch (Exception e) {

			throw new SeleniumException("selenium.dragdrop(\"" + locator
					+ "\",\"" + movementsString + "\")");
		}
	}

	@Override
	public void setMouseSpeed(String pixels) throws SeleniumException {
		log("selenium.setMouseSpeed(\"" + pixels + "\");");
		try {
			super.setMouseSpeed(pixels);
		} catch (Exception e) {

			throw new SeleniumException("selenium.setMouseSpeed(\"" + pixels
					+ "\")");
		}
	}

	@Override
	public Number getMouseSpeed() throws SeleniumException {
		log("selenium.getMouseSpeed();");
		Number ret = -1;
		try {
			ret = super.getMouseSpeed();
		} catch (Exception e) {

			throw new SeleniumException("selenium.getMouseSpeed()");
		}
		return ret;
	}

	@Override
	public void dragAndDrop(String locator, String movementsString)
			throws SeleniumException {

		log("selenium.dragAndDrop(\"" + locator + "\",\"" + movementsString
				+ "\");");
		try {
			super.dragAndDrop(locator, movementsString);
		} catch (Exception e) {

			throw new SeleniumException("selenium.dragAndDrop(\"" + locator
					+ "\",\"" + movementsString + "\")");
		}
	}

	@Override
	public void dragAndDropToObject(String locatorOfObjectToBeDragged,
			String locatorOfDragDestinationObject) {
		log("selenium.dragAndDropToObject(\"" + locatorOfObjectToBeDragged
				+ "\",\"" + locatorOfDragDestinationObject + "\");");
		try {
			super.dragAndDropToObject(locatorOfObjectToBeDragged,
					locatorOfDragDestinationObject);
		} catch (Exception e) {

			throw new SeleniumException("selenium.dragAndDropToObject(\""
					+ locatorOfObjectToBeDragged + "\",\""
					+ locatorOfDragDestinationObject + "\")");
		}
	}

	@Override
	public void windowFocus() throws SeleniumException {
		log("selenium.windowFocus();");
		try {
			super.windowFocus();
		} catch (Exception e) {

			throw new SeleniumException("selenium.windowFocus()");
		}
	}

	@Override
	public void windowMaximize() throws SeleniumException {
		log("selenium.windowMaximize();");
		try {
			super.windowMaximize();
		} catch (Exception e) {

			throw new SeleniumException("selenium.windowMaximize()");
		}
	}

	@Override
	public String[] getAllWindowIds() throws SeleniumException {
		log("selenium.getAllWindowIds();");
		String ret[] = { "FAILED" };
		try {
			ret = super.getAllWindowIds();
		} catch (Exception e) {

			throw new SeleniumException("selenium.getAllWindowIds()");
		}
		return ret;
	}

	@Override
	public String[] getAllWindowNames() throws SeleniumException {
		log("selenium.getAllWindowNames();");
		String ret[] = { "FAILED" };
		try {
			ret = super.getAllWindowNames();
		} catch (Exception e) {

			throw new SeleniumException("selenium.getAllWindowNames()");
		}
		return ret;
	}

	@Override
	public String[] getAllWindowTitles() throws SeleniumException {
		log("selenium.getAllWindowTitles();");
		String ret[] = { "FAILED" };
		try {
			ret = super.getAllWindowTitles();
		} catch (Exception e) {

			throw new SeleniumException("selenium.getAllWindowTitles()");
		}
		return ret;
	}

	@Override
	public String getHtmlSource() throws SeleniumException {
		log("selenium.getHtmlSource();");
		String ret = "FAILED";
		try {
			ret = super.getHtmlSource();
		} catch (Exception e) {

			throw new SeleniumException("selenium.getHtmlSource()");
		}
		return ret;
	}

	@Override
	public void setCursorPosition(String locator, String position)
			throws SeleniumException {
		log("selenium.setCursorPosition(\"" + locator + "\",\"" + position
				+ "\");");
		try {
			super.setCursorPosition(locator, position);
		} catch (Exception e) {

			throw new SeleniumException("selenium.setCursorPosition(\""
					+ locator + "\",\"" + position + "\")");
		}
	}

	@Override
	public Number getElementIndex(String locator) throws SeleniumException {
		log("selenium.getElementIndex(\"" + locator + "\");");
		Number ret = -1;
		try {
			ret = super.getElementIndex(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getElementIndex(\"" + locator
					+ "\")");
		}
		return ret;
	}

	@Override
	public boolean isOrdered(String locator1, String locator2)
			throws SeleniumException {
		log("selenium.isOrdered(\"" + locator1 + "\",\"" + locator2 + "\");");
		boolean ret = false;
		try {
			ret = super.isOrdered(locator1, locator2);
		} catch (Exception e) {

			throw new SeleniumException("selenium.isOrdered(\"" + locator1
					+ "\",\"" + locator2 + "\")");
		}
		return ret;
	}

	@Override
	public Number getElementPositionLeft(String locator)
			throws SeleniumException {
		log("selenium.getElementPositionLeft(\"" + locator + "\");");
		Number ret = -1;
		try {
			ret = super.getElementPositionLeft(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getElementPositionLeft(\""
					+ locator + "\")");
		}
		return ret;
	}

	@Override
	public Number getElementPositionTop(String locator)
			throws SeleniumException {
		log("selenium.getElementPositionTop(\"" + locator + "\");");
		Number ret = -1;
		try {
			ret = super.getElementPositionTop(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getElementPositionTop(\""
					+ locator + "\")");
		}
		return ret;
	}

	@Override
	public Number getElementWidth(String locator) throws SeleniumException {
		log("selenium.getElementWidth(\"" + locator + "\");");
		Number ret = -1;
		try {
			ret = super.getElementWidth(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getElementWidth(\"" + locator
					+ "\")");
		}
		return ret;
	}

	@Override
	public Number getElementHeight(String locator) throws SeleniumException {
		log("selenium.getElementHeight(\"" + locator + "\");");
		Number ret = -1;
		try {
			ret = super.getElementHeight(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getElementHeight(\""
					+ locator + "\")");
		}
		return ret;
	}

	@Override
	public Number getCursorPosition(String locator) throws SeleniumException {
		log("selenium.getCursorPosition(\"" + locator + "\");");
		Number ret = -1;
		try {
			ret = super.getCursorPosition(locator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getCursorPosition(\""
					+ locator + "\")");
		}

		return ret;
	}

	@Override
	public String getExpression(String expression) throws SeleniumException {
		log("selenium.getExpression(\"" + expression + "\");");
		String ret = "FAILED";
		try {
			ret = super.getExpression(expression);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getExpression(\""
					+ expression + "\")");
		}
		return ret;
	}

	@Override
	public Number getXpathCount(String xpath) throws SeleniumException {
		log("selenium.getXpathCount(\"" + xpath + "\");");
		Number ret = -1;
		try {
			ret = super.getXpathCount(xpath);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getXpathCount(\"" + xpath
					+ "\")");
		}
		return ret;
	}

	@Override
	public void assignId(String locator, String identifier)
			throws SeleniumException {
		log("selenium.assignId(\"" + locator + "\",\"" + identifier + "\");");
		try {
			super.assignId(locator, identifier);
		} catch (Exception e) {

			throw new SeleniumException("selenium.assignId(\"" + locator
					+ "\",\"" + identifier + "\")");
		}
	}

	@Override
	public void allowNativeXpath(String allow) throws SeleniumException {
		log("selenium.allowNativeXpath(\"" + allow + "\");");
		try {
			super.allowNativeXpath(allow);
		} catch (Exception e) {

			throw new SeleniumException("selenium.allowNativeXpath(\"" + allow
					+ "\")");
		}
	}

	@Override
	public void ignoreAttributesWithoutValue(String ignore)
			throws SeleniumException {
		log("selenium.ignoreAttributesWithoutValue(\"" + ignore + "\");");
		try {
			super.ignoreAttributesWithoutValue(ignore);
		} catch (Exception e) {

			throw new SeleniumException(
					"selenium.ignoreAttributesWithoutValue(\"" + ignore + "\")");
		}
	}

	@Override
	public void waitForCondition(String script, String timeout)
			throws SeleniumException {
		log("selenium.waitForCondition(\"" + script + "\",\"" + timeout
				+ "\");");
		try {
			super.waitForCondition(script, timeout);
		} catch (Exception e) {

			throw new SeleniumException("selenium.waitForCondition(\"" + script
					+ "\",\"" + timeout + "\")");
		}
	}

	@Override
	public void setTimeout(String timeout) throws SeleniumException {
		log("selenium.setTimeout(\"" + timeout + "\");");
		try {
			super.setTimeout(timeout);
		} catch (Exception e) {

			throw new SeleniumException("selenium.setTimeout(\"" + timeout
					+ "\")");
		}
	}

	@Override
	public void waitForPageToLoad(String timeout) throws SeleniumException {
		log("selenium.waitForPageToLoad(\"" + timeout + "\");");
		try {
			super.waitForPageToLoad(timeout);
		} catch (Exception e) {

			throw new SeleniumException("selenium.waitForPageToLoad(\""
					+ timeout + "\")");
		}
	}

	@Override
	public void waitForFrameToLoad(String frameAddress, String timeout)
			throws SeleniumException {
		log("selenium.waitForFrameToLoad(\"" + frameAddress + "\",\"" + timeout
				+ "\");");
		try {
			super.waitForFrameToLoad(frameAddress, timeout);
		} catch (Exception e) {

			throw new SeleniumException("selenium.waitForFrameToLoad(\""
					+ frameAddress + "\",\"" + timeout + "\")");
		}
	}

	@Override
	public String getCookie() throws SeleniumException {
		log("selenium.getCookie();");
		String ret = "FAILED";
		try {
			ret = super.getCookie();
		} catch (Exception e) {

			throw new SeleniumException("selenium.getCookie()");
		}
		return ret;
	}

	@Override
	public String getCookieByName(String name) throws SeleniumException {
		log("selenium.getCookieByName(\"" + name + "\");");
		String ret = "FAILED";
		try {
			ret = super.getCookieByName(name);
		} catch (Exception e) {

			throw new SeleniumException("selenium.getCookieByName(\"" + name
					+ "\")");
		}

		return ret;
	}

	@Override
	public boolean isCookiePresent(String name) throws SeleniumException {
		log("selenium.isCookiePresent(\"" + name + "\");");
		boolean ret = false;
		try {
			ret = super.isCookiePresent(name);
		} catch (Exception e) {

			throw new SeleniumException("selenium.isCookiePresent(\"" + name
					+ "\")");
		}

		return ret;
	}

	@Override
	public void createCookie(String nameValuePair, String optionsString)
			throws SeleniumException {
		log("selenium.createCookie(\"" + nameValuePair + "\",\""
				+ optionsString + "\");");
		try {
			super.createCookie(nameValuePair, optionsString);
		} catch (Exception e) {

			throw new SeleniumException("selenium.createCookie(\""
					+ nameValuePair + "\",\"" + optionsString + "\")");
		}
	}

	@Override
	public void deleteCookie(String name, String optionsString)
			throws SeleniumException {
		log("selenium.deleteCookie(\"" + name + "\",\"" + optionsString
				+ "\");");
		try {
			super.deleteCookie(name, optionsString);
		} catch (Exception e) {

			throw new SeleniumException("selenium.deleteCookie(\"" + name
					+ "\",\"" + optionsString + "\")");
		}
	}

	@Override
	public void deleteAllVisibleCookies() throws SeleniumException {
		log("selenium.deleteAllVisibleCookies();");
		try {
			super.deleteAllVisibleCookies();
		} catch (Exception e) {

			throw new SeleniumException("selenium.deleteAllVisibleCookies()");
		}
	}

	@Override
	public void setBrowserLogLevel(String logLevel) throws SeleniumException {
		log("selenium.setBrowserLogLevel(\"" + logLevel + "\");");
		super.setBrowserLogLevel(logLevel);
	}

	@Override
	public void runScript(String script) throws SeleniumException {
		log("selenium.runScript(\"" + script + "\");");
		try {
			super.runScript(script);
		} catch (Exception e) {

			throw new SeleniumException("selenium.runScript(\"" + script
					+ "\")");
		}
	}

	@Override
	public void addLocationStrategy(String strategyName,
			String functionDefinition) {
		log("selenium.addLocationStrategy(\"" + strategyName + "\",\""
				+ functionDefinition + "\");");
		try {
			super.addLocationStrategy(strategyName, functionDefinition);
		} catch (Exception e) {

			throw new SeleniumException("selenium.addLocationStrategy(\""
					+ strategyName + "\",\"" + functionDefinition + "\")");
		}
	}

	@Override
	public void captureEntirePageScreenshot(String filename, String kwargs)
			throws SeleniumException {
		log("selenium.captureEntirePageScreenshot(\"" + filename + "\",\""
				+ kwargs + "\");");
		try {
			super.captureEntirePageScreenshot(filename, kwargs);
		} catch (Exception e) {

			throw new SeleniumException(
					"selenium.captureEntirePageScreenshot(\"" + filename
							+ "\",\"" + kwargs + "\")");
		}
	}

	@Override
	public void rollup(String rollupName, String kwargs)
			throws SeleniumException {
		log("selenium.rollup(\"" + rollupName + "\",\"" + kwargs + "\");");
		try {
			super.rollup(rollupName, kwargs);
		} catch (Exception e) {

			throw new SeleniumException("selenium.rollup(\"" + rollupName
					+ "\",\"" + kwargs + "\")");
		}
	}

	@Override
	public void addScript(String scriptContent, String scriptTagId)
			throws SeleniumException {
		log("selenium.addScript(\"" + scriptContent + "\",\"" + scriptTagId
				+ "\");");
		try {
			super.addScript(scriptContent, scriptTagId);
		} catch (Exception e) {

			throw new SeleniumException("selenium.addScript(\"" + scriptContent
					+ "\",\"" + scriptTagId + "\")");
		}
	}

	@Override
	public void removeScript(String scriptTagId) throws SeleniumException {
		log("selenium.removeScript(\"" + scriptTagId + "\");");
		try {
			super.removeScript(scriptTagId);
		} catch (Exception e) {

			throw new SeleniumException("selenium.removeScript(\""
					+ scriptTagId + "\")");
		}
	}

	@Override
	public void useXpathLibrary(String libraryName) throws SeleniumException {
		log("selenium.useXpathLibrary(\"" + libraryName + "\");");
		try {
			super.useXpathLibrary(libraryName);
		} catch (Exception e) {

			throw new SeleniumException("selenium.useXpathLibrary(\""
					+ libraryName + "\")");
		}
	}

	@Override
	public void setContext(String context) throws SeleniumException {
		log("selenium.setContext(\"" + context + "\");");
		try {
			super.setContext(context);
		} catch (Exception e) {

			throw new SeleniumException("selenium.setContext(\"" + context
					+ "\")");
		}
	}

	@Override
	public void attachFile(String fieldLocator, String fileLocator)
			throws SeleniumException {
		log("selenium.attachFile(\"" + fieldLocator + "\",\"" + fileLocator
				+ "\");");
		try {
			super.attachFile(fieldLocator, fileLocator);
		} catch (Exception e) {

			throw new SeleniumException("selenium.attachFile(\"" + fieldLocator
					+ "\",\"" + fileLocator + "\")");
		}
	}

	@Override
	public void captureScreenshot(String filename) throws SeleniumException {
		log("selenium.captureScreenshot(\"" + filename + "\");");
		try {
			super.captureScreenshot(filename);
		} catch (Exception e) {

			throw new SeleniumException("selenium.captureScreenshot(\""
					+ filename + "\")");
		}
	}

	@Override
	public String captureScreenshotToString() throws SeleniumException {

		log("selenium.captureScreenshotToString();");
		String ret = "FAILED";
		try {
			ret = super.captureScreenshotToString();
		} catch (Exception e) {
			ret = e.getCause().getMessage();
			throw new SeleniumException("selenium.captureScreenshotToString()");
		}
		return ret;
	}

	@Override
	public String captureNetworkTraffic(String type) throws SeleniumException {
		log("selenium.captureNetworkTraffic(\"" + type + "\");");
		String ret = "FAILED";
		try {
			ret = super.captureScreenshotToString();
		} catch (Exception e) {
			ret = super.captureNetworkTraffic(type);
			throw new SeleniumException("selenium.captureNetworkTraffic(\""
					+ type + "\")");
		}
		return ret;
	}

	@Override
	public void addCustomRequestHeader(String key, String value)
			throws SeleniumException {
		log("selenium.addCustomRequestHeader(\"" + key + "\",\"" + value
				+ "\");");
		try {
			super.addCustomRequestHeader(key, value);
		} catch (Exception e) {

			throw new SeleniumException("selenium.addCustomRequestHeader(\""
					+ key + "\",\"" + value + "\")");
		}
	}

	@Override
	public String captureEntirePageScreenshotToString(String kwargs)
			throws SeleniumException {
		log("selenium.captureEntirePageScreenshotToString(\"" + kwargs + "\");");
		String ret = "FAILED";
		try {
			ret = super.captureEntirePageScreenshotToString(kwargs);
		} catch (Exception e) {
			ret = e.getCause().getMessage();
			throw new SeleniumException(
					"selenium.captureEntirePageScreenshotToString(\"" + kwargs
							+ "\")");
		}
		return ret;
	}

	@Override
	public void shutDownSeleniumServer() throws SeleniumException {
		log("selenium.shutDownSeleniumServer();");
		try {
			super.shutDownSeleniumServer();
		} catch (Exception e) {

			throw new SeleniumException("selenium.shutDownSeleniumServer()");
		}
	}

	@Override
	public String retrieveLastRemoteControlLogs() throws SeleniumException {
		log("selenium.retrieveLastRemoteControlLogs();");
		return super.retrieveLastRemoteControlLogs();
	}

	@Override
	public void keyDownNative(String keycode) throws SeleniumException {
		log("selenium.keyUpNative(\"" + keycode + "\");");
		try {
			super.keyDownNative(keycode);
		} catch (Exception e) {

			throw new SeleniumException("selenium.keyUpNative(\"" + keycode
					+ "\")");
		}
	}

	@Override
	public void keyUpNative(String keycode) throws SeleniumException {
		log("selenium.keyUpNative(\"" + keycode + "\");");
		try {
			super.keyUpNative(keycode);
		} catch (Exception e) {

			throw new SeleniumException("selenium.keyUpNative(\"" + keycode
					+ "\")");
		}
	}

	@Override
	public void keyPressNative(String keycode) throws SeleniumException {
		log("selenium.keyPressNative(\"" + keycode + "\");");
		try {
			super.keyPressNative(keycode);
		} catch (Exception e) {

			throw new SeleniumException("selenium.keyPressNative(\"" + keycode
					+ "\")");
		}
	}
	
	/**
	 * Check if Html contains the given string
	 */
	public boolean isTextPresentAnywhereOnPage(String query) throws SeleniumException {
		boolean ret = false;
		log("selenium.isTextPresentAnywhereOnPage(\"" + query + "\");");
		try {
			ret = RegexHelper.match(query,this.getHtmlSource());
		} catch (Exception e) {
			throw new SeleniumException("selenium.isTextPresentAnywhereOnPage(\"" + query
					+ "\")");
		}
		return ret;
	}
}