package com.zoxis.qa.automation.seleniumframework.core.commons;

public class SeleniumStep
{
  private final String title;

  public SeleniumStep(String title)
  {
    this.title = title;
  }

  public String toString() {
    return this.title;
  }
}