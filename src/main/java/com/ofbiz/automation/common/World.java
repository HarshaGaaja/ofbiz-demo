package com.ofbiz.automation.common;

import com.ofbiz.automation.drivers.*;
import com.saucelabs.saucerest.SauceREST;
import com.ofbiz.automation.exceptions.SauceConnectionException;
import cucumber.api.Scenario;
import org.openqa.selenium.WebDriver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class World {
	private String browser;
	private String sauceTunnelId;
	private String tunnelRequired;
	private String locale;
	private String orderNum;
	private String email;
	public String emailSubject;
	private String browserVersion;
	private String browserPlatform;
	private String platform;
	private String platformVersion;
	private String sessionId;
	private List<String> sauceWebLink = new ArrayList<>();
	private String testEnvironment;
	private Boolean isCoachUser = false;
	WebDriverManager webDriver = new WebDriverManager(this);
	SauceLabsDriver sauceDriver = new SauceLabsDriver(this);
	SeleniumGridDriver gridDriver = new SeleniumGridDriver(this);
	RESTDriver restDriver = new RESTDriver(this);
	SOAPDriver soapDriver = new SOAPDriver(this);
	public WebDriver driver;
	public WebDriver ieDriver;
	public WebDriver exeDriver;
	public WebDriver ieSauceDriver;
	public WebDriver ieCommonDriver;
	public Scenario scenario;

	Map<String, String> testDataJson = new HashMap<>();
	Map<String, String> generatedDataJson = new HashMap<>();
	Map<String, String> apiTestDataJson = new HashMap<>();
	Map<String, String> dataBaseInputTestDataJson = new HashMap<>();
	Map<String, String> dataBaseOutputJson = new HashMap<>();

	private boolean isMobile;
	private String mobilePlatform;
	private String mobileDeviceName;
	private String mobileDeviceOrientation;
	private String mobilePlatformVersion;
	private String mobilePlatformName;
	private String mobileBrowser;
	private Constants.DRIVERTYPE driverType;
	private Constants.BROWSER browserName;
	private SauceREST sauceRest;
	private Locale formattedLocale;
	private String userName;

	/*
	 * Method to get the driver based on the environment setup
	 */
	public synchronized WebDriver getDriver() throws Exception {
		try {
			switch (this.driverType) {
			case SAUCE:
				this.driver = sauceDriver.getDriver();
				break;
			case GRID:
				this.driver = gridDriver.getDriver();
				break;
			default:
				this.driver = webDriver.getDriver();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.driver;
	}

	public String getBrowser() {
		return browser;
	}

	public String getBrowserPlatform() {
		return browserPlatform;
	}

	public SauceREST getSauceRest() {
		return sauceRest;
	}

//synchronized
	public synchronized void setSauceRest(SauceREST sauceRest) {
		this.sauceRest = sauceRest;
	}

	public String getBrowserVersion() {
		return browserVersion;
	}

	public WebDriver getIEDriver() {
		WebDriver driver = null;
		if (this.driverType.equals(Constants.DRIVERTYPE.SAUCE)) {
			try {
				driver = sauceDriver.getSauceIEDriver();
			} catch (SauceConnectionException e) {
				e.printStackTrace();
			}
		} else {
			driver = this.ieDriver = webDriver.getIEDriver();
		}
		return driver;
	}

	public String getLocale() {
		return locale;
	}

	public String getPlatform() {
		return platform;
	}

	public String getPlatformVersion() {
		return platformVersion;
	}

	public WebDriver getSauceIEDriver() throws SauceConnectionException {
		return this.ieDriver = sauceDriver.getSauceIEDriver();
	}

	public String getSauceTunnelId() {
		return sauceTunnelId;
	}

	public List<String> getSauceWebLink() {
		return sauceWebLink;
	}

	public String getScenario() {
		return this.scenario.getName();
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getTunnelRequired() {
		return tunnelRequired;
	}

	public synchronized void setBrowser(String browser) {
		this.browser = browser;
	}

	public synchronized void setBrowserPlatform(String browserPlatform) {
		this.browserPlatform = browserPlatform;
	}

	public synchronized void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
	}

	public synchronized void setLocale(String locale) {
		this.locale = locale;
	}

	public synchronized void setSauceTunnelId(String sauceTunnelId) {
		this.sauceTunnelId = sauceTunnelId;
	}

	public synchronized void setSauceWebLink(String links) {
		sauceWebLink.add("<h2><a href=" + links + " target='_blank'>Please Click here for the Video</a><h2>");
	}

	public synchronized void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public synchronized void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public synchronized void setTunnelRequired(String tunnelRequired) {
		this.tunnelRequired = tunnelRequired;
	}

	public synchronized void setMobilePlatform(String mobilePlatform) {
		this.mobilePlatform = mobilePlatform;
	}

	public String getMobileDeviceName() {
		return mobileDeviceName;
	}

	public synchronized void setMobileDeviceName(String mobileDeviceName) {
		this.mobileDeviceName = mobileDeviceName;
	}

	public String getMobileDeviceOrientation() {
		return mobileDeviceOrientation;
	}

	public synchronized void setMobileDeviceOrientation(String mobileDeviceOrientation) {
		this.mobileDeviceOrientation = mobileDeviceOrientation;
	}

	public String getMobilePlatformVersion() {
		return mobilePlatformVersion;
	}

	public synchronized void setMobilePlatformVersion(String mobilePlatformVersion) {
		this.mobilePlatformVersion = mobilePlatformVersion;
	}

	public String getMobilePlatformName() {
		return mobilePlatformName;
	}

	public synchronized void setMobilePlatformName(String mobilePlatformName) {
		this.mobilePlatformName = mobilePlatformName;
	}

	public String getMobileBrowser() {
		return mobileBrowser;
	}

	public synchronized void setMobileBrowser(String mobileBrowser) {
		this.mobileBrowser = mobileBrowser;
	}

	public boolean isMobile() {
		return isMobile;
	}

	public synchronized void setMobile(boolean mobile) {
		isMobile = mobile;
	}

	public Constants.DRIVERTYPE getDriverType() {
		return driverType;
	}

	public synchronized void setDriverType(Constants.DRIVERTYPE driverType) {
		this.driverType = driverType;
	}

	public synchronized void setBrowserName(Constants.BROWSER browserName) {
		this.browserName = browserName;
	}

	public synchronized Map<String, String> getTestDataJson() {
		return testDataJson;
	}

	public synchronized Map<String, String> getGeneratedDataJson() {
		return generatedDataJson;
	}

	public synchronized Map<String, String> getApiTestDataJson() {
		return apiTestDataJson;
	}

	public synchronized Map<String, String> getDataBaseInputTestDataJson() {
		return dataBaseInputTestDataJson;
	}

	public synchronized Map<String, String> getDataBaseOutputJson() {
		return dataBaseOutputJson;
	}

	public String getTestEnvironment() {
		return testEnvironment;
	}

	private ResourceBundle localeResource;

	public synchronized void setLocaleResource(ResourceBundle localeResource) {
		this.localeResource = localeResource;
	}

	public synchronized void setTestEnvironment(String testEnvironment) throws FileNotFoundException, IOException {

		this.testEnvironment = testEnvironment;
	}

	public Locale getFormattedLocale() {
		return formattedLocale;
	}

	public void setFormattedLocale(Locale formattedLocale) {
		this.formattedLocale = formattedLocale;
	}

	public String getMyMethodName() {
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}
}
