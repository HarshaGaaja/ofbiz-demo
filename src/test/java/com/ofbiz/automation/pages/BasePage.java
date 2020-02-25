package com.ofbiz.automation.pages;

import com.ofbiz.automation.common.Constants;
import com.ofbiz.automation.common.World;
import com.ofbiz.automation.exceptions.AppIssueException;
import com.ofbiz.automation.exceptions.ApplicationTooSlowException;
import com.ofbiz.automation.exceptions.TooManySessionsException;
import com.ofbiz.automation.libraries.ConfigFileReader;
import com.ofbiz.automation.utilities.TextUtils;
//import com.ted.automation.services.OnlineAPIServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * Super Class of All Pages, every page should inherit this page
 */
public class BasePage {
	// Create dependency injection
	protected World world;
	private WebDriver driver;
	protected ResourceBundle configLib;
	protected ClassLoader loader;
	Logger logger = LogManager.getLogger(BasePage.class);
	private String parentWindow;
	private Locale theLocale = null; // Locale.getDefault();
	public BasePage(World world, WebDriver driver) {
		this.world = world;
		this.driver = driver;
		File file = new File("config");
		try {
			URL[] urls = { file.toURI().toURL() };
			loader = new URLClassLoader(urls);
		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		theLocale = Locale.getDefault();
		configLib = ResourceBundle.getBundle("config", theLocale, loader);
	}

    public boolean loadPage(String url) {
		logger.info("Loading:\t" + url);
		world.driver.get(url);
		world.driver.manage().window().maximize();
		world.driver.manage().timeouts().implicitlyWait(ConfigFileReader.getConfigFileReader().getImplicitlyWait(),
				TimeUnit.SECONDS);
		configLib = ResourceBundle.getBundle("config", theLocale, loader);
		world.driver.get(url);
		return world.driver.getCurrentUrl().length() > 0;
	}

	/*
	 * Check if the page actually served something, if not then fail
	 */
	private void checkSomethingServed() throws Exception {
		// Check if body tag appears
		try {
			driver.findElement(By.tagName("body"));
		} catch (WebDriverException e) {
			logger.info(" ### RETRYING To Find Body tag ###");
			driver.navigate().refresh();
			driver.findElement(By.tagName("body"));
			logger.info("Failed in verifying page and the reason for failure is " + e.getMessage());
			throw new AppIssueException("No page content served even after refreshing the page", e);
		}
		WebElement titleTag = null;
		try {
			if (driver.findElements(By.tagName("title")).size() > 0) {
				titleTag = driver.findElement(By.tagName("title"));
			}
		} catch (StaleElementReferenceException e) {
			if (driver.findElements(By.tagName("title")).size() > 0) {
				titleTag = driver.findElement(By.tagName("title"));
			}
			logger.info("Failed in verifying page and the reason for failure is " + e.getMessage());
			throw new Exception("Page loaded but unable to verify the title", e);
		}
	}

	private void verifyJavaScriptDone() throws InterruptedException {
		int retries = 20;
		int wait = 0;
		while (!isJavaScriptsDoneLoading()) {
			Thread.sleep(100);
			if (wait >= retries)
				break;
			wait++;
		}
	}

	private boolean isJavaScriptsDoneLoading() {
		logger.info("Loading JavaScript");
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		return new Boolean((Boolean) executor
				.executeScript("return (typeof window.CJS === 'undefined' || window.CJS.done === true)"))
						.booleanValue();
	}

	public boolean letmeFinishLoading(int poolTimes) throws Exception {
		if(world.getBrowser().equalsIgnoreCase("safari")){
			//pause(poolTimes*1000);
			return true;
		}

		boolean ret = false;
		JavascriptExecutor js = (JavascriptExecutor) driver;

		long start = System.currentTimeMillis();
		// Initially bellow given if condition will check ready state of page.
		try {
			if (js.executeScript("return document.readyState").toString().equals("complete")) {
				System.out.println("Page synced (" + (System.currentTimeMillis() - start) + " ms.)");
				ret = true;
			}
		} catch (WebDriverException e) {
			if (e.getMessage().contains("Missing Template ERR_CONNECT_FAIL"))
				logger.info("Skipping Missing Template ERR_CONNECT_FAIL exception");
			else if (e.getMessage().contains("JavaScript error"))
				logger.warn("Skipping exception with JavaScript error");
			else throw new Exception(e.getMessage());

		}

		if (!ret) {
			// This loop will rotate for <poolTimes> times to check If page Is ready after
			// every 1 second.
			for (int i = 0; i < poolTimes; i++) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				// To check page ready state.
				try {
					if (js.executeScript("return document.readyState").toString().equals("complete")) {
						ret = true;
						break;
					}
				} catch (JavascriptException e) {
					logger.info("Skippping JavaScript Error");
				}

			}
		}
		long end = System.currentTimeMillis();
		if (ret == true) {
			if ((end - start) > 60000) {
				System.out.println("This page took '" + (end - start) / 1000 + "' secs. to load.");
				logger.warn("This page took '" + (end - start) / 1000 + "' secs. to sync.");
			}
		} else {
			throw new ApplicationTooSlowException("Occurred in " + this.getClass().getSimpleName());
		}
		// checkForAppIssue();
		waitUntilJQueryReady();
		return ret;
	}

	public void waitUntilOIMBusy(long waitSecs) {
		waitForElementToDisplay(By.xpath("//span[@id='pt1:bbar:status']/img[@title='Idle']"), waitSecs);
	}

	public void waitUntilBusyCursor() {
		for (int i = 0; i < ConfigFileReader.getConfigFileReader().getExplicitlyWait(); i++) {
			if (driver.findElement(By.xpath("//body")).getCssValue("cursor").equalsIgnoreCase("wait"))
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			else {
				break;
			}
		}
	}

	public void waitUntilJQueryReady() {
		try{
			JavascriptExecutor jsExec = (JavascriptExecutor) driver;
			Boolean jQueryDefined = (Boolean) jsExec.executeScript("return typeof jQuery != 'undefined'");
			if (jQueryDefined) {
				poll(20);

				waitForJQueryLoad(10);

				poll(20);
			}
		} catch (WebDriverException e){
			logger.info("Skipping all unknown errors on sauce labs");
		}
	}

	private void waitForJQueryLoad(int secs) {
		try {
			JavascriptExecutor jsExec = (JavascriptExecutor) driver;
			WebDriverWait jsWait = new WebDriverWait(driver, secs);
			ExpectedCondition<Boolean> jQueryLoad = driver -> ((Long) ((JavascriptExecutor) this.driver)
					.executeScript("return jQuery.active") == 0);

			boolean jqueryReady = (Boolean) jsExec.executeScript("return jQuery.active==0");

			if (!jqueryReady) {
				jsWait.until(jQueryLoad);
			}
		} catch (WebDriverException ignored) {
		}
	}

	public void waitUntilAngularReady() {
		try {
			JavascriptExecutor jsExec = (JavascriptExecutor) driver;
			Boolean angularUnDefined = (Boolean) jsExec.executeScript("return window.angular === undefined");
			if (!angularUnDefined) {
				Boolean angularInjectorUnDefined = (Boolean) jsExec
						.executeScript("return angular.element(document).injector() === undefined");
				if (!angularInjectorUnDefined) {
					poll(20);

					waitForAngularLoad();

					poll(20);
				}
			}
		} catch (WebDriverException ignored) {
		}
	}

	private void waitForAngularLoad() {
		String angularReadyScript = "return angular.element(document).injector().get('$http').pendingRequests.length === 0";
		angularLoads(angularReadyScript);
	}

	public void waitUntilAngular5Ready() {

		try {
			JavascriptExecutor jsExec = (JavascriptExecutor) driver;
			Object angular5Check = jsExec
					.executeScript("return getAllAngularRootElements()[0].attributes['ng-version']");
			if (angular5Check != null) {
				Boolean angularPageLoaded = (Boolean) jsExec
						.executeScript("return window.getAllAngularTestabilities().findIndex(x=>!x.isStable()) === -1");
				if (!angularPageLoaded) {
					poll(20);

					waitForAngular5Load();

					poll(20);
				}
			}
		} catch (WebDriverException ignored) {
		}
	}

	private void waitForAngular5Load() {
		String angularReadyScript = "return window.getAllAngularTestabilities().findIndex(x=>!x.isStable()) === -1";
		angularLoads(angularReadyScript);
	}

	private void angularLoads(String angularReadyScript) {
		try {
			JavascriptExecutor jsExec = (JavascriptExecutor) driver;
			WebDriverWait jsWait = new WebDriverWait(driver, 10);
			ExpectedCondition<Boolean> angularLoad = driver -> Boolean
					.valueOf(((JavascriptExecutor) driver).executeScript(angularReadyScript).toString());

			boolean angularReady = Boolean.valueOf(jsExec.executeScript(angularReadyScript).toString());

			if (!angularReady) {
				jsWait.until(angularLoad);
			}
		} catch (WebDriverException ignored) {
		}
	}

