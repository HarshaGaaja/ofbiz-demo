package com.ofbiz.automation.singletons;

import com.ofbiz.automation.utilities.TextUtils;
import cucumber.api.Result.Type;

public class StepTestResultData extends TestResultData {
	public Type result;
	public int line = -1;
	public String scenarioName;
	public String featureName;

	public StepTestResultData(String id) {
		rid = id;
	}

	@Override
	public void addRuntimeDetails(ScenarioTestResultData d) {
		scenarioName = d.name;
		featureName = d.featureName;
		super.addRuntimeDetails(d);
	}

	public void printStep() {
		cleanData();
//		logger.info(TextUtils.center("----- <> <> <> ------"));

		logger.info("RunId@ " + rid);

		logger.info(TextUtils.center("-- step data --"));

		logger.info(TextUtils.center(name));
		logger.info(TextUtils.format("Start", start));
		logger.info(TextUtils.format("End", end));
		logger.info(TextUtils.format("Duration", duration));
		logger.info(TextUtils.format("Application", application));
		logger.info(TextUtils.format("Set", suite));
		logger.info(TextUtils.format("Testrail", testrail));
		logger.info(TextUtils.format("Phase", phase));
		logger.info(TextUtils.format("Sprint", sprint));
		logger.info(TextUtils.format("Result", result));

	}

}