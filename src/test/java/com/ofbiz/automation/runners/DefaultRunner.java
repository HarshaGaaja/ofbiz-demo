package com.ofbiz.automation.runners;

import cucumber.api.CucumberOptions;

@CucumberOptions(glue = { "com.ofbiz.automation.stepdefs", "com.ofbiz.automation.hooks" }, plugin = {
		"json:target/json-cucumber-reports/cukejson.json", "testng:target/testng-cucumber-reports/cuketestng.xml",
		"html:target/cucumber", "html:target/reports/htmlreport", "json:target/cucumber1.json",
		"html:target/site/cucumber-pretty" },
		tags={"@Login"},
		features = "src/test/resources/features/")

public class DefaultRunner extends AbstractTestNGCucumberParallelTests {

}