	private void poll(long milis) {
		try {
			Thread.sleep(milis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean navigateToWebPage(String url) throws Exception {
		logger.info("navigate to webpage");
		long start = 0, end = 0;
		try {

			if (driver == null) {
				driver = world.getDriver();
			}
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			start = System.currentTimeMillis();
			if (world.getDriverType() == Constants.DRIVERTYPE.SAUCE && world.isMobile()) {
				logger.info("Selected execution environment type as SAUCE>>Mobile");
				driver.get(url);
			} else if (world.getDriverType() == Constants.DRIVERTYPE.LOCAL && world.isMobile()) {
				logger.info("Selected execution environment type as Local>>Mobile");
				driver.get(url);
				driver.manage().window().setSize(new Dimension(370, 801));
			} else {
				logger.info("Selected execution environment type as Local");
				driver.get(url);
				driver.manage().window().maximize();
			}
			// driver.manage().timeouts().implicitlyWait(ConfigFileReader.getConfigFileReader().getImplicitlyWait(),
			// TimeUnit.SECONDS);
			checkSomethingServed();
			checkForAppIssue();
			return true;
		} catch (Exception e) {
			if (e.getMessage().contains("Missing Template ERR_CONNECT_FAIL")){
				logger.info("Skipping Missing Template ERR_CONNECT_FAIL");
				navigateToWebPage(url);
			} else failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		} /*
			 * catch(Exception e) { driver = world.getDriver(); if (navigateToWebPage(url))
			 * { return true; } logger.info("Failed to navigate web page using URL : " +
			 * url); throw new ScriptException("Not able to navigate to " + url, e); }
			 */
		finally {
			end = System.currentTimeMillis();
			logger.info("Navigation to '" + url + "'\n\tTime taken: " + (end - start) + " ms.");
			driver.manage().timeouts().implicitlyWait(ConfigFileReader.getConfigFileReader().getImplicitlyWait(),
					TimeUnit.SECONDS);
		}
		return false;
	}

	public boolean wakeMeup() {
		try {
			driver.manage().window().fullscreen();
			pause(5000);
			world.driver.manage().window().maximize();
			pause(5000);
		} catch (Exception e) {
			logger.info("Some error occurred while waking this page up\n");
			e.printStackTrace();
		}
		return true;
	}

	public void moveAbit(long wait) {
		try {
			JavascriptExecutor jse = (JavascriptExecutor) world.driver;
			jse.executeScript("window.scrollBy(0,250)");
			pause(wait);
		} catch (Exception e) {
		}
	}

	public boolean sendKeys(By element, Keys text) {
		try {
			driver.findElement(element).sendKeys(text);
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return true;
	}

	public boolean sendKeys(By element, String text) {
		try {
			driver.findElement(element).sendKeys(text);
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return true;
	}

	public boolean clearText(By element) {
		try {
			WebElement el = driver.findElement(element);
			if (el.getTagName().equalsIgnoreCase("input") && el.getAttribute("type").equalsIgnoreCase("text")) {
				driver.findElement(element).clear();
				/*
				 * for (int i = 0; i < 20; i++) { el.sendKeys(Keys.BACK_SPACE); pause(10); }
				 */
			}

		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return true;
	}

	public boolean moveToElementAndClick(By element) {
		try {
			WebElement el = driver.findElement(element);
			Actions actions = new Actions(world.driver);
			actions.moveToElement(world.driver.findElement(element), 0, 0);
			actions.moveByOffset(3, 3).click().build().perform();
			pause(2000);
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return true;
	}

	public boolean lowLevelEnterText(By element, String toEnter) {
		try {
			logger.info("entering text: " + toEnter + " to element: " + element.toString());
			clearText(element);
			moveToElementAndClick(element);
			pause(1000);
			sendKeys(element, toEnter);
			pause(1000);
			sendKeys(element, Keys.TAB);
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return true;
	}

	public boolean enterText(By element, String toEnter) throws Exception {
		try {
			logger.info("Entering text using By element : " + element);
			((JavascriptExecutor) driver).executeScript("window.focus();");
			try {
				waitUntilElementIsClickable(element, ConfigFileReader.getConfigFileReader().getExplicitlyWait());
			} catch (Exception e) {
				//do nothing, try to enter text still
			}
			logger.info("Element is clickable with condition Implict wait");
			WebElement webElement = driver.findElement(element);
			if (webElement.isEnabled() && webElement.isDisplayed()) {
				logger.info("Element is enabled or displayed in page:" + element);
				webElement.clear();
				webElement.sendKeys(toEnter);
				return true;
			} else {
				logger.info("Element is not displayed / enabled ");
				throw new Exception("Element is not displayed / enabled:" + element);
			}
		} catch (WebDriverException e) {
			logger.info("By Passing the exception on sauce lab " + e.getMessage());
			if (e.getMessage().contains("Missing Template ERR_CONNECT_FAIL")) {
				new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait()).until(ExpectedConditions.elementToBeClickable(element)).sendKeys(toEnter);
				return true;
			} else return enterTextByJS(element, toEnter);
		} catch (Exception e) {
			logger.info("Failed to enter text into : " + element);
			enterTextByJS(element, toEnter);
		}
		return false;
	}

	public boolean click(By element) throws Exception {
		try {
			logger.info("Clicking object using By element : " + element);
			try {
				waitUntilElementIsClickable(element, ConfigFileReader.getConfigFileReader().getExplicitlyWait());
			} catch (Exception e) {

			}
			scrollElementIntoViewJS(element);
			logger.info("Element is clickable with condition Implict wait");
			WebElement webElement = driver.findElement(element);
			if (webElement.isEnabled() && webElement.isDisplayed()) {
				logger.info("Element is enabled or displayed in page");
				webElement.click();
				return true;
			} else {
				logger.info("Element is not enabled or displayed for click, will try javascript click next.");
				return clickElementUsingJavaScript(element);
			}
		} catch (ElementNotInteractableException e) {
			logger.info("Element not interactable during click " + e.getMessage());
			syncObjects("hcwait5");
			return clickElementUsingJavaScript(element);
		} catch (WebDriverException e) {
			e.printStackTrace();
			logger.info("WebDriver exception during click " + e.getMessage());
			syncObjects("hcwait5");
			if (e.getMessage().contains("Missing Template ERR_CONNECT_FAIL")) {
				new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait()).until(ExpectedConditions.elementToBeClickable(element)).click();
				return true;
			} else
				return clickElementUsingJavaScript(element);
		} catch (Exception e) {
			e.printStackTrace();
            return clickElementUsingJavaScript(element);
		}
	}
	public  boolean clickElementWithoutExpectedConditions(By element) throws Exception{
		try{
		JavascriptExecutor executor = (JavascriptExecutor) world.driver;
		executor.executeScript("arguments[0].click();", world.driver.findElement(element));
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return true;
	}

    public boolean setFocus(String id) throws Exception {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("document.getElementById("+id+").focus();");
		return true;
	}

	public boolean clickByJS(By element) throws Exception {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		try {
			logger.info("Clicking object using JS and By element : " + element);
			try {
				waitUntilElementIsClickable(element, ConfigFileReader.getConfigFileReader().getExplicitlyWait());
			} catch (Exception e) {

			}
			logger.info("Element is clickable with condition Implict wait:" + element);
			WebElement webElement = driver.findElement(element);
			if (webElement.isEnabled() && webElement.isDisplayed()) {
				logger.info("Element is enabled or displayed in page:" + element);
				executor.executeScript("arguments[0].click();", webElement);
				// syncObjects("minWait");
				return true;
			} else {
				logger.info("Element is not clicked using JS");
				throw new AppIssueException("The element is not present/interactable on screen:" + element);
			}
		} catch (JavascriptException e) {
			logger.info("Skipping JavascriptException");
			return true;
		} catch (WebDriverException e) {
			if (e.getMessage().contains("Missing Template ERR_CONNECT_FAIL"))
				executor.executeScript("arguments[0].click();", new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait()).until(ExpectedConditions.visibilityOfElementLocated(element)));
			else throw (e);
		} catch (Exception e) {
			logger.info("Failed to click : " + element);
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return false;
	}

	public boolean isAlertPresent() {
		boolean foundAlert = false;
		try {
			WebDriverWait wait = new WebDriverWait(driver,
					ConfigFileReader.getConfigFileReader().getWaitTime("mediumWait") / 1000 /* timeout in seconds */);
			wait.until(ExpectedConditions.alertIsPresent());
			foundAlert = true;
		} catch (Exception e) {
			foundAlert = false;
		}
		return foundAlert;
	}

	public boolean verifyElementDisplayed(By element) throws Exception {
		if (isElementDisplayed(element)) {
			WebElement webElement = null;
			try {
				webElement = driver.findElement(element);
			} catch (WebDriverException e) {
				if (e.getMessage().contains("Missing Template ERR_CONNECT_FAIL")) {
					logger.info("Skipping Missing Template ERR_CONNECT_FAIL");
					webElement = new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait()).until(ExpectedConditions.visibilityOfElementLocated(element));
				}else throw(e);
			}
			if (webElement.isEnabled() && webElement.isDisplayed()) {
				logger.info("Element is enabled or displayed in page");
				syncObjects("hcwait2");
				return true;
			} else {
				throw new AppIssueException("Element is not displayed / enabled:" + element);
			}
		}
		return false;
		/*
		 * int seconds = 5000; try { Thread.sleep(seconds); WebElement webElement =
		 * driver.findElement(element); if (webElement.isEnabled() &&
		 * webElement.isDisplayed()) {
		 * logger.info("Element is enabled or displayed in page"); return true; } else {
		 * throw new AppIssueException("Element is not displayed / enabled:" + element);
		 * } } catch (Exception e) {
		 * logger.info("Error : Failed to verify element status"); throw new
		 * Exception("Element not displayed:" + element, e); }
		 */
	}

	/**
	 * Wait for element to display
	 *
	 * @param locator          to locate
	 * @param maxSecondsToWait for element to display
	 * @return true if element is found within max time to wait
	 */
	public boolean waitForElementToDisplay(By locator, long maxSecondsToWait) {
		for (int i = 0; i < maxSecondsToWait; i++) {
			try {
				Thread.sleep(1000);
				if (isElementCurrentlyDisplayed(locator)) {
					return true;
				}
			} catch (Exception e) {
				// do nothing, let it keep looping to wait for object
			}
		}
		return false;
	}

	public boolean selectByValueinDropDown(By element, String value) throws Exception {
		int retry = 0;
		try {
			logger.info("Selecting value : " + value + " : from dropdown using element : " + element);
			try {
				waitUntilElementIsClickable(element, ConfigFileReader.getConfigFileReader().getExplicitlyWait());
			} catch (Exception e) {
				//do nothing, still try to select value in dropdown
			}
			logger.info("Element is clickable with condition Explicit wait");
			WebElement webElement = driver.findElement(element);
			retry++;
			if (webElement.isEnabled() && webElement.isDisplayed()) {
				logger.info("Element is enabled or displayed in page");
//				if (world.getBrowser().equalsIgnoreCase(String.valueOf(Constants.BrowserName.EDGE))) {
//					return selectValueUsingJavaScript(element, value);
//				} else {
					Select s = new Select(webElement);
					s.selectByValue(value);
					return true;
//				}
			} else {
				logger.info("Failed Selecting value : " + value + " : from dropdown using element : " + element);
				throw new AppIssueException("Element not displayed:" + element);
			}
		} catch (ElementNotInteractableException e) {
			logger.info("Failed Selecting value : " + value + " : from dropdown using element : " + element);
			throw new AppIssueException("Element not interactable:" + element, e);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			logger.info("Failed Selecting value : " + value + " : from dropdown using element : " + element);
			throw new AppIssueException("The element is not present on screen:" + element, e);
		} catch (WebDriverException e) {
			logger.info("By-Passing the web driver exception on sauce lab " + e.getMessage());
			if (retry < 2 && e.getMessage().contains("Missing Template ERR_CONNECT_FAIL"))
				return selectByValueinDropDown(element, value);
			else
				throw new Exception("Failed to select drop down value");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Failed Selecting value : " + value + " : from dropdown using element : " + element);
			throw new Exception("There is an issue when trying to click, please check logs:" + element, e);
		}
	}

	public String getSelectedValueTextFromDropdown(By element) throws Exception {
		try {
			logger.info("Getting selected value from dropdown");
			new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait())
					.until(ExpectedConditions.visibilityOfElementLocated(element));
			logger.info("Element is clickable with condition Implict wait");
			WebElement webelement = driver.findElement(element);
			if (webelement.isEnabled() && webelement.isDisplayed()) {
				logger.info("Element is enabled or displayed in page");
				Select s = new Select(webelement);
				return s.getFirstSelectedOption().getText();
			} else {
				logger.info("Failed to getting selected value from dropdown");
				throw new Exception("Element is not displayed:" + element);
			}
		} catch (Exception e) {
			logger.info("Error : Failed to getting selected value from dropdown");
			throw new Exception("Value is not selected:" + element, e);
		}
	}

	public String getTextFromElement(By element) throws Exception {
		try {
			logger.info("Getting text from element : " + element + "");
			String innerText =  new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait())
					.until(ExpectedConditions.visibilityOfElementLocated(element)).getText().trim();
			logger.info("The Inner Text Of An Element is : "+innerText);
			return innerText;
		} catch(StaleElementReferenceException e){
			return new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait()).until(ExpectedConditions.visibilityOfElementLocated(element)).getText();
		} catch(WebDriverException e) {
			e.printStackTrace();
			logger.info("By Passing the exception on sauce lab " + e.getMessage());
			if (e.getMessage().contains("Missing Template ERR_CONNECT_FAIL"))
				return new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait()).until(ExpectedConditions.visibilityOfElementLocated(element)).getText();
			else
				throw (e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Error : Failed to getting text from element");
			throw new Exception("Failed to getting text from element:" + element, e);
		}
	}

	public boolean selectByIndexinDropDown(By element, int index) throws Exception {
		try {
			logger.info("Select value from dropdown using element : " + element + " using index : " + index + "");
			try {
				waitUntilElementIsClickable(element, ConfigFileReader.getConfigFileReader().getExplicitlyWait());
			} catch(Exception e) {
				//still try to select item
			}
			//scrollElementIntoViewJS(element);
			logger.info("Element is clickable with condition Implict wait");
			WebElement webElement = driver.findElement(element);
			if (webElement.isEnabled() && webElement.isDisplayed()) {
				logger.info("Element is enabled or displayed in page");
				Select s = new Select(webElement);
				s.selectByIndex(index);
				return true;
			} else {
				logger.info("Failed to select value from dropdown using index");
				throw new AppIssueException("Element not displayed:" + element);
			}
		} catch (ElementNotInteractableException e) {
			logger.info("Failed Selecting value : " + index + " : from dropdown using element : " + element);
			throw new AppIssueException("Element not interactable:" + element, e);
		} catch (WebDriverException e){
			if(e.getMessage().contains("Missing Template ERR_CONNECT_FAIL")){
				new Select(new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait()).until(ExpectedConditions.visibilityOfElementLocated(element))).selectByIndex(index);
				return true;
			} else  throw (e);
		} catch (Exception e) {
			logger.info("Failed Selecting value : " + index + " : from dropdown using element : " + element);
			throw new Exception("There is an issue when trying to click, please check logs:" + element, e);
		}
	}

	public String getAttributeValueFromElement(By element, String attribute) throws Exception {
		try {
			logger.info("Getting attribute : " + attribute + " value using by element : " + element + " ");
			new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait())
					.until(ExpectedConditions.visibilityOfElementLocated(element));
			logger.info("Visiblity of Element is located as expected with condition Implict wait");
			WebElement webElement = driver.findElement(element);
			if (webElement.isEnabled() && webElement.isDisplayed()) {
				logger.info("Element is enabled or displayed in page");
				return webElement.getAttribute(attribute);
			} else {
				logger.info("Failed to getting attribute : " + attribute + " value using by element : " + element + "");
				throw new Exception("Element is not displayed / enabled:" + element);
			}
		} catch (WebDriverException e){
			if(e.getMessage().contains("Missing Template ERR_CONNECT_FAIL"))
				return new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait()).until(ExpectedConditions.visibilityOfElementLocated(element)).getAttribute(attribute);
			else throw (e);
		} catch (Exception e) {
			logger.info(
					"Error : Failed to getting attribute : " + attribute + " value using by element : " + element + "");
			throw new Exception("Not able to get element attribute value due to:" + element + e.getMessage(), e);
		}
	}

