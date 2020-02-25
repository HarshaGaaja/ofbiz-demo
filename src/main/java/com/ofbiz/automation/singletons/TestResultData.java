package com.ofbiz.automation.singletons;

import com.ofbiz.automation.utilities.TextUtils;
import cucumber.api.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TestResultData {
	Logger logger = LogManager.getLogger(TestResultData.class);

	public long start = 0;
	public long end = 0;
	public long duration;
	public String name = "undefined";
	public Result.Type result;
	public String rid;
	public String browser;
	public String env;
	public String phase = "undefined";
	public String suite = "unknown";
	public String testrail = "n/a";
	public String testrailLink = "n/a";
	public String locale = null;

	public String sprint = "undefined";
	public static String application = "OFBIZ";
	public List<String> tags = new ArrayList<String>();

	public TestResultData() {
		start = System.currentTimeMillis();

	}

	public TestResultData(String id) {
		start = System.currentTimeMillis();
		rid = id;

	}

	public void setupTagFields() {
		String testrailPattern = "[C|T]\\d{7,}";
		for (String s : tags) {
			if (s.startsWith("@")) {
				s = s.replace("@", "");

			}
//			if (s.matches(testrailPattern)) {
//				logger.info("is?" + s.matches(testrailPattern));
//				testrail = s;
//			} else if (s.equalsIgnoreCase("BodGroups")) {
//				s = "BodGroups";
//			} else if (s.contains("Phase")) {
//				phase = s;
//			} else if (s.contains("Sprint")) {
//				sprint = s;
//			} else if (s.contains("Smoke") || s.contains("CriticalRegression") || s.contains("FullRegression")
//					|| s.contains("Frontend")) {
//				suite = s;
//			} else if ((s.toLowerCase().contains("tbb") || s.toLowerCase().contains("byd")
//					|| s.toLowerCase().contains("coo") || s.toLowerCase().contains("shac")
//					|| s.toLowerCase().contains("sse") || s.toLowerCase().contains("e2e"))) {
//				s = s.toUpperCase();
//				application = s;
				// this is for reporting do not comment out -snelson
				if (RuntimeSingleton.getInstance().debugMode) {
					application = "OFBIZ";
					logger.info("You are in DEBUG mo0de application set to OFBIZ <--- " + s);
				}
			}
		}

	protected void cleanData() {
		if (testrail.startsWith("@")) {
			// testrail = testrail.substring(1);
			// removes the first char of the testcase ID which testrail rejects for some
			// reason

			testrail = testrail.substring(1);
		}

//		if (testrail.startsWith("T")) {
//			testrailLink = "<a href=http://beachbody.testrail.net/index.php?/tests/view/" + testrail.substring(1)
//					+ " target=\"_blank\">" + testrail + "</a>";
//		} else if (testrail.startsWith("C")) {
//			testrailLink = "<a href=http://beachbody.testrail.net/index.php?/cases/view/" + testrail.substring(1)
//					+ " target=\"_blank\">" + testrail + "</a>";
//		}
		if (application.startsWith("@")) {
			application = application.substring(1);
		}
		if (suite.startsWith("@")) {
			suite = suite.substring(1);
		}
	}

	@Override
	public String toString() {

		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(this.getClass().getName());
		result.append(" Object {");
		result.append(newLine);

		// determine fields declared in this class only (no fields of superclass)
		Field[] fields = this.getClass().getDeclaredFields();

		// print field names paired with their values
		for (Field field : fields) {
			result.append("  ");
			try {
				result.append(TextUtils.format(field.getName(), field.get(this)));
//				result.append(field.getName());
//				result.append(": ");
//				// requires access to private field:
//				result.append(field.get(this));
			} catch (IllegalAccessException ex) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		result.append("}");

		return result.toString();
	}

	public void addRuntimeDetails(ScenarioTestResultData d) {
		browser = d.browser;
		phase = d.phase;
		sprint = d.sprint;
		suite = d.suite;
		env = d.env;
		locale = d.locale;

		application = d.application;
		testrail = d.testrail;
		rid = d.rid;
	}

	public void checkBrowser() {
		if (!(browser.equals("chrome") || browser.equals("edge") || browser.equals("safari")
				|| browser.equals("iexplorer") || browser.equals("firefox"))) {
			logger.warn("unknown browser: " + browser);
			browser = "chrome";

		}

	}
}
