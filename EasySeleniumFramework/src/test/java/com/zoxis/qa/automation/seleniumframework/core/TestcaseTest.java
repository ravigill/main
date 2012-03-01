package com.zoxis.qa.automation.seleniumframework.core;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.zoxis.qa.automation.seleniumframework.core.Testcase;

public class TestcaseTest extends Testcase{
	
	@Parameters({"ui_properties","env_properties"})
	@BeforeClass
	public void init(String envProperties){
		super.init(envProperties);
	}

//	@Test
//	public void testBrowserOpenWithGivenBrowser(){
//		selenium.open("");
//		selenium.waitForPageToLoad("30000");
//		selenium.close();
//	}
	
}
