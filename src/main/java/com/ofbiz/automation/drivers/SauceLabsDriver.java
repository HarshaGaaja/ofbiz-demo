package com.ofbiz.automation.drivers;

import com.ofbiz.automation.common.World;
import com.saucelabs.saucerest.SauceREST;
import com.ofbiz.automation.exceptions.SauceConnectionException;
import com.ofbiz.automation.singletons.RuntimeSingleton;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class SauceLabsDriver {

	private World world;
	private ResourceBundle configLib;;
	private WebDriver driver;
	private String USERNAME, ACCESS_KEY, SAUCE_URL;
	int profileCnt = 0;

	public SauceLabsDriver(World world) {
		this.world = world;
	}

	// To initialize Sauce ie driver
	public WebDriver getSauceIEDriver() throws SauceConnectionException {

		DesiredCapabilities caps = null;

		SauceREST sauceRest = world.getSauceRest();
		try {
			// Reading config info from config file
			File file = new File("config");
			URL[] urls = { file.toURI().toURL() };
			ClassLoader loader = new URLClassLoader(urls);
			configLib = ResourceBundle.getBundle("config", Locale.getDefault(), loader);
			// Sauce user name
			caps = DesiredCapabilities.internetExplorer();
			caps.setCapability("platform", configLib.getString("IE_PLATFORM"));
			caps.setCapability("version", configLib.getString("IE_VERSION"));
			caps.setCapability("name", world.scenario.getName());
			caps.setCapability("commandTimeout", 600);
			caps.setCapability("idleTimeout", 10000);

			this.driver = new RemoteWebDriver(new URL(SAUCE_URL), caps);
		} catch (NullPointerException e) {
			throw new SauceConnectionException("SessionId was null");
		} catch (Exception e) {
			driver = null;
		}
		String sessionId = ((RemoteWebDriver) this.driver).getSessionId().toString();
		String tempWebLink = sauceRest.getPublicJobLink(sessionId);
		world.setSauceWebLink(tempWebLink);
		return driver;
	}

	// To initialize Sauce webdriver instances depending on parameters passed in
	// testng
	public synchronized WebDriver getDriver() throws Exception {
		DesiredCapabilities caps = null;
		String PLATFORM = null;
		SauceREST sauceRest = null;
		try {
			// Reading config info from config file
			File file = new File("config");
			URL[] urls = { file.toURI().toURL() };
			ClassLoader loader = new URLClassLoader(urls);
			configLib = ResourceBundle.getBundle("config", Locale.getDefault(), loader);

			// Sauce user name
			USERNAME = configLib.getString("SAUCE_USERNAME");
			// Sauce key
			ACCESS_KEY = configLib.getString("SAUCE_ACCESS_KEY");
			// Sauce connection url
			SAUCE_URL = configLib.getString("SAUCE_URL").replaceFirst("USERNAME", USERNAME).replaceFirst("ACCESS_KEY",
					ACCESS_KEY);

			sauceRest = new SauceREST(USERNAME, ACCESS_KEY);
			world.setSauceRest(sauceRest);
			RuntimeSingleton.getInstance().isMobile = world.isMobile();
			RuntimeSingleton.getInstance().mobileDevice = world.getMobileDeviceName();
			RuntimeSingleton.getInstance().mobilePlatform = world.getMobilePlatformName();
			RuntimeSingleton.getInstance().mobileVersion = world.getMobilePlatformVersion();
			RuntimeSingleton.getInstance().mobileBrowser = world.getMobileBrowser();
			RuntimeSingleton.getInstance().locale = world.getLocale();

			RuntimeSingleton.getInstance().syncExecution();

			if (!world.isMobile()) {
				switch (world.getBrowser()) {
				case "firefox":
					caps = DesiredCapabilities.firefox();
					break;
				case "chrome":
					caps = DesiredCapabilities.chrome();
					HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
					chromePrefs.put("profile.default_content_settings.popups", 0);
					System.out.println(System.getenv("TEMP"));

					String chromeProfile = System.getenv("TEMP");
					chromePrefs.put("--user-data-dir", chromeProfile);
					chromePrefs.put("--profile-directory",
							System.getenv("HOSTNAME") + "_" + Integer.valueOf(++profileCnt));
					ChromeOptions chromeOptions = new ChromeOptions();
					chromeOptions.setExperimentalOption("prefs", chromePrefs);
					System.out.println("merged!");
					caps.merge(chromeOptions);
					break;
				case "safari":
					caps = DesiredCapabilities.safari();
					break;
				case "iexplorer":
					caps = DesiredCapabilities.internetExplorer();
					break;
				case "edge":
					caps = DesiredCapabilities.edge();
					break;
				default:
					caps = DesiredCapabilities.chrome();
				}
				caps.setCapability("unhandledPromptBehavior", "accept");
				caps.setCapability("platform", world.getBrowserPlatform());
				caps.setCapability("version", world.getBrowserVersion());
				caps.setCapability("seleniumVersion", "3.4.0");
				caps.setCapability("name", world.scenario.getName());
				caps.setCapability("commandTimeout", 600);
				caps.setCapability("idleTimeout", 10000);
				caps.setCapability("maxDuration", 3600);
				caps.setCapability("extendedDebugging", true);
			} else {
				// Mobile
				caps = DesiredCapabilities.iphone();
				caps.setCapability("deviceName", world.getMobileDeviceName());
				caps.setCapability("deviceOrientation", world.getMobileDeviceOrientation());
				caps.setCapability("platformVersion", world.getMobilePlatformVersion());
				caps.setCapability("platformName", world.getMobilePlatformName());
				caps.setCapability("browserName", world.getMobileBrowser());
				caps.setCapability("unicodeKeyboard", true);
				caps.setCapability("resetKeyboard", true);
			}
			if (world.getTunnelRequired().equalsIgnoreCase("yes")) {
				caps.setCapability("tunnelIdentifier", world.getSauceTunnelId());
			}
			/// ********************************************************
			// this sc proxy tunnel is needed for yopmail and jenkins
			// removing this will brake yopmail tests in ci/cd
			/// ********************************************************
			caps.setCapability("tunnelIdentifier", "sc-proxy-tunnel");
			// also, if something is wrong here. You need to run a saucelabs connect tunnel
			// here's an example: bin/sc --pidfile /tmp/sc.pid1 -u "SAUCE_USER" -k
			// "SAUCEKEY" -i sc-proxy-tunnel
			// --no-remove-colliding-tunnels
			/// ********************************************************
			this.driver = new RemoteWebDriver(new URL(SAUCE_URL), caps);
			String sessionId = ((RemoteWebDriver) this.driver).getSessionId().toString();
			String tempWebLink = sauceRest.getPublicJobLink(sessionId);
			world.setSessionId(sessionId);
			world.setSauceWebLink(tempWebLink);

		} catch (Exception e) {
			e.printStackTrace();

			throw new Exception("Sauce Driver failed - " + e.getMessage());
		}
		return driver;
	}
}
