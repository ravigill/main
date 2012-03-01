package com.zoxis.qa.automation.seleniumframework.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.annotations.Test;
import com.thoughtworks.selenium.Selenium;
import common.Logger;

public class WebDriverTest {
	private final String baseUrl = "http://google.com";
	private WebDriver driver;
	private Logger logger = Logger.getLogger(WebDriverTest.class);

	@Test
	public void testWebdriverBackedSeleniumWithFirefox() {	
		logger.info("Running testWebdriverBackedSeleniumWithFirefox");
		driver = new FirefoxDriver();
		Selenium selenium = new WebDriverBackedSelenium(driver, baseUrl);
		selenium.open("/");
		driver.close();
	}
	
	@Test
	public void testWebdriverBackedSeleniumWithHtmlUnit() {
		logger.info("Running testWebdriverBackedSeleniumWithHtmlUnit");
		driver = new HtmlUnitDriver();
		Selenium selenium = new WebDriverBackedSelenium(driver, baseUrl);
		selenium.open("/");
		driver.close();
	}
	
	@Test
	public void testHtmlUnitDriver(){
		logger.info("Running testHtmlUnitDriver");
		driver = new HtmlUnitDriver();
		driver.get(baseUrl);
		driver.close();
	}
	
	@Test
	public void testFirefoxDriver(){
		logger.info("Running testFirefoxDriver");
		driver = new FirefoxDriver();
		driver.get(baseUrl);
		driver.close();
	}
}
