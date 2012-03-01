package com.zoxis.qa.automation.seleniumframework.core.utilities;

/**
 * Utility which works as a container for all the propertyfiles, it provides
 * standard interface to access data from any of the containing property files.
 * 
 * @author Ravidev Gill (ravi.gill@spindriftgroup.com)
 */
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import common.Logger;

public class SeleniumProperties {
	private static List<XProperties> properties = new ArrayList<XProperties>();

	/**
	 * Constructor
	 */
	public SeleniumProperties() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param p
	 */
	public SeleniumProperties(XProperties p) {
		properties.add(p);
	}

	/**
	 * Constructor
	 * 
	 * @param propertyFilePath
	 */
	public SeleniumProperties(String propertyFilePath) {
		XProperties p = getProperties(propertyFilePath);
		if (p != null) {
			properties.add(p);
		}
	}

	/**
	 * Try to add a {@link Properties} file from the given filepath to the list
	 * 
	 * @param propertyFilePath
	 * @return
	 */
	public boolean add(String propertyFilePath) {
		boolean ret = false;
		XProperties p = getProperties(propertyFilePath);
		if (p != null) {
			properties.add(p);
			ret = true;
			Logger.getLogger(SeleniumProperties.class).info(SeleniumProperties.class+": Added property file to the Container: "+propertyFilePath);
		}
		return ret;
	}
	
	/**
	 * Add the given {@link Properties} to the list
	 * 
	 * @param e
	 */
	public void add(XProperties e) {
		if (!properties.contains(e)) {
			properties.add(e);
		}
	}

	/**
	 * Remove the given {@link Properties} from the list
	 * 
	 * @param e
	 */
	public void remove(Properties e) {
		if (properties.contains(e)) {
			properties.remove(e);
		}
	}

	/**
	 * Search for the given propertyName in any of the containing {@link Properties} and return its Value
	 * 
	 * @param propertyName
	 * @return propertyValue as a String
	 */
	public String getProperty(String propertyName) {
		String ret = null;
		
		if((ret = System.getProperty(propertyName)) != null){
			return ret;
		}
		
		for (Properties p : properties) {
			String value;
			if ((value = p.getProperty(propertyName)) != null) {
				ret = value;
				break;
			}
		}
		return ret;
	}

	/**
	 * Search for the given propertyName in any of the containing {@link Properties} and return its Value, if not found then return the given Default value
	 * 
	 * @param propertyName
	 * @param propertyValue or defaultValue as a String
	 * @return
	 */
	public String getProperty(String propertyName, String defaultVal) {
		String ret = null;
		ret = getProperty(propertyName);
		if (ret == null) {
			ret = defaultVal;
		}
		return ret;
	}

	/**
	 * Create a object for the given property file, Note: Static method and it doesn't add the property file to the list
	 * @return {@link Properties}
	 */
	public static XProperties getProperties(String propertyFilePath) {
		XProperties props = new XProperties();
		InputStream is = null;
		try {
			//props.load(new FileInputStream(propertyFilePath));
			is = SeleniumProperties.class.getResourceAsStream(propertyFilePath);
			props.load(is);
		} catch (Exception e) {
			Logger.getLogger(SeleniumProperties.class).error("Given Propertyfile couldn't be found: "+propertyFilePath);
			e.printStackTrace();
			return null;
		}
		return props;
	}
}
