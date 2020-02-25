package com.ofbiz.automation.utilities;

import java.util.Random;

public class CommonUtilities {

	private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyz";
	private static final String NUM_LIST = "1234567890";

	private static final int RANDOM_STRING_LENGTH = 6;

	/**
	 * This method generates random string
	 * 
	 * @return
	 */
	public String generateRandomString() {

		StringBuffer randStr = new StringBuffer();
		for (int i = 0; i < RANDOM_STRING_LENGTH; i++) {
			int number = getRandomNumber();
			char ch = CHAR_LIST.charAt(number);
			randStr.append(ch);
		}
		return randStr.toString();
	}

	/**
	 * randomstring with length
	 * 
	 * @param length
	 * @return
	 */
	public static String randomString(int length) {
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		String candidateChars = "abcdefghijklmnopqrstuvwxyz";
		for (int i = 0; i < length; i++) {
			sb.append(candidateChars.charAt(random.nextInt(candidateChars.length())));
		}
		return sb.toString();
	}

	/**
	 * replace smart chars with simple chars
	 * 
	 * @param text
	 * @return
	 */
	public static String replaceSmartChars(String text) {
		return text.replaceAll("[\\u2013]", "-").replaceAll("[\\u2014]", "-").replaceAll("[\\u2015]", "-")
				.replaceAll("[\\u2017]", "_").replaceAll("[\\u2018]", "\'").replaceAll("[\\u2019]", "\'")
				.replaceAll("[\\u201a]", ",").replaceAll("[\\u201b]", "\'").replaceAll("[\\u201c]", "\"")
				.replaceAll("[\\u201d]", "\"").replaceAll("[\\u201e]", "\"").replaceAll("[\\u2026]", "...")
				.replaceAll("[\\u2032]", "\'").replaceAll("[\\u2033]", "\"").replaceAll("[\\u00A0]", " ");
	}

	/**
	 * This method generates random numbers
	 * 
	 * @return int
	 */
	private int getRandomNumber() {
		int randomInt = 0;
		Random randomGenerator = new Random();
		randomInt = randomGenerator.nextInt(NUM_LIST.length());
		if (randomInt - 1 == -1) {
			return randomInt;

		} else {
			return randomInt - 1;
		}
	}

	public static int getRandomNumber(int min, int max) {
		int x = (int) ((Math.random() * ((max - min) + 1)) + min);
		return x;
	}

	/**
	 * This method generates random email
	 * 
	 * @return int
	 */
	public static String randomEmail() {
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(10000);
		return randomString(6) + randomInt + "@yopmail.com";
	}

	/**
	 * This method generates random phone number
	 * 
	 * @return String
	 */
	public static String randomPhone() {
		Random randomGenerator = new Random();
		// int randomCell= randomGenerator.nextInt(10000);
		int randomCell = ((1 + randomGenerator.nextInt(2)) * 10000 + randomGenerator.nextInt(10000));
		String cellnum = ("79393" + randomCell);
		return cellnum;
	}

	/**
	 * This method introduces some wait time
	 * 
	 * @return int
	 */
	public void pause(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}