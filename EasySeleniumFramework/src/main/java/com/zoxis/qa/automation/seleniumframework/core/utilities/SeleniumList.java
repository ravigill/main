package com.zoxis.qa.automation.seleniumframework.core.utilities;

import java.util.ArrayList;

import com.zoxis.qa.automation.seleniumframework.core.utilities.SeleniumDataset;

/**
 * Collection to hold the List dataset.
 * 
 * @author Ravidev Gill (ravi.gill@spindriftgroup.com)
 *
 * @param <T>
 */
public class SeleniumList<T> extends ArrayList<T> implements SeleniumDataset{

	public Object getData() {
		return super.toArray();
	}

}
