package com.ofbiz.automation.pages.ofbiz;

import com.ofbiz.automation.common.World;
import com.ofbiz.automation.exceptions.ToInvestigateException;
import com.ofbiz.automation.libraries.ConfigFileReader;
import com.ofbiz.automation.pages.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import java.util.ResourceBundle;

public class OfbizLoginPage extends BasePage {
    private World world;

    ResourceBundle loginPageElements;
    Logger logger;
    By inputEmail;
    By inputPassword;
    By buttonSignin;
    By buttonAdvance;
    By linkproceed;

    public void InitElements() {
        logger = LogManager.getLogger(OfbizLoginPage.class);
        loginPageElements = ResourceBundle.getBundle("com.ofbiz.automation.elementlib.OFBIZ.LoginPage", world.getFormattedLocale());
        inputEmail = By.xpath(loginPageElements.getString("input_Email"));
        inputPassword = By.xpath(loginPageElements.getString("input_Password"));
        buttonSignin = By.xpath(loginPageElements.getString("button_Signin"));
        buttonAdvance = By.xpath(loginPageElements.getString("button_Advance"));
        linkproceed = By.xpath(loginPageElements.getString("link_proceed"));
    }

    public OfbizLoginPage(World world) {
        super(world, world.driver);
        this.world = world;
        InitElements();
    }

    public boolean navigateToOfbizLoginPage() throws Exception {
        try {
            logger.info("Navigating to Ofbiz Login Page");
            navigateToWebPage(ConfigFileReader.getConfigFileReader().getOFBIZUrl(world.getTestEnvironment()));
            pause(20000);
            syncObjects("hcwait2");
            click(buttonAdvance);
            logger.info("Clicking on proceed link");
            click(linkproceed);
        } catch (Exception e) {
            throw new ToInvestigateException(e.getMessage());
        }
        return true;
    }

    public boolean login(String username, String password) throws Exception {
        try {
            logger.info("Trying to Login to Ofbiz");
            waitForElementToDisplay(inputEmail, 10);
            logger.info("Entering username");
            enterText(inputEmail, username);
            logger.info("Entering Password");
            enterText(inputPassword, password);
            logger.info("Clicking on SignIn button");
            verifyElementDisplayed(buttonSignin);
            click(buttonSignin);
            return true;
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
        return false;
    }
}
