package com.ofbiz.automation.stepdefs.ofbiz;

import com.ofbiz.automation.common.World;
import com.ofbiz.automation.pages.ofbiz.OfbizHomePage;
import com.ofbiz.automation.pages.ofbiz.OfbizLoginPage;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import java.util.ResourceBundle;

public class OfbizLoginPageStepDefs {
    private World world;
    OfbizLoginPage ofbizloginpage;
    OfbizHomePage ofbizhomepage;
    private String emailId;
    private String password;
    ResourceBundle loginTestData;

    public OfbizLoginPageStepDefs(World world) {
        this.world = world;
        ofbizloginpage = new OfbizLoginPage(this.world);
        ofbizhomepage = new OfbizHomePage(this.world);
        loginTestData = ResourceBundle.getBundle("com.ofbiz.automation.locales.Locale", world.getFormattedLocale());
    }

    @Given("I am on Ofbiz Login Page")
    public void iAmOnOfbizLoginPage() {
        try {
            ofbizloginpage.navigateToOfbizLoginPage();
        } catch (Exception e) {
            ofbizloginpage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }

    }

    @When("I login Ofbiz with existing user")
    public void iLoginOfbizWithExistingUser() {
        try {
            world.getTestDataJson().put("Ofbiz_Email", loginTestData.getString("Ofbiz_login_email"));
            world.getTestDataJson().put("Ofbiz_Password", loginTestData.getString("Ofbiz_login_password"));
            emailId = loginTestData.getString("Ofbiz_login_email");
            password = loginTestData.getString("Ofbiz_login_password");
            ofbizloginpage.login(emailId, password);
        } catch (Exception e) {
            ofbizloginpage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }
}