package com.zoxis.qa.automation.seleniumframework.core.commons;

/**
 * Data object to hold data returned inside the {@link com.zoxis.qa.automation.seleniumframework.core.commons.SeleniumResult}
 * 
 * @author JamesBond
 *
 */
public interface SeleniumData {
	/**
	 * Set the data of any type
	 * @param data
	 */
	public void setData(Object data);
	
	/**
	 * Get the data 
	 * Note: you need to know which type of data it holds yourself.
	 * 
	 * @return
	 */
	public Object getData();
}
