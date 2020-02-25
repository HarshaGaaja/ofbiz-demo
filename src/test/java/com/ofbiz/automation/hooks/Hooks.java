package com.ofbiz.automation.hooks;

import com.ofbiz.automation.common.Constants;
import com.ofbiz.automation.common.World;
import com.ofbiz.automation.libraries.ConfigFileReader;
import com.ofbiz.automation.singletons.RuntimeSingleton;
import com.ofbiz.automation.singletons.ScenarioTestResultData;
import com.ofbiz.automation.singletons.SetTestResultData;
import com.ofbiz.automation.singletons.StepTestResultData;
import com.ofbiz.automation.utilities.MongoDBUtils;
import com.ofbiz.automation.utilities.TextUtils;
import cucumber.api.PickleStepTestStep;
import cucumber.api.Result.Type;
import cucumber.api.Scenario;
import cucumber.api.TestCase;
import cucumber.api.java.After;
import cucumber.api.java.AfterStep;
import cucumber.api.java.Before;
import cucumber.api.java.BeforeStep;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.influxdb.InfluxDBException;
import org.influxdb.dto.Point;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Reporter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Hooks {

	int currentStepDefIndex = 0;
	private String scenDesc;
	private World world;
	static Logger logger = LogManager.getLogger(Hooks.class);
	static {
		Configurator.initialize(null, "log4j2.xml");
	}

	public Hooks(World world) {
		this.world = world;
	}

	@Before
	public synchronized void incTotal() {
		if (Objects.isNull(RuntimeSingleton.getInstance().setData)) {
			RuntimeSingleton.getInstance().setData = new SetTestResultData(RuntimeSingleton.getInstance().runid);
		}
		RuntimeSingleton.getInstance().setData.total++;
		// logger.info("SNELSON BEFORE CNT: " +
		// RuntimeSingleton.getInstance().setData.total);
	}

	@Before
	public synchronized void beforeScenario(Scenario s) {
		try {
			// let's store the number of scenarios in a set context
			logger.info(TextUtils.center("<> <before scenario> <>"));
			logger.info("INTAKE >>> " + getStepCount(s));
			// logger.info("INTAKE" + getStepss(s).size());

			ScenarioTestResultData d = new ScenarioTestResultData(s.getName(), RuntimeSingleton.getInstance().runid);
			d.total = getStepCount(s);
			d.tags.addAll(s.getSourceTagNames());
			d.setupTagFields();
			d.printScenario();
			d.featureName = s.getId().substring(s.getId().lastIndexOf("/") + 1, s.getId().lastIndexOf(".feature"));

			Scenario scenario = s;
			world.setSauceTunnelId(System.getenv("TUNNEL_IDENTIFIER"));
//		d = embedVideo(d);
//		d.setSauce();
			this.scenDesc = scenario.getName();
			world.setScenario(scenario);
			logger.info(this.scenDesc);
			RuntimeSingleton.getInstance().setTags(scenario.getSourceTagNames());
			RuntimeSingleton.getInstance().setScenario(s);
			worldInit();

			d.browser = world.getBrowser();
			d.env = world.getTestEnvironment();
			d.locale = world.getLocale();
			List<StepTestResultData> intake = new ArrayList<StepTestResultData>();
			for (PickleStepTestStep ps : getSteps(s)) {
				StepTestResultData data = new StepTestResultData(RuntimeSingleton.getInstance().runid);
				data.addRuntimeDetails(d);
				data.name = ps.getStepText();
				data.line = ps.getStepLine();
				data.result = null; // Type.UNDEFINED;
				data.scenarioName = s.getName();
				intake.add(data);
			}

			logger.info("Your steps:\t" + intake.size());
			RuntimeSingleton.getInstance().scenarios.put(s.getId(), d);
			RuntimeSingleton.getInstance().steps.put(s.getId(), intake);

			// Mobile
			String isMobile = System.getProperty("isMobile");
			if (isMobile != null && !isMobile.isEmpty()) {
				world.setMobile(Boolean.parseBoolean(isMobile));
			} else {
				world.setMobile(Boolean.parseBoolean(
						Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("isMobile")));
			}

			if (world.isMobile()) {
				initMobile();
			}

			world.getDriver();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// this is to delete the oats related saved screenshot
			File screen = new File("OATS/DataBank/screenshot.png");
			screen.delete();
		}

		logger.info(TextUtils.center("----- <> <> <> ------"));
		// sendSuace(d);
	}

	@BeforeStep
	public void beforeStep(Scenario s) throws InterruptedException {
		logger.info(TextUtils.center("<> <before step> <>"));
		logger.info("uri:\t" + s.getUri());
		logger.info("id:\t" + s.getId());

		// String stepText = getStepText(s);
		StepTestResultData stp = RuntimeSingleton.getInstance().steps.get(s.getId()).get(currentStepDefIndex);
		stp.start = System.currentTimeMillis();
		RuntimeSingleton.getInstance().steps.get(s.getId()).remove(currentStepDefIndex);
		RuntimeSingleton.getInstance().steps.get(s.getId()).add(currentStepDefIndex, stp);

		logger.info(TextUtils.center("<> <> <>"));

	}

	@After
	public void updateJsonData() {
		MongoDBUtils.testDataList.add(world.getTestDataJson());
		MongoDBUtils.generatedTestDataList.add(world.getGeneratedDataJson());
		MongoDBUtils.apiTestDataList.add(world.getApiTestDataJson());
		MongoDBUtils.dataBaseInputList.add(world.getDataBaseInputTestDataJson());
		MongoDBUtils.dataBaseOutputList.add(world.getDataBaseOutputJson());
	}

	@After
	public synchronized void after(Scenario scenario) {
		try {
			/// logger.info(scenario.getLines());
			logger.info("Pre after scenario pulling id:\t" + scenario.getId());

			for (Entry<String, ScenarioTestResultData> test : RuntimeSingleton.getInstance().scenarios.entrySet()) {
				logger.info(TextUtils.format(test.getKey(), test.getValue().testrail));

			}
			ScenarioTestResultData d = RuntimeSingleton.getInstance().scenarios.get(scenario.getId());
			d.end = System.currentTimeMillis();
			d.duration = d.end - d.start;

			Date date = new Date(d.duration);
			DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			String dateFormatted = formatter.format(date);
			logger.info("Clocked@" + dateFormatted);
			logger.info(TextUtils.center("<> <after scenario> <>"));
			logger.info("OUTTAKE <<<< " + getStepCount(scenario) + "|" + RuntimeSingleton.getInstance().steps.size());
			logger.info(String.format("Completed step %s of %s", currentStepDefIndex, d.total));

			// if (scenario.getStatus().equals(Type.FAILED) ||
			// scenario.getStatus().equals(Type.UNDEFINED)) {

//		Integer k = Integer.valueOf(d.total) - currentStepDefIndex;
			logger.info("Result queue check \t" + RuntimeSingleton.getInstance().steps.get(scenario.getId()).size()
					+ " vs " + d.total);
			for (int i = 0; i < RuntimeSingleton.getInstance().steps.get(scenario.getId()).size(); i++) {

				StepTestResultData stp = RuntimeSingleton.getInstance().steps.get(scenario.getId()).get(i);
				RuntimeSingleton.getInstance().steps.get(scenario.getId()).remove(i);

				if (stp.result == null) {
					stp.result = Type.SKIPPED;
				}
				// stp.application = d.application;
				RuntimeSingleton.getInstance().steps.get(scenario.getId()).add(i, stp);
				sendStepStatus(stp);
			}
			// }
			d.result = scenario.getStatus();
			d.tabulateSteps(RuntimeSingleton.getInstance().steps.get(scenario.getId()));
			d = embedVideo(d);
			// d.printScenario();
			this.scenDesc = scenario.getName();
			// logger.info("im coming out of scenario " + this.scenDesc + world.getBrowser()
			// + world.getLocale());
			// logger.info("status" + scenario.getStatus());
			logger.info("id" + scenario.getId());
			// d.sauceLink = "<a href=" + d.sauceLink + "> Video</a>";
			logger.info("Sauce (" + d.sauceLink + ")");

			embedScreenshot(scenario);
			try {
				logger.info("Closing browser");
				if (this.world.driver != null) {
					world.driver.quit();
					logger.info("Closing browser");
				}
				if (this.world.ieDriver != null) {
					world.ieDriver.quit();
				}
			Constants.world = world;
			} catch (Throwable e) {
				logger.info("Unable to quit the driver");
				e.printStackTrace();
			}
			// Trying to release the BYD user
			logger.info(TextUtils.center("<> <> <>"));
			RuntimeSingleton.getInstance().setData.addRuntimeDetails(d);

			sendScenarioStatus(d);
			logger.info(TextUtils.center("----- <> <> <> ------"));
		} catch (InfluxDBException e) {
			logger.info("Influx exception occurred. Results may not have been pushed to the grafana db\n"
					+ e.getStackTrace());
		}
	}

	@AfterStep
	public void afterStep(Scenario scenario) {
		try {
			logger.info(TextUtils.center("------- <> <after step> <> -------"));
			// logger.info("Step Status:\t" + scenario.getStatus());
			List<StepTestResultData> sr = RuntimeSingleton.getInstance().steps.get(scenario.getId());

			StepTestResultData r = sr.get(currentStepDefIndex);
			// update step results
			r.end = System.currentTimeMillis();
			r.result = scenario.getStatus();
			r.duration = r.end - r.start;
			sr.remove(currentStepDefIndex);
			sr.add(currentStepDefIndex++, r);

			// remove and add it back to the singleton for reporting at the end
			RuntimeSingleton.getInstance().steps.remove(scenario.getId());

			RuntimeSingleton.getInstance().steps.put(scenario.getId(), sr);

			// logger.info(TextUtils.center("----- <> <> <> ------"));
			// sendStepStatus(r);
			// currentStepDefIndex += 1;
		} catch (InfluxDBException e) {
			logger.info("Influx exception occurred. Results may not have been pushed to the grafana db\n"
					+ e.getStackTrace());
		}
	}

	public void embedScreenshot(Scenario scenario) {

		if (scenario.isFailed()) {
			byte[] screenshot = null;
			List<String> scenarioFailed = world.getSauceWebLink();

			try {
				if (world.getDriverType().equals(Constants.DRIVERTYPE.SAUCE)) {
					if (this.world.driver != null) {
						screenshot = ((TakesScreenshot) this.world.driver).getScreenshotAs(OutputType.BYTES);
						scenario.embed(screenshot, "image/png");
					}
					if (this.world.ieDriver != null) {
						screenshot = ((TakesScreenshot) this.world.ieDriver).getScreenshotAs(OutputType.BYTES);
						scenario.embed(screenshot, "image/png");
					}
				} else {
					if (this.world.driver != null) {
						screenshot = ((TakesScreenshot) this.world.driver).getScreenshotAs(OutputType.BYTES);
						scenario.embed(screenshot, "image/png");
					}
					if (this.world.ieDriver != null) {
						screenshot = ((TakesScreenshot) this.world.ieDriver).getScreenshotAs(OutputType.BYTES);
						scenario.embed(screenshot, "image/png");
					}
				}

				logger.info("Screen shot\t" + screenshot.length);
				RuntimeSingleton.getInstance().scenarios.get(scenario.getId()).screenshot = screenshot;

				for (String links : scenarioFailed) {
					scenario.embed(links.getBytes(StandardCharsets.UTF_8), "text/html");
				}
				// Capture and embed screenshot for EBS if its available
				File screen = new File("OATS/DataBank/screenshot.png");
				if (screen.exists()) {
					byte[] fileContent = FileUtils.readFileToByteArray(screen);
					scenario.embed(fileContent, "image/png");

				}
				if (world.getSauceRest() != null)
					world.getSauceRest().jobFailed(world.getSessionId());
			} catch (Exception e) {
				logger.info("Exception thrown while attaching screenshot");
				e.printStackTrace();
			}

		}

		if (scenario.getStatus().toString().equalsIgnoreCase("PASSED") && world.getSauceRest() != null
				&& world.getSessionId() != null) {
			List<String> scenarioPassed = world.getSauceWebLink();
			for (String links : scenarioPassed) {
				scenario.embed(links.getBytes(StandardCharsets.UTF_8), "text/html");
			}
			world.getSauceRest().jobPassed(world.getSessionId());
		}

	}

	private void sendStepStatus(StepTestResultData d) {
		// logger.info(TextUtils.center("<> <> send step <> <>"));.
		if (d.browser.equals("edge")) {
			return;
		}
		if (RuntimeSingleton.getInstance().debugMode) {
			return;
		}
		d.checkBrowser();
		// let's default the suite/set to Mobile for mobile runs to make reporting
		// simpler in grafana

		d.printStep();
		RuntimeSingleton.getInstance().syncExecution();
		if (RuntimeSingleton.getInstance().platform == null) {
			if (RuntimeSingleton.getInstance().isMobile) {
				RuntimeSingleton.getInstance().platform = world.getMobilePlatformName();
			} else {
				RuntimeSingleton.getInstance().platform = world.getBrowserPlatform();
			}
		}

		Point point = Point.measurement("steps").time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.addField("mobile", RuntimeSingleton.getInstance().isMobile)
				.addField("platform", RuntimeSingleton.getInstance().platform)
				.addField("lid", RuntimeSingleton.getInstance().id)
				.addField("user", RuntimeSingleton.getInstance().whoami).addField("duration", d.duration)
				.addField("start", d.start).addField("end", d.end).addField("line", d.line)
				.addField("suite", d.suite).addField("result", d.result.toString()).addField("step", d.name)
				.addField("application", d.application).addField("phase", d.phase).addField("env", d.env)
				.addField("scenario", d.scenarioName).addField("testrail", d.testrail).addField("browser", d.browser)
				.tag("step", d.name).tag("scenario", d.scenarioName).tag("result", d.result.toString())
				.tag("env", d.env).tag("sprint", d.sprint).tag("browser", d.browser).tag("application", d.application)
				.tag("feature", d.featureName).tag("testrail", d.testrail).tag("phase", d.phase)
				.tag("user", RuntimeSingleton.getInstance().whoami).tag("line", String.valueOf(d.line))
				.tag("suite", d.suite).tag("localeTag", RuntimeSingleton.getInstance().locale).build();

//		ResultSender.send(point);
//		ResultSenderProd.send(point);
		logger.info(TextUtils.center("<> <> step sent <> <>"));
		d.printStep();

	}

	private void sendScenarioStatus(ScenarioTestResultData d) {
		if (d.browser.equals("edge")) {
			return;
		}
		if (RuntimeSingleton.getInstance().debugMode) {
			return;
		}
		d.checkBrowser();
		String base64Encoded = "no image";
		if (RuntimeSingleton.getInstance().isMobile == true) {
			d.suite = "Mobile";
		}
		BufferedImage img = null;
		if (d.screenshot != null) {
			String encodedString = Base64.getEncoder().encodeToString(d.screenshot);
			base64Encoded = "data:image/png;base64," + new String(d.screenshot, StandardCharsets.UTF_8);
			base64Encoded = "  <img src=\"data:image/gif;base64," + encodedString + "\" />";

			try {
				img = ImageIO.read(new ByteArrayInputStream(d.screenshot));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		d.setSauce();
		// logger.info(d.sauceHtml);
		if (d.sauceLink == null)
			d.sauceLink = "none";

		Point point = Point.measurement("scenarios").time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.addField("mobile", RuntimeSingleton.getInstance().isMobile)
				.addField("platform", RuntimeSingleton.getInstance().platform)
				.addField("user", RuntimeSingleton.getInstance().whoami).addField("duration", d.duration)
				.addField("start", d.start).addField("end", d.end).addField("suite", d.suite)
				.addField("lid", RuntimeSingleton.getInstance().id).addField("total", d.total).addField("env", d.env)
				.addField("name", d.name).addField("browser", d.browser).addField("skipped", d.skipped)
				.addField("phase", d.phase).addField("failed", d.failed).addField("feature", d.featureName)
				.addField("passed", d.passed).addField("application", d.application).addField("testrail", d.testrail)
				.addField("sauce", d.sauceLink).addField("sauceHtml", d.sauceHtml)
				.addField("result", d.result.toString()).addField("shot", base64Encoded)
				.addField("testlink", d.testrailLink).addField("undefined", d.undefined)
				.addField("locale", RuntimeSingleton.getInstance().locale).tag("scenario", d.name)
				.tag("sprint", d.sprint).tag("result", d.result.toString()).tag("env", d.env).tag("browser", d.browser)
				.tag("suite", d.suite).tag("application", d.application).tag("feature", d.featureName)
				.tag("user", RuntimeSingleton.getInstance().whoami).tag("testrail", d.testrail).tag("phase", d.phase)
				.tag("localeTag", RuntimeSingleton.getInstance().locale).build(); // .tag("runid", d.rid)
		;
//		ResultSender.send(point);
//		ResultSenderProd.send(point);

	}

	private String getStepText(Scenario s) {
		Field f;
		TestCase r = null;
		try {
			f = s.getClass().getDeclaredField("testCase");
			f.setAccessible(true);
			r = (TestCase) f.get(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<PickleStepTestStep> stepDefs = r.getTestSteps().stream().filter(x -> x instanceof PickleStepTestStep)
				.map(x -> (PickleStepTestStep) x).collect(Collectors.toList());

		// This object now holds the information about the current step definition
		// If you are using pico container
		// just store it somewhere in your world state object
		// and to make it available in your step definitions.
		PickleStepTestStep currentStepDef = stepDefs.get(currentStepDefIndex);
		return currentStepDef.getStepText();
	}

	private static final String regex = "^[A-Z][0-9]"; // alpha-numeric uppercase

	public static boolean isUpperCase(String str) {
		return Pattern.compile(regex).matcher(str).find();
	}

	public int getStepCount(Scenario s) {
		Field f;
		TestCase r = null;
		try {
			f = s.getClass().getDeclaredField("testCase");
			f.setAccessible(true);
			r = (TestCase) f.get(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<PickleStepTestStep> stepDefs = r.getTestSteps().stream().filter(x -> x instanceof PickleStepTestStep)
				.map(x -> (PickleStepTestStep) x).collect(Collectors.toList());
		return stepDefs.size();
	}

	public List<PickleStepTestStep> getSteps(Scenario s) {
		Field f;
		TestCase r = null;
		for (Field fi : s.getClass().getDeclaredFields()) {
			// logger.info(fi.getName());
		}
		try {
			f = s.getClass().getDeclaredField("testCase");
			f.setAccessible(true);
			r = (TestCase) f.get(s);

		} catch (Exception e) {
			e.printStackTrace();
		}
		List<PickleStepTestStep> stepDefs = r.getTestSteps().stream().filter(x -> x instanceof PickleStepTestStep)
				.map(x -> (PickleStepTestStep) x).collect(Collectors.toList());

		return stepDefs;
	}

	private synchronized void worldInit() {
		// App Related properties
		String locale = System.getProperty("locale");
		String driverType = System.getProperty("driverType");
		RuntimeSingleton.getInstance().debugMode = ConfigFileReader.getConfigFileReader().isDebugModeOn();

		logger.info("D3BUG M0D3\t" + RuntimeSingleton.getInstance().debugMode);
		if (locale != null && !locale.isEmpty()) {
			world.setLocale(locale);
		} else {
			world.setLocale(
					Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("locale"));
		}
		RuntimeSingleton.getInstance().locale = world.getLocale();

		switch (world.getLocale().toLowerCase()) {
		case "en_ca":
			world.setFormattedLocale(new Locale("en", "CA"));
			world.setLocaleResource(
					ResourceBundle.getBundle("com.ofbiz.automation.locales.Locale", new Locale("en", "CA")));
			break;
		case "en_gb":
			world.setFormattedLocale(new Locale("en", "GB"));
			world.setLocaleResource(
					ResourceBundle.getBundle("com.ofbiz.automation.locales.Locale", new Locale("en", "GB")));
			break;
		case "en_us":
			world.setFormattedLocale(Locale.US);
			world.setLocaleResource(ResourceBundle.getBundle("com.ofbiz.automation.locales.Locale", Locale.US));
			break;
		case "es_us":
			world.setFormattedLocale(new Locale("es", "US"));
			world.setLocaleResource(
					ResourceBundle.getBundle("com.ofbiz.automation.locales.Locale", new Locale("es", "US")));
			break;
		case "fr_ca":
			world.setFormattedLocale(Locale.CANADA_FRENCH);
			world.setLocaleResource(
					ResourceBundle.getBundle("com.ofbiz.automation.locales.Locale", Locale.CANADA_FRENCH));
			break;
		default:
			world.setFormattedLocale(Locale.US);
			world.setLocaleResource(ResourceBundle.getBundle("com.ofbiz.automation.locales.Locale", Locale.US));
			break;
		}

		if (driverType != null && !driverType.isEmpty()) {
		} else {
			driverType = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
					.getParameter("driverType");
		}

		switch (driverType.toLowerCase()) {
		case "local":
			world.setDriverType(Constants.DRIVERTYPE.LOCAL);
			break;
		case "sauce":
			world.setDriverType(Constants.DRIVERTYPE.SAUCE);
			break;
		case "grid":
			world.setDriverType(Constants.DRIVERTYPE.GRID);
			break;
		default:
			world.setDriverType(Constants.DRIVERTYPE.LOCAL);
			break;
		}

		// Sauce Properties
		String tunnelRequired = System.getProperty("tunnel");
		if (tunnelRequired != null && !tunnelRequired.isEmpty()) {
			world.setTunnelRequired(tunnelRequired);
		} else {
			world.setTunnelRequired(
					Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("tunnel"));
		}

		// Browser Related Properties
		String browser = System.getProperty("browser");
		String browserVersion = System.getProperty("browserVersion");
		String browserPlatform = System.getProperty("browserPlatform");
		String environment = System.getProperty("environment");

		// logger.info("url\t" + System.getProperty("build"));
		if (browser != null && !browser.isEmpty()) {
			world.setBrowser(browser);

		} else {
			browser = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("browser");
			world.setBrowser(browser);
		}
		switch (browser.toLowerCase()) {
		case "chrome":
			world.setBrowserName(Constants.BROWSER.CHROME);
			break;
		case "firefox":
			world.setBrowserName(Constants.BROWSER.FIREFOX);
			break;
		case "iexplorer":
			world.setBrowserName(Constants.BROWSER.IEXPLORER);
			break;
		case "safari":
			world.setBrowserName(Constants.BROWSER.SAFARI);
			break;
		case "edge":
			world.setBrowserName(Constants.BROWSER.EDGE);
			break;
		default:
			world.setBrowserName(Constants.BROWSER.CHROME);
			break;

		}

		if (browserVersion != null && !browserVersion.isEmpty()) {
			world.setBrowserVersion(browserVersion);
		} else {
			world.setBrowserVersion(Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
					.getParameter("browserVersion"));
		}
		if (browserPlatform != null && !browserPlatform.isEmpty()) {
			world.setBrowserPlatform(browserPlatform);
		} else {
			world.setBrowserPlatform(Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
					.getParameter("browserPlatform"));
		}
		if (environment != null && !environment.isEmpty()) {
			try {
				world.setTestEnvironment(environment);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				world.setTestEnvironment(Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
						.getParameter("environment"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void initMobile() {
		try {
			String mobilePlatform = System.getProperty("mobilePlatform");
			String mobileDeviceName = System.getProperty("mobileDeviceName");
			String mobileDeviceOrientation = System.getProperty("mobileDeviceOrientation");
			String mobilePlatformVersion = System.getProperty("mobilePlatformVersion");
			String mobilePlatformName = System.getProperty("mobilePlatformName");
			String mobileBrowser = System.getProperty("mobileBrowser");

			if (world.isMobile()) {
				if (mobilePlatform != null && !mobilePlatform.isEmpty()) {
					world.setMobilePlatform(mobilePlatform);
				} else {
					world.setMobilePlatform(Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
							.getParameter("mobilePlatform"));
				}

				if (mobileDeviceName != null && !mobileDeviceName.isEmpty()) {
					world.setMobileDeviceName(mobileDeviceName);
				} else {
					world.setMobileDeviceName(Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
							.getParameter("mobileDeviceName"));
				}

				if (mobileDeviceOrientation != null && !mobileDeviceOrientation.isEmpty()) {
					world.setMobileDeviceOrientation(mobileDeviceOrientation);
				} else {
					world.setMobileDeviceOrientation(Reporter.getCurrentTestResult().getTestContext()
							.getCurrentXmlTest().getParameter("mobileDeviceOrientation"));
				}

				if (mobilePlatformVersion != null && !mobilePlatformVersion.isEmpty()) {
					world.setMobilePlatformVersion(mobilePlatformVersion);
				} else {
					world.setMobilePlatformVersion(Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
							.getParameter("mobilePlatformVersion"));
				}

				if (mobilePlatformName != null && !mobilePlatformName.isEmpty()) {
					world.setMobilePlatformName(mobilePlatformName);
				} else {
					world.setMobilePlatformName(Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
							.getParameter("mobilePlatformName"));
				}

				if (mobileBrowser != null && !mobileBrowser.isEmpty()) {
					world.setMobileBrowser(mobileBrowser);
				} else {
					world.setMobileBrowser(Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
							.getParameter("mobileBrowser"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ScenarioTestResultData embedVideo(ScenarioTestResultData s) {
		String sessionId = ((RemoteWebDriver) world.driver).getSessionId().toString();
		logger.info("Session:\t" + sessionId);
		if (world.getDriverType().equals(Constants.DRIVERTYPE.SAUCE)) {
			String tempWebLink = world.getSauceRest().getPublicJobLink(sessionId);
			logger.info("Session:\t" + tempWebLink);
			s.sauceLink = tempWebLink;
		}
		return s;
	}
}
