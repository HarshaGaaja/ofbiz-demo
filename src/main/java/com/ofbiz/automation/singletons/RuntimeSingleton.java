package com.ofbiz.automation.singletons;

import cucumber.api.Scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class RuntimeSingleton {
	private static RuntimeSingleton parameters = null;
	public String whoami = System.getProperty("user.name");

	public SetTestResultData setData;
	List<String> tags = null;
	String featureName = null;
	String browser = null;
	public String locale = null;
	Scenario scenario = null;
	public int events = 0;
	public Boolean isMobile = false;
	public String mobileDevice = null;
	public String mobilePlatform = null;
	public String browserPlatform = null;
	public String mobileVersion = null;
	public String mobileBrowser = null;
	public String platform = null;
	public Boolean debugMode = false;
	public String runid = null;
	public Long id = null;
	public HashMap<String, ScenarioTestResultData> scenarios = new HashMap<String, ScenarioTestResultData>();
	public HashMap<String, List<StepTestResultData>> steps = new HashMap<String, List<StepTestResultData>>();

	public RuntimeSingleton(String id) {
		runid = id;
		setData = new SetTestResultData(id);

		if (whoami.equals("stick")) {
			whoami = "snelson";
		}
	}

	public RuntimeSingleton() {
		if (whoami.equals("stick")) {
			whoami = "snelson";
		}
	}

	public synchronized static RuntimeSingleton getInstance(String id) {
		if (parameters == null) {
			parameters = new RuntimeSingleton(id);
		}
		return parameters;
	}

	public synchronized static RuntimeSingleton getInstance() {
		if (parameters == null) {
			parameters = new RuntimeSingleton();
		}

		return parameters;
	}

	public void setTags(Collection<String> t) {
		tags = new ArrayList<String>(t);
	}

	public void setFeatureName(String s) {
		featureName = s;
	}

	public void setScenario(Scenario s) {
		scenario = s;
	}

	public List<String> getTags() {
		return tags;
	}

	public String getFeatureName() {
		return featureName;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void syncExecution() {
		if (RuntimeSingleton.getInstance().platform != null) {
			return;
		}
		// System.out.println("browserPlatform " + browserPlatform);
		platform = isMobile ? mobileDevice : browserPlatform;

		if (platform == null && browserPlatform != null) {
			platform = browserPlatform;
		}
		if (isMobile) {
			browser = mobileBrowser;
		}
		if (isMobile) {

		}
	}

}