	public boolean scrollToElementAndClick(WebDriver driver, By locator) throws Exception {
		try {
			logger.info("Scroll to element and perform click operation");
			WebDriverWait wait = new WebDriverWait(driver, 20);
			Actions actions = new Actions(driver);
			if (!(world.getBrowser().equalsIgnoreCase("firefox") || (world.getBrowser().equalsIgnoreCase("safari"))))
				actions.moveToElement(wait.until(ExpectedConditions.elementToBeClickable(locator))).build().perform();
			logger.info("Element is clicked successfully");
			click(locator);
			return true;
		} catch (MoveTargetOutOfBoundsException e) {
			return clickElementUsingJavaScript(locator);
		} catch (Exception exception) {
			return false;
		}
	}

	public boolean scrollToElementAndClick(WebDriver driver, By locator, int seconds) {
		try {
			logger.info("Scroll to element and perform click operation");
			WebDriverWait wait = new WebDriverWait(driver, seconds);
			Actions actions = new Actions(driver);
			actions.moveToElement(wait.until(ExpectedConditions.elementToBeClickable(locator))).build().perform();
			click(locator);
			logger.info("Element is clicked successfully");
			return true;
		} catch (Exception exception) {
			return false;
		}
	}

	public boolean verifyElementPresence(By element) throws Exception {
		try {
			logger.info("Verification of element presence status");
			new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait())
					.until(ExpectedConditions.presenceOfElementLocated(element));
			logger.info("Visibility of Element is located as expected with condition Implicit wait");
			return true;
		} catch (WebDriverException e) {
		    e.printStackTrace();
			logger.info("By Passing the exception on sauce lab " + e.getMessage());
			if (e.getMessage().contains("Missing Template ERR_CONNECT_FAIL")) {
				new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait())
						.until(ExpectedConditions.presenceOfElementLocated(element));
				return true;
			} else
				throw (e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Error : Failed to verify element presence status");
			throw new Exception("Element not present on page:" + element, e);
		}
	}

	public boolean isElementDisplayed(By element) throws Exception {
		boolean isDisplayed = false;
		int retry = 0;
		try {
			logger.info("Verification of element displayed status");
			new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait())
					.until(ExpectedConditions.visibilityOfElementLocated(element));
			logger.info("Visibility of Element is located as expected with condition Implicit wait");
			retry++;
			WebElement webElement = driver.findElement(element);
			if (webElement.isEnabled() && webElement.isDisplayed()) {
				logger.info("Element is enabled or displayed in page:" + element);
				return isDisplayed = true;
			}
		} catch (WebDriverException e) {
			logger.info("By Passing the exception on sauce lab " + e.getMessage());
			if (retry < 2 && e.getMessage().contains("Missing Template ERR_CONNECT_FAIL"))
				isElementDisplayed(element);
		} catch (Exception e) {
			return isDisplayed;
		}
		return isDisplayed;
	}

	public boolean scrollTillEndOfPage() throws Exception {
		boolean isDisplayed = false;
		try {
			logger.info("Scroll to curser to Height");
			((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
			return true;
		} catch (Exception e) {
			return isDisplayed;
		}
	}

	public boolean selectFromMobileFauxDropDown(String xPath, String valueToSelect) throws Exception {
		boolean isDisplayed = false;
		try {
			logger.info("Select from mobile faux dropdown using xpath with value : " + valueToSelect + "");
			logger.info("Cliking link using xpath : " + xPath
					+ " with appended framed path : //following::div[@class='select-faux mod show-for-small-only']//li");
			if(world.getBrowser().equalsIgnoreCase("safari")&& world.isMobile()){
				clickByJS(By.xpath(xPath + "//following::div[@class='select-faux mod show-for-small-only']//li"));

			}else {
				click(By.xpath(xPath + "//following::div[@class='select-faux mod show-for-small-only']//li"));
			}
			Thread.sleep(500);
			logger.info(
					"Clicking element using xpath and sending @data-value attribute value as : " + valueToSelect + "");
			return click(By.xpath("//li[@data-value='" + valueToSelect + "']"));
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return false;
	}

	public boolean acceptAddressValidationPopup() throws Exception {
		try {
			return click(By.cssSelector(".address-validation-modal .submit"));
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return false;
	}

	public boolean dismissAddressValidationPopup() throws Exception {
		try {
			return clickIfPresent(By.cssSelector(".flip-modal"));
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return false;
	}

	public boolean selectByIndexFromMobileFauxDropDown(String xPath, int index) throws Exception {
		boolean isDisplayed = false;
		try {
			if (world.isMobile()) {
				scrollToElementAndClick(driver,
						By.xpath(xPath + "//following::div[contains(@class,'select-faux')]//li"));
				syncObjects("mediumWait");
				List<WebElement> prodcuctOptions = driver.findElements(By.xpath("//*[@class='drawer-overlay']//li"));
				for (WebElement productOpt : prodcuctOptions) {
					String optClass = productOpt.getAttribute("class");
					if (productOpt.getAttribute("class").equalsIgnoreCase(" ")) {
						if (!productOpt.getAttribute("data-value").isEmpty()) {
							productOpt.click();
							break;
						}
					} else if (productOpt.getAttribute("class").toLowerCase().contains("active")) {
						if (!productOpt.getAttribute("data-value").isEmpty()) {
							productOpt.click();
							break;
						}
					}
				}
				return true;
			} else
				return selectByIndexinDropDown(By.xpath(xPath), index);
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return false;
	}

	public String returnPageTitle() throws Exception {
		String title = "";
		try {
			title = driver.getTitle().trim();
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return title;
	}

	public boolean selectByIdDropdownByValueJS(String dropDownElement, String value) throws Exception {
		boolean isDisplayed = false;
		try {
			((JavascriptExecutor) driver)
					.executeScript("document.getElementById('" + dropDownElement + "').value = '" + value + "'");
			return true;
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
		}
		return isDisplayed;
	}

	public boolean enterTextByJS(By locator, String value) throws Exception {
		try {
			logger.info("enter text by js");
			WebElement element = driver.findElement(locator);
			element.clear();
			JavascriptExecutor exe = (JavascriptExecutor) driver;
			exe.executeScript("arguments[0].value=arguments[1];", element, value);
			return true;
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
		}
		return false;
	}

	public boolean selectByNameDropdownByValueJS(String dropDownElement, String value, String elementIndex)
			throws Exception {
		boolean isDisplayed = false;
		try {
			((JavascriptExecutor) driver).executeScript("document.getElementsByName('" + dropDownElement + "')["
					+ elementIndex + "].value = '" + value + "'");
			return true;
		} catch (Exception e) {
			return isDisplayed;
		}
	}

	public boolean mouseHoverAndClickElement(By element) throws Exception {
		try {
			WebElement webElement = driver.findElement(element);
			Actions act = new Actions(driver);
			act.moveToElement(webElement).click().build().perform();
			return true;
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return false;
	}

	public String getWebElementTextUsingJS(String xpath) throws Exception {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		String javaScriptTOExecute = "document.evaluate(\"" + xpath
				+ "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.textContent";
		String popupText = executor.executeScript(javaScriptTOExecute).toString().trim();
		return popupText;
	}

	public boolean selectValueUsingJavaScript(By locator, String value) throws Exception {
		boolean textEntered = false;
		try {
			WebElement element = driver.findElement(locator);
			JavascriptExecutor exe = (JavascriptExecutor) driver;
			exe.executeScript("var select = arguments[0]; " + "for(var i = 0; i < select.options.length; i++)" + "{ "
					+ "if(select.options[i].value == arguments[1])" + "{ " + "select.options[i].selected = true; "
					+ "} " + "}", element, value);
			textEntered = true;
		} catch (Exception e) {
			throw new Exception("Failed to select value due to" + e.getMessage());
		}
		return textEntered;
	}

	public boolean clickElementByIdUsingJavaScript(String id) throws Exception {
		boolean result = false;
		try {
			JavascriptExecutor jse = (JavascriptExecutor) world.driver;
			jse.executeScript("document.getElementById('" + id + "').click();");
			result = true;
		} catch (Exception e) {

			throw new Exception("Failed to select value due to" + e.getMessage());
		}
		return result;
	}

	public boolean clickElementUsingJavaScript(By locator) throws Exception {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
			try {
				try {
					waitUntilElementIsClickable(locator, ConfigFileReader.getConfigFileReader().getExplicitlyWait());
				} catch (Exception e) {
					//do nothing, continue to try and click element
				}
				scrollElementIntoViewJS(locator);
				jse.executeScript("arguments[0].click();", driver.findElement(locator));
				return true;
			} catch (TimeoutException e) {
				throw new Exception("Element " + locator.toString() + " was not found\n" + e.getMessage(), e);
			} catch (WebDriverException e) {
                if (e.getMessage().contains("JavaScript error")) {
                    logger.warn("Skipping exception with JavaScript error");
                } else if (!e.getMessage().contains("Missing Template ERR_CONNECT_FAIL")) {
                    logger.info("Failed to click: " + locator + " by javascript. Retrying..");
                    jse.executeScript("arguments[0].click();", new WebDriverWait(driver,ConfigFileReader.getConfigFileReader().getExplicitlyWait()).until(ExpectedConditions.elementToBeClickable(locator)));
                    return  true;
                } else throw new Exception("Web driver exception clicking element with javascript " + locator.toString() + "\n" + e.getMessage());
			} catch (Exception e) {
                failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
			}
		return false;
	}

	public boolean clickElementUsingJavaScriptNoWait(By locator) throws Exception {
		try {
			scrollElementIntoViewJS(locator);
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].click();", driver.findElement(locator));
			return true;
		} catch(Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return false;
	}

	public boolean scrollTillTopOfPage() throws Exception {
		boolean isDisplayed = false;
		try {
			((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
			Thread.sleep(1000);
			return true;
		} catch (Exception e) {
			return isDisplayed;
		}
	}

	public void syncObjects(String waitTime) throws Exception {
		int waitSecs = 0;
		if (waitTime.toLowerCase().startsWith("hcwait")) {
			waitTime = waitTime.toLowerCase();
			waitSecs = Integer.parseInt(waitTime.replace("hcwait", ""));
			logger.info("Hard Wait ("+waitSecs+" secs.)");
			Thread.sleep(waitSecs * 1000);
		} else {
			waitSecs = (int) (ConfigFileReader.getConfigFileReader().getWaitTime(waitTime) / 1000);
		}
		// Thread.sleep(ConfigFileReader.getConfigFileReader().getWaitTime(waitTime));
	}

	public void syncObjects(String waitTime, int numberMinutes) throws Exception {
		Thread.sleep(ConfigFileReader.getConfigFileReader().getWaitTime(waitTime) * numberMinutes);
	}

	public void changeEvent(By locator) {
		((RemoteWebDriver) world.driver).executeScript("$(arguments[0]).change(); return true;",
				world.driver.findElement(locator));
	}

	public void reactChangeEvent(String elementID) {
		((JavascriptExecutor) world.driver).executeScript("document.getElementById('"+elementID+"').dispatchEvent(new Event('change', { bubbles: true }));");
    }

	/**
	 * @Description: Failed to wait until element click using by locator
	 * @param driver
	 * @param locator
	 * @param seconds
	 * @throws Exception
	 */
	/*
	 * public void waitUntilElementClickableUsingByPath(WebDriver driver, By
	 * locator, int seconds) throws Exception { try { new WebDriverWait(driver,
	 * seconds).until(ExpectedConditions.elementToBeClickable(locator)); } catch
	 * (Exception e) { e.printStackTrace(); throw new
	 * Exception("ERROR : Failed wait until element clickcable"); } }
	 */

	/**
	 * @Description Failed to wait until element click using by locator
	 * @param driver
	 * @param element
	 * @param seconds
	 * @throws Exception
	 */
	/*
	 * public void waitUntilElementClickableUsingByElement(WebDriver driver,
	 * WebElement element, int seconds) throws Exception { try { new
	 * WebDriverWait(driver,
	 * seconds).until(ExpectedConditions.elementToBeClickable(element)); } catch
	 * (Exception e) { e.printStackTrace(); throw new
	 * Exception("ERROR : Failed wait until element clickcable"); } }
	 */

	/**
	 * @Description : Getting element using By locator
	 * @param driver
	 * @param locator
	 * @return
	 * @throws Exception
	 */
	public WebElement getElemntUsingByLocator(WebDriver driver, By locator) throws Exception {
		WebElement element = null;
		try {
			element = driver.findElement(locator);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("ERROR : Failed to Get element using by locator");
		}
		return element;
	}

	public void pause(long ms) {
		try {
			logger.info("Hard Wait ("+ms+" ms.)");
			Thread.sleep(ms);
		} catch (InterruptedException ie) {
		}
	}

	public void waitForElementInvisibility(By locator) {
		try {
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			pause(1000);
			WebElement element = driver.findElement(locator);
			if (element.isDisplayed())
				new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait())
						.until(ExpectedConditions.invisibilityOf(element));
		} catch (NoSuchElementException nse) {
		} finally {
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		}
	}

	public void waitForElementInvisibility(By locator,long timeoutSecs) {
		try {
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			pause(1000);
			WebElement element = driver.findElement(locator);
			if (element.isDisplayed())
				new WebDriverWait(driver, timeoutSecs)
						.until(ExpectedConditions.invisibilityOf(element));
		} catch (NoSuchElementException nse) {
		} finally {
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		}
	}

	public void waitForURLContains(String subURL) {
		new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait())
				.until(ExpectedConditions.urlContains(subURL));
	}

	public void waitForURLEquals(String expectedURL) {
		new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait())
				.until(ExpectedConditions.urlToBe(expectedURL));
	}

	public boolean verifyURL(String url) {
		try {
			waitForURLEquals(url);
		} catch(Exception e) {
			//do nothing, finish check and return boolean
		}
		logger.info("Verify expected URL: " + url);
		logger.info("Actual URL: " + driver.getCurrentUrl());
		return driver.getCurrentUrl().equalsIgnoreCase(url);
	}

	public boolean verifyURLContains(String url) {
		try {
			waitForURLContains(url);
		} catch(Exception e) {
			//do nothing, finish check and return boolean
		}
		logger.info("Verify URL contains: " + url);
		logger.info("Actual URL: " + driver.getCurrentUrl());
		return driver.getCurrentUrl().contains(url);
	}

	public void waitForElementTextToBe(By locator, String expcetedText) {
		new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait())
				.until(ExpectedConditions.textToBe(locator, expcetedText));
	}

	public void waitForElementTextToBeNonNull(By locator) {
		for (int i = 0; i < 3; i++) {
			try {
				WebElement webElement = driver.findElement(locator);
				if (!webElement.getText().equals("")) {
					return;
				}
				//keep looping
				logger.info("Element text not found, looping again to wait for it to populate.");
				sleep(10000);
			} catch (Exception e) {
				//do nothing unless on the final loop, let loop keep running to wait for text
				failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName() + "\n", e);
			}
		}
	}

	public boolean selectByVisibleTextinDropDown(By element, String value) throws Exception {
		try {
			logger.info("Selecting value : " + value + " : from dropdown using element : " + element);
			waitUntilElementIsClickable(element, ConfigFileReader.getConfigFileReader().getExplicitlyWait());
			Select s = new Select(driver.findElement(element));
			s.selectByVisibleText(value);
			return true;
		} catch (ElementNotInteractableException e) {
			logger.info("Failed Selecting value : " + value + " : from dropdown using element : " + element);
			throw new AppIssueException("Element not interactable:" + element, e);
		} catch (Exception e) {
			logger.info("Failed Selecting value : " + value + " : from dropdown using element : " + element);
			throw new Exception("There is an issue when trying to click, please check logs:" + element, e);
		}
	}

	public boolean selectByVisibleTextInDropDownWithoutWait(By element, String value) throws Exception {
		try {
			logger.info("Selecting value : " + value + " : from dropdown using element : " + element);
			waitUntilElementIsClickable(element, ConfigFileReader.getConfigFileReader().getExplicitlyWait());
			syncObjects("mediumWait");
			final Select s = new Select(driver.findElement(element));
			s.selectByVisibleText(value);
			return true;
		} catch (ElementNotInteractableException e) {
			logger.info("Failed Selecting value : " + value + " : from dropdown using element : " + element);
			throw new AppIssueException("Element not interactable:" + element, e);
		} catch (Exception e) {
			logger.info("Failed Selecting value : " + value + " : from dropdown using element : " + element);
			throw new Exception("There is an issue when trying to click, please check logs:" + element, e);
		}
	}

	public void switchToChildWindow() {
		parentWindow = driver.getWindowHandle();
		driver.getWindowHandles().forEach(x -> {
			if (!x.equalsIgnoreCase(parentWindow))
				driver.switchTo().window(x);
		});
	}

	public boolean focusOnFirstBrowserTab() {
		boolean result = false;
		try {
			logger.info("Focus on first browser tab");
			List<String> browserTabs = new ArrayList<String>(world.driver.getWindowHandles());
			world.driver.switchTo().window(browserTabs.get(0));
			result = true;
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return result;
	}

	public void switchToParentWindow() {
		driver.switchTo().window(parentWindow);
	}

	public boolean clickAndVerifyNewTabStatus(By locatorToClick, By locatorOfElementToVerifyInNewTab) throws Exception {
		try {
			click(locatorToClick);
			switchToChildWindow();
			boolean status = isElementDisplayed(locatorOfElementToVerifyInNewTab);
			switchToParentWindow();
			return status;
		} catch (Exception e) {
			throw new Exception("ERROR: New tab or Window not found.");
		}
	}

	public boolean isElementLeftToRespectiveElement(By leftElement, By rightElement) throws Exception {
		try {
			waitUntilElementIsClickable(rightElement, ConfigFileReader.getConfigFileReader().getExplicitlyWait());
			return world.driver.findElement(leftElement).getLocation().getX() < world.driver.findElement(rightElement)
					.getLocation().getX();
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return false;
	}

	public boolean clickIfPresent(By locator) throws Exception {
		try {
			List<WebElement> webElements = new ArrayList<>();
			try {
				webElements = driver.findElements(locator);
				logger.info("Found "+webElements.size() +" elements with the locator"+ locator);
			}catch (WebDriverException e){
				if(e.getMessage().contains(" Missing Template ERR_CONNECT_FAIL")) {
					syncObjects("hcwait5");
					webElements = driver.findElements(locator);
				}
			}
            boolean status = false;
            if (webElements.size() > 0) {
                for (WebElement webElement : webElements) {
					logger.info("element displayed status "+webElement.isDisplayed());
                    if (webElement.isDisplayed()) {
                        webElement.click();
                        status = true;
                        break;
                    }
                }
                return status;
            } else {
                return status;
            }
        }catch (Exception e){
		    e.printStackTrace();
		    throw new Exception(e.getMessage());
        }
	}

	public boolean clickIfPresentWithJavaScript(By locator) throws Exception {
		try {
			List<WebElement> webElements = null;
			try {
				webElements = driver.findElements(locator);
				logger.info("Found "+webElements.size() +" elements with the locator"+ locator);
			}catch (WebDriverException e){
				if(e.getMessage().contains(" Missing Template ERR_CONNECT_FAIL")) {
					syncObjects("hcwait5");
					webElements = driver.findElements(locator);
				}
			}
			boolean status = false;
			if (webElements.size() > 0) {
				for (WebElement webElement : webElements) {
					logger.info("element displayed status "+webElement.isDisplayed());
					if (webElement.isDisplayed()) {
						JavascriptExecutor jsexecutor = (JavascriptExecutor) driver;
						jsexecutor.executeScript("arguments[0].click();",webElement);
						status = true;
						break;
					}
				}
				return status;
			} else {
				return status;
			}
		}catch (Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	protected By xp(ResourceBundle res, String label) {
		By element = By.xpath(res.getString(label));

		if (element == null) {
			logger.error("Resource:\t" + label + " is null");
		}
		return element;
	}

	public boolean verifyPopUpMessage(By locator, String expectedMessage) throws Exception {
		try {
			if (expectedMessage.equalsIgnoreCase(getTextFromElement(locator).trim())) {
				return true;
			} else {
				throw new AppIssueException("Popup does not have the expected message");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("ERROR : Failed to verify message in popup");
		}
	}

	/*
	 * compare two prices with quantity
	 */
	public boolean compareTwoPrices(String firstPrice, String secondPrice, String qty) throws Exception {
		try {
			logger.info("comparing the two prices");
			Double quantity = Double.parseDouble(qty.trim());
			Double retailsPrice = Double.parseDouble(firstPrice.trim());
			Double subtotal = Double.parseDouble(secondPrice.trim());
			if (retailsPrice * quantity == subtotal) {
				return true;
			} else {
				throw new AppIssueException("Retail price and subtotal are not same in OrderSummery on shipping page \nSubtotal: "+subtotal +"Retail With Quantity: "+retailsPrice*quantity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			failScenarioAndReportInfo("Verify Prices\n", e);
		}
		return false;
	}

	/*
	 * compare two prices with quantity
	 */
	public boolean compareTwoPrices(String firstPrice, String secondPrice) throws Exception {
		try {
			logger.info("comparing the two prices");
			Double retailsPrice = Double.parseDouble(firstPrice.trim());
			Double subtotal = Double.parseDouble(secondPrice.trim());
			if (retailsPrice.equals(subtotal)) {
				return true;
			} else {
				throw new AppIssueException("Retail price (" + retailsPrice + ") and subtotal (" + subtotal
						+ ") are not same in OrderSummery on shippingpage");
			}
		} catch (Exception e) {
			e.printStackTrace();
			failScenarioAndReportInfo("Verify Prices\n", e);
		}
		return false;
	}

	/**
	 * @Description : Verify expected message with text from Element
	 * @param locator,ExpectedMessage
	 * @return
	 * @throws Exception
	 */
	public boolean verifyMessageWithElementText(By locator, String expectedMessage) throws Exception {
		String actualMessage = "";
		try {
			pause(2000);
			scrollElementIntoViewJS(locator);
			actualMessage = getTextFromElement(locator).trim();
			if (expectedMessage.equalsIgnoreCase(actualMessage)) {
				return true;
			} else {
				throw new AppIssueException("Expected message ("+expectedMessage+") not matched with actual ("+actualMessage+")");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception( e.getMessage() +"\n"+
					"ERROR : Failed to verify message. \nEXPECTED: " + expectedMessage + "\n ACTUAL: " + actualMessage);
		}
	}

	public boolean verifyCheckboxStatus(By locator) throws Exception {
		try {
			logger.info("Validating checkbox status");
			boolean Checked = world.driver.findElement(locator).isSelected();
			if (!Checked == true) {
				return true;
			} else {
				throw new AppIssueException("Checkbox selected by default");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("ERROR : Failed to verify status of checkbox");
		}
	}

	public boolean getSelectedStatus(By locator) throws Exception {
		try {
			logger.info("Getting selection status: " + locator + " " + world.driver.findElement(locator).isSelected());
			return world.driver.findElement(locator).isSelected();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("ERROR : Failed to get selected status of element: " + locator);
		}
	}

	public void failScenarioAndReportInfo(String info, Exception exception) {
		try {
			checkForAppIssue();
		} catch (Exception e) {
			exception = e;
		}
		if (!(exception instanceof RuntimeException)) {
			info = exception.getClass().getSimpleName() + " occurred in \n" + info;
		}

		logger.info(info);
		throw new RuntimeException(info + "\n" + exception.getMessage());
	}

	public void failScenarioAndReportInfo(String info) {
		try {
			checkForAppIssue();
		} catch (Exception e) {
			if (!(e instanceof RuntimeException)) {
				info = e.getClass().getSimpleName() + " occurred in \n" + info;
			}
		}
		logger.info(info);
		throw new RuntimeException(info);
	}

	public boolean acceptAlert() throws Exception {
		boolean result = false;
		try {
			logger.info("Entered Alert method");
			new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait())
					.until(ExpectedConditions.alertIsPresent());
			// Using Alert class to first switch to or focus to the alert box
			Alert alert = driver.switchTo().alert();
			logger.info("Need to Handle Alert");
			// Using accept() method to accept the alert box
			alert.accept();
			logger.info("Handled Alert");
			return true;
		} catch (NoAlertPresentException e) {
			logger.info("No Alert Present");
		}
		return result;
	}

//	public void acceptAlert() throws Exception {
//			//Using Alert class to first switch to or focus to the alert box
//			Alert alert = driver.switchTo().alert();
//			//Using accept() method to accept the alert box
//			alert.accept();
//	}

	public boolean getCheckBoxStatus(By locator) {
		boolean isCheked = false;
		try {
			WebElement element = driver.findElement(locator);
			isCheked = element.isSelected();

		} catch (Exception e) {
			e.printStackTrace();
			failScenarioAndReportInfo("Failed to get status of checkbox", e);
		}
		return isCheked;
	}

	/*
	 * calculating the discount on the given price
	 */
	public double getDiscountPrice(String price, String percentage) throws Exception {
		try {
			logger.info("calculating the discount percentage on price");
			double percentageValue = Double.parseDouble(percentage);
			double priceValue = Double.parseDouble(price);
			double finalDiscountPrice = priceValue * percentageValue / 100;
			return priceValue - finalDiscountPrice;
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return 0;
	}

	public void setLocale(String language, String country) {
		theLocale = new Locale(language, country);
		logger.info(TextUtils.format("Set Locale", theLocale.getDisplayName()));
		getLocale();
		Locale.setDefault(new Locale(language, country));
	}

	public Locale getLocale() {
		logger.info(TextUtils.format("Get Locale", theLocale.getDisplayName()));
		return Locale.getDefault();
	}

	public By getLocaleResource(String label) {
		String path = "com.ted.automation.elementlib.testdata.LoginPage_" + Locale.getDefault().getLanguage() + "_"
				+ Locale.getDefault().getCountry();
		System.out.println(path);
		ResourceBundle bundle = ResourceBundle.getBundle(path);
		String s = bundle.getString(label);

		logger.info(TextUtils.format("Label", label));
		logger.info(TextUtils.format("Resource", s));

		return By.xpath(s);
	}

	public String getLocaleTxtResource(String label) {
		String path = "com.ted.automation.elementlib.testdata.LoginPage_" + getLocale().getLanguage() + "_"
				+ getLocale().getCountry();
		System.out.println(path);
		ResourceBundle bundle = ResourceBundle.getBundle(path);
		String s = bundle.getString(label);

		logger.info(TextUtils.format("Label", label));
		logger.info(TextUtils.format("Resource", s));

		return s;
	}

	/**
	 * Scrolls element to top of view via Javascript
	 *
	 * @param selector
	 * @return Boolean
	 * @throws Exception
	 */
	public boolean scrollElementIntoViewJS(By selector) throws Exception {
		JavascriptExecutor exe = (JavascriptExecutor) driver;
		try {
			logger.info("Scrolling Element into view by JS");
			exe.executeScript("arguments[0].scrollIntoView(true);", new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getImplicitlyWait()).until(ExpectedConditions.presenceOfElementLocated(selector)));
			logger.info("Element is scrolled to successfully");
			return true;
		} catch (WebDriverException e){
			if (e.getMessage().contains(" Missing Template ERR_CONNECT_FAIL")) {
				exe.executeScript("arguments[0].scrollIntoView(true);", new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait()).until(ExpectedConditions.presenceOfElementLocated(selector)));
				return true;
			} else throw (e);
		} catch (Exception e) {
			logger.info("Could not scroll element" + selector.toString() + " into view by JS");
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return false;
	}

	/**
	 * Scrolls the element into view via Javascript and can scroll to "bottom" or
	 * "top" of view
	 *
	 * @param selector
	 * @param orientation
	 * @return Boolean
	 * @throws Exception
	 */
	public boolean scrollElementIntoViewJS(By selector, String orientation) throws Exception {
		JavascriptExecutor exe = (JavascriptExecutor) driver;
		try {
			logger.info("Scrolling Element into view by JS");
			// defaults to top
			boolean location = true;
			if (orientation.equalsIgnoreCase("bottom") || orientation.equalsIgnoreCase("end")
					|| orientation.equalsIgnoreCase("false")) {
				location = false;
			}
			WebElement element = driver.findElement(selector);
			exe.executeScript("arguments[0].scrollIntoView(" + location + ");", element);
			logger.info("Element is scrolled to successfully");
			return true;
		} catch (WebDriverException e){
			if (e.getMessage().contains(" Missing Template ERR_CONNECT_FAIL")) {
				exe.executeScript("arguments[0].scrollIntoView(true);", new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait()).until(ExpectedConditions.visibilityOfElementLocated(selector)));
				return true;
			} else throw (e);
		} catch (Exception e) {
			throw new Exception("Could not scroll element " + selector + " into view by JS", e);
		}
	}

	public boolean verifyElementDisplayedWithWait(By element) throws Exception {
		boolean isDisplayed = false;
		try {
			logger.info("Verification of element displayed status");
			new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait())
					.until(ExpectedConditions.visibilityOfElementLocated(element));
			logger.info("Visibility of Element is located as expected with condition Implicit wait");
			WebElement webElement = driver.findElement(element);
			if (webElement.isEnabled() && webElement.isDisplayed()) {
				logger.info("Element is enabled or displayed in page:" + element);
				return isDisplayed = true;
			}
		} catch (Exception e) {
			return isDisplayed;
		}
		return isDisplayed;
	}

	public boolean verifyElementDisplayedWithWait(By element, long secondsToWait) throws Exception {
		boolean isDisplayed = false;
		try {
			logger.info("Verification of element displayed status");
			WebElement webElement  = new WebDriverWait(driver, secondsToWait).until(ExpectedConditions.visibilityOfElementLocated(element));
			logger.info("Visibility of Element is located as expected with condition Implicit wait");
			if (webElement.isEnabled() && webElement.isDisplayed()) {
				logger.info("Element is enabled or displayed in page:" + element);
				return isDisplayed = true;
			}
		} catch (Exception e) {
			return isDisplayed;
		}
		return isDisplayed;
	}

	/**
	 * Wait for element to become clickable by locator
	 *
	 * @param locator the locator for an element for which to wait to become
	 *                clickable
	 * @param seconds the number of seconds to wait
	 * @return true if element is found to be clickable in time
	 * @throws Exception
	 * @Description: Failed to wait until element click using by locator
	 */
	public boolean waitUntilElementIsClickable(By locator, long seconds) throws Exception {
		WebElement element = null;
		try {
			element = driver.findElement(locator);
			new WebDriverWait(driver, seconds).until(ExpectedConditions.elementToBeClickable(locator));
		} catch (Exception e) {
			logger.info("Failed to wait for element to be clickable");
			throw e;
		}
		return true;
	}

	/**
	 * Wait for element to be clickable
	 *
	 * @param element the element to wait for to become clickable
	 * @param seconds number of seconds to wait
	 * @return true if element is found to be clickable in time
	 * @throws Exception
	 * @Description Failed to wait until element click using by locator
	 */
	public boolean waitUntilElementIsClickable(WebElement element, long seconds) throws Exception {
		try {
			new WebDriverWait(driver, seconds).until(ExpectedConditions.elementToBeClickable(element));
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("ERROR : Failed wait until element clickable");
		}
		return true;
	}

	/**
	 * Searches the Directory passed in for the File passed in based on name
	 *
	 * @param downloadPath
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public boolean isFileNameDownloaded(String downloadPath, String fileName) throws Exception {
		try {
			File dir = new File(downloadPath);
			File[] dir_contents = dir.listFiles();
			for (int i = 0; i < dir_contents.length; i++) {
				if (dir_contents[i].getName().equalsIgnoreCase(fileName)) {
					logger.info("Found File: " + fileName);
					return true;
				}
			}
			logger.info("Could not find " + fileName + " in Directory: " + downloadPath);
			return false;
		} catch (Exception e) {
			throw new Exception("Error occurred with the File or Path Specified check " + e);
		}
	}

	/**
	 * Searches supplied Directory for files with specified file extension
	 *
	 * @param downloadPath
	 * @param fileExtension
	 * @return
	 * @throws Exception
	 */
	public boolean isFileExtensionDownloaded(String downloadPath, String fileExtension) throws Exception {
		try {
			File[] listOfFiles = null;
			int count = 0;
			do {
				logger.info("Searching Directory for Files");
				File directory = new File(downloadPath);
				listOfFiles = directory.listFiles();
				count++;
				pause(3000);
			} while (listOfFiles.length <= 0 && count < 30);

			for (File listOfFile : listOfFiles) {
				logger.info("Found Files, checking they match file extension: " + fileExtension);
				if (listOfFile.isFile()) {
					String fileName = listOfFile.getName();
					logger.info("Found file: " + fileName);
					if (fileName.contains(fileExtension)) {
						logger.info("File Matches file extension");
						File file = new File(downloadPath + fileName);
						if (file.exists()) {
							return true;
						}
					}
				}
			}
			logger.info("Could Not find Export Report File by File Extension: " + fileExtension);
			return false;
		} catch (Exception e) {
			throw new Exception("Error occurred with the File Extension : " + fileExtension + " in Directory: "
					+ downloadPath + "Error: " + e);
		}
	}

	/**
	 * Searches supplied Directory for files with specified file extension and
	 * deletes the file
	 *
	 * @param downloadPath
	 * @param fileExtension
	 * @return
	 * @throws Exception
	 */
	public boolean isFileExtensionDownloaded(String downloadPath, String fileExtension, boolean delete)
			throws Exception {
		try {
			File[] listOfFiles = null;
			int count = 0;
			do {
				logger.info("Searching Directory for Files");
				File directory = new File(downloadPath);
				listOfFiles = directory.listFiles();
				count++;
				pause(3000);
			} while (listOfFiles.length <= 0 && count < 30);

			for (File listOfFile : listOfFiles) {
				logger.info("Found Files, checking they match file extension: " + fileExtension);
				if (listOfFile.isFile()) {
					String fileName = listOfFile.getName();
					logger.info("Found file: " + fileName);
					if (fileName.contains(fileExtension)) {
						logger.info("File Matches file extension");
						File file = new File(downloadPath + fileName);
						if (file.exists() && delete == true) {
							file.delete();
							return true;
						}
					}
				}
			}
			logger.info("Could Not find Export Report File by File Extension: " + fileExtension);
			return false;
		} catch (Exception e) {
			throw new Exception("Error occurred with the File Extension : " + fileExtension + " in Directory: "
					+ downloadPath + "Error: " + e);
		}

	}

	/**
	 * Returns the number of elements currently in a view
	 *
	 * @param element
	 * @return
	 * @throws Exception
	 */
	public int getElementCount(By element) throws Exception {
		try {
			List<WebElement> elementCountList = driver.findElements(element);
			if (elementCountList.size() <= 0) {
				logger.info("Could not find any elements that match criteria " + element);
				return elementCountList.size();
			} else {
				logger.info("Found " + elementCountList.size() + " many elements");
				return elementCountList.size();
			}
		} catch (Exception e) {
			throw new Exception("Error occurred trying to find number of elements that match " + element, e);
		}
	}

	public int getTotalNumberOfElementsThroughAllPages(By element, By nextPageButton) throws Exception {
		try {
			logger.info("Getting total of elements while paginating");
			int tries = 0;
			int pageTotalElements = 0;
			int totalElements = 0;
			verifyElementDisplayedWithWait(element, ConfigFileReader.getConfigFileReader().getImplicitlyWait());
			List<WebElement> initialElementsCount = world.driver.findElements(element);
			int initialPageElementCount = initialElementsCount.size();
			if (initialPageElementCount <= 0) {
				logger.info("Found No elements");
				return totalElements;
			} else {
				if (verifyElementDisplayedWithWait(nextPageButton)) {
					scrollElementIntoViewJS(nextPageButton, "top");
					while (isElementCurrentlyDisplayed(nextPageButton) && tries < 150) {
						List<WebElement> currentPageRows = world.driver.findElements(element);
						pageTotalElements = currentPageRows.size();
						logger.info("Found " + pageTotalElements + " elements on this page");
						totalElements += pageTotalElements;
						logger.info("Total elements currently is " + totalElements);
						click(nextPageButton);
						tries++;
						pause(3000);
						if (verifyElementDisplayedWithWait(nextPageButton)) {
							scrollElementIntoViewJS(nextPageButton, "top");
						} else {
							break;
						}
					}
					List<WebElement> lastPageElements = world.driver.findElements(element);
					totalElements += lastPageElements.size();
					logger.info("Found a total of " + totalElements + " elements");
					return totalElements;
				} else {
					logger.info("Found " + initialPageElementCount + " elements on the only page");
					return initialPageElementCount;
				}
			}
		} catch (Exception e) {
			throw new Exception("Error occurred trying to find elements by " + element + " and pagination button "
					+ nextPageButton + "Error: " + e.getMessage() + e.getStackTrace(), e);
		}
	}

	public boolean isElementCurrentlyDisplayed(By element) throws Exception {
		boolean isDisplayed = false;
		List<WebElement> elementList = driver.findElements(element);
		if (elementList.size() <= 0) {
			return isDisplayed;
		} else if (elementList.size() > 1) {
			throw new Exception("Error: Found multiple elements");
		} else {
			WebElement foundElement = elementList.get(0);
			if (foundElement.isDisplayed()) {
				isDisplayed = true;
				return isDisplayed;
			} else {
				return isDisplayed;
			}
		}
	}

	/**
	 * Handle Exceptions
	 *
	 * @param e the exception which has been caught and passed in
	 * @throws Exception
	 */
	protected void handleExceptions(Exception e) throws Exception {
		logger.error("Caught " + e.getClass().getSimpleName());
		failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
	}

	public void checkForAppIssue() throws Exception {
		List<String> errXpaths = new ArrayList<String>();
		List<String> skipThese = new ArrayList<String>();
		errXpaths.add("//label[@class='error']");
		errXpaths.add("//div[@class='title'][.='Can’t reach this page']");
		skipThese.add("Please sign in with your updated information. Thank you!");
		skipThese.add("Email Address not valid. Please enter a valid email address, example: name@email.com");
		skipThese.add("The email address or password you entered is incorrect.");
		skipThese.add("Email Address not valid. Please enter a valid email address, example: name@email.com");
		errXpaths.add("//div[@id='error-main']");
		boolean displayed = false;
		boolean skip = false;
		for (String xpath : errXpaths) {
			By errElem = By.xpath(xpath);
			if (isElemDisplayed(errElem, 1)) {
				displayed = true;
				String msg = getTextFromElement(errElem);
				for (String skipMsg : skipThese) {
					if (msg.equalsIgnoreCase(skipMsg)) {
						skip = true;
						break;
					}
				}
				if(msg.contains("The user has already reached the maximum allowed number of sessions")){
					throw new TooManySessionsException("Too many sessions in Ted for this user");
				}
				if (displayed && !skip) {
					throw new AppIssueException("Possible App Issue\nMessage = " + msg);
				}
			}
		}
	}

	public boolean isElemDisplayed(By element, int timeoutsecs) {
		long configTimeout = ConfigFileReader.getConfigFileReader().getImplicitlyWait();
		try {
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			new WebDriverWait(driver, timeoutsecs).until(ExpectedConditions.visibilityOfElementLocated(element));
			WebElement webElement = driver.findElement(element);
			if (webElement.isEnabled() && webElement.isDisplayed()) {
				return true;
			}
		} catch (Exception e) {
			return false;
		} finally {
			driver.manage().timeouts().implicitlyWait(configTimeout, TimeUnit.SECONDS);
		}
		return false;
	}

	public void handleBitDefenderSecurity() throws Exception {
		By bitDefenderTitle = By
				.xpath("//div[@class='title'][.='Bitdefender Endpoint Security Tools blocked this page']");
		if (isElemDisplayed(bitDefenderTitle, 1)) {
			click(By.xpath("//a[@href='javascript:proceedAddMitmExclusion()']"));
		}
	}

	public void waitForTBBSpinner() {
		try {
			waitForElementInvisibility(By.xpath("//div[@class='icon icon-spinner icon-spin cart-spinner']"));
		}catch (Exception ignored){}
	}

	public void waitForBODShacSpinner() {
		try {
			waitForElementInvisibility(By.xpath("//form//i[@class='fas fa-spinner fa-spin']"));
		}catch (Exception ignored){}
	}

	public void waitForBODShacSpinner(long timeoutSecs) {
		try {
			waitForElementInvisibility(By.xpath("//form//i[@class='fas fa-spinner fa-spin']"),timeoutSecs);
		}catch (Exception ignored){}
	}

	public void selectSubMenuInBYD(By mainMenu, By subMenu) throws Exception {
		try {
			verifyElementPresence(mainMenu);
			Actions actions = new Actions(driver);
			actions.moveToElement(driver.findElement(mainMenu)).click().build().perform();
			logger.info("Clicked Main Menu " + mainMenu.toString());
			syncObjects("hcwait2");
			try {
				clickElementUsingJavaScript(subMenu);
			} catch (Exception e) {
				logger.info("Failed to click sub menu using JS");
				actions.moveToElement(driver.findElement(mainMenu)).click().build().perform();
				logger.info("Agiain Clicked Main Menu " + mainMenu.toString());
				try {
					actions.pause(2000).click(driver.findElement(subMenu)).build().perform();
				} catch (MoveTargetOutOfBoundsException ex) {
					logger.info("Failed to click sub menu using actions");
					selectSubMenuInBYD(mainMenu, subMenu);
				}
			}
			logger.info("Clicked Sub-Main Menu " + subMenu.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to click sub menu " + subMenu + " in main menu " + mainMenu, e);
		}
	}

	public void openHamburgerMenu() throws Exception {
		try {
			ResourceBundle homePageElements = ResourceBundle
					.getBundle("com.ted.automation.elementlib.TBB.HomePage", Locale.getDefault());
			if (getAttributeValueFromElement(By.cssSelector(homePageElements.getString("mobile_Grip_Open")), "class")
					.toLowerCase().endsWith("small-only".toLowerCase())) {
				click(By.xpath(homePageElements.getString("mobile_grip")));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to open hamburger menu", e);
		}
	}

	public void closeHamburgerMenu() throws Exception {
		try {
			ResourceBundle homePageElements = ResourceBundle
					.getBundle("com.ted.automation.elementlib.TBB.HomePage", Locale.getDefault());
			if (getAttributeValueFromElement(By.cssSelector(homePageElements.getString("mobile_Grip_Open")), "class")
					.toLowerCase().endsWith("isOpen".toLowerCase())) {
				click(By.xpath(homePageElements.getString("mobile_grip")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to close hamburger menu", e);
		}

	}

	public String switchToChildWindowValidate() throws Exception {
		try {
			parentWindow = world.driver.getWindowHandle();
			Set<String> redirectWindow = world.driver.getWindowHandles();
			String redirectURL = "";
			logger.info("Browser window count: " + redirectWindow.size());

			for (String window : redirectWindow) {
				if (!window.equals(parentWindow)) {
					driver.switchTo().window(window);
					redirectURL = driver.getCurrentUrl();
					logger.info("Window Re-Route URL : " + redirectURL);
					driver.switchTo().window(window).close();
					switchToParentWindow();
				}
			}
			return redirectURL;
		} catch (Exception e) {
			throw new Exception("ERROR: Redirect URL not found i.e., new browser tab or Window not found.");
		}
	}

	public boolean hoverAndClick(By locator) {
		try {
			WebElement element = getElemntUsingByLocator(driver, locator);
			Actions action = new Actions(driver);
			action.moveToElement(element).perform();
			action.click(element).perform();
			// action.moveToElement(element).click(element).build().perform();
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return true;
	}

	public boolean submit(By locator) {
		try {
			WebElement element = getElemntUsingByLocator(driver, locator);
			element.submit();
//			waitUntilJQueryReady();

		} catch (Exception e) {
		    if(e.getMessage().contains(" Missing Template ERR_CONNECT_FAIL")){
		    	submit(locator);
			}
		    else {
				failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
			}
		}
		return true;
	}

	public boolean onMouseOver(By locator) {
		String mouseOverScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover',"
				+ "true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject)"
				+ "{ arguments[0].fireEvent('onmouseover');}";
		try {
			WebElement element = getElemntUsingByLocator(driver, locator);
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript(mouseOverScript, element);
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return true;

	}

	public boolean onClick(By locator) {
		String onClickScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('click',"
				+ "true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject)"
				+ "{ arguments[0].fireEvent('onclick');}";
		try {
			WebElement element = getElemntUsingByLocator(driver, locator);
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript(onClickScript, element);
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return true;

	}

	public boolean onChange(By locator) {
		String onchangeScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover',"
				+ "true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject)"
				+ "{ arguments[0].fireEvent('change');}";
		try {
			WebElement element = getElemntUsingByLocator(driver, locator);
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript(onchangeScript, element);
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return true;
	}

	public boolean clickWithoutSync(By locator) throws Exception {
		try {
			logger.info("Clicking object using By element : " + locator);
			new WebDriverWait(driver, ConfigFileReader.getConfigFileReader().getExplicitlyWait()).until(ExpectedConditions.elementToBeClickable(locator)).click();
			return true;
		} catch (ElementNotInteractableException e) {
			syncObjects("hcwait1");
			return clickElementUsingJavaScript(locator);
		} catch (WebDriverException e) {
			if (e.getMessage().contains("Missing Template ERR_CONNECT_FAIL")) {
				logger.info("By Passing the exception on sauce lab " + e.getMessage());
				clickWithoutSync(locator);
			}
		} catch (Exception e) {
			logger.info("Failed to click : " + locator);
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
		}
		return false;
	}


    public void javaScriptClickUsingXpath(String xPath) {
        try {
			String javaScriptTOExecute = "(document.evaluate(\"" + xPath + "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue).click()";
			logger.info("Java Script to be executed ------>" +javaScriptTOExecute);
					((JavascriptExecutor) driver).executeScript(javaScriptTOExecute);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }

	public boolean jsClickUsingXpath(String xPath) {
		try {
			String javaScriptTOExecute = "(document.evaluate(\"" + xPath + "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue).click()";
			logger.info("Java Script to be executed ------>" +javaScriptTOExecute);
			((JavascriptExecutor) driver).executeScript(javaScriptTOExecute);
		} catch (Exception e) {
			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
		}
		return true;
	}

//	public Boolean createNewCoachWithTDM(String customerType) {
//		try {
//			List<HashMap> users= OnlineAPIServices.getUserDetailsFromTDM(customerType,world.getLocale(),world.getTestEnvironment());
//			pause(30);
//			world.getCustomerDetails().put("TBB_ExistingFreeUser_Username", users.get(0).get("Email").toString());
//			world.getCustomerDetails().put("TBB_ExistingFreeUser_Password",  users.get(0).get("Password").toString());
//			world.getCustomerDetails().put("CoachName",users.get(0).get("GncCoachID").toString());
//			world.getCustomerDetails().put("fname1",users.get(0).get("FirstName").toString());
//			world.getCustomerDetails().put("lname1",users.get(0).get("LastName").toString());
//			logger.info(world.getCustomerDetails().get("CoachName"));
//			logger.info(world.getCustomerDetails().get("TBB_ExistingFreeUser_Username"));
//			logger.info(world.getCustomerDetails().get("TBB_ExistingFreeUser_Password"));
//			return true;
//		} catch (Exception e) {
//			failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
//		}
//		return false;
//	}

	/**
	 * @Description : Verify expected message with text from Element removing spaces, as actual text from safari browser is different
	 * @param locator,ExpectedMessage
	 * @return
	 * @throws Exception
	 */
	public boolean verifyMessageWithElementTextRemovingSpaces(By locator, String expectedMessage) throws Exception {
		String actualMessage = "";
		try {
			pause(2000);
			scrollElementIntoViewJS(locator);
			actualMessage = getTextFromElement(locator).trim();
			if ((expectedMessage.replaceAll(" ","")).equalsIgnoreCase(actualMessage.replaceAll(" ",""))) {
				return true;
			} else {
				throw new AppIssueException("Expected message ("+expectedMessage+") not matched with actual ("+actualMessage+")");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception( e.getMessage() +"\n"+
					"ERROR : Failed to verify message. \nEXPECTED: " + expectedMessage + "\n ACTUAL: " + actualMessage);
		}
	}

	public void handleNonSecureWarningPopupInIos(){
		if (world.getBrowser().equalsIgnoreCase(String.valueOf(Constants.BROWSER.SAFARI))) {
			pause(4000);
			driver.switchTo().activeElement().sendKeys(Keys.ESCAPE);
			driver.navigate().refresh();
		}
	}
}
