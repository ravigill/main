package com.zoxis.qa.automation.seleniumframework.core.commons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.zoxis.qa.automation.seleniumframework.core.TestcaseTest;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SeleniumTestcase {
	
	public String userStoryId() default "";
	public String description() default "";
	public String userStoryUrl() default "";
	public SeleniumTestcaseGroup group() default SeleniumTestcaseGroup.ALL;
}
