package com.zoxis.qa.automation.seleniumframework.core.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Regex helper class that can be used to validate of a string contains the given pattern or substring.
 * @author Ravidev Gill
 *
 */
public class RegexHelper {
	private Pattern pattern;
	private Matcher matcher;
	
	public void setPattern(String pattern){
		this.pattern = Pattern.compile("?m"+pattern);
	}
	
	public String getMatch(String line) throws Exception{
		this.matcher = pattern.matcher(line);
		String out = "";
		if(matcher.find()){
			out = matcher.group();
		}else{
			throw new Exception("Pattern: "+ pattern.toString()+ "could not be found");
		}
		return out;
	}	
	
	public static boolean match(String regex, String string){
		boolean ret = false;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(string);
		
		if(m.find()){
			ret = true;
		}
		
		return ret;
	}
}
