package com.ofbiz.automation.singletons;

import com.ofbiz.automation.utilities.TextUtils;
import cucumber.api.Result.Type;

import java.util.HashMap;
import java.util.Map.Entry;

public class SetTestResultData extends TestResultData {
	public int total = 0;
	public int failed = 0;
	public int passed = 0;
	public int skipped = 0;
	public int undefined = 0;
	public String jenkinsUrl = "undefined";
	public String jenkinsLink = "undefined";

	public String build = "local";
	public String branch = "user";
	public String logLink = null;

	public SetTestResultData(String id) {
		super(id);
		build = System.getProperty("build");
		branch = System.getProperty("branch");
		// jenkinsUrl = System.getProperty(:)
		jenkinsUrl = System.getProperty("build");
		jenkinsLink = "<a href=\"" + jenkinsUrl + "\" target=_new>Build</a>";
		;
		// BUILD_URL
	}

	public void tabulateScenarios(HashMap<String, ScenarioTestResultData> input) {

		for (Entry<String, ScenarioTestResultData> e : input.entrySet()) {
			ScenarioTestResultData data = e.getValue();
			logger.info(e.getKey() + "|scen" + data.name + "|" + data.result);

			if (data.result == Type.FAILED) {
				failed++;
			}

			if (data.result == Type.PASSED) {
				passed++;
			}

			if (data.result == Type.SKIPPED) {
				skipped++;
			}
			if (data.result == Type.UNDEFINED) {
				undefined++;
			}
		}
	}

	public void printSet() {
		// logger.info(TextUtils.center("----- <> <> <> ------"));

		logger.info("RunId@ " + rid);

		logger.info(TextUtils.center("-- set --"));

		cleanData();
		// logger.info(TextUtils.center(name));
//		logger.info(TextUtils.format("runtime", rid));
//		logger.info(TextUtils.format("Start", start));
//		logger.info(TextUtils.format("End", end));
		logger.info(TextUtils.format("duration", duration));

		logger.info(TextUtils.format("Application", application));
//		logger.info(TextUtils.format("Set", suite));
//		logger.info(TextUtils.format("Testrail", testrail));
		logger.info(TextUtils.format("browser", browser));
		logger.info(TextUtils.format("env", env));
		logger.info(TextUtils.format("Total", total));
		logger.info(TextUtils.format("Passed", passed));
		logger.info(TextUtils.format("Failed", failed));
		logger.info(TextUtils.format("Skipped", skipped));
		logger.info(TextUtils.format("Undefined", undefined));
		logger.info(TextUtils.format("log", logLink));
	}

	public void syncTagData() {
		if (application.equals("TBB") && suite.equals("Smoke") && (!(total > 13 && total < 20))) {
			application = "Beachbody";
		}

		if (application.equals("TBB") && suite.equals("CriticalRegression") && total < 30) {
			application = "Beachbody";
		}

		if (application.equals("COO") && suite.equals("Smoke") && total < 26) {
			application = "Beachbody";
		}

		if (application.equals("COO") && suite.equals("CriticalRegression") && total < 26) {
			application = "Beachbody";
		}

		if (!(suite.equals("CriticalRegression") || suite.equals("Smoke") || suite.equals("FullRegression")
				|| suite.equals("Mobile"))) {
			logger.warn("the application / set isn't to spec; hardcording your run in Grafana to something valid");
			logger.warn(application + "|" + suite + "NOW>>> Beachbody Smoke!");
			suite = "Smoke";
			application = "Beachbody";
		}

		if (RuntimeSingleton.getInstance().isMobile == true) {
			suite = "Mobile";
		}
	}
}
