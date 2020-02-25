package com.ofbiz.automation.utilities;

import org.apache.commons.lang3.StringUtils;

public class TextUtils {
	public static String format(String key, Object val) {
		return String.format("%-40s%-40s", key + ": ", val);
	}

	public static String center(String note) {
		int i = 70 - note.length();
		i = i / 2;
		i = (int) Math.floor(i);
		String repeatedLeft = StringUtils.repeat("<", i);
		String repeatedRight = StringUtils.repeat(">", i);
		note = repeatedLeft + "|" + note + "|" + repeatedRight;
		return note;
	}
}
