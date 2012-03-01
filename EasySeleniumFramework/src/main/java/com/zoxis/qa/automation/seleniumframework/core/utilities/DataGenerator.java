package com.zoxis.qa.automation.seleniumframework.core.utilities;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Utility to generate random Data
 * 
 * @author Ravidev Gill (ravi.gill@spindriftgroup.com)
 * 
 */
public class DataGenerator {

	public static int getRandomNumber() {
		return new Random().nextInt();
	}

	/**
	 * Get a list with the given number of random numbers
	 * 
	 * @param count
	 * @return ArrayList
	 */
	public static ArrayList<Integer> getRandomNumbers(int count) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		Random r = new Random();
		for (int x = 0; x < count; x++) {
			ret.add(r.nextInt());
		}
		return ret;
	}

	/**
	 * Get the cuurent TimeStamp
	 * 
	 * @return timeStamp as a Long
	 */
	public static long getTimestamp() {
		return System.currentTimeMillis();
	}

	/**
	 * Get a Unique UUID
	 * 
	 * @return UUID as a String
	 */
	public static String getUniqueUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Get a random string with the given length
	 * 
	 * @param length
	 * @return random String
	 */
	public static String getRandomString(int length) {
		RandomString rs = new RandomString(length);
		return rs.nextString();
	}

	/**
	 * Get a list of Random Strings with the given length and count.
	 * 
	 * @param length
	 * @param count
	 * @return randomStringList of type ArrayList
	 */
	public static ArrayList<String> getRandomStringList(int length, int count) {
		ArrayList<String> ret = new ArrayList<String>();
		RandomString rs = new RandomString(length);
		for (int x = 0; x < count; x++) {
			ret.add(rs.nextString());
		}
		return ret;
	}

	/**
	 * Get a list of Random Emails with the given length, count, email prefix,
	 * email suffix.
	 * 
	 * @param length
	 * @param count
	 * @param prefix
	 * @param suffix
	 * @return randomStringList of type ArrayList
	 */
	public static ArrayList<String> getRandomEmailList(int length, int count,
			String prefix, String suffix) {
		ArrayList<String> ret = new ArrayList<String>();
		RandomString rs = new RandomString(length);
		for (int x = 0; x < count; x++) {
			ret.add(prefix + rs.nextString() + suffix);
		}
		return ret;
	}
}