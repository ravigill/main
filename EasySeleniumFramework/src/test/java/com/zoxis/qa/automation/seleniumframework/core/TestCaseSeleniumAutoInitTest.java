package com.zoxis.qa.automation.seleniumframework.core;

import java.lang.annotation.Annotation;

import org.testng.annotations.Test;

import com.zoxis.qa.automation.seleniumframework.core.Testcase;
import com.zoxis.qa.automation.seleniumframework.core.commons.SeleniumTestcase;
import com.zoxis.qa.automation.seleniumframework.core.commons.SeleniumTestcaseGroup;

import common.Logger;

@SeleniumTestcase(group = SeleniumTestcaseGroup.ALL, userStoryId = "TC-01")
public class TestCaseSeleniumAutoInitTest extends Testcase {
	private final String baseUrl = "http://google.com";
	private Logger logger = Logger
			.getLogger(TestCaseSeleniumAutoInitTest.class);

	@Test(testName = "goToGoole")
	private void goToGoogle() {
		handleAnnotations();
		selenium.open(baseUrl);
		selenium.waitFor(1000);
	}
	
	private void handleAnnotations(){
			Annotation[] annotations = this.getClass().getAnnotations();
			for (Annotation a : annotations) {
				System.out.println(a.toString());
				SeleniumTestcase stc = (SeleniumTestcase) a;
				System.out.println("UserStoryId: " + stc.userStoryId());
			}
	}
}
