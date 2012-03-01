package com.zoxis.qa.automation.seleniumframework.core;
/**
 * 
 * @author Ravidev Gill (ravi.gill@spindriftgroup.com)
 *
 */
public final class AssertionException extends RuntimeException {
	private static final long serialVersionUID = -6909332674786449656L;

	public AssertionException(String cause){
		super(cause);
	}
}
