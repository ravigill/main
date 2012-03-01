package org.openqa.selenium;

import org.openqa.selenium.internal.WrapsDriver;
import com.google.common.base.Supplier;
import com.zoxis.qa.automation.seleniumframework.core.Selenium;

public class WebDriverBackedSelenium extends Selenium implements WrapsDriver {
  public WebDriverBackedSelenium(Supplier<WebDriver> maker, String baseUrl) {
    super(new WebDriverCommandProcessor(baseUrl, maker));
  }

  public WebDriverBackedSelenium(WebDriver baseDriver, String baseUrl) {
    super(new WebDriverCommandProcessor(baseUrl, baseDriver));
  }

  public WebDriver getWrappedDriver() {
    return ((WrapsDriver) commandProcessor).getWrappedDriver();
  }
}