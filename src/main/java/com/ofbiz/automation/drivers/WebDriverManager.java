package com.ofbiz.automation.drivers;

import com.ofbiz.automation.common.World;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.safari.SafariDriver;

import java.util.HashMap;

public class WebDriverManager {
	private World world;
	private WebDriver driver;

	public WebDriverManager(World world) {
		this.world = world;
	}

	public WebDriver getIEDriver() {
		String version = world.getBrowserVersion();
		if (version.equalsIgnoreCase("latest")) {
			io.github.bonigarcia.wdm.WebDriverManager.iedriver().arch32().setup();
		} else {
			io.github.bonigarcia.wdm.WebDriverManager.iedriver().arch32().version(version).setup();
		}

		InternetExplorerOptions options = new InternetExplorerOptions();
		options.setCapability("unhandledPromptBehavior", "accept");
		options.destructivelyEnsureCleanSession();
		this.driver = new InternetExplorerDriver(options);
		return this.driver;
	}

	/**
	 * Local Driver Initialization
	 *
	 * @return
	 * @throws Exception
	 */
	public WebDriver getDriver() throws Exception {
		String version = world.getBrowserVersion();
		try {

			switch (world.getBrowser()) {
			case "firefox":
				if (version.equalsIgnoreCase("latest")) {
					io.github.bonigarcia.wdm.WebDriverManager.firefoxdriver().setup();
				} else {
					io.github.bonigarcia.wdm.WebDriverManager.firefoxdriver().version(version).setup();
				}
				FirefoxOptions firefoxOptions = new FirefoxOptions();
				firefoxOptions.setCapability("unhandledPromptBehavior", "accept");
				FirefoxProfile firefoxProfile = new FirefoxProfile();
				firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk",
						"text/plain, text/csv, application/octet-stream");
				firefoxProfile.setPreference("browser.download.folderList", 2);
				firefoxProfile.setPreference("browser.download.manager.showWhenStarting", false);
				firefoxProfile.setPreference("browser.download.dir", System.getProperty("user.dir") + "//downloads");
				firefoxOptions.setProfile(firefoxProfile);
				this.driver = new FirefoxDriver(firefoxOptions);
				break;
			case "chrome":
				if (version.equalsIgnoreCase("latest")) {
					io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
				} else {
					io.github.bonigarcia.wdm.WebDriverManager.chromedriver().version(version).setup();
				}
				HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
				chromePrefs.put("profile.default_content_settings.popups", 0);
				ChromeOptions chromeOptions = new ChromeOptions();
				chromeOptions.setExperimentalOption("prefs", chromePrefs);
				this.driver = new ChromeDriver(chromeOptions);
				break;
			case "safari":
				this.driver = new SafariDriver();
				break;
			case "iexplorer":
				this.driver = getIEDriver();
				break;
			case "edge":
				if (version.equalsIgnoreCase("latest")) {
					io.github.bonigarcia.wdm.WebDriverManager.edgedriver().setup();
				} else {
					io.github.bonigarcia.wdm.WebDriverManager.edgedriver().version(version).setup();
				}
				this.driver = new EdgeDriver();
				break;
			default:
				io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
				this.driver = new ChromeDriver();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Get Driver Initialization failed due to " + e.getMessage());

		}
		return driver;
	}
}
