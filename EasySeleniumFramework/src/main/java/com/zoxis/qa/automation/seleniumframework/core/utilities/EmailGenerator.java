package com.zoxis.qa.automation.seleniumframework.core.utilities;

/**
 * Utility to generate random email addresses
 * 
 * @author Ravidev Gill (ravi.gill@spindriftgroup.com)
 */
public class EmailGenerator {
	public static final int UUID = 0;
	public static final int TIMESTAMP = 1;
	private String prefix;
	private String suffix;
	private int method = TIMESTAMP;
	
	/**
	 * Get the prefix defined for the email
	 * @return prefix as a  String
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Get the suffix defined for the email
	 * 
	 * @return suffix as a String
	 */
	public String getSuffix() {
		return suffix;
	}
	
	/**
	 * Constructor which takes a Prefix and Suffix for the email generation. 
	 * 
	 * @param prefix
	 * @param suffix
	 */
	public EmailGenerator(String prefix, String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
	}
	
	
	/**
	 * Get a new email address
	 * 
	 * @return
	 */
	public String getNextEmail(){
		switch(method){
			case 0: return (prefix+DataGenerator.getUniqueUUID().toString()+suffix); 
			case 1 : return (prefix+DataGenerator.getTimestamp()+suffix); 
			default : return (prefix+DataGenerator.getTimestamp()+suffix); 
		}
	}
	
	
	/**
	 * Set the method for generating and adding unique data to the email (between prefix and suffix)
	 * 
	 * @param method
	 */
	public void setMethod( int method){
		this.method = method;
	}

}
