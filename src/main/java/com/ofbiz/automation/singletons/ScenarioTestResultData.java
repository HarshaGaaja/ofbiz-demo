package com.ofbiz.automation.singletons;

import com.ofbiz.automation.utilities.TextUtils;
import cucumber.api.Result.Type;

import java.util.List;

public class ScenarioTestResultData extends TestResultData {
	public String failedRed;
	public String passedGreen;
	public int total = 0;
	public int failed = 0;
	public int passed = 0;
	public int skipped = 0;
	public int undefined = 0;
	public Type result;
	public String featureName;
	public byte[] screenshot = null;
	public String sauceHtml = null;
	public String sauceLink = null;

	public ScenarioTestResultData(String id) {
		rid = id;
	}

	public ScenarioTestResultData(String n, String id) {
		name = n;
		rid = id;

	}

	public ScenarioTestResultData() {
		// rid = id;
	}

	public void tabulateSteps(List<StepTestResultData> srs) {
		// logger.info("steps size results\t" + srs.toString());

//		logger.info("steps size results\t" + srs.size());

		for (StepTestResultData data : srs) {
			// logger.info(data.result + "|steps size results\t" + data.toString());

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
		String s = String.valueOf(failed);
		failedRed = "<font color=\"red\">" + s + "</font>";
		s = String.valueOf(failed);
		passedGreen = "<font color=\"green\">" + s + "</font>";
		printScenario();
	}

	public void printScenario() {
		// logger.info(TextUtils.center("----- <> <> <> ------"));

		logger.info("RunId@ " + rid);
		logger.info(TextUtils.center("-- scenario --"));

		cleanData();
		logger.info(TextUtils.center(name));
		logger.info(TextUtils.format("runtime", rid));
		logger.info(TextUtils.format("feature", featureName));
		logger.info(TextUtils.format("Start", start));
		logger.info(TextUtils.format("Application", application));
		logger.info(TextUtils.format("Set", suite));
		logger.info(TextUtils.format("Testrail", testrail));
		logger.info(TextUtils.format("browser", browser));
		logger.info(TextUtils.format("env", env));
		logger.info(TextUtils.format("Steps total", total));
		logger.info(TextUtils.format("Steps passed", passed));
		logger.info(TextUtils.format("failed", failed));
		logger.info(TextUtils.format("skipped", skipped));
		logger.info(TextUtils.format("undefined", undefined));
		logger.info(TextUtils.format("status", result));
		logger.info(TextUtils.format("duration", duration));
	}

	public void setSauce() {
		logger.info("Sauce (" + sauceLink + ")");

		// sauceLink = url;
		sauceHtml = "<html>\n" + "<style>\n" + "\n" + "p.ex1 {\n" + "  padding-top: 250px;\n" + "}\n" + "\n"
				+ "body {\n" + "  display: flex;\n" + "  flex-direction: column;\n" + "  height: 93vh;\n"
				+ "  justify-content: center;\n" + "  align-items: center;\n" + "  background: #222;\n"
				+ "  color: #eee;\n" + "  font-family: \"Dosis\", sans-serif;\n" + "}\n" + "\n" + ".underlined-a {\n"
				+ "  text-decoration: none;\n" + "  color: aqua;\n" + "  padding-bottom: 0.15em;\n"
				+ "  box-sizing: border-box;\n" + "  box-shadow: inset 0 -0.2em 0 aqua;\n" + "  transition: 0.2s;\n"
				+ "  &:hover {\n" + "    color: #222;\n" + "    box-shadow: inset 0 -2em 0 aqua;\n"
				+ "    transition: all 0.45s cubic-bezier(0.86, 0, 0.07, 1);\n" + "  }\n" + "}\n" + "\n"
				+ ".brk-btn {\n" + "  position: relative;\n" + "  background: none;\n" + "  color: aqua;\n"
				+ "  text-transform: uppercase;\n" + "  text-decoration: none;\n" + "  border: 0.2em solid aqua;\n"
				+ "  padding: 0.5em 1em;\n" + "  &::before {\n" + "    content: \"\";\n" + "    display: block;\n"
				+ "    position: absolute;\n" + "    width: 10%;\n" + "    background: #222;\n" + "    height: 0.3em;\n"
				+ "    right: 20%;\n" + "    top: 0.21em;\n" + "    transform: skewX(-45deg);\n"
				+ "    -webkit-transition: all 0.45s cubic-bezier(0.86, 0, 0.07, 1);\n"
				+ "    transition: all 0.45s cubic-bezier(0.86, 0, 0.07, 1);\n" + "  }\n" + "  &::after {\n"
				+ "    content: \"\";\n" + "    display: block;\n" + "    position: absolute;\n" + "    width: 10%;\n"
				+ "    background: #222;\n" + "    height: 0.3em;\n" + "    left: 20%;\n" + "    bottom: -0.25em;\n"
				+ "    transform: skewX(45deg);\n"
				+ "    -webkit-transition: all 0.45 cubic-bezier(0.86, 0, 0.07, 1);\n"
				+ "    transition: all 0.45s cubic-bezier(0.86, 0, 0.07, 1);\n" + "  }\n" + "  &:hover {\n"
				+ "    &::before {\n" + "      right: 80%;\n" + "    }\n" + "    &::after {\n" + "      left: 80%;\n"
				+ "    }\n" + "  }\n" + "}\n" + "</style>\n" + "<body>\n" + "<br>\n" + "<br>\n" + "<a href=\""
				+ sauceLink + "\" class=\"brk-btn\" target=\"_new\"+>\n" + "  Video\n" + "</a>\n" + "</body>\n"
				+ "</html>\n";
		;

	}
}
